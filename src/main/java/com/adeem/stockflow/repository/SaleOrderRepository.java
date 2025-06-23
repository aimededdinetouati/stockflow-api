package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.SaleOrder;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SaleOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder, Long>, JpaSpecificationExecutor<SaleOrder> {
    List<SaleOrder> findByClientAccountId(Long currentClientAccountId);

    @Query(value = "SELECT reference FROM sale_order so WHERE so.client_account_id = ?1 ORDER BY so.id DESC LIMIT 1", nativeQuery = true)
    Optional<String> getLastReference(Long clientAccountId);

    @Query("SELECT s FROM SaleOrder s WHERE s.reservationExpiresAt <= ?1")
    List<SaleOrder> findExpiredReservations(ZonedDateTime time);
}
