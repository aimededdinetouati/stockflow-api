package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.ProductFamily;
import com.adeem.stockflow.repository.projection.FamilyDetailStatsProjection;
import com.adeem.stockflow.repository.projection.ProductFamilyStatsProjection;
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
 * Spring Data JPA repository for the ProductFamily entity.
 * Enhanced with multi-tenant support and comprehensive statistics queries.
 */
@Repository
public interface ProductFamilyRepository extends JpaRepository<ProductFamily, Long>, JpaSpecificationExecutor<ProductFamily> {
    // Basic multi-tenant queries

    Optional<ProductFamily> findByIdAndClientAccountId(Long id, Long clientAccountId);

    boolean existsByNameAndClientAccountId(String name, Long clientAccountId);

    boolean existsByNameAndClientAccountIdAndIdNot(String name, Long clientAccountId, Long excludeId);

    Page<ProductFamily> findByNameContainingIgnoreCaseAndClientAccountId(String name, Long clientAccountId, Pageable pageable);

    // OPTIMIZED REPOSITORY METHODS - Replace multiple queries with 3 efficient ones

    /**
     * QUERY 1: Comprehensive overview statistics in a single query
     * Replaces 12+ separate queries with one optimized native SQL query
     */
    @Query(
        value = """
        WITH family_stats AS (
            SELECT
                pf.id as family_id,
                pf.name as family_name,
                pf.created_date,
                pf.last_modified_date,
                COUNT(p.id) as product_count,
                COALESCE(SUM(p.selling_price * i.available_quantity), 0) as family_value,
                COUNT(CASE WHEN i.available_quantity <= p.minimum_stock_level AND i.available_quantity > 0 THEN 1 END) as low_stock_count,
                COUNT(CASE WHEN i.available_quantity = 0 THEN 1 END) as out_of_stock_count
            FROM product_family pf
            LEFT JOIN product p ON p.product_family_id = pf.id
            LEFT JOIN inventory i ON i.product_id = p.id
            WHERE pf.client_account_id = :clientAccountId
            GROUP BY pf.id, pf.name, pf.created_date, pf.last_modified_date
        ),
        time_boundaries AS (
            SELECT
                NOW() - INTERVAL '7 days' as one_week_ago,
                NOW() - INTERVAL '30 days' as one_month_ago
        ),
        comprehensive_stats AS (
            SELECT
                COUNT(fs.family_id) as total_families,
                COALESCE(SUM(fs.product_count), 0) as total_products,
                COUNT(CASE WHEN fs.product_count > 0 THEN 1 END) as families_with_products,
                COUNT(CASE WHEN fs.low_stock_count > 0 THEN 1 END) as families_with_low_stock,
                COUNT(CASE WHEN fs.out_of_stock_count > 0 THEN 1 END) as families_with_out_of_stock,
                COALESCE(SUM(fs.family_value), 0) as total_inventory_value,
                COUNT(CASE WHEN fs.created_date >= tb.one_week_ago THEN 1 END) as families_created_this_week,
                COUNT(CASE WHEN fs.created_date >= tb.one_month_ago THEN 1 END) as families_created_this_month,
                MAX(fs.created_date) as last_family_created,
                MAX(fs.last_modified_date) as last_family_modified,
                MAX(fs.family_value) as highest_family_value,
                MIN(CASE WHEN fs.product_count > 0 THEN fs.family_value END) as lowest_family_value,
                MAX(fs.product_count) as largest_family_size,
                MIN(CASE WHEN fs.product_count > 0 THEN fs.product_count END) as smallest_family_size
            FROM family_stats fs
            CROSS JOIN time_boundaries tb
        )
        SELECT
            cs.total_families as totalFamilies,
            cs.total_products as totalProducts,
            cs.families_with_products as familiesWithProducts,
            cs.families_with_low_stock as familiesWithLowStock,
            cs.families_with_out_of_stock as familiesWithOutOfStock,
            cs.total_inventory_value as totalInventoryValue,
            cs.families_created_this_week as familiesCreatedThisWeek,
            cs.families_created_this_month as familiesCreatedThisMonth,
            cs.last_family_created as lastFamilyCreated,
            cs.last_family_modified as lastFamilyModified,
            cs.highest_family_value as highestFamilyValue,
            cs.lowest_family_value as lowestFamilyValue,
            cs.largest_family_size as largestFamilySize,
            cs.smallest_family_size as smallestFamilySize
        FROM comprehensive_stats cs
        """,
        nativeQuery = true
    )
    Optional<ProductFamilyStatsProjection> getComprehensiveOverviewStats(@Param("clientAccountId") Long clientAccountId);

