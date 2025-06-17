package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Shipment;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Shipment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long>, JpaSpecificationExecutor<Shipment> {
    @Query(value = "SELECT reference FROM shipment s WHERE s.client_account_id = ?1 ORDER BY s.id DESC LIMIT 1", nativeQuery = true)
    Optional<String> getLastReference(Long clientAccountId);
}
