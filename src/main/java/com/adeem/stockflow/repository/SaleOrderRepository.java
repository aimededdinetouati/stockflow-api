package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.SaleOrder;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SaleOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder, Long> {}
