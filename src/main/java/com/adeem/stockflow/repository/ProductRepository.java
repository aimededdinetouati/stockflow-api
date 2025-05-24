package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Product;
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
}
