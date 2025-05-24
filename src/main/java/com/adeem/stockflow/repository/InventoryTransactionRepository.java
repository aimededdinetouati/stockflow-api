package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.InventoryTransaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InventoryTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    @Query(value = "SELECT reference_number FROM inventory_transaction ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<String> getLastReference();
}