    /**
     * Returns all family data needed for both "top by products" and "top by value" rankings
     */
    @Query(
        value = """
        SELECT
        pf.id as familyId,
        pf.name as familyName,
        COUNT(p.id) as productCount,
        COALESCE(SUM(p.selling_price * i.available_quantity), 0) as totalValue,
        COALESCE(SUM(i.available_quantity), 0) as totalQuantity,
        COUNT(CASE WHEN i.available_quantity <= p.minimum_stock_level AND i.available_quantity > 0 THEN 1 END) as lowStockProducts,
        COUNT(CASE WHEN i.available_quantity = 0 THEN 1 END) as outOfStockProducts,
        pf.last_modified_date as lastModifiedDate
        FROM product_family pf
        LEFT JOIN product p ON p.product_family_id = pf.id
        LEFT JOIN inventory i ON i.product_id = p.id
        WHERE pf.client_account_id = :clientAccountId
        GROUP BY pf.id, pf.name, pf.last_modified_date
        HAVING COUNT(p.id) > 0
        ORDER BY totalValue DESC
        LIMIT :limit * 2
        """,
        nativeQuery = true
    )
    List<FamilyDetailStatsProjection> getTopFamiliesStats(@Param("clientAccountId") Long clientAccountId, @Param("limit") int limit);

    /**
     * This mega-query gets almost everything in one go, but is more complex
     */
    @Query(
        value = """
        WITH family_details AS (
            SELECT
                pf.id as family_id,
                pf.name as family_name,
                pf.created_date,
                pf.last_modified_date,
                COUNT(p.id) as product_count,
                COALESCE(SUM(p.selling_price * i.available_quantity), 0) as family_value,
                COALESCE(SUM(i.available_quantity), 0) as total_quantity,
                COUNT(CASE WHEN i.available_quantity <= p.minimum_stock_level AND i.available_quantity > 0 THEN 1 END) as low_stock_count,
                COUNT(CASE WHEN i.available_quantity = 0 THEN 1 END) as out_of_stock_count,
                p.category
            FROM product_family pf
            LEFT JOIN product p ON p.product_family_id = pf.id
            LEFT JOIN inventory i ON i.product_id = p.id
            WHERE pf.client_account_id = :clientAccountId
            GROUP BY pf.id, pf.name, pf.created_date, pf.last_modified_date, p.category
        ),
        aggregated_family_stats AS (
            SELECT
                fd.family_id,
                fd.family_name,
                fd.created_date,
                fd.last_modified_date,
                SUM(fd.product_count) as total_products,
                SUM(fd.family_value) as total_value,
                SUM(fd.total_quantity) as total_quantity,
                SUM(fd.low_stock_count) as low_stock_products,
                SUM(fd.out_of_stock_count) as out_of_stock_products
            FROM family_details fd
            GROUP BY fd.family_id, fd.family_name, fd.created_date, fd.last_modified_date
        ),
        time_boundaries AS (
            SELECT
                NOW() - INTERVAL '7 days' as one_week_ago,
                NOW() - INTERVAL '30 days' as one_month_ago
        ),
        overall_stats AS (
            SELECT
                COUNT(afs.family_id) as total_families,
                COALESCE(SUM(afs.total_products), 0) as total_products,
                COUNT(CASE WHEN afs.total_products > 0 THEN 1 END) as families_with_products,
                COUNT(CASE WHEN afs.low_stock_products > 0 THEN 1 END) as families_with_low_stock,
                COUNT(CASE WHEN afs.out_of_stock_products > 0 THEN 1 END) as families_with_out_of_stock,
                COALESCE(SUM(afs.total_value), 0) as total_inventory_value,
                COUNT(CASE WHEN afs.created_date >= tb.one_week_ago THEN 1 END) as families_created_this_week,
                COUNT(CASE WHEN afs.created_date >= tb.one_month_ago THEN 1 END) as families_created_this_month,
                MAX(afs.created_date) as last_family_created,
                MAX(afs.last_modified_date) as last_family_modified,
                MAX(afs.total_value) as highest_family_value,
                MIN(CASE WHEN afs.total_products > 0 THEN afs.total_value END) as lowest_family_value,
                MAX(afs.total_products) as largest_family_size,
                MIN(CASE WHEN afs.total_products > 0 THEN afs.total_products END) as smallest_family_size
            FROM aggregated_family_stats afs
            CROSS JOIN time_boundaries tb
        ),
        top_families_by_products AS (
            SELECT
                afs.family_id,
                afs.family_name,
                afs.total_products,
                afs.total_value,
                ROW_NUMBER() OVER (ORDER BY afs.total_products DESC) as product_rank
            FROM aggregated_family_stats afs
            WHERE afs.total_products > 0
        ),
        top_families_by_value AS (
            SELECT
                afs.family_id,
                afs.family_name,
                afs.total_products,
                afs.total_value,
                ROW_NUMBER() OVER (ORDER BY afs.total_value DESC) as value_rank
            FROM aggregated_family_stats afs
            WHERE afs.total_products > 0
        ),
        category_stats AS (
            SELECT
                fd.category,
                COUNT(DISTINCT fd.family_id) as family_count,
                SUM(fd.product_count) as product_count,
                SUM(fd.family_value) as total_value
            FROM family_details fd
            WHERE fd.category IS NOT NULL
            GROUP BY fd.category
        )
        SELECT
            'OVERVIEW' as data_type,
            os.total_families,
            os.total_products,
            os.families_with_products,
            os.families_with_low_stock,
            os.families_with_out_of_stock,
            os.total_inventory_value,
            os.families_created_this_week,
            os.families_created_this_month,
            os.last_family_created,
            os.last_family_modified,
            os.highest_family_value,
            os.lowest_family_value,
            os.largest_family_size,
            os.smallest_family_size,
            NULL as family_id,
            NULL as family_name,
            NULL as category
        FROM overall_stats os

        UNION ALL

        SELECT
            'TOP_BY_PRODUCTS' as data_type,
            NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
            tfp.family_id,
            tfp.family_name,
            NULL as category
        FROM top_families_by_products tfp
        WHERE tfp.product_rank <= 5

        UNION ALL

        SELECT
            'TOP_BY_VALUE' as data_type,
            NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
            tfv.family_id,
            tfv.family_name,
            NULL as category
        FROM top_families_by_value tfv
        WHERE tfv.value_rank <= 5

        UNION ALL

        SELECT
            'CATEGORY' as data_type,
            cs.family_count, cs.product_count, NULL, NULL, NULL, cs.total_value, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
            NULL as family_id,
            NULL as family_name,
            cs.category
        FROM category_stats cs

        ORDER BY data_type, family_name, category
        """,
        nativeQuery = true
    )
    List<Object[]> getAllStatisticsInOneQuery(@Param("clientAccountId") Long clientAccountId);

