package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.repository.projection.CategoryStatsProjection;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByCodeAndClientAccountId(String code, Long clientAccountId);

    @Query(
        "SELECT p, i FROM Product p " +
        "LEFT JOIN p.inventories i " +
        "WHERE p.clientAccount.id = :clientAccountId " +
        "AND i.quantity <= p.minimumStockLevel"
    )
    Page<Object[]> findProductsWithLowStock(@Param("clientAccountId") Long clientAccountId, Pageable pageable);

    @Query(
        value = """
        WITH category_families AS (
        SELECT
            p.category,
            pf.id as family_id,
            COUNT(p.id) as products_in_family,
            COALESCE(SUM(p.selling_price * i.available_quantity), 0) as family_value_in_category
        FROM product p
        LEFT JOIN product_family pf ON pf.id = p.product_family_id
        LEFT JOIN inventory i ON i.product_id = p.id
        WHERE p.client_account_id = :clientAccountId
        GROUP BY p.category, pf.id
        )
        SELECT
        cf.category as category,
        COUNT(DISTINCT cf.family_id) as familyCount,
        COALESCE(SUM(cf.products_in_family), 0) as productCount,
        COALESCE(SUM(cf.family_value_in_category), 0) as totalValue
        FROM category_families cf
        WHERE cf.category IS NOT NULL
        GROUP BY cf.category
        ORDER BY totalValue DESC
        """,
        nativeQuery = true
    )
    List<CategoryStatsProjection> getCategoryStatsOptimized(@Param("clientAccountId") Long clientAccountId);

    boolean existsByProductFamilyId(Long familyId);

    @Query("SELECT p.id FROM Product p WHERE p.id IN :productIds AND p.clientAccount.id = :clientAccountId")
    List<Long> findValidProductIdsForClientAccount(
        @Param("productIds") List<Long> productIds,
        @Param("clientAccountId") Long clientAccountId
    );

    /**
     * Find products by IDs that belong to the specified client account.
     *
     * @param productIds the list of product IDs
     * @param clientAccountId the client account ID for security validation
     * @return list of products that belong to the client account
     */
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds AND p.clientAccount.id = :clientAccountId")
    List<Product> findByIdsAndClientAccount(@Param("productIds") List<Long> productIds, @Param("clientAccountId") Long clientAccountId);

    @Modifying
    @Query("DELETE FROM Product p WHERE p.id IN :productIds AND p.clientAccount.id = :clientAccountId")
    int deleteByIdsAndClientAccount(@Param("productIds") List<Long> productIds, @Param("clientAccountId") Long clientAccountId);

    @Modifying
    @Query(
        """
        UPDATE Product p
        SET p.isVisibleToCustomers = CASE
            WHEN p.isVisibleToCustomers = true THEN false
            ELSE true
        END
        WHERE p.id IN :productIds AND p.clientAccount.id = :clientAccountId
        """
    )
    int toggleVisibilityByIdsAndClientAccount(@Param("productIds") List<Long> productIds, @Param("clientAccountId") Long clientAccountId);
}
