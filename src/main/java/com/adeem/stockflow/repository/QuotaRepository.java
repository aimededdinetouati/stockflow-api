package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Quota;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Quota entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuotaRepository extends JpaRepository<Quota, Long> {
    Optional<Quota> findByClientAccountId(Long clientAccountId);
}
