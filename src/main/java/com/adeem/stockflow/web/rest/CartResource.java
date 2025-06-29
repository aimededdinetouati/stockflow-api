// File: src/main/java/com/adeem/stockflow/web/rest/CartResource.java
package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.security.AuthoritiesConstants;
import com.adeem.stockflow.service.CartService;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing marketplace cart functionality.
 * Provides comprehensive cart management for authenticated customers.
 */
@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.USER_CUSTOMER + "')")
public class CartResource {

    private static final Logger LOG = LoggerFactory.getLogger(CartResource.class);

    private static final String ENTITY_NAME = "cart";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartService cartService;

    public CartResource(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * {@code GET  /api/cart} : Get current user's cart with totals and details.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cart with totals.
     */
    @GetMapping
    public ResponseEntity<CartWithTotalsDTO> getCurrentCart() {
        LOG.debug("REST request to get current user's cart");

        CartWithTotalsDTO cart = cartService.getCurrentUserCart();
        return ResponseEntity.ok().body(cart);
    }

    /**
     * {@code GET  /api/cart/summary} : Get cart summary for current user.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cart summary.
     */
    @GetMapping("/summary")
    public ResponseEntity<CartSummaryDTO> getCartSummary() {
        LOG.debug("REST request to get cart summary");

        CartSummaryDTO summary = cartService.getCartSummary();
        return ResponseEntity.ok().body(summary);
    }

    /**
     * {@code POST  /api/cart/items} : Add item to cart.
     *
     * @param request the add to cart request containing product ID and quantity.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the added cart item detail,
     *         or with status {@code 400 (Bad Request)} if the request is not valid.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/items")
    public ResponseEntity<CartItemDetailDTO> addItemToCart(@Valid @RequestBody AddToCartRequestDTO request) throws URISyntaxException {
        LOG.debug("REST request to add item to cart: {}", request);

        CartItemDetailDTO result = cartService.addItemToCart(request);

        return ResponseEntity.created(new URI("/api/cart/items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /api/cart/items/{itemId}/quantity} : Update cart item quantity.
     *
     * @param itemId the cart item ID.
     * @param quantity the new quantity.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cart item,
     *         or with status {@code 400 (Bad Request)} if the quantity is not valid,
     *         or with status {@code 404 (Not Found)} if the cart item is not found.
     */
    @PutMapping("/items/{itemId}/quantity")
    public ResponseEntity<CartItemDetailDTO> updateItemQuantity(
        @PathVariable Long itemId,
        @RequestBody @NotNull @DecimalMin(value = "0.01") BigDecimal quantity
    ) {
        LOG.debug("REST request to update cart item quantity: itemId={}, quantity={}", itemId, quantity);

        CartItemDetailDTO result = cartService.updateCartItemQuantity(itemId, quantity);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, itemId.toString()))
            .body(result);
    }

    /**
     * {@code DELETE  /api/cart/items/{itemId}} : Remove item from cart.
     *
     * @param itemId the cart item ID to remove.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long itemId) {
        LOG.debug("REST request to remove cart item: {}", itemId);

        cartService.removeCartItem(itemId);

        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, itemId.toString()))
            .build();
    }

    /**
     * {@code DELETE  /api/cart} : Clear entire cart.
     *
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        LOG.debug("REST request to clear cart");

        cartService.clearCart();

        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "Cart cleared successfully", "")).build();
    }

    /**
     * {@code POST  /api/cart/validate} : Validate cart before checkout.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the validation response.
     */
    @PostMapping("/validate")
    public ResponseEntity<CartValidationResponseDTO> validateCart() {
        LOG.debug("REST request to validate cart");

        CartValidationResponseDTO validation = cartService.validateCart();
        return ResponseEntity.ok().body(validation);
    }

    /**
     * {@code POST  /api/cart/migrate/{sessionId}} : Migrate guest cart to authenticated user cart.
     *
     * @param sessionId the guest cart session ID.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the migrated cart,
     *         or with status {@code 404 (Not Found)} if the guest cart is not found.
     */
    @PostMapping("/migrate/{sessionId}")
    public ResponseEntity<CartWithTotalsDTO> migrateGuestCart(@PathVariable String sessionId) {
        LOG.debug("REST request to migrate guest cart: {}", sessionId);

        CartWithTotalsDTO result = cartService.migrateGuestCart(sessionId);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "Guest cart migrated successfully", sessionId))
            .body(result);
    }
}
