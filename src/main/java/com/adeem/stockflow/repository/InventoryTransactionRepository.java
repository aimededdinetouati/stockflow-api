package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.InventoryTransaction;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InventoryTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InventoryTransactionRepository
    extends JpaRepository<InventoryTransaction, Long>, JpaSpecificationExecutor<InventoryTransaction> {
    @Query(
        value = "SELECT reference_number FROM inventory_transaction it WHERE it.client_account_id = ?1 ORDER BY it.id DESC LIMIT 1",
        nativeQuery = true
    )
    Optional<String> getLastReference(Long clientAccountId);

    List<InventoryTransaction> findByProductId(Long id);
}
