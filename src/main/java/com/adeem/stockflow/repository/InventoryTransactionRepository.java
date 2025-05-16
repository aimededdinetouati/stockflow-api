package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.InventoryTransaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InventoryTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {}