    @Modifying(clearAutomatically = true)
    @Query(
        """
        UPDATE Product p
        SET p.productFamily = :family
        WHERE p.id IN :productIds
        AND p.clientAccount.id = :clientAccountId
        """
    )
    Integer assignProductToFamily(
        @Param("clientAccountId") Long clientAccountId,
        @Param("family") ProductFamily family,
        @Param("productIds") List<Long> productIds
    );

    @Modifying(clearAutomatically = true)
    @Query(
        """
        UPDATE Product p
        SET p.productFamily = null
        WHERE p.id IN :productIds
        AND p.clientAccount.id = :clientAccountId
        """
    )
    Integer removeProductsFromFamily(
        @Param("clientAccountId") Long clientAccountId,
        @Param("family") ProductFamily family,
        @Param("productIds") List<Long> productIds
    );

    @Query(
        value = """
        SELECT
            COALESCE(COUNT(p.id), 0) as product_count,
            COALESCE(SUM(p.selling_price * i.available_quantity), 0) as total_value,
            COALESCE(SUM(i.available_quantity), 0) as total_quantity,
            COALESCE(COUNT(CASE WHEN i.available_quantity <= p.minimum_stock_level AND i.available_quantity > 0 THEN 1 END), 0) as low_stock_products,
            COALESCE(COUNT(CASE WHEN i.available_quantity = 0 THEN 1 END), 0) as out_of_stock_products
        FROM product_family pf
        LEFT JOIN product p ON p.product_family_id = pf.id AND p.client_account_id = :clientAccountId
        LEFT JOIN inventory i ON i.product_id = p.id
        WHERE pf.id = :familyId
        AND pf.client_account_id = :clientAccountId
        """,
        nativeQuery = true
    )
    Object[] getFamilyDetailStatsOptimized(@Param("familyId") Long familyId, @Param("clientAccountId") Long clientAccountId);
}
