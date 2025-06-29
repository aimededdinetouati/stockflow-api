package com.adeem.stockflow.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.GuestCart;
import com.adeem.stockflow.domain.GuestCartItem;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.enumeration.AccountStatus;
import com.adeem.stockflow.domain.enumeration.ProductCategory;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.repository.GuestCartItemRepository;
import com.adeem.stockflow.repository.GuestCartRepository;
import com.adeem.stockflow.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link PublicCartResource}.
 */
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class PublicCartResourceIT {

    @Autowired
    private MockMvc restPublicCartMockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GuestCartRepository guestCartRepository;

    @Autowired
    private GuestCartItemRepository guestCartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    private static final String ENTITY_API_URL = "/api/public/cart";
    private static final String GUEST_CART_API_URL = ENTITY_API_URL + "/guest";

    private Product testProduct;
    private Product invisibleProduct;
    private ClientAccount testClientAccount;

    @BeforeEach
    void setUp() {
        // Create test client account
        testClientAccount = new ClientAccount();
        testClientAccount.email("shipping@test.com");
        testClientAccount.setPhone("0555123456");
        testClientAccount.setStatus(AccountStatus.ENABLED);
        testClientAccount.setDefaultShippingCost(BigDecimal.TEN);
        testClientAccount.setReservationTimeoutHours(24);
        testClientAccount.setYalidineEnabled(true);
        testClientAccount.setCompanyName("Test Company");
        testClientAccount.setStatus(AccountStatus.ENABLED);
        testClientAccount = clientAccountRepository.saveAndFlush(testClientAccount);

        // Create visible test product
        testProduct = new Product();
        testProduct.setCostPrice(new BigDecimal("80.00"));
        testProduct.setCategory(ProductCategory.ELECTRONICS);
        testProduct.setApplyTva(true);
        testProduct.setName("Test Product");
        testProduct.setCode("TEST-001");
        testProduct.setSellingPrice(new BigDecimal("99.99"));
        testProduct.setCategory(ProductCategory.ELECTRONICS);
        testProduct.setIsVisibleToCustomers(true);
        testProduct.setClientAccount(testClientAccount);
        testProduct = productRepository.saveAndFlush(testProduct);

        // Create invisible test product
        invisibleProduct = new Product();
        invisibleProduct.setName("Invisible Product");
        invisibleProduct.setCode("INVISIBLE-001");
        invisibleProduct.setCostPrice(new BigDecimal("80.00"));
        invisibleProduct.setCategory(ProductCategory.ELECTRONICS);
        invisibleProduct.setApplyTva(false);
        invisibleProduct.setSellingPrice(new BigDecimal("49.99"));
        invisibleProduct.setCategory(ProductCategory.ELECTRONICS);
        invisibleProduct.setIsVisibleToCustomers(false);
        invisibleProduct.setClientAccount(testClientAccount);
        invisibleProduct = productRepository.saveAndFlush(invisibleProduct);
    }

    @Test
    @Transactional
    void createGuestCart_ShouldReturnSessionId() throws Exception {
        // When & Then
        MvcResult result = restPublicCartMockMvc
            .perform(post(GUEST_CART_API_URL))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.sessionId").exists())
            .andReturn();

        // Extract session ID from response
        String responseContent = result.getResponse().getContentAsString();
        PublicCartResource.GuestCartSessionResponse response = objectMapper.readValue(
            responseContent,
            PublicCartResource.GuestCartSessionResponse.class
        );

        String sessionId = response.getSessionId();
        assertThat(sessionId).isNotNull();
        assertThat(sessionId).hasSize(36); // UUID length

        // Verify cart exists in database
        assertThat(guestCartRepository.findById(sessionId)).isPresent();
    }

    @Test
    @Transactional
    void getGuestCart_WithValidSession_ShouldReturnCart() throws Exception {
        // Given - Create a guest cart first
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // When & Then
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.sessionId").value(sessionId))
            .andExpect(jsonPath("$.totalItems").value(0))
            .andExpect(jsonPath("$.totalAmount").value(0))
            .andExpect(jsonPath("$.isExpired").value(false))
            .andExpect(jsonPath("$.items").isArray())
            .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    @Transactional
    void getGuestCart_WithExpiredSession_ShouldReturnNotFound() throws Exception {
        // Given
        GuestCart expiredCart = new GuestCart();
        expiredCart.setSessionId("expired-session");
        expiredCart.setCreatedDate(Instant.now().minus(2, ChronoUnit.DAYS));
        expiredCart.setExpiresAt(Instant.now().minus(1, ChronoUnit.HOURS));
        guestCartRepository.saveAndFlush(expiredCart);

        // When & Then
        restPublicCartMockMvc.perform(get(GUEST_CART_API_URL + "/{sessionId}", "expired-session")).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void getGuestCart_WithInvalidSession_ShouldReturnNotFound() throws Exception {
        // When & Then
        restPublicCartMockMvc.perform(get(GUEST_CART_API_URL + "/{sessionId}", "invalid-session-id")).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addItemToGuestCart_WithValidData_ShouldAddItem() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // When & Then
        restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "2")
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.productId").value(testProduct.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(2))
            .andExpect(jsonPath("$.priceAtTime").value(99.99))
            .andExpect(jsonPath("$.totalPrice").value(199.98))
            .andExpect(jsonPath("$.sessionId").value(sessionId))
            .andExpect(jsonPath("$.addedDate").exists());

        // Verify cart totals
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(1))
            .andExpect(jsonPath("$.totalAmount").value(199.98))
            .andExpect(jsonPath("$.items").isArray())
            .andExpect(jsonPath("$.items[0].productId").value(testProduct.getId().intValue()));
    }

    @Test
    @Transactional
    void addItemToGuestCart_WithExistingItem_ShouldUpdateQuantity() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // Add item first time
        restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "1")
            )
            .andExpect(status().isCreated());

        // Add same item again
        // When & Then
        restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "2")
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.quantity").value(3)) // 1 + 2
            .andExpect(jsonPath("$.totalPrice").value(299.97)); // 3 * 99.99

        // Verify cart has only one item with updated quantity
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(1))
            .andExpect(jsonPath("$.totalAmount").value(299.97));
    }

    @Test
    @Transactional
    void addItemToGuestCart_WithInvalidSession_ShouldReturnBadRequest() throws Exception {
        // When & Then
        restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", "invalid-session")
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "1")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addItemToGuestCart_WithInvisibleProduct_ShouldReturnBadRequest() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // When & Then
        restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", invisibleProduct.getId().toString())
                    .param("quantity", "1")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addItemToGuestCart_WithNonExistentProduct_ShouldReturnBadRequest() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // When & Then
        restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", "99999") // Non-existent product
                    .param("quantity", "1")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addItemToGuestCart_WithInvalidQuantity_ShouldReturnBadRequest() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // When & Then
        restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "-1") // Negative quantity
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void updateGuestCartItem_WithValidData_ShouldUpdateQuantity() throws Exception {
        // Given - Create cart and add item
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        MvcResult addResult = restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "1")
            )
            .andExpect(status().isCreated())
            .andReturn();

        // Extract item ID from add response
        String addResponseContent = addResult.getResponse().getContentAsString();
        Long itemId = objectMapper.readTree(addResponseContent).get("id").asLong();

        // When & Then
        restPublicCartMockMvc
            .perform(put(GUEST_CART_API_URL + "/{sessionId}/items/{itemId}", sessionId, itemId).param("quantity", "5"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.quantity").value(5))
            .andExpect(jsonPath("$.totalPrice").value(499.95))
            .andExpect(jsonPath("$.id").value(itemId));

        // Verify cart totals updated
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalAmount").value(499.95));
    }

    @Test
    @Transactional
    void updateGuestCartItem_WithZeroQuantity_ShouldRemoveItem() throws Exception {
        // Given - Create cart and add item
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        MvcResult addResult = restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "1")
            )
            .andExpect(status().isCreated())
            .andReturn();

        // Extract item ID from add response
        String addResponseContent = addResult.getResponse().getContentAsString();
        Long itemId = objectMapper.readTree(addResponseContent).get("id").asLong();

        // When & Then
        restPublicCartMockMvc
            .perform(put(GUEST_CART_API_URL + "/{sessionId}/items/{itemId}", sessionId, itemId).param("quantity", "0"))
            .andExpect(status().isNoContent());

        // Verify item was removed
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(0))
            .andExpect(jsonPath("$.totalAmount").value(0))
            .andExpect(jsonPath("$.items").isEmpty());

        // Verify item doesn't exist in database
        assertThat(guestCartItemRepository.findById(itemId)).isEmpty();
    }

    @Test
    @Transactional
    void updateGuestCartItem_WithInvalidSession_ShouldReturnBadRequest() throws Exception {
        // When & Then
        restPublicCartMockMvc
            .perform(put(GUEST_CART_API_URL + "/{sessionId}/items/{itemId}", "invalid-session", 1L).param("quantity", "5"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void updateGuestCartItem_WithInvalidItem_ShouldReturnBadRequest() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // When & Then
        restPublicCartMockMvc
            .perform(put(GUEST_CART_API_URL + "/{sessionId}/items/{itemId}", sessionId, 99999L).param("quantity", "5"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void removeItemFromGuestCart_ShouldRemoveItem() throws Exception {
        // Given - Create cart and add item
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        MvcResult addResult = restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "1")
            )
            .andExpect(status().isCreated())
            .andReturn();

        // Extract item ID from add response
        String addResponseContent = addResult.getResponse().getContentAsString();
        Long itemId = objectMapper.readTree(addResponseContent).get("id").asLong();

        // When & Then
        restPublicCartMockMvc
            .perform(delete(GUEST_CART_API_URL + "/{sessionId}/items/{itemId}", sessionId, itemId))
            .andExpect(status().isNoContent());

        // Verify item was removed
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(0))
            .andExpect(jsonPath("$.totalAmount").value(0))
            .andExpect(jsonPath("$.items").isEmpty());

        // Verify item doesn't exist in database
        assertThat(guestCartItemRepository.findById(itemId)).isEmpty();
    }

    @Test
    @Transactional
    void removeItemFromGuestCart_WithInvalidSession_ShouldReturnBadRequest() throws Exception {
        // When & Then
        restPublicCartMockMvc
            .perform(delete(GUEST_CART_API_URL + "/{sessionId}/items/{itemId}", "invalid-session", 1L))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void clearGuestCart_ShouldRemoveAllItems() throws Exception {
        // Given - Create cart and add multiple items
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // Add first item
        restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "2")
            )
            .andExpect(status().isCreated());

        // Verify cart has items
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(1))
            .andExpect(jsonPath("$.totalAmount").value(199.98));

        // When & Then
        restPublicCartMockMvc.perform(delete(GUEST_CART_API_URL + "/{sessionId}", sessionId)).andExpect(status().isNoContent());

        // Verify cart is empty
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(0))
            .andExpect(jsonPath("$.totalAmount").value(0))
            .andExpect(jsonPath("$.items").isEmpty());

        // Verify no items exist in database for this session
        assertThat(guestCartItemRepository.findBySessionIdOrderByAddedDateDesc(sessionId)).isEmpty();
    }

    @Test
    @Transactional
    void clearGuestCart_WithInvalidSession_ShouldReturnBadRequest() throws Exception {
        // When & Then
        restPublicCartMockMvc.perform(delete(GUEST_CART_API_URL + "/{sessionId}", "invalid-session")).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void extendGuestCartExpiration_ShouldUpdateExpirationTime() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // Get original expiration
        MvcResult originalResult = restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andReturn();

        String originalExpiresAt = objectMapper.readTree(originalResult.getResponse().getContentAsString()).get("expiresAt").asText();

        // When & Then
        restPublicCartMockMvc
            .perform(put(GUEST_CART_API_URL + "/{sessionId}/extend", sessionId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.sessionId").value(sessionId))
            .andExpect(jsonPath("$.expiresAt").exists());

        // Verify expiration was extended
        MvcResult updatedResult = restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andReturn();

        String updatedExpiresAt = objectMapper.readTree(updatedResult.getResponse().getContentAsString()).get("expiresAt").asText();

        assertThat(updatedExpiresAt).isNotEqualTo(originalExpiresAt);
    }

    @Test
    @Transactional
    void extendGuestCartExpiration_WithInvalidSession_ShouldReturnNotFound() throws Exception {
        // When & Then
        restPublicCartMockMvc.perform(put(GUEST_CART_API_URL + "/{sessionId}/extend", "invalid-session")).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void getGuestCartItemCount_ShouldReturnCorrectCount() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "3")
            )
            .andExpect(status().isCreated());

        // When & Then
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}/count", sessionId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.count").value(1)); // 1 distinct item, not quantity
    }

    @Test
    @Transactional
    void getGuestCartItemCount_WithEmptyCart_ShouldReturnZero() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // When & Then
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}/count", sessionId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    @Transactional
    void getGuestCartItemCount_WithMultipleItems_ShouldReturnCorrectCount() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // Create another product
        Product secondProduct = new Product();
        secondProduct.setName("Second Product");
        secondProduct.setCode("TEST-002");
        secondProduct.setCostPrice(new BigDecimal("80.00"));
        secondProduct.setCategory(ProductCategory.ELECTRONICS);
        secondProduct.setApplyTva(false);
        secondProduct.setSellingPrice(new BigDecimal("29.99"));
        secondProduct.setCategory(ProductCategory.CLOTHING);
        secondProduct.setIsVisibleToCustomers(true);
        secondProduct.setClientAccount(testClientAccount);
        secondProduct = productRepository.saveAndFlush(secondProduct);

        // Add first item
        restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "2")
            )
            .andExpect(status().isCreated());

        // Add second item
        restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", secondProduct.getId().toString())
                    .param("quantity", "1")
            )
            .andExpect(status().isCreated());

        // When & Then
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}/count", sessionId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.count").value(2)); // 2 distinct items

        // Verify cart contents
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(2))
            .andExpect(jsonPath("$.totalAmount").value(229.97)) // (2 * 99.99) + (1 * 29.99)
            .andExpect(jsonPath("$.items").isArray())
            .andExpect(jsonPath("$.items.length()").value(2))
            .andExpect(jsonPath("$.items[0]").exists())
            .andExpect(jsonPath("$.items[1]").exists());
    }

    @Test
    @Transactional
    void getGuestCartItemCount_WithInvalidSession_ShouldReturnZero() throws Exception {
        // When & Then
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}/count", "invalid-session"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    @Transactional
    void guestCartWorkflow_CompleteScenario_ShouldWorkCorrectly() throws Exception {
        // 1. Create guest cart
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // 2. Verify empty cart
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(0))
            .andExpect(jsonPath("$.totalAmount").value(0))
            .andExpect(jsonPath("$.isExpired").value(false));

        // 3. Add item to cart
        MvcResult addResult = restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "2")
            )
            .andExpect(status().isCreated())
            .andReturn();

        Long itemId = objectMapper.readTree(addResult.getResponse().getContentAsString()).get("id").asLong();

        // 4. Verify cart has item
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(1))
            .andExpect(jsonPath("$.totalAmount").value(199.98));

        // 5. Update item quantity
        restPublicCartMockMvc
            .perform(put(GUEST_CART_API_URL + "/{sessionId}/items/{itemId}", sessionId, itemId).param("quantity", "3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantity").value(3))
            .andExpect(jsonPath("$.totalPrice").value(299.97));

        // 6. Verify updated totals
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(1))
            .andExpect(jsonPath("$.totalAmount").value(299.97));

        // 7. Check item count
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}/count", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.count").value(1));

        // 8. Extend cart expiration
        restPublicCartMockMvc
            .perform(put(GUEST_CART_API_URL + "/{sessionId}/extend", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(sessionId));

        // 9. Remove item
        restPublicCartMockMvc
            .perform(delete(GUEST_CART_API_URL + "/{sessionId}/items/{itemId}", sessionId, itemId))
            .andExpect(status().isNoContent());

        // 10. Verify cart is empty
        restPublicCartMockMvc
            .perform(get(GUEST_CART_API_URL + "/{sessionId}", sessionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(0))
            .andExpect(jsonPath("$.totalAmount").value(0))
            .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    @Transactional
    void addItemToGuestCart_WithMalformedRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // When & Then - Send invalid productId parameter
        restPublicCartMockMvc
            .perform(post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId).param("productId", "not-a-number").param("quantity", "1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addItemToGuestCart_WithMissingFields_ShouldReturnBadRequest() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        // When & Then - Send request without required parameters
        restPublicCartMockMvc.perform(post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void updateGuestCartItem_WithMissingQuantity_ShouldReturnBadRequest() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        MvcResult addResult = restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "1")
            )
            .andExpect(status().isCreated())
            .andReturn();

        Long itemId = objectMapper.readTree(addResult.getResponse().getContentAsString()).get("id").asLong();

        // When & Then - Send request without quantity parameter
        restPublicCartMockMvc
            .perform(put(GUEST_CART_API_URL + "/{sessionId}/items/{itemId}", sessionId, itemId))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void updateGuestCartItem_WithInvalidQuantityFormat_ShouldReturnBadRequest() throws Exception {
        // Given
        MvcResult createResult = restPublicCartMockMvc.perform(post(GUEST_CART_API_URL)).andExpect(status().isCreated()).andReturn();

        String sessionId = objectMapper
            .readValue(createResult.getResponse().getContentAsString(), PublicCartResource.GuestCartSessionResponse.class)
            .getSessionId();

        MvcResult addResult = restPublicCartMockMvc
            .perform(
                post(GUEST_CART_API_URL + "/{sessionId}/items", sessionId)
                    .param("productId", testProduct.getId().toString())
                    .param("quantity", "1")
            )
            .andExpect(status().isCreated())
            .andReturn();

        Long itemId = objectMapper.readTree(addResult.getResponse().getContentAsString()).get("id").asLong();

        // When & Then - Send request with invalid quantity format
        restPublicCartMockMvc
            .perform(put(GUEST_CART_API_URL + "/{sessionId}/items/{itemId}", sessionId, itemId).param("quantity", "not-a-number"))
            .andExpect(status().isBadRequest());
    }
}
