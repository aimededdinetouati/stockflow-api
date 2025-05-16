package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.ReturnOrder;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReturnOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Long> {}
