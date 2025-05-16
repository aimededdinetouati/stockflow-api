package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.ReturnOrderItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReturnOrderItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReturnOrderItemRepository extends JpaRepository<ReturnOrderItem, Long> {}
