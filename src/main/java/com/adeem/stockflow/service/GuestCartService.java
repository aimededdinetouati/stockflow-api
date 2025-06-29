// File: src/main/java/com/adeem/stockflow/service/GuestCartService.java
package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.GuestCart;
import com.adeem.stockflow.domain.GuestCartItem;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.repository.GuestCartItemRepository;
import com.adeem.stockflow.repository.GuestCartRepository;
import com.adeem.stockflow.repository.ProductRepository;
import com.adeem.stockflow.service.dto.GuestCartDTO;
import com.adeem.stockflow.service.dto.GuestCartItemDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import com.adeem.stockflow.service.mapper.GuestCartItemMapper;
import com.adeem.stockflow.service.mapper.GuestCartMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.MappingTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Guest Cart functionality.
 * Handles session-based cart management for anonymous users.
 */
@Service
@Transactional
public class GuestCartService {

    private static final Logger LOG = LoggerFactory.getLogger(GuestCartService.class);
    private static final int GUEST_CART_EXPIRY_HOURS = 24;

    private final GuestCartRepository guestCartRepository;
    private final GuestCartItemRepository guestCartItemRepository;
    private final ProductRepository productRepository;
    private final GuestCartMapper guestCartMapper;
    private final GuestCartItemMapper guestCartItemMapper;

    public GuestCartService(
        GuestCartRepository guestCartRepository,
        GuestCartItemRepository guestCartItemRepository,
        ProductRepository productRepository,
        GuestCartMapper guestCartMapper,
        GuestCartItemMapper guestCartItemMapper
    ) {
        this.guestCartRepository = guestCartRepository;
        this.guestCartItemRepository = guestCartItemRepository;
        this.productRepository = productRepository;
        this.guestCartMapper = guestCartMapper;
        this.guestCartItemMapper = guestCartItemMapper;
    }

    /**
     * Create a new guest cart session.
     *
     * @return the session ID for the new guest cart
     */
    public String createGuestCart() {
        LOG.debug("Request to create new guest cart");

        String sessionId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        Instant expiresAt = now.plus(GUEST_CART_EXPIRY_HOURS, ChronoUnit.HOURS);

        GuestCart guestCart = new GuestCart();
        guestCart.setSessionId(sessionId);
        guestCart.setCreatedDate(now);
        guestCart.setExpiresAt(expiresAt);

        guestCartRepository.save(guestCart);

        LOG.debug("Created guest cart with session ID: {}", sessionId);
        return sessionId;
    }

    /**
     * Get guest cart by session ID.
     *
     * @param sessionId the session ID
     * @return the guest cart DTO if found and not expired
     */
    @Transactional(readOnly = true)
    public GuestCartDTO findGuestCart(String sessionId) {
        LOG.debug("Request to get guest cart: {}", sessionId);
        GuestCart guestCart = guestCartRepository
            .findValidGuestCart(sessionId, Instant.now())
            .orElseThrow(() -> new BadRequestAlertException("Guest cart not found", "", ErrorConstants.GUEST_CART_NOT_FOUND));
        GuestCartDTO guestCartDTO = guestCartMapper.toDto(guestCart);
        List<GuestCartItemDTO> guestCartItems = guestCartItemRepository
            .findBySessionId(sessionId)
            .stream()
            .map(guestCartItemMapper::toDto)
            .toList();
        guestCartDTO.setItems(guestCartItems);
        calculateTotals(guestCartDTO);

        return guestCartDTO;
    }

    public GuestCartDTO calculateTotals(GuestCartDTO dto) {
        if (dto.getItems() != null) {
            dto.setTotalItems(dto.getItems().size());
            dto.setTotalAmount(
                dto
                    .getItems()
                    .stream()
                    .map(item -> item.getQuantity().multiply(item.getPriceAtTime()))
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
            );
        } else {
            dto.setTotalItems(0);
            dto.setTotalAmount(java.math.BigDecimal.ZERO);
        }

        // Check if cart is expired
        dto.setIsExpired(dto.getExpiresAt() != null && dto.getExpiresAt().isBefore(Instant.now()));
        return dto;
    }

    /**
     * Add item to guest cart.
     *
     * @param sessionId the session ID
     * @return the added cart item DTO
     */
    public GuestCartItemDTO addItemToGuestCart(String sessionId, Long productId, BigDecimal quantity) {
        LOG.debug("Request to add item to guest cart: {} - Product: {}, Quantity: {}", sessionId, productId, quantity);

        // Validate session
        getValidGuestCart(sessionId);

        // Validate product
        Product product = getValidMarketplaceProduct(productId);

        // Check if item already exists in cart
        Optional<GuestCartItem> existingItem = guestCartItemRepository.findBySessionIdAndProductId(sessionId, productId);

        GuestCartItem cartItem;
        if (existingItem.isPresent()) {
            // Update existing item
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity().add(quantity));
        } else {
            // Create new item
            cartItem = new GuestCartItem();
            cartItem.setSessionId(sessionId);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPriceAtTime(product.getSellingPrice());
            cartItem.setAddedDate(Instant.now());
        }

        cartItem = guestCartItemRepository.save(cartItem);

