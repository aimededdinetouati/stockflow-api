package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.repository.projection.InventoryFinancialStatsDTO;
import com.adeem.stockflow.repository.projection.InventoryStockLevelStatsDTO;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Inventory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {
    Optional<Inventory> findByProductId(Long id);

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
}
