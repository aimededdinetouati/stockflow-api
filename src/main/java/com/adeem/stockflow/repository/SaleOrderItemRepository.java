package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.SaleOrderItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SaleOrderItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SaleOrderItemRepository extends JpaRepository<SaleOrderItem, Long> {
    void deleteBySaleOrderId(Long id);
}
