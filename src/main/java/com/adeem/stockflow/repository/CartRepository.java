package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Cart;
import com.adeem.stockflow.domain.enumeration.CartStatus;
import com.adeem.stockflow.repository.projection.CartStatsProjection;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>, JpaSpecificationExecutor<Cart> {
    @Query("SELECT c FROM Cart c WHERE c.customer.id = :customerId")
    Optional<Cart> findByCustomerId(@Param("customerId") Long customerId);

    List<Cart> findByCustomerIdOrderByCreatedDateDesc(Long customerId);
}
