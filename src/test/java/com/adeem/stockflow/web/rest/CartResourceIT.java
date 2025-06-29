package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.security.TestSecurityContextHelper.setSecurityContextWithUserId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.*;
import com.adeem.stockflow.repository.*;
import com.adeem.stockflow.security.AuthoritiesConstants;
import com.adeem.stockflow.service.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CartResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class CartResourceIT {

    private static final String ENTITY_API_URL = "/api/cart";
    private static final String ENTITY_API_URL_ITEMS = ENTITY_API_URL + "/items";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCartMockMvc;

    private User user;
    private Customer customer;
    private ClientAccount clientAccount;
    private Product product1;
    private Product product2;
    private Inventory inventory1;
    private Inventory inventory2;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    @AfterEach
    void tearDown() {
        cleanupTestData();
    }

    void setupTestData() {
        // Create client account
        clientAccount = createTestClientAccount();
        clientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        // Create user
        user = createTestUser();
        user = userRepository.saveAndFlush(user);

        // Create customer
        customer = createTestCustomer();
        customer.setUser(user);
        customer = customerRepository.saveAndFlush(customer);

        // Create products
        product1 = createTestProduct("Test Product 1", "TP001", new BigDecimal("10.00"));
        product1.setClientAccount(clientAccount);
        product1 = productRepository.saveAndFlush(product1);

        product2 = createTestProduct("Test Product 2", "TP002", new BigDecimal("20.00"));
        product2.setClientAccount(clientAccount);
        product2 = productRepository.saveAndFlush(product2);

        // Create inventories
        inventory1 = createTestInventory(product1, new BigDecimal("100"));
        inventory1 = inventoryRepository.saveAndFlush(inventory1);

        inventory2 = createTestInventory(product2, new BigDecimal("50"));
        inventory2 = inventoryRepository.saveAndFlush(inventory2);
    }

    void cleanupTestData() {
        // Clear the persistence context to avoid dirty state issues
        em.clear();

        // Use deleteAllInBatch for better performance and fewer cascading issues
        cartItemRepository.deleteAllInBatch();
        cartRepository.deleteAllInBatch();
        inventoryRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        clientAccountRepository.deleteAllInBatch();

        // Flush to ensure all deletions are executed
        em.flush();
    }

    @Test
    @Transactional
    void getCurrentCart_WhenNoCartExists_ShouldCreateNewCart() throws Exception {
        setSecurityContextWithUserId(user.getId());
        // When
        MvcResult result = restCartMockMvc
            .perform(get(ENTITY_API_URL))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        // Then
        CartWithTotalsDTO cartDTO = om.readValue(result.getResponse().getContentAsString(), CartWithTotalsDTO.class);
        assertThat(cartDTO).isNotNull();
        assertThat(cartDTO.getId()).isNotNull();
        assertThat(cartDTO.getItems()).isEmpty();
        assertThat(cartDTO.getGrandTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(cartDTO.getTotalItems()).isZero();
    }

    @Test
    @Transactional
    void addItemToCart_WithValidRequest_ShouldAddItem() throws Exception {
        setSecurityContextWithUserId(user.getId());
        // Given
        AddToCartRequestDTO request = new AddToCartRequestDTO();
        request.setProductId(product1.getId());
        request.setQuantity(new BigDecimal("5"));

        // When
        MvcResult result = restCartMockMvc
            .perform(post(ENTITY_API_URL_ITEMS).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        // Then
        CartItemDetailDTO itemDTO = om.readValue(result.getResponse().getContentAsString(), CartItemDetailDTO.class);
        assertThat(itemDTO).isNotNull();
        assertThat(itemDTO.getProductId()).isEqualTo(product1.getId());
        assertThat(itemDTO.getQuantity()).isEqualByComparingTo(new BigDecimal("5"));
        assertThat(itemDTO.getPrice()).isEqualByComparingTo(product1.getSellingPrice());
        assertThat(itemDTO.getLineTotal()).isEqualByComparingTo(new BigDecimal("50.00"));

        // Verify cart was created and item added
        List<Cart> carts = cartRepository.findByCustomerIdOrderByCreatedDateDesc(customer.getId());
        assertThat(carts).hasSize(1);
        assertThat(carts.get(0).getCartItems()).hasSize(1);
    }

    @Test
    @Transactional
    void addItemToCart_WithInsufficientStock_ShouldReturnBadRequest() throws Exception {
        setSecurityContextWithUserId(user.getId());
        // Given - Request more than available stock
        AddToCartRequestDTO request = new AddToCartRequestDTO();
        request.setProductId(product1.getId());
        request.setQuantity(new BigDecimal("150")); // More than available (100)

        // When & Then
        restCartMockMvc
            .perform(post(ENTITY_API_URL_ITEMS).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addItemToCart_WithInvalidQuantity_ShouldReturnBadRequest() throws Exception {
        setSecurityContextWithUserId(user.getId());
        // Given
        AddToCartRequestDTO request = new AddToCartRequestDTO();
        request.setProductId(product1.getId());
        request.setQuantity(BigDecimal.ZERO); // Invalid quantity

        // When & Then
        restCartMockMvc
            .perform(post(ENTITY_API_URL_ITEMS).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addItemToCart_ExistingItem_ShouldUpdateQuantity() throws Exception {
        setSecurityContextWithUserId(user.getId());
        // Given - Add item first time
        AddToCartRequestDTO request1 = new AddToCartRequestDTO();
        request1.setProductId(product1.getId());
        request1.setQuantity(new BigDecimal("3"));

        restCartMockMvc
            .perform(post(ENTITY_API_URL_ITEMS).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request1)))
            .andExpect(status().isCreated());

        // When - Add same item again
        AddToCartRequestDTO request2 = new AddToCartRequestDTO();
        request2.setProductId(product1.getId());
        request2.setQuantity(new BigDecimal("2"));

        MvcResult result = restCartMockMvc
            .perform(post(ENTITY_API_URL_ITEMS).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request2)))
            .andExpect(status().isCreated())
            .andReturn();

        // Then - Quantity should be updated (3 + 2 = 5)
        CartItemDetailDTO itemDTO = om.readValue(result.getResponse().getContentAsString(), CartItemDetailDTO.class);
        assertThat(itemDTO.getQuantity()).isEqualByComparingTo(new BigDecimal("5"));

        // Verify only one cart item exists
        List<Cart> carts = cartRepository.findByCustomerIdOrderByCreatedDateDesc(customer.getId());
        assertThat(carts.get(0).getCartItems()).hasSize(1);
    }

    @Test
    @Transactional
    void updateItemQuantity_WithValidData_ShouldUpdateQuantity() throws Exception {
        setSecurityContextWithUserId(user.getId());
        // Given - Create cart with item
        Cart cart = createCartWithItems();
        CartItem cartItem = cart.getCartItems().iterator().next();

        // When
        restCartMockMvc
            .perform(
                put(ENTITY_API_URL_ITEMS + "/{itemId}/quantity", cartItem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(new BigDecimal("10")))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantity").value(10))
            .andExpect(jsonPath("$.lineTotal").value(100.00));
    }

    @Test
    @Transactional
    void removeCartItem_WithValidId_ShouldRemoveItem() throws Exception {
        setSecurityContextWithUserId(user.getId());
        // Given
        Cart cart = createCartWithItems();
        CartItem cartItem = cart.getCartItems().iterator().next();
        Long cartItemId = cartItem.getId();

        // When
        restCartMockMvc.perform(delete(ENTITY_API_URL_ITEMS + "/{itemId}", cartItemId)).andExpect(status().isNoContent());

        // Then
        assertThat(cartItemRepository.findById(cartItemId)).isEmpty();
    }

    @Test
    @Transactional
    void clearCart_ShouldRemoveAllItems() throws Exception {
        setSecurityContextWithUserId(user.getId());
        // Given
        Cart cart = createCartWithItems();
        assertThat(cart.getCartItems()).isNotEmpty();

        // When
        restCartMockMvc.perform(delete(ENTITY_API_URL)).andExpect(status().isNoContent());

        // Then
        Cart updatedCart = cartRepository.findByCustomerId(customer.getId()).orElseThrow();
        assertThat(cartItemRepository.findByCartIdOrderByAddedDateDesc(updatedCart.getId())).isEmpty();
    }

    @Test
    @Transactional
    void validateCart_WithValidCart_ShouldReturnValid() throws Exception {
        setSecurityContextWithUserId(user.getId());
        // Given
        createCartWithItems();

        // When
        MvcResult result = restCartMockMvc.perform(post(ENTITY_API_URL + "/validate")).andExpect(status().isOk()).andReturn();

        // Then
        CartValidationResponseDTO validation = om.readValue(result.getResponse().getContentAsString(), CartValidationResponseDTO.class);
        assertThat(validation.getIsValid()).isTrue();
        assertThat(validation.getIssues()).isEmpty();
    }

    @Test
    @Transactional
    void validateCart_WithInsufficientStock_ShouldReturnInvalid() throws Exception {
        setSecurityContextWithUserId(user.getId());
        // Given - Create cart with item that has insufficient stock
        Cart cart = createCartWithItems();
        CartItem cartItem = cart.getCartItems().iterator().next();

        // Reduce inventory to create insufficient stock
        inventory1.setAvailableQuantity(new BigDecimal("2"));
        inventoryRepository.saveAndFlush(inventory1);

        // When
        MvcResult result = restCartMockMvc.perform(post(ENTITY_API_URL + "/validate")).andExpect(status().isOk()).andReturn();

        // Then
        CartValidationResponseDTO validation = om.readValue(result.getResponse().getContentAsString(), CartValidationResponseDTO.class);
        assertThat(validation.getIsValid()).isFalse();
        assertThat(validation.getIssues()).isNotEmpty();
        assertThat(validation.getIssues().get(0).getIssueType()).isEqualTo(CartValidationIssueDTO.IssueType.INSUFFICIENT_STOCK);
    }

    @Test
    @Transactional
    void getCartSummary_ShouldReturnSummary() throws Exception {
        setSecurityContextWithUserId(user.getId());
        // Given
        createCartWithItems();

        // When
        MvcResult result = restCartMockMvc.perform(get(ENTITY_API_URL + "/summary")).andExpect(status().isOk()).andReturn();

        // Then
        CartSummaryDTO summary = om.readValue(result.getResponse().getContentAsString(), CartSummaryDTO.class);
        assertThat(summary.getItemCount()).isEqualTo(2);
        assertThat(summary.getTotal()).isEqualByComparingTo(new BigDecimal("90.00"));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.USER_ADMIN)
    void accessCartAsAdmin_ShouldBeForbidden() throws Exception {
        setSecurityContextWithUserId(user.getId());
        // When & Then - Admin should not be able to access customer cart endpoints
        restCartMockMvc.perform(get(ENTITY_API_URL)).andExpect(status().isForbidden());
    }

    // Helper methods

    private Cart createCartWithItems() {
        Cart cart = createEmptyCart();

        CartItem item1 = createCartItem(cart, product1, new BigDecimal("5"));
        CartItem item2 = createCartItem(cart, product2, new BigDecimal("2"));

        cartItemRepository.saveAndFlush(item1);
        cartItemRepository.saveAndFlush(item2);

        cart.addCartItems(item1);
        cart.addCartItems(item2);

        // Refresh cart to get items
        return cartRepository.findById(cart.getId()).orElseThrow();
    }

    private Cart createEmptyCart() {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setCreatedDate(Instant.now());
        cart.setLastModifiedDate(Instant.now());
        return cartRepository.saveAndFlush(cart);
    }

    private CartItem createCartItem(Cart cart, Product product, BigDecimal quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getSellingPrice());
        cartItem.setAddedDate(Instant.now());
        cartItem.setCreatedDate(Instant.now());
        return cartItem;
    }

    private ClientAccount createTestClientAccount() {
        ClientAccount clientAccount = new ClientAccount();
        clientAccount.setCompanyName("Test Company");
        clientAccount.setPhone("0676841436");
        clientAccount.setEmail("test@company.com");
        clientAccount.setStatus(AccountStatus.ENABLED);
        clientAccount.setCreatedDate(Instant.now());
        return clientAccount;
    }

    private User createTestUser() {
        User user = new User();
        user.setLogin(String.valueOf(new Random().nextDouble()));
        user.setPassword("hashedpassword");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test" + new Random().nextDouble() + "@example.com");
        user.setActivated(true);
        user.setLangKey("en");
        user.setCreatedDate(Instant.now());
        return user;
    }

    private Customer createTestCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Test");
        customer.setLastName("Customer");
        customer.setPhone("+1234567890");
        customer.setEnabled(true);
        customer.setCreatedDate(Instant.now());
        return customer;
    }

    private Product createTestProduct(String name, String code, BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setCode(code);
        product.setDescription("Test product description");
        product.setSellingPrice(price);
        product.setCostPrice(price.multiply(new BigDecimal("0.7")));
        product.setCategory(ProductCategory.ELECTRONICS);
        product.setIsVisibleToCustomers(true);
        product.setApplyTva(false);
        product.setMinimumStockLevel(new BigDecimal("10"));
        product.setCreatedDate(Instant.now());
        return product;
    }

    private Inventory createTestInventory(Product product, BigDecimal quantity) {
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(quantity);
        inventory.setAvailableQuantity(quantity);
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventory.setCreatedDate(Instant.now());
        return inventory;
    }
}
