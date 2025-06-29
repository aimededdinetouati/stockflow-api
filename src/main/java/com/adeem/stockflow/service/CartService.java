package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.repository.*;
import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.mapper.CartMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Cart}.
 * Enhanced with multi-tenant security, inventory integration, and marketplace business logic.
 */
@Service
@Transactional
public class CartService {

    private static final Logger LOG = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final GuestCartService guestCartService;
    private final CartMapper cartMapper;

    public CartService(
        CartRepository cartRepository,
        CartItemRepository cartItemRepository,
        CustomerRepository customerRepository,
        ProductRepository productRepository,
        InventoryRepository inventoryRepository,
        GuestCartService guestCartService,
        CartMapper cartMapper
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.guestCartService = guestCartService;
        this.cartMapper = cartMapper;
    }

    /**
     * Get current user's active cart with detailed items and totals.
     * Creates a new cart if none exists.
     *
     * @return the cart with totals
     */
    @Transactional(readOnly = true)
    public CartWithTotalsDTO getCurrentUserCart() {
        LOG.debug("Request to get current user's cart");

        Long customerId = getCurrentCustomerId();

        Cart cart = cartRepository.findByCustomerId(customerId).orElseGet(() -> createNewCartForCustomer(customerId));

        return buildCartWithTotals(cart);
    }

