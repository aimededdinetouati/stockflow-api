package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.repository.projection.CategoryStatsProjection;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    /**
     * Find one product with a specific code.
     *
     * @param code the product code.
     * @return the product.
     */
    Optional<Product> findByCode(String code);

    /**
     * Find products by client account ID.
     *
     * @param clientAccountId the client account ID.
     * @return the products.
     */
    List<Product> findByClientAccountId(Long clientAccountId);

    /**
     * Find product by ID and client account ID.
     *
     * @param id the product ID.
     * @param clientAccountId the client account ID.
     * @return the product.
     */
    Optional<Product> findByIdAndClientAccountId(Long id, Long clientAccountId);

    Optional<Product> findByCodeAndClientAccountId(String code, Long clientAccountId);

    /**
     * Find products with inventory below minimum stock level for a client account.
     *
     * @param clientAccountId the client account ID.
     * @param pageable the pagination information.
     * @return the products with low stock.
     */
    @Query(
        "SELECT p, i FROM Product p " +
        "LEFT JOIN p.inventories i " +
        "WHERE p.clientAccount.id = :clientAccountId " +
        "AND i.quantity <= p.minimumStockLevel"
    )
    Page<Object[]> findProductsWithLowStock(@Param("clientAccountId") Long clientAccountId, Pageable pageable);

    /**
     * Find products with zero inventory for a client account.
     *
     * @param clientAccountId the client account ID.
     * @param pageable the pagination information.
     * @return the products with zero stock.
     */
    @Query(
        "SELECT p, i FROM Product p " + "LEFT JOIN p.inventories i " + "WHERE p.clientAccount.id = :clientAccountId " + "AND i.quantity = 0"
    )
    Page<Object[]> findProductsWithZeroStock(@Param("clientAccountId") Long clientAccountId, Pageable pageable);

    // Additional methods to add to the existing ProductRepository interface

    /**
     * Additional repository methods for Product Family integration.
     * These should be added to the existing ProductRepository interface.
     */

    // Basic family relationship queries

    boolean existsByProductFamilyId(Long productFamilyId);

    Long countByProductFamilyIdAndClientAccountId(Long productFamilyId, Long clientAccountId);

    Page<Product> findByProductFamilyIdAndClientAccountId(Long productFamilyId, Long clientAccountId, Pageable pageable);

    List<Product> findByProductFamilyIdAndClientAccountId(Long productFamilyId, Long clientAccountId);

    // Statistics queries for product family analytics

    @Query(
        """
        SELECT COALESCE(SUM(p.sellingPrice * i.availableQuantity), 0)
        FROM Product p
        INNER JOIN Inventory i ON i.product.id = p.id
        WHERE p.clientAccount.id = :clientAccountId
        """
    )
    BigDecimal calculateTotalInventoryValueByClientAccount(@Param("clientAccountId") Long clientAccountId);

    @Query(
        """
        SELECT COALESCE(SUM(p.sellingPrice * i.availableQuantity), 0)
        FROM Product p
        INNER JOIN Inventory i ON i.product.id = p.id
        WHERE p.productFamily.id = :productFamilyId
        """
    )
    BigDecimal calculateTotalValueByProductFamily(@Param("productFamilyId") Long productFamilyId);

    @Query(
        """
        SELECT COALESCE(SUM(i.availableQuantity), 0)
        FROM Product p
        INNER JOIN Inventory i ON i.product.id = p.id
        WHERE p.productFamily.id = :productFamilyId
        """
    )
    BigDecimal calculateTotalQuantityByProductFamily(@Param("productFamilyId") Long productFamilyId);

    // Stock status queries by family

    @Query(
        """
        SELECT COUNT(p.id)
        FROM Product p
        INNER JOIN Inventory i ON i.product.id = p.id
        WHERE p.productFamily.id = :productFamilyId
        AND i.availableQuantity <= p.minimumStockLevel
        AND i.availableQuantity > 0
        """
    )
    Long countLowStockProductsByFamily(@Param("productFamilyId") Long productFamilyId);

    @Query(
        """
        SELECT COUNT(p.id)
        FROM Product p
        INNER JOIN Inventory i ON i.product.id = p.id
        WHERE p.productFamily.id = :productFamilyId
        AND i.availableQuantity = 0
        """
    )
    Long countOutOfStockProductsByFamily(@Param("productFamilyId") Long productFamilyId);

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
}
