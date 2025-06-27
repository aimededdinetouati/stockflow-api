package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.GuestCartItem;
import com.adeem.stockflow.service.dto.GuestCartItemDTO;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GuestCartItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GuestCartItemRepository extends JpaRepository<GuestCartItem, Long> {
    /**
     * Find all items for a specific guest cart session
     */
    List<GuestCartItem> findBySessionIdOrderByAddedDateDesc(String sessionId);

    /**
     * Find specific item by session and product
     */
    @Query("SELECT gci FROM GuestCartItem gci WHERE gci.sessionId = :sessionId AND gci.product.id = :productId")
    Optional<GuestCartItem> findBySessionIdAndProductId(@Param("sessionId") String sessionId, @Param("productId") Long productId);

    /**
     * Delete all items for expired carts
     */
    @Modifying
    @Query(
        value = "DELETE FROM guest_cart_item gci USING guest_cart gc " +
        "WHERE gci.session_id = gc.session_id AND gc.expires_at < :expirationTime",
        nativeQuery = true
    )
    int deleteItemsForExpiredCarts(@Param("expirationTime") Instant expirationTime);

    /**
     * Count items in a guest cart
     */
    @Query("SELECT COUNT(gci) FROM GuestCartItem gci WHERE gci.sessionId = :sessionId")
    Long countItemsBySessionId(@Param("sessionId") String sessionId);

    List<GuestCartItem> findBySessionId(String sessionId);
}