        LOG.debug("Added/updated item in guest cart: {}", cartItem.getId());
        return guestCartItemMapper.toDto(cartItem);
    }

    /**
     * Update guest cart item quantity.
     *
     * @param sessionId the session ID
     * @param itemId the cart item ID
     * @param quantity the quantity
     * @return the updated cart item DTO
     */
    public GuestCartItemDTO updateGuestCartItem(String sessionId, Long itemId, BigDecimal quantity) {
        LOG.debug("Request to update guest cart item: {} - Item: {}, Quantity: {}", sessionId, itemId, quantity);

        // Validate session
        getValidGuestCart(sessionId);

        // Find and validate cart item
        GuestCartItem cartItem = guestCartItemRepository
            .findById(itemId)
            .filter(item -> item.getSessionId().equals(sessionId))
            .orElseThrow(() -> new BadRequestAlertException("Cart item not found", "GuestCartItem", ErrorConstants.ID_NOT_FOUND));

        // Update quantity or remove if zero
        if (quantity.compareTo(BigDecimal.ZERO) == 0) {
            guestCartItemRepository.delete(cartItem);
            LOG.debug("Removed item from guest cart: {}", itemId);
            return null;
        } else {
            cartItem.setQuantity(quantity);
            cartItem = guestCartItemRepository.save(cartItem);
            LOG.debug("Updated guest cart item: {}", itemId);
            return guestCartItemMapper.toDto(cartItem);
        }
    }

    /**
     * Remove item from guest cart.
     *
     * @param sessionId the session ID
     * @param itemId the cart item ID
     */
    public void removeItemFromGuestCart(String sessionId, Long itemId) {
        LOG.debug("Request to remove item from guest cart: {} - Item: {}", sessionId, itemId);

        // Validate session
        getValidGuestCart(sessionId);

        // Find and validate cart item
        GuestCartItem cartItem = guestCartItemRepository
            .findById(itemId)
            .filter(item -> item.getSessionId().equals(sessionId))
            .orElseThrow(() -> new BadRequestAlertException("Cart item not found", "GuestCartItem", ErrorConstants.ID_NOT_FOUND));

        guestCartItemRepository.delete(cartItem);
        LOG.debug("Removed item from guest cart: {}", itemId);
    }

    /**
     * Clear all items from guest cart.
     *
     * @param sessionId the session ID
     */
    public void clearGuestCart(String sessionId) {
        LOG.debug("Request to clear guest cart: {}", sessionId);

        // Validate session
        getValidGuestCart(sessionId);

        // Remove all items
        List<GuestCartItem> items = guestCartItemRepository.findBySessionIdOrderByAddedDateDesc(sessionId);
        guestCartItemRepository.deleteAll(items);

        LOG.debug("Cleared guest cart: {}", sessionId);
    }

    /**
     * Extend guest cart expiration.
     *
     * @param sessionId the session ID
     * @return the updated guest cart DTO
     */
    public Optional<GuestCartDTO> extendGuestCartExpiration(String sessionId) {
        LOG.debug("Request to extend guest cart expiration: {}", sessionId);

        return guestCartRepository
            .findValidGuestCart(sessionId, Instant.now())
            .map(guestCart -> {
                guestCart.setExpiresAt(Instant.now().plus(GUEST_CART_EXPIRY_HOURS, ChronoUnit.HOURS));
                return guestCartRepository.save(guestCart);
            })
            .map(guestCartMapper::toDto);
    }

    /**
     * Count items in guest cart.
     *
     * @param sessionId the session ID
     * @return the number of items in the cart
     */
    @Transactional(readOnly = true)
    public Long countGuestCartItems(String sessionId) {
        LOG.debug("Request to count guest cart items: {}", sessionId);

        return guestCartItemRepository.countItemsBySessionId(sessionId);
    }

    /**
     * Scheduled task to cleanup expired guest carts.
     * Runs every hour to remove expired carts and their items.
     */
    @Scheduled(fixedRate = 60 * 60 * 1000) // Every hour
    public void cleanupExpiredGuestCarts() {
        LOG.debug("Running scheduled cleanup of expired guest carts");

        try {
            Instant now = Instant.now();

            // Delete items first (due to foreign key constraints)
            int deletedItems = guestCartItemRepository.deleteItemsForExpiredCarts(now);

            // Then delete expired carts
            int deletedCarts = guestCartRepository.deleteExpiredGuestCarts(now);

            LOG.info("Cleaned up {} expired guest carts and {} items", deletedCarts, deletedItems);
        } catch (Exception e) {
            LOG.error("Error during guest cart cleanup: {}", e.getMessage(), e);
        }
    }

    // Private helper methods

    private GuestCart getValidGuestCart(String sessionId) {
        return guestCartRepository
            .findValidGuestCart(sessionId, Instant.now())
            .orElseThrow(() -> new BadRequestAlertException("Guest cart not found or expired", "GuestCart", ErrorConstants.ENTITY_NOT_FOUND)
            );
    }

    private Product getValidMarketplaceProduct(Long productId) {
        return productRepository
            .findById(productId)
            .filter(Product::getIsVisibleToCustomers) // Only marketplace visible products
            .orElseThrow(() ->
                new BadRequestAlertException(
                    "Product not found or not available for marketplace",
                    "Product",
                    ErrorConstants.ENTITY_NOT_FOUND
                )
            );
    }

    public void deleteGuestCart(String sessionId) {
        guestCartItemRepository.deleteBySessionId(sessionId);
        guestCartRepository.deleteById(sessionId);
    }
}
