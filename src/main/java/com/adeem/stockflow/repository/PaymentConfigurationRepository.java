package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.PaymentConfiguration;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PaymentConfiguration entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentConfigurationRepository extends JpaRepository<PaymentConfiguration, Long> {}
