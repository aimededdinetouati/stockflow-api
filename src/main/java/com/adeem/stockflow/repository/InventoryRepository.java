package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.repository.projection.InventoryFinancialStatsDTO;
import com.adeem.stockflow.repository.projection.InventoryStockLevelStatsDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Inventory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {
    Set<Inventory> findByProductId(Long id);

    // Financial aggregations
    @Query(
        """
        SELECT new com.adeem.stockflow.repository.projection.InventoryFinancialStatsDTO(
            SUM(i.quantity),
            SUM(i.availableQuantity),
            SUM(i.quantity - i.availableQuantity),
            SUM(CASE WHEN p.costPrice IS NOT NULL THEN i.availableQuantity * p.costPrice ELSE 0 END)
        )
        FROM Inventory i
        JOIN i.product p
        WHERE i.clientAccount.id = :clientAccountId
        """
    )
    InventoryFinancialStatsDTO getFinancialStats(@Param("clientAccountId") Long clientAccountId);

    // Stock level counts
    @Query(
        """
        SELECT new com.adeem.stockflow.repository.projection.InventoryStockLevelStatsDTO(
            COUNT(*),
            SUM(CASE WHEN i.availableQuantity = 0 THEN 1 ELSE 0 END),
            SUM(CASE WHEN i.availableQuantity > 0 AND i.availableQuantity <= p.minimumStockLevel THEN 1 ELSE 0 END),
            SUM(CASE WHEN i.availableQuantity > p.minimumStockLevel AND i.availableQuantity <= p.minimumStockLevel * 3 THEN 1 ELSE 0 END),
            SUM(CASE WHEN i.availableQuantity > p.minimumStockLevel * 3 THEN 1 ELSE 0 END)
        )
        FROM Inventory i
        JOIN i.product p
        WHERE i.clientAccount.id = :clientAccountId
        AND p.minimumStockLevel IS NOT NULL
        """
    )
    InventoryStockLevelStatsDTO getStockLevelStats(@Param("clientAccountId") Long clientAccountId);

    Optional<Inventory> findByProductIdAndClientAccountId(Long productId, Long currentClientAccountId);

    @Query("SELECT i FROM Inventory i WHERE i.product.id IN :productIds")
    List<Inventory> findByProductIdIn(@Param("productIds") List<Long> productIds);

    @Modifying
    @Query("DELETE FROM Inventory i WHERE i.product.id IN :productIds")
    int deleteByProductIdIn(@Param("productIds") List<Long> productIds);

    @Query("SELECT COALESCE(SUM(i.availableQuantity), 0) FROM Inventory i WHERE i.product.id = :productId")
    Optional<BigDecimal> getTotalAvailableQuantityForProduct(@Param("productId") Long productId);

    @Query(
        "SELECT CASE WHEN COALESCE(SUM(i.availableQuantity), 0) >= :requiredQuantity THEN true ELSE false END " +
        "FROM Inventory i WHERE i.product.id = :productId"
    )
    boolean hasInsufficientStock(@Param("productId") Long productId, @Param("requiredQuantity") BigDecimal requiredQuantity);

    @Query(
        "SELECT DISTINCT p FROM Product p " +
        "JOIN p.inventories i " +
        "JOIN CartItem ci ON ci.product = p " +
        "WHERE i.availableQuantity <= p.minimumStockLevel " +
        "AND p.clientAccount.id = :clientAccountId"
    )
    List<Product> findLowStockProductsInCarts(@Param("clientAccountId") Long clientAccountId);
}
