package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.CartItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CartItem entity.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long>, JpaSpecificationExecutor<CartItem> {
    @Query("SELECT ci FROM CartItem ci WHERE ci.id = :itemId AND ci.cart.customer.id = :customerId")
    Optional<CartItem> findByIdAndCustomerId(@Param("itemId") Long itemId, @Param("customerId") Long customerId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
    Optional<CartItem> findByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);

    List<CartItem> findByCartIdOrderByAddedDateDesc(Long cartId);

    List<CartItem> findByProductId(Long productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteAllByCartId(@Param("cartId") Long cartId);
}
