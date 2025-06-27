package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.GuestCart;
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
 * Spring Data JPA repository for the GuestCart entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GuestCartRepository extends JpaRepository<GuestCart, String> {
    /**
     * Find all expired guest carts for cleanup
     */
    List<GuestCart> findByExpiresAtBefore(Instant expirationTime);

    /**
     * Find valid (non-expired) guest cart by session ID
     */
    @Query("SELECT gc FROM GuestCart gc WHERE gc.sessionId = :sessionId AND gc.expiresAt > :currentTime")
    Optional<GuestCart> findValidGuestCart(@Param("sessionId") String sessionId, @Param("currentTime") Instant currentTime);

    /**
     * Delete expired guest carts
     */
    @Modifying
    @Query("DELETE FROM GuestCart gc WHERE gc.expiresAt < :expirationTime")
    int deleteExpiredGuestCarts(@Param("expirationTime") Instant expirationTime);
}
