package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Supplier;
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
     * Find active suppliers by client account.
     */
    Page<Supplier> findByClientAccountIdAndActiveTrue(Long clientAccountId, Pageable pageable);

    /**
     * Find all suppliers by client account (active and inactive).
     */
    Page<Supplier> findByClientAccountId(Long clientAccountId, Pageable pageable);

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
     * Count active suppliers by client account.
     */
    long countByClientAccountIdAndActiveTrue(Long clientAccountId);

    /**
     * Count inactive suppliers by client account.
     */
    long countByClientAccountIdAndActiveFalse(Long clientAccountId);

    /**
     * Count total suppliers by client account.
     */
    long countByClientAccountId(Long clientAccountId);

    /**
     * Count suppliers with addresses by client account.
     */
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.clientAccount.id = :clientAccountId AND s.address IS NOT NULL")
    long countSuppliersWithAddresses(@Param("clientAccountId") Long clientAccountId);

    /**
     * Count suppliers without addresses by client account.
     */
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.clientAccount.id = :clientAccountId AND s.address IS NULL")
    long countSuppliersWithoutAddresses(@Param("clientAccountId") Long clientAccountId);

    /**
     * Count suppliers added within date range.
     */
    long countByClientAccountIdAndCreatedDateBetween(Long clientAccountId, Instant startDate, Instant endDate);

    /**
     * Find latest created supplier by client account.
     */
    @Query("SELECT s FROM Supplier s WHERE s.clientAccount.id = :clientAccountId ORDER BY s.createdDate DESC")
    List<Supplier> findLatestCreated(@Param("clientAccountId") Long clientAccountId, Pageable pageable);

    /**
     * Find latest modified supplier by client account.
     */
    @Query("SELECT s FROM Supplier s WHERE s.clientAccount.id = :clientAccountId ORDER BY s.lastModifiedDate DESC")
    List<Supplier> findLatestModified(@Param("clientAccountId") Long clientAccountId, Pageable pageable);

    /**
     * Find recent supplier activities for statistics.
     */
    @Query(
        """
        SELECT s.id,
               CASE
                   WHEN s.createdDate > s.lastModifiedDate THEN 'CREATED'
                   ELSE 'UPDATED'
               END as action,
               CASE
                   WHEN s.companyName IS NOT NULL AND s.companyName != '' THEN s.companyName
                   ELSE CONCAT(s.firstName, ' ', s.lastName)
               END as displayName,
               CASE
                   WHEN s.createdDate > s.lastModifiedDate THEN s.createdDate
                   ELSE s.lastModifiedDate
               END as activityDate,
               s.notes
        FROM Supplier s
        WHERE s.clientAccount.id = :clientAccountId
        ORDER BY activityDate DESC
        """
    )
    List<Object[]> findRecentActivities(@Param("clientAccountId") Long clientAccountId, Pageable pageable);

    /**
     * Get top suppliers by purchase order count.
     */
    @Query(
        """
        SELECT s.id,
               CASE
                   WHEN s.companyName IS NOT NULL AND s.companyName != '' THEN s.companyName
                   ELSE CONCAT(s.firstName, ' ', s.lastName)
               END as displayName,
               COUNT(po.id) as orderCount,
               COALESCE(SUM(po.total), 0) as totalValue
        FROM Supplier s
        LEFT JOIN PurchaseOrder po ON po.supplier.id = s.id
        WHERE s.clientAccount.id = :clientAccountId
        AND s.active = true
        GROUP BY s.id, s.companyName, s.firstName, s.lastName
        ORDER BY orderCount DESC
        """
    )
    List<Object[]> findTopSuppliersByOrderCount(@Param("clientAccountId") Long clientAccountId, Pageable pageable);

    /**
     * Get top suppliers by purchase order value.
     */
    @Query(
        """
        SELECT s.id,
               CASE
                   WHEN s.companyName IS NOT NULL AND s.companyName != '' THEN s.companyName
                   ELSE CONCAT(s.firstName, ' ', s.lastName)
               END as displayName,
               COUNT(po.id) as orderCount,
               COALESCE(SUM(po.total), 0) as totalValue
        FROM Supplier s
        LEFT JOIN PurchaseOrder po ON po.supplier.id = s.id
        WHERE s.clientAccount.id = :clientAccountId
        AND s.active = true
        GROUP BY s.id, s.companyName, s.firstName, s.lastName
        ORDER BY totalValue DESC
        """
    )
    List<Object[]> findTopSuppliersByValue(@Param("clientAccountId") Long clientAccountId, Pageable pageable);

    /**
     * Count suppliers with purchase orders.
     */
    @Query(
        """
        SELECT COUNT(DISTINCT s.id)
        FROM Supplier s
        INNER JOIN PurchaseOrder po ON po.supplier.id = s.id
        WHERE s.clientAccount.id = :clientAccountId
        AND s.active = true
        """
    )
    long countSuppliersWithPurchaseOrders(@Param("clientAccountId") Long clientAccountId);

    /**
     * Get total purchase order value for all suppliers.
     */
    @Query(
        """
        SELECT COALESCE(SUM(po.total), 0)
        FROM PurchaseOrder po
        WHERE po.supplier.clientAccount.id = :clientAccountId
        """
    )
    BigDecimal getTotalPurchaseOrderValue(@Param("clientAccountId") Long clientAccountId);

    /**
     * Find suppliers by address city.
     */
    @Query("SELECT s FROM Supplier s WHERE s.clientAccount.id = :clientAccountId AND s.address.city = :city")
    List<Supplier> findByAddressCity(@Param("clientAccountId") Long clientAccountId, @Param("city") String city);

    /**
     * Find suppliers by address country.
     */
    @Query("SELECT s FROM Supplier s WHERE s.clientAccount.id = :clientAccountId AND s.address.country = :country")
    List<Supplier> findByAddressCountry(@Param("clientAccountId") Long clientAccountId, @Param("country") String country);

    /**
     * Soft delete supplier by setting active to false.
     */
    @Modifying
    @Query("UPDATE Supplier s SET s.active = false WHERE s.id = :id AND s.clientAccount.id = :clientAccountId")
    int softDeleteSupplier(@Param("id") Long id, @Param("clientAccountId") Long clientAccountId);

    /**
     * Reactivate supplier by setting active to true.
     */
    @Modifying
    @Query("UPDATE Supplier s SET s.active = true WHERE s.id = :id AND s.clientAccount.id = :clientAccountId")
    int reactivateSupplier(@Param("id") Long id, @Param("clientAccountId") Long clientAccountId);

    /**
     * Check if supplier has any purchase orders.
     */
    @Query("SELECT COUNT(po.id) > 0 FROM PurchaseOrder po WHERE po.supplier.id = :supplierId")
    boolean hasActivePurchaseOrders(@Param("supplierId") Long supplierId);
}
