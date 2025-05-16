package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Subscription;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Subscription entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {}
