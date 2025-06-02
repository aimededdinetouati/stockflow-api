package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Supplier;
import com.adeem.stockflow.repository.projection.SupplierActivityProjection;
import com.adeem.stockflow.repository.projection.SupplierStatsProjection;
import com.adeem.stockflow.repository.projection.TopSuppliersProjection;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Supplier entity.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier> {
    /**
     * Find supplier by ID and client account ID.
     */
    Optional<Supplier> findByIdAndClientAccountId(Long id, Long clientAccountId);

    /**
     * Check if supplier exists by email and client account.
     */
    boolean existsByEmailAndClientAccountId(String email, Long clientAccountId);

    /**
     * Check if supplier exists by phone and client account.
     */
    boolean existsByPhoneAndClientAccountId(String phone, Long clientAccountId);

    /**
     * Check if supplier exists by taxId and client account.
     */
    boolean existsByTaxIdAndClientAccountId(String taxId, Long clientAccountId);

    /**
     * Find supplier by email and client account.
     */
    Optional<Supplier> findByEmailAndClientAccountId(String email, Long clientAccountId);

    /**
     * Search suppliers by query across multiple fields.
     */
    @Query(
        """
        SELECT s FROM Supplier s
        WHERE s.clientAccount.id = :clientAccountId
        AND s.active = true
        AND (
            LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(s.companyName) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(s.phone) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(s.taxId) LIKE LOWER(CONCAT('%', :query, '%'))
        )
        """
    )
    Page<Supplier> searchSuppliers(@Param("query") String query, @Param("clientAccountId") Long clientAccountId, Pageable pageable);

    /**
     * Soft delete supplier by setting active to false.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Supplier s SET s.active = false WHERE s.id = :id AND s.clientAccount.id = :clientAccountId")
    int softDeleteSupplier(@Param("id") Long id, @Param("clientAccountId") Long clientAccountId);

    /**
     * Reactivate supplier by setting active to true.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Supplier s SET s.active = true WHERE s.id = :id AND s.clientAccount.id = :clientAccountId")
    int reactivateSupplier(@Param("id") Long id, @Param("clientAccountId") Long clientAccountId);

    /**
     * Check if supplier has any purchase orders.
     */
    @Query("SELECT COUNT(po.id) > 0 FROM PurchaseOrder po WHERE po.supplier.id = :supplierId")
    boolean hasActivePurchaseOrders(@Param("supplierId") Long supplierId);

    /**
     * QUERY 1: Comprehensive overview statistics in a single query
     * Replaces 11+ separate count queries with one optimized native SQL query
     */
    @Query(
        value = """
        WITH supplier_stats AS (
            SELECT
                s.id,
                s.active,
                s.created_date,
                s.last_modified_date,
                CASE WHEN s.address_id IS NOT NULL THEN 1 ELSE 0 END as has_address,
                COUNT(po.id) as order_count,
                COALESCE(SUM(po.total), 0) as total_value
            FROM supplier s
            LEFT JOIN purchase_order po ON po.supplier_id = s.id
            WHERE s.client_account_id = :clientAccountId
            GROUP BY s.id, s.active, s.created_date, s.last_modified_date, s.address_id
        ),
        time_boundaries AS (
            SELECT
                NOW() - INTERVAL '7 days' as one_week_ago,
                NOW() - INTERVAL '30 days' as one_month_ago
        ),
        comprehensive_stats AS (
            SELECT
                COUNT(ss.id) as total_suppliers,
                COUNT(CASE WHEN ss.active = true THEN 1 END) as active_suppliers,
                COUNT(CASE WHEN ss.active = false THEN 1 END) as inactive_suppliers,
                COUNT(CASE WHEN ss.has_address = 1 THEN 1 END) as suppliers_with_addresses,
                COUNT(CASE WHEN ss.has_address = 0 THEN 1 END) as suppliers_without_addresses,
                COUNT(CASE WHEN ss.created_date >= tb.one_week_ago THEN 1 END) as suppliers_added_this_week,
                COUNT(CASE WHEN ss.created_date >= tb.one_month_ago THEN 1 END) as suppliers_added_this_month,
                COUNT(CASE WHEN ss.order_count > 0 THEN 1 END) as suppliers_with_purchase_orders,
                COALESCE(SUM(ss.total_value), 0) as total_purchase_order_value,
                MAX(ss.created_date) as last_supplier_created,
                MAX(ss.last_modified_date) as last_supplier_modified
            FROM supplier_stats ss
            CROSS JOIN time_boundaries tb
        )
        SELECT
            cs.total_suppliers as totalSuppliers,
            cs.active_suppliers as activeSuppliers,
            cs.inactive_suppliers as inactiveSuppliers,
            cs.suppliers_with_addresses as suppliersWithAddresses,
            cs.suppliers_without_addresses as suppliersWithoutAddresses,
            cs.suppliers_added_this_week as suppliersAddedThisWeek,
            cs.suppliers_added_this_month as suppliersAddedThisMonth,
            cs.suppliers_with_purchase_orders as suppliersWithPurchaseOrders,
            cs.total_purchase_order_value as totalPurchaseOrderValue,
            cs.last_supplier_created as lastSupplierCreated,
            cs.last_supplier_modified as lastSupplierModified
        FROM comprehensive_stats cs
        """,
        nativeQuery = true
    )
    Optional<SupplierStatsProjection> getComprehensiveSupplierStats(@Param("clientAccountId") Long clientAccountId);

    /**
     * QUERY 2: Get top suppliers data for both rankings (by order count and by value)
     * Replaces 2 separate top supplier queries with one optimized query
     */
    @Query(
        value = """
        SELECT
            s.id as supplierId,
            CASE
                WHEN s.company_name IS NOT NULL AND s.company_name != '' THEN s.company_name
                ELSE CONCAT(s.first_name, ' ', s.last_name)
            END as displayName,
            COUNT(po.id) as orderCount,
            COALESCE(SUM(po.total), 0) as totalValue
        FROM supplier s
        LEFT JOIN purchase_order po ON po.supplier_id = s.id
        WHERE s.client_account_id = :clientAccountId
        AND s.active = true
        GROUP BY s.id, s.company_name, s.first_name, s.last_name
        HAVING COUNT(po.id) > 0
        ORDER BY totalValue DESC
        LIMIT :limit * 2
        """,
        nativeQuery = true
    )
    List<TopSuppliersProjection> getTopSuppliersStats(@Param("clientAccountId") Long clientAccountId, @Param("limit") int limit);

    /**
     * QUERY 3: Get recent supplier activities efficiently
     * Optimized version of the existing recent activities query
     */
    @Query(
        value = """
        SELECT
            CASE
                WHEN s.created_date > s.last_modified_date THEN 'CREATED'
                ELSE 'UPDATED'
            END as action,
            CASE
                WHEN s.company_name IS NOT NULL AND s.company_name != '' THEN s.company_name
                ELSE CONCAT(s.first_name, ' ', s.last_name)
            END as displayName,
            CASE
                WHEN s.created_date > s.last_modified_date THEN s.created_date
                ELSE s.last_modified_date
            END as activityDate,
            s.notes
        FROM supplier s
        WHERE s.client_account_id = :clientAccountId
        ORDER BY activityDate DESC
        LIMIT :limit
        """,
        nativeQuery = true
    )
    List<SupplierActivityProjection> getRecentActivitiesOptimized(
        @Param("clientAccountId") Long clientAccountId,
        @Param("limit") int limit
    );
}
