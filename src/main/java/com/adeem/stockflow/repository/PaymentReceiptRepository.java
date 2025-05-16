package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.PaymentReceipt;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PaymentReceipt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, Long> {}