    /**
     * Add item to current user's cart.
     *
     * @param request the add to cart request
     * @return the added cart item with details
     */
    //@CacheEvict(value = "userCarts", key = "#root.target.getCurrentCustomerId()")
    public CartItemDetailDTO addItemToCart(AddToCartRequestDTO request) {
        LOG.debug("Request to add item to cart: {}", request);

        Long customerId = getCurrentCustomerId();
        validateAddToCartRequest(request);

        Product product = productRepository
            .findById(request.getProductId())
            .orElseThrow(() -> new BadRequestAlertException("Product not found", "cart", "productnotfound"));

        validateProductAvailability(product, request.getQuantity());

        Cart cart = cartRepository.findByCustomerId(customerId).orElseGet(() -> createNewCartForCustomer(customerId));

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId());

        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            BigDecimal newQuantity = cartItem.getQuantity().add(request.getQuantity());
            validateProductAvailability(product, newQuantity);
            cartItem.setQuantity(newQuantity);
            cartItem.setLastModifiedDate(Instant.now());
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getSellingPrice());
            cartItem.setAddedDate(Instant.now());
            cartItem.setCreatedDate(Instant.now());
            cart.addCartItems(cartItem);
        }

        cartItem = cartItemRepository.save(cartItem);
        updateCartLastModified(cart);

        return buildCartItemDetail(cartItem);
    }

    /**
     * Update cart item quantity.
     *
     * @param cartItemId the cart item ID
     * @param quantity the new quantity
     * @return the updated cart item
     */
    //@CacheEvict(value = "userCarts", key = "#root.target.getCurrentCustomerId()")
    public CartItemDetailDTO updateCartItemQuantity(Long cartItemId, BigDecimal quantity) {
        LOG.debug("Request to update cart item quantity: {}, quantity: {}", cartItemId, quantity);

        Long customerId = getCurrentCustomerId();

        CartItem cartItem = cartItemRepository
            .findByIdAndCustomerId(cartItemId, customerId)
            .orElseThrow(() -> new BadRequestAlertException("Cart item not found", "cart", "itemnotfound"));

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestAlertException("Quantity must be greater than zero", "cart", "invalidquantity");
        }

        validateProductAvailability(cartItem.getProduct(), quantity);

        cartItem.setQuantity(quantity);
        cartItem.setLastModifiedDate(Instant.now());
        cartItem = cartItemRepository.save(cartItem);

        updateCartLastModified(cartItem.getCart());

        return buildCartItemDetail(cartItem);
    }

    /**
     * Remove item from cart.
     *
     * @param cartItemId the cart item ID
     */
    //@CacheEvict(value = "userCarts", key = "#root.target.getCurrentCustomerId()")
    public void removeCartItem(Long cartItemId) {
        LOG.debug("Request to remove cart item: {}", cartItemId);

        Long customerId = getCurrentCustomerId();

        CartItem cartItem = cartItemRepository
            .findByIdAndCustomerId(cartItemId, customerId)
            .orElseThrow(() -> new BadRequestAlertException("Cart item not found", "cart", "itemnotfound"));

        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        updateCartLastModified(cart);
    }

    /**
     * Clear entire cart.
     */
    //@CacheEvict(value = "userCarts", key = "#root.target.getCurrentCustomerId()")
    public void clearCart() {
        LOG.debug("Request to clear cart");

        Long customerId = getCurrentCustomerId();

        cartRepository
            .findByCustomerId(customerId)
            .ifPresent(cart -> {
                cartItemRepository.deleteAllByCartId(cart.getId());
                updateCartLastModified(cart);
            });
    }

    /**
     * Validate cart before checkout.
     *
     * @return validation response with issues and updated cart
     */
    @Transactional(readOnly = true)
    public CartValidationResponseDTO validateCart() {
        LOG.debug("Request to validate cart");

        Long customerId = getCurrentCustomerId();

        Cart cart = cartRepository
            .findByCustomerId(customerId)
            .orElseThrow(() -> new BadRequestAlertException("No active cart found", "cart", "noactivecart"));

        List<CartValidationIssueDTO> issues = new ArrayList<>();

        for (CartItem item : cart.getCartItems()) {
            validateCartItem(item, issues);
        }

        CartValidationResponseDTO response = new CartValidationResponseDTO();
        response.setIsValid(issues.isEmpty());
        response.setIssues(issues);
        response.setUpdatedCart(buildCartWithTotals(cart));

        return response;
    }

    /**
     * Migrate guest cart to authenticated user cart.
     *
     * @param sessionId the guest cart session ID
     * @return the migrated cart
     */
    //@CacheEvict(value = "userCarts", key = "#root.target.getCurrentCustomerId()")
    public CartWithTotalsDTO migrateGuestCart(String sessionId) {
        LOG.debug("Request to migrate guest cart: {}", sessionId);

        Long customerId = getCurrentCustomerId();

        GuestCartDTO guestCart = guestCartService.findGuestCart(sessionId);

        Cart userCart = cartRepository.findByCustomerId(customerId).orElseGet(() -> createNewCartForCustomer(customerId));

        // Migrate items from guest cart
        for (GuestCartItemDTO guestItem : guestCart.getItems()) {
            migrateGuestCartItem(userCart, guestItem);
        }

        // Clean up guest cart
        guestCartService.deleteGuestCart(sessionId);

        return buildCartWithTotals(userCart);
    }

    /**
     * Get cart summary for current user.
     *
     * @return cart summary
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "userCarts", key = "#root.target.getCurrentCustomerId()")
    public CartSummaryDTO getCartSummary() {
        LOG.debug("Request to get cart summary");

        Long customerId = getCurrentCustomerId();

        return cartRepository.findByCustomerId(customerId).map(this::buildCartSummary).orElse(new CartSummaryDTO());
    }

    // Private helper methods

    private Long getCurrentCustomerId() {
        Long userId = SecurityUtils.getCurrentUserId();
        return customerRepository
            .findByUserId(userId)
            .map(Customer::getId)
            .orElseThrow(() -> new AccessDeniedException("Current user is not a customer"));
    }

    private Cart createNewCartForCustomer(Long customerId) {
        Customer customer = customerRepository
            .findById(customerId)
            .orElseThrow(() -> new BadRequestAlertException("Customer not found", "cart", "customernotfound"));

        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setCreatedDate(Instant.now());
        cart.setLastModifiedDate(Instant.now());

        return cartRepository.save(cart);
    }

    private void validateAddToCartRequest(AddToCartRequestDTO request) {
        if (request.getProductId() == null) {
            throw new BadRequestAlertException("Product ID is required", "cart", "productidrequired");
        }
        if (request.getQuantity() == null || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestAlertException("Valid quantity is required", "cart", "quantityrequired");
        }
    }

    private void validateProductAvailability(Product product, BigDecimal requestedQuantity) {
        if (!product.getIsVisibleToCustomers()) {
            throw new BadRequestAlertException("Product is not available for purchase", "cart", "productnotavailable");
        }

        BigDecimal availableQuantity = inventoryRepository.getTotalAvailableQuantityForProduct(product.getId()).orElse(BigDecimal.ZERO);

        if (availableQuantity.compareTo(requestedQuantity) < 0) {
            throw new BadRequestAlertException(
                "Insufficient stock. Available: " + availableQuantity + ", Requested: " + requestedQuantity,
                "cart",
                "insufficientstock"
            );
        }
    }

    private void validateCartItem(CartItem item, List<CartValidationIssueDTO> issues) {
        Product product = item.getProduct();

        // Check product visibility
        if (!product.getIsVisibleToCustomers()) {
            CartValidationIssueDTO issue = new CartValidationIssueDTO();
            issue.setCartItemId(item.getId());
            issue.setProductId(product.getId());
            issue.setProductName(product.getName());
            issue.setIssueType(CartValidationIssueDTO.IssueType.PRODUCT_NOT_VISIBLE);
            issue.setMessage("Product is no longer available for purchase");
            issues.add(issue);
            return;
        }

        // Check stock availability
        BigDecimal availableQuantity = inventoryRepository.getTotalAvailableQuantityForProduct(product.getId()).orElse(BigDecimal.ZERO);

        if (availableQuantity.compareTo(BigDecimal.ZERO) == 0) {
            CartValidationIssueDTO issue = new CartValidationIssueDTO();
            issue.setCartItemId(item.getId());
            issue.setProductId(product.getId());
            issue.setProductName(product.getName());
            issue.setIssueType(CartValidationIssueDTO.IssueType.OUT_OF_STOCK);
            issue.setMessage("Product is out of stock");
            issue.setRequestedQuantity(item.getQuantity());
            issue.setAvailableQuantity(BigDecimal.ZERO);
            issues.add(issue);
        } else if (availableQuantity.compareTo(item.getQuantity()) < 0) {
            CartValidationIssueDTO issue = new CartValidationIssueDTO();
            issue.setCartItemId(item.getId());
            issue.setProductId(product.getId());
            issue.setProductName(product.getName());
            issue.setIssueType(CartValidationIssueDTO.IssueType.INSUFFICIENT_STOCK);
            issue.setMessage("Insufficient stock available");
            issue.setRequestedQuantity(item.getQuantity());
            issue.setAvailableQuantity(availableQuantity);
            issues.add(issue);
        }

        // Check price changes
        if (item.getPrice().compareTo(product.getSellingPrice()) != 0) {
            CartValidationIssueDTO issue = new CartValidationIssueDTO();
            issue.setCartItemId(item.getId());
            issue.setProductId(product.getId());
            issue.setProductName(product.getName());
            issue.setIssueType(CartValidationIssueDTO.IssueType.PRICE_CHANGED);
            issue.setMessage("Product price has changed");
            issue.setOldPrice(item.getPrice());
            issue.setNewPrice(product.getSellingPrice());
            issues.add(issue);
        }
    }

    private CartWithTotalsDTO buildCartWithTotals(Cart cart) {
        CartWithTotalsDTO dto = new CartWithTotalsDTO();
        dto.setId(cart.getId());
        dto.setCreatedDate(cart.getCreatedDate());
        dto.setLastModifiedDate(cart.getLastModifiedDate());

        List<CartItemDetailDTO> items = cart.getCartItems().stream().map(this::buildCartItemDetail).collect(Collectors.toList());
        dto.setItems(items);

        // Group by company
        Map<String, CompanyOrderSummaryDTO> ordersByCompany = items
            .stream()
            .collect(
                Collectors.groupingBy(
                    CartItemDetailDTO::getCompanyName,
                    Collectors.collectingAndThen(Collectors.toList(), this::buildCompanyOrderSummary)
                )
            );
        dto.setOrdersByCompany(ordersByCompany);

        // Calculate totals
        BigDecimal grandTotal = ordersByCompany
            .values()
            .stream()
            .map(CompanyOrderSummaryDTO::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setGrandTotal(grandTotal);

        BigDecimal totalShipping = ordersByCompany
            .values()
            .stream()
            .map(CompanyOrderSummaryDTO::getShippingCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalShipping(totalShipping);

        dto.setTotalItems(items.size());
        dto.setTotalQuantity(items.stream().mapToInt(item -> item.getQuantity().intValue()).sum());

        return dto;
    }

    private CartItemDetailDTO buildCartItemDetail(CartItem cartItem) {
        Product product = cartItem.getProduct();

        CartItemDetailDTO dto = new CartItemDetailDTO();
        dto.setId(cartItem.getId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getPrice());
        dto.setCurrentPrice(product.getSellingPrice());
        dto.setAddedDate(cartItem.getAddedDate());
        dto.setPriceChanged(!cartItem.getPrice().equals(product.getSellingPrice()));

        // Product details
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setProductDescription(product.getDescription());
        //dto.setProductImageUrl(product.getImageUrl());
        dto.setProductSku(product.getCode());

        // Company details
        dto.setCompanyId(product.getClientAccount().getId());
        dto.setCompanyName(product.getClientAccount().getCompanyName());

        // Availability details
        BigDecimal availableQuantity = inventoryRepository.getTotalAvailableQuantityForProduct(product.getId()).orElse(BigDecimal.ZERO);
        dto.setAvailableQuantity(availableQuantity);
        dto.setInStock(availableQuantity.compareTo(BigDecimal.ZERO) > 0);
        dto.setAvailabilityChanged(availableQuantity.compareTo(cartItem.getQuantity()) < 0);

        // Calculated fields
        dto.setLineTotal(cartItem.getPrice().multiply(cartItem.getQuantity()));

        return dto;
    }

    private CompanyOrderSummaryDTO buildCompanyOrderSummary(List<CartItemDetailDTO> items) {
        if (items.isEmpty()) {
            return new CompanyOrderSummaryDTO();
        }

        CartItemDetailDTO firstItem = items.get(0);
        CompanyOrderSummaryDTO summary = new CompanyOrderSummaryDTO();
        summary.setCompanyId(firstItem.getCompanyId());
        summary.setCompanyName(firstItem.getCompanyName());
        summary.setItems(items);

        BigDecimal subtotal = items.stream().map(CartItemDetailDTO::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setSubtotal(subtotal);

        // For now, simple shipping calculation - could be enhanced later
        BigDecimal shippingCost = subtotal.compareTo(new BigDecimal("100")) >= 0 ? BigDecimal.ZERO : new BigDecimal("10.00");
        summary.setShippingCost(shippingCost);
        summary.setTotal(subtotal.add(shippingCost));

        summary.setItemCount(items.size());
        summary.setTotalQuantity(items.stream().mapToInt(item -> item.getQuantity().intValue()).sum());

        return summary;
    }

    private CartSummaryDTO buildCartSummary(Cart cart) {
        CartSummaryDTO summary = new CartSummaryDTO();
        summary.setCartId(cart.getId());
        summary.setItemCount(cart.getCartItems().size());

        BigDecimal total = cart
            .getCartItems()
            .stream()
            .map(item -> item.getPrice().multiply(item.getQuantity()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotal(total);

        Integer totalQuantity = cart.getCartItems().stream().mapToInt(item -> item.getQuantity().intValue()).sum();
        summary.setTotalQuantity(totalQuantity);

        return summary;
    }

    private void updateCartLastModified(Cart cart) {
        cart.setLastModifiedDate(Instant.now());
        cartRepository.save(cart);
    }

    private void migrateGuestCartItem(Cart userCart, GuestCartItemDTO guestItem) {
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(userCart.getId(), guestItem.getProductId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            BigDecimal newQuantity = item.getQuantity().add(guestItem.getQuantity());

            // Validate availability for new quantity
            Product product = item.getProduct();
            try {
                validateProductAvailability(product, newQuantity);
                item.setQuantity(newQuantity);
                item.setLastModifiedDate(Instant.now());
                cartItemRepository.save(item);
            } catch (BadRequestAlertException e) {
                LOG.warn("Could not migrate guest cart item due to availability: {}", e.getMessage());
            }
        } else {
            // Create new cart item
            Product product = productRepository.findById(guestItem.getProductId()).orElse(null);
            if (product != null) {
                try {
                    validateProductAvailability(product, guestItem.getQuantity());

                    CartItem newItem = new CartItem();
                    newItem.setCart(userCart);
                    newItem.setProduct(product);
                    newItem.setQuantity(guestItem.getQuantity());
                    newItem.setPrice(guestItem.getPriceAtTime());
                    newItem.setAddedDate(Instant.now());
                    newItem.setCreatedDate(Instant.now());

                    cartItemRepository.save(newItem);
                } catch (BadRequestAlertException e) {
                    LOG.warn("Could not migrate guest cart item due to availability: {}", e.getMessage());
                }
            }
        }
    }
}
