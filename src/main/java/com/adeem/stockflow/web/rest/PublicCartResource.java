// File: src/main/java/com/adeem/stockflow/web/rest/PublicCartResource.java
package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.service.GuestCartService;
import com.adeem.stockflow.service.dto.GuestCartDTO;
import com.adeem.stockflow.service.dto.GuestCartItemDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing guest cart functionality in the public marketplace.
 * Handles session-based cart operations for anonymous users.
 */
@RestController
@RequestMapping("/api/public/cart")
public class PublicCartResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicCartResource.class);

    private static final String ENTITY_NAME = "guestCart";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GuestCartService guestCartService;

    public PublicCartResource(GuestCartService guestCartService) {
        this.guestCartService = guestCartService;
    }

    /**
     * {@code POST  /api/public/cart/guest} : Create a new guest cart session.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body containing the session ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/guest")
    public ResponseEntity<GuestCartSessionResponse> createGuestCart() throws URISyntaxException {
        LOG.debug("REST request to create new guest cart");

        String sessionId = guestCartService.createGuestCart();

        GuestCartSessionResponse response = new GuestCartSessionResponse();
        response.setSessionId(sessionId);

        return ResponseEntity.created(new URI("/api/public/cart/guest/" + sessionId))
            .headers(HeaderUtil.createAlert(applicationName, "Guest cart created successfully", sessionId))
            .body(response);
    }

    /**
     * {@code GET  /api/public/cart/guest/:sessionId} : Get guest cart by session ID.
     *
     * @param sessionId the session ID of the guest cart to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the guestCartDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/guest/{sessionId}")
    public ResponseEntity<GuestCartDTO> getGuestCart(@PathVariable String sessionId) {
        LOG.debug("REST request to get guest cart : {}", sessionId);

        GuestCartDTO guestCartDTO = guestCartService.findGuestCart(sessionId);
        return ResponseEntity.ok(guestCartDTO);
    }

    /**
     * {@code POST  /api/public/cart/guest/:sessionId/items} : Add item to guest cart.
     *
     * @param sessionId the session ID of the guest cart.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new guestCartItemDTO.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/guest/{sessionId}/items")
    public ResponseEntity<GuestCartItemDTO> addToGuestCart(
        @PathVariable String sessionId,
        @RequestParam Long productId,
        @RequestParam @Min(1) BigDecimal quantity
    ) throws URISyntaxException {
        LOG.debug("REST request to add item to guest cart : {} - Product: {}, Quantity: {}", sessionId, productId, quantity);

        GuestCartItemDTO result = guestCartService.addItemToGuestCart(sessionId, productId, quantity);

        return ResponseEntity.created(new URI("/api/public/cart/guest/" + sessionId + "/items/" + result.getId()))
            .headers(HeaderUtil.createAlert(applicationName, "Item added to cart successfully", result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /api/public/cart/guest/:sessionId/items/:itemId} : Update guest cart item quantity.
     *
     * @param sessionId the session ID of the guest cart.
     * @param itemId the ID of the cart item to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated guestCartItemDTO,
     * or {@code 204 (No Content)} if the item was removed (quantity = 0).
     */
    @PutMapping("/guest/{sessionId}/items/{itemId}")
    public ResponseEntity<GuestCartItemDTO> updateGuestCartItem(
        @PathVariable String sessionId,
        @PathVariable Long itemId,
        @RequestParam BigDecimal quantity
    ) {
        LOG.debug("REST request to update guest cart item : {} - Item: {}, Quantity: {}", sessionId, itemId, quantity);

        GuestCartItemDTO result = guestCartService.updateGuestCartItem(sessionId, itemId, quantity);

        if (result == null) {
            // Item was removed (quantity = 0)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createAlert(applicationName, "Item removed from cart", itemId.toString()))
                .build();
        } else {
            return ResponseEntity.ok()
                .headers(HeaderUtil.createAlert(applicationName, "Item quantity updated", itemId.toString()))
                .body(result);
        }
    }

    /**
     * {@code DELETE  /api/public/cart/guest/:sessionId/items/:itemId} : Remove item from guest cart.
     *
     * @param sessionId the session ID of the guest cart.
     * @param itemId the ID of the cart item to remove.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping("/guest/{sessionId}/items/{itemId}")
    public ResponseEntity<Void> removeItemFromGuestCart(@PathVariable String sessionId, @PathVariable Long itemId) {
        LOG.debug("REST request to remove item from guest cart : {} - Item: {}", sessionId, itemId);

        guestCartService.removeItemFromGuestCart(sessionId, itemId);

        return ResponseEntity.noContent()
            .headers(HeaderUtil.createAlert(applicationName, "Item removed from cart", itemId.toString()))
            .build();
    }

    /**
     * {@code DELETE  /api/public/cart/guest/:sessionId} : Clear all items from guest cart.
     *
     * @param sessionId the session ID of the guest cart.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping("/guest/{sessionId}")
    public ResponseEntity<Void> clearGuestCart(@PathVariable String sessionId) {
        LOG.debug("REST request to clear guest cart : {}", sessionId);

        guestCartService.clearGuestCart(sessionId);

        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "Cart cleared successfully", sessionId)).build();
    }

    /**
     * {@code PUT  /api/public/cart/guest/:sessionId/extend} : Extend guest cart expiration.
     *
     * @param sessionId the session ID of the guest cart.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated guestCartDTO.
     */
    @PutMapping("/guest/{sessionId}/extend")
    public ResponseEntity<GuestCartDTO> extendGuestCartExpiration(@PathVariable String sessionId) {
        LOG.debug("REST request to extend guest cart expiration : {}", sessionId);

        Optional<GuestCartDTO> result = guestCartService.extendGuestCartExpiration(sessionId);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createAlert(applicationName, "Cart expiration extended", sessionId));
    }

    /**
     * {@code GET  /api/public/cart/guest/:sessionId/count} : Get count of items in guest cart.
     *
     * @param sessionId the session ID of the guest cart.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the item count.
     */
    @GetMapping("/guest/{sessionId}/count")
    public ResponseEntity<GuestCartCountResponse> getGuestCartItemCount(@PathVariable String sessionId) {
        LOG.debug("REST request to get guest cart item count : {}", sessionId);

        Long count = guestCartService.countGuestCartItems(sessionId);

        GuestCartCountResponse response = new GuestCartCountResponse();
        response.setCount(count);

        return ResponseEntity.ok(response);
    }

    /**
     * Response DTO for guest cart session creation.
     */
    public static class GuestCartSessionResponse {

        private String sessionId;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    /**
     * Response DTO for guest cart item count.
     */
    public static class GuestCartCountResponse {

        private Long count;

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}
