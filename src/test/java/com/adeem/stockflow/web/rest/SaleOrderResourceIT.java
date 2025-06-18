package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.security.TestSecurityContextHelper.setSecurityContextWithClientAccountId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.*;
import com.adeem.stockflow.repository.*;
import com.adeem.stockflow.security.AuthoritiesConstants;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import com.adeem.stockflow.service.mapper.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the enhanced {@link SaleOrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class SaleOrderResourceIT {

    // Constants
    private static final String DEFAULT_REFERENCE = "2025/0001";
    private static final String UPDATED_REFERENCE = "2025/0001";
    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.now();
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now().plusDays(1);
    private static final OrderStatus DEFAULT_STATUS = OrderStatus.DRAFTED;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.CONFIRMED;
    private static final OrderType DEFAULT_ORDER_TYPE = OrderType.STORE_PICKUP;
    private static final OrderType UPDATED_ORDER_TYPE = OrderType.DELIVERY;
    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal("1000.00");
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal("1500.00");
    private static final BigDecimal DEFAULT_TVA_AMOUNT = DEFAULT_TOTAL.multiply(BigDecimal.valueOf(19)).divide(
        BigDecimal.valueOf(100),
        2,
        BigDecimal.ROUND_HALF_UP
    );
    private static final BigDecimal DEFAULT_STAMP_AMOUNT = DEFAULT_TOTAL.add(DEFAULT_TVA_AMOUNT)
        .multiply(BigDecimal.valueOf(1))
        .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal DEFAULT_SHIPPING_COST = new BigDecimal("500.00");
    private static final String ENTITY_API_URL = "/api/sale-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    // Dependencies
    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private SaleOrderMapper saleOrderMapper;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private MockMvc restSaleOrderMockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private EntityManager em;

    // Test data
    private SaleOrder saleOrder;
    private ClientAccount clientAccount;
    private Customer customer;
    private Product product;
    private Inventory inventory;

    @BeforeEach
    void initTest() {
        clientAccount = createAndSaveClientAccount();
        customer = createAndSaveCustomer(clientAccount);
    }

    // ===============================
    // CENTRALIZED OBJECT CREATION
    // ===============================

    private ClientAccount createAndSaveClientAccount() {
        return createAndSaveClientAccount("Test Company", "test@company.com", "0676841436", 24);
    }

    private ClientAccount createAndSaveClientAccount(String companyName, String email, String phone, int timeoutHours) {
        ClientAccount account = new ClientAccount();
        account.setCompanyName(companyName);
        account.email(email);
        account.setPhone(phone);
        account.setStatus(AccountStatus.ENABLED);
        account.setDefaultShippingCost(DEFAULT_SHIPPING_COST);
        account.setReservationTimeoutHours(timeoutHours);
        account.setYalidineEnabled(false);
        return clientAccountRepository.saveAndFlush(account);
    }

    private Customer createAndSaveCustomer(ClientAccount clientAccount) {
        return createAndSaveCustomer("John", "Doe", "+213555123456", clientAccount);
    }

    private Customer createAndSaveCustomer(String firstName, String lastName, String phone, ClientAccount clientAccount) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPhone(phone);
        customer.setCreatedByClientAccount(clientAccount);
        return customerRepository.saveAndFlush(customer);
    }

    private Product createAndSaveProduct(ClientAccount clientAccount) {
        return createAndSaveProduct("Test Product", "TEST-001", new BigDecimal("100.00"), new BigDecimal("80.00"), false, clientAccount);
    }

    private Product createAndSaveProduct(
        String name,
        String code,
        BigDecimal sellingPrice,
        BigDecimal costPrice,
        boolean tvaApplied,
        ClientAccount clientAccount
    ) {
        Product product = new Product();
        product.setName(name);
        product.setCode(code);
        product.applyTva(tvaApplied);
        product.setSellingPrice(sellingPrice);
        product.setCostPrice(costPrice);
        product.setCategory(ProductCategory.ELECTRONICS);
        product.setClientAccount(clientAccount);
        return productRepository.saveAndFlush(product);
    }

    private Inventory createAndSaveInventory(Product product, ClientAccount clientAccount) {
        return createAndSaveInventory(product, new BigDecimal("100"), new BigDecimal("100"), clientAccount);
    }

    private Inventory createAndSaveInventory(
        Product product,
        BigDecimal quantity,
        BigDecimal availableQuantity,
        ClientAccount clientAccount
    ) {
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(quantity);
        inventory.setAvailableQuantity(availableQuantity);
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventory.setClientAccount(clientAccount);
        return inventoryRepository.saveAndFlush(inventory);
    }

    private SaleOrder createSaleOrder(ClientAccount clientAccount, Customer customer) {
        return createSaleOrder(
            DEFAULT_REFERENCE,
            DEFAULT_DATE,
            DEFAULT_STATUS,
            DEFAULT_ORDER_TYPE,
            DEFAULT_TOTAL,
            DEFAULT_TOTAL,
            false,
            false,
            clientAccount,
            customer
        );
    }

    private SaleOrder createSaleOrder(
        String reference,
        ZonedDateTime date,
        OrderStatus status,
        OrderType orderType,
        BigDecimal subTotal,
        BigDecimal total,
        boolean tvaApplied,
        boolean stampApplied,
        ClientAccount clientAccount,
        Customer customer
    ) {
        SaleOrder order = new SaleOrder();
        order.setReference(reference);
        order.setDate(date);
        order.setStatus(status);
        order.setTvaApplied(tvaApplied);
        order.setStampApplied(stampApplied);
        order.setOrderType(orderType);
        order.setSubTotal(subTotal);
        order.setTotal(total);
        order.setClientAccount(clientAccount);
        order.setCustomer(customer);
        return order;
    }

    private SaleOrderItem createSaleOrderItem(Product product, BigDecimal quantity, BigDecimal unitPrice, SaleOrder saleOrder) {
        SaleOrderItem item = new SaleOrderItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setTotal(quantity.multiply(unitPrice));
        item.setSaleOrder(saleOrder);
        return item;
    }

    private SaleOrder createCompleteTestOrder() {
        return createCompleteTestOrder(BigDecimal.TEN, new BigDecimal("100.00"));
    }

    private SaleOrder createCompleteTestOrder(BigDecimal quantity, BigDecimal unitPrice) {
        product = createAndSaveProduct(clientAccount);
        inventory = createAndSaveInventory(product, clientAccount);

        saleOrder = createSaleOrder(clientAccount, customer);
        SaleOrderItem item = createSaleOrderItem(product, quantity, unitPrice, saleOrder);
        saleOrder.addOrderItem(item);

        return saleOrder;
    }

    private Set<SaleOrderItemDTO> createValidOrderItems() {
        if (product == null) {
            product = createAndSaveProduct(clientAccount);
        }

        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(product));
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);
        return orderItems;
    }

    private SaleOrderDTO createBasicSaleOrderDTO() {
        SaleOrderDTO dto = new SaleOrderDTO();
        dto.setReference(DEFAULT_REFERENCE);
        dto.setDate(DEFAULT_DATE);
        dto.setStatus(DEFAULT_STATUS);
        dto.setOrderType(DEFAULT_ORDER_TYPE);
        dto.setCustomer(customerMapper.toDto(customer));
        return dto;
    }

    private void setupSecurityContext() {
        setSecurityContextWithClientAccountId(clientAccount.getId());
    }

    // ===============================
    // BASIC CRUD TESTS
    // ===============================

    @Test
    @Transactional
    void createSaleOrder() throws Exception {
        setupSecurityContext();
        int databaseSizeBeforeCreate = saleOrderRepository.findAll().size();

        createCompleteTestOrder();

        // Create the SaleOrder
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);
        saleOrderDTO.setId(null); // Ensure it's a new order

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.orderType").value(DEFAULT_ORDER_TYPE.toString()))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL.doubleValue()));

        // Validate the SaleOrder in the database
        List<SaleOrder> saleOrderList = saleOrderRepository.findAll();
        assertThat(saleOrderList).hasSize(databaseSizeBeforeCreate + 1);
        SaleOrder testSaleOrder = saleOrderList.get(saleOrderList.size() - 1);
        assertThat(testSaleOrder.getReference()).isEqualTo(DEFAULT_REFERENCE);
        assertThat(testSaleOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testSaleOrder.getOrderType()).isEqualTo(DEFAULT_ORDER_TYPE);
    }

    @Test
    @Transactional
    void createSaleOrderWithTvaAndStampApplied() throws Exception {
        setupSecurityContext();
        int databaseSizeBeforeCreate = saleOrderRepository.findAll().size();

        createCompleteTestOrder();

        // Update order to apply TVA and stamp
        saleOrder.setTvaApplied(true);
        saleOrder.setStampApplied(true);

        // Create the SaleOrder
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);
        saleOrderDTO.setId(null); // Ensure it's a new order

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.orderType").value(DEFAULT_ORDER_TYPE.toString()))
            .andExpect(jsonPath("$.tvaAmount").value(DEFAULT_TVA_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.stampRate").value(1.00))
            .andExpect(jsonPath("$.stampAmount").value(DEFAULT_STAMP_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL.add(DEFAULT_TVA_AMOUNT).add(DEFAULT_STAMP_AMOUNT).doubleValue()));

        // Validate the SaleOrder in the database
        List<SaleOrder> saleOrderList = saleOrderRepository.findAll();
        assertThat(saleOrderList).hasSize(databaseSizeBeforeCreate + 1);
        SaleOrder testSaleOrder = saleOrderList.get(saleOrderList.size() - 1);
        assertThat(testSaleOrder.getReference()).isEqualTo(DEFAULT_REFERENCE);
        assertThat(testSaleOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testSaleOrder.getOrderType()).isEqualTo(DEFAULT_ORDER_TYPE);
    }

    // ===============================
    // VALIDATION TESTS
    // ===============================

    @Test
    @Transactional
    void createSaleOrder_WithNullOrderItems_ShouldThrowException() throws Exception {
        setupSecurityContext();

        SaleOrderDTO saleOrderDTO = createBasicSaleOrderDTO();
        saleOrderDTO.setOrderItems(null); // Null order items

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorKey").value(containsString(ErrorConstants.REQUIRED_ORDER_ITEMS)));
    }

    @Test
    @Transactional
    void createSaleOrder_WithEmptyOrderItems_ShouldThrowException() throws Exception {
        setupSecurityContext();

        SaleOrderDTO saleOrderDTO = createBasicSaleOrderDTO();
        saleOrderDTO.setOrderItems(new HashSet<>()); // Empty order items

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorKey").value(containsString(ErrorConstants.REQUIRED_ORDER_ITEMS)));
    }

    @Test
    @Transactional
    void createSaleOrder_WithNonExistentCustomer_ShouldThrowException() throws Exception {
        setupSecurityContext();

        CustomerDTO nonExistentCustomer = new CustomerDTO();
        nonExistentCustomer.setId(99999L); // Non-existent customer ID

        SaleOrderDTO saleOrderDTO = createBasicSaleOrderDTO();
        saleOrderDTO.setCustomer(nonExistentCustomer);
        saleOrderDTO.setOrderItems(createValidOrderItems());

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void createSaleOrder_WithNonExistentProduct_ShouldThrowException() throws Exception {
        setupSecurityContext();

        // Create order item with non-existent product
        SaleOrderItemDTO item = new SaleOrderItemDTO();
        ProductDTO nonExistentProduct = new ProductDTO();
        nonExistentProduct.setId(99999L); // Non-existent product ID
        item.setProduct(nonExistentProduct);
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = createBasicSaleOrderDTO();
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void createSaleOrder_WithProductFromDifferentClientAccount_ShouldThrowException() throws Exception {
        setupSecurityContext();

        // Create a product for a different client account
        ClientAccount otherClientAccount = createAndSaveClientAccount("Other Company", "other@company.com", "0676841437", 24);
        Product otherProduct = createAndSaveProduct(
            "Other Product",
            "OTHER-001",
            new BigDecimal("100.00"),
            new BigDecimal("80.00"),
            false,
            otherClientAccount
        );

        // Create order item with product from different client account
        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(otherProduct));
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = createBasicSaleOrderDTO();
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void createSaleOrder_WithProductWithoutPrice_ShouldThrowException() throws Exception {
        setupSecurityContext();

        // Create product without selling price
        Product productWithoutPrice = createAndSaveProduct(
            "No Price Product",
            "NOPRICE-001",
            null,
            new BigDecimal("80.00"),
            false,
            clientAccount
        );

        // Create order item without unit price (should use product price which is null)
        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(productWithoutPrice));
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(null); // No unit price, should use product price

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = createBasicSaleOrderDTO();
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorKey").value(containsString(ErrorConstants.REQUIRED_UNIT_PRICE)));
    }

    // ===============================
    // EDGE CASE TESTS
    // ===============================

    @Test
    @Transactional
    void createSaleOrder_WithZeroQuantity_ShouldSucceed() throws Exception {
        setupSecurityContext();
        int databaseSizeBeforeCreate = saleOrderRepository.findAll().size();

        product = createAndSaveProduct(clientAccount);

        // Create order item with zero quantity
        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(product));
        item.setQuantity(BigDecimal.ZERO); // Zero quantity
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = createBasicSaleOrderDTO();
        saleOrderDTO.setSubTotal(BigDecimal.ZERO);
        saleOrderDTO.setTotal(BigDecimal.ZERO);
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorKey").value(ErrorConstants.QUANTITY_INVALID));
    }

    @Test
    @Transactional
    void createSaleOrder_WithMultipleItemsSameProduct_ShouldSucceed() throws Exception {
        setupSecurityContext();
        int databaseSizeBeforeCreate = saleOrderRepository.findAll().size();

        product = createAndSaveProduct(clientAccount);

        // Create multiple order items with same product
        SaleOrderItemDTO item1 = new SaleOrderItemDTO();
        item1.setProduct(productMapper.toDto(product));
        item1.setQuantity(new BigDecimal("5"));
        item1.setUnitPrice(new BigDecimal("100.00"));

        SaleOrderItemDTO item2 = new SaleOrderItemDTO();
        item2.setProduct(productMapper.toDto(product));
        item2.setQuantity(new BigDecimal("3"));
        item2.setUnitPrice(new BigDecimal("120.00")); // Different price

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item1);
        orderItems.add(item2);

        SaleOrderDTO saleOrderDTO = createBasicSaleOrderDTO();
        saleOrderDTO.setSubTotal(new BigDecimal("860.00")); // 500 + 360
        saleOrderDTO.setTotal(new BigDecimal("860.00"));
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderItems", hasSize(2)));

        List<SaleOrder> saleOrderList = saleOrderRepository.findAll();
        assertThat(saleOrderList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void createSaleOrder_WithVeryLargeQuantityAndPrice_ShouldSucceed() throws Exception {
        setupSecurityContext();

        product = createAndSaveProduct(clientAccount);

        // Create order item with very large quantity and price
        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(product));
        item.setQuantity(new BigDecimal("999999999.99"));
        item.setUnitPrice(new BigDecimal("999999999.99"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        BigDecimal expectedTotal = new BigDecimal("999999999.99").multiply(new BigDecimal("999999999.99"));

        SaleOrderDTO saleOrderDTO = createBasicSaleOrderDTO();
        saleOrderDTO.setSubTotal(expectedTotal);
        saleOrderDTO.setTotal(expectedTotal);
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderItems[0].quantity").value(999999999.99))
            .andExpect(jsonPath("$.orderItems[0].unitPrice").value(999999999.99));
    }

    @Test
    @Transactional
    void createSaleOrder_WithComplexDiscountAndTaxCalculations_ShouldSucceed() throws Exception {
        setupSecurityContext();

        product = createAndSaveProduct(clientAccount);

        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(product));
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = createBasicSaleOrderDTO();
        saleOrderDTO.setSubTotal(new BigDecimal("1000.00"));
        saleOrderDTO.setTotal(new BigDecimal("1000.00"));
        saleOrderDTO.setOrderItems(orderItems);
        saleOrderDTO.setTvaApplied(true);
        saleOrderDTO.setStampApplied(true);
        saleOrderDTO.setDiscountRate(new BigDecimal("10.00")); // 10% discount

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tvaApplied").value(true))
            .andExpect(jsonPath("$.stampApplied").value(true))
            .andExpect(jsonPath("$.discountRate").value(10.0))
            .andExpect(jsonPath("$.discountAmount").exists())
            .andExpect(jsonPath("$.tvaAmount").exists())
            .andExpect(jsonPath("$.stampAmount").exists());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.USER_ADMIN)
    void createSaleOrder_WithoutSecurityContext_ShouldThrowException() throws Exception {
        // Don't set security context - should fail

        product = createAndSaveProduct(clientAccount);

        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(product));
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = createBasicSaleOrderDTO();
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().is5xxServerError());
    }

    // ===============================
    // ORDER CONFIRMATION TESTS
    // ===============================

    @Test
    @Transactional
    void confirmOrder_Success() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        BigDecimal originalAvailableQuantity = inventory.getAvailableQuantity();
        BigDecimal orderQuantity = saleOrder.getOrderItems().iterator().next().getQuantity();

        // Confirm the order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/confirm", saleOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.status").value(OrderStatus.CONFIRMED.toString()))
            .andExpect(jsonPath("$.reservationExpiresAt").exists());

        // Verify order status change
        SaleOrder confirmedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(confirmedOrder).isNotNull();
        assertThat(confirmedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(confirmedOrder.getReservationExpiresAt()).isNotNull();

        // Verify inventory reservation (available quantity should decrease)
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElse(null);
        assertThat(updatedInventory).isNotNull();
        assertThat(updatedInventory.getAvailableQuantity()).isEqualByComparingTo(originalAvailableQuantity.subtract(orderQuantity));

        // Total quantity should remain the same (only reserved, not consumed)
        assertThat(updatedInventory.getQuantity()).isEqualByComparingTo(inventory.getQuantity());
    }

    @Test
    @Transactional
    void confirmOrder_WithExactInventoryMatch_ShouldSucceed() throws Exception {
        setupSecurityContext();

        // Create product with limited inventory that exactly matches order quantity
        product = createAndSaveProduct(clientAccount);
        BigDecimal exactQuantity = new BigDecimal("15");
        inventory = createAndSaveInventory(product, exactQuantity, exactQuantity, clientAccount);

        // Create order that requires exactly the available inventory
        saleOrder = createSaleOrder(clientAccount, customer);
        SaleOrderItem item = createSaleOrderItem(product, exactQuantity, new BigDecimal("100.00"), saleOrder);
        saleOrder.addOrderItem(item);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Confirm the order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/confirm", saleOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.status").value(OrderStatus.CONFIRMED.toString()))
            .andExpect(jsonPath("$.reservationExpiresAt").exists());

        // Verify order status change
        SaleOrder confirmedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(confirmedOrder).isNotNull();
        assertThat(confirmedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);

        // Verify inventory is completely reserved (available quantity should be zero)
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElse(null);
        assertThat(updatedInventory).isNotNull();
        assertThat(updatedInventory.getAvailableQuantity()).isEqualByComparingTo(BigDecimal.ZERO);

        // Total quantity should remain the same (only reserved, not consumed)
        assertThat(updatedInventory.getQuantity()).isEqualByComparingTo(exactQuantity);
    }

    @Test
    @Transactional
    void confirmOrder_WithMixedInventoryLevels_ShouldSucceed() throws Exception {
        setupSecurityContext();

        // Create multiple products with different inventory levels
        Product productAbundant = createAndSaveProduct(
            "Abundant Product",
            "ABUND-001",
            new BigDecimal("50.00"),
            new BigDecimal("40.00"),
            false,
            clientAccount
        );
        Product productLimited = createAndSaveProduct(
            "Limited Product",
            "LIMIT-001",
            new BigDecimal("100.00"),
            new BigDecimal("80.00"),
            false,
            clientAccount
        );
        Product productExact = createAndSaveProduct(
            "Exact Product",
            "EXACT-001",
            new BigDecimal("200.00"),
            new BigDecimal("160.00"),
            false,
            clientAccount
        );

        // Create inventories with different availability levels
        Inventory inventoryAbundant = createAndSaveInventory(
            productAbundant,
            new BigDecimal("1000"),
            new BigDecimal("1000"),
            clientAccount
        ); // Plenty available
        Inventory inventoryLimited = createAndSaveInventory(productLimited, new BigDecimal("20"), new BigDecimal("15"), clientAccount); // Some already reserved
        Inventory inventoryExact = createAndSaveInventory(productExact, new BigDecimal("5"), new BigDecimal("5"), clientAccount); // Exact match

        // Create order with mixed quantities
        saleOrder = createSaleOrder(
            DEFAULT_REFERENCE,
            DEFAULT_DATE,
            OrderStatus.DRAFTED,
            DEFAULT_ORDER_TYPE,
            new BigDecimal("1750.00"),
            new BigDecimal("1750.00"),
            false,
            false,
            clientAccount,
            customer
        );

        SaleOrderItem itemAbundant = createSaleOrderItem(productAbundant, new BigDecimal("10"), new BigDecimal("50.00"), saleOrder); // Uses 1% of available
        SaleOrderItem itemLimited = createSaleOrderItem(productLimited, new BigDecimal("5"), new BigDecimal("100.00"), saleOrder); // Uses 1/3 of available
        SaleOrderItem itemExact = createSaleOrderItem(productExact, new BigDecimal("5"), new BigDecimal("200.00"), saleOrder); // Uses all available

        saleOrder.addOrderItem(itemAbundant);
        saleOrder.addOrderItem(itemLimited);
        saleOrder.addOrderItem(itemExact);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Confirm the order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/confirm", saleOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.CONFIRMED.toString()))
            .andExpect(jsonPath("$.orderItems", hasSize(3)));

        // Verify all inventories were properly reserved
        Inventory updatedAbundant = inventoryRepository.findById(inventoryAbundant.getId()).orElse(null);
        assertThat(updatedAbundant).isNotNull();
        assertThat(updatedAbundant.getAvailableQuantity()).isEqualByComparingTo(new BigDecimal("990")); // 1000 - 10

        Inventory updatedLimited = inventoryRepository.findById(inventoryLimited.getId()).orElse(null);
        assertThat(updatedLimited).isNotNull();
        assertThat(updatedLimited.getAvailableQuantity()).isEqualByComparingTo(new BigDecimal("10")); // 15 - 5

        Inventory updatedExact = inventoryRepository.findById(inventoryExact.getId()).orElse(null);
        assertThat(updatedExact).isNotNull();
        assertThat(updatedExact.getAvailableQuantity()).isEqualByComparingTo(BigDecimal.ZERO); // 5 - 5

        // Verify total quantities remain unchanged
        assertThat(updatedAbundant.getQuantity()).isEqualByComparingTo(new BigDecimal("1000"));
        assertThat(updatedLimited.getQuantity()).isEqualByComparingTo(new BigDecimal("20"));
        assertThat(updatedExact.getQuantity()).isEqualByComparingTo(new BigDecimal("5"));
    }

    @Test
    @Transactional
    void confirmOrder_WithCustomReservationTimeout_ShouldSetCorrectExpiration() throws Exception {
        setupSecurityContext();

        // Create client account with specific custom timeout (72 hours)
        clientAccount.setReservationTimeoutHours(72);
        clientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        createCompleteTestOrder();
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        ZonedDateTime beforeConfirm = ZonedDateTime.now();

        // Confirm the order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/confirm", saleOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.CONFIRMED.toString()))
            .andExpect(jsonPath("$.reservationExpiresAt").exists());

        ZonedDateTime afterConfirm = ZonedDateTime.now();

        // Verify order was confirmed
        SaleOrder confirmedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(confirmedOrder).isNotNull();
        assertThat(confirmedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);

        // Verify reservation expiration is set to exactly 72 hours from confirmation time
        assertThat(confirmedOrder.getReservationExpiresAt()).isNotNull();

        ZonedDateTime expectedMinExpiration = beforeConfirm.plusHours(72);
        ZonedDateTime expectedMaxExpiration = afterConfirm.plusHours(72);

        assertThat(confirmedOrder.getReservationExpiresAt())
            .isAfterOrEqualTo(expectedMinExpiration)
            .isBeforeOrEqualTo(expectedMaxExpiration);

        // Verify the expiration is approximately 72 hours from now (within reasonable tolerance)
        long hoursDifference = java.time.Duration.between(ZonedDateTime.now(), confirmedOrder.getReservationExpiresAt()).toHours();
        assertThat(hoursDifference).isBetween(71L, 73L); // Allow for small timing differences

        // Verify inventory reservation still occurred correctly
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElse(null);
        assertThat(updatedInventory).isNotNull();
        // Original inventory starts with 100 available, after reserving 10 should be 90
        BigDecimal expectedAvailable = new BigDecimal("90");
        assertThat(updatedInventory.getAvailableQuantity()).isEqualByComparingTo(expectedAvailable);

        // Test different timeout value by updating client account
        clientAccount.setReservationTimeoutHours(24); // Change to 24 hours
        clientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        // Create another order to test different timeout
        Product secondProduct = createAndSaveProduct(
            "Timeout Test Product",
            "TIMEOUT-001",
            new BigDecimal("75.00"),
            new BigDecimal("60.00"),
            false,
            clientAccount
        );
        Inventory secondInventory = createAndSaveInventory(secondProduct, clientAccount);

        SaleOrder secondOrder = createSaleOrder(
            "2025/0002",
            DEFAULT_DATE,
            OrderStatus.DRAFTED,
            DEFAULT_ORDER_TYPE,
            new BigDecimal("750.00"),
            new BigDecimal("750.00"),
            false,
            false,
            clientAccount,
            customer
        );
        SaleOrderItem secondItem = createSaleOrderItem(secondProduct, new BigDecimal("5"), new BigDecimal("75.00"), secondOrder);
        secondOrder.addOrderItem(secondItem);
        secondOrder = saleOrderRepository.saveAndFlush(secondOrder);

        ZonedDateTime beforeSecondConfirm = ZonedDateTime.now();

        // Confirm second order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/confirm", secondOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.CONFIRMED.toString()));

        ZonedDateTime afterSecondConfirm = ZonedDateTime.now();

        // Verify second order has 24-hour expiration
        SaleOrder confirmedSecondOrder = saleOrderRepository.findById(secondOrder.getId()).orElse(null);
        assertThat(confirmedSecondOrder).isNotNull();
        assertThat(confirmedSecondOrder.getReservationExpiresAt()).isNotNull();

        ZonedDateTime expectedSecondMinExpiration = beforeSecondConfirm.plusHours(24);
        ZonedDateTime expectedSecondMaxExpiration = afterSecondConfirm.plusHours(24);

        assertThat(confirmedSecondOrder.getReservationExpiresAt())
            .isAfterOrEqualTo(expectedSecondMinExpiration)
            .isBeforeOrEqualTo(expectedSecondMaxExpiration);

        long secondOrderHoursDifference = java.time.Duration.between(
            ZonedDateTime.now(),
            confirmedSecondOrder.getReservationExpiresAt()
        ).toHours();
        assertThat(secondOrderHoursDifference).isBetween(23L, 25L); // Should be approximately 24 hours
    }

    @Test
    @Transactional
    void confirmOrder_InsufficientInventory_ShouldFail() throws Exception {
        setupSecurityContext();

        // Create test data with limited inventory
        product = createAndSaveProduct(clientAccount);
        inventory = createAndSaveInventory(product, new BigDecimal("5"), new BigDecimal("5"), clientAccount);

        // Create order that requires more than available
        saleOrder = createSaleOrder(clientAccount, customer);
        SaleOrderItem item = createSaleOrderItem(product, BigDecimal.TEN, new BigDecimal("100.00"), saleOrder);
        saleOrder.addOrderItem(item);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Attempt to confirm should fail
        ResultActions resultActions = restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/confirm", saleOrder.getId()))
            .andExpect(status().isBadRequest());
        resultActions
            .andExpect(jsonPath("$.errorKey").value(ErrorConstants.INSUFFICIENT_INVENTORY))
            .andExpect(jsonPath("$.message").value(containsString("has only 5 units available")));

        // Verify order status remains DRAFTED
        SaleOrder unchangedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(unchangedOrder).isNotNull();
        assertThat(unchangedOrder.getStatus()).isEqualTo(OrderStatus.DRAFTED);

        // Verify inventory unchanged
        Inventory unchangedInventory = inventoryRepository.findById(inventory.getId()).orElse(null);
        assertThat(unchangedInventory).isNotNull();
        assertThat(unchangedInventory.getAvailableQuantity()).isEqualByComparingTo(new BigDecimal("5"));
    }

    @Test
    @Transactional
    void confirmOrder_ProductNotInInventory_ShouldFail() throws Exception {
        setupSecurityContext();

        // Create product without inventory
        product = createAndSaveProduct(clientAccount);
        // Note: Not creating inventory for this product

        saleOrder = createSaleOrder(clientAccount, customer);
        SaleOrderItem item = createSaleOrderItem(product, BigDecimal.TEN, new BigDecimal("100.00"), saleOrder);
        saleOrder.addOrderItem(item);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Attempt to confirm should fail
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/confirm", saleOrder.getId()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("not found in inventory")));

        // Verify order status remains DRAFTED
        SaleOrder unchangedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(unchangedOrder).isNotNull();
        assertThat(unchangedOrder.getStatus()).isEqualTo(OrderStatus.DRAFTED);
    }

    @Test
    @Transactional
    void confirmOrder_AlreadyConfirmed_ShouldFail() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.CONFIRMED);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Attempt to confirm already confirmed order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/confirm", saleOrder.getId()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Cannot confirm order with status CONFIRMED")));
    }

    @Test
    @Transactional
    void confirmOrder_OrderFromDifferentClientAccount_ShouldFail() throws Exception {
        setupSecurityContext();

        // Create another client account
        ClientAccount otherClientAccount = createAndSaveClientAccount("Other Company", "other@company.com", "0676841437", 24);

        createCompleteTestOrder();
        saleOrder.setClientAccount(otherClientAccount); // Set to different client account
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Attempt to confirm should fail (access denied)
        restSaleOrderMockMvc.perform(post(ENTITY_API_URL_ID + "/confirm", saleOrder.getId())).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void confirmOrder_NonExistentOrder_ShouldFail() throws Exception {
        setupSecurityContext();

        // Attempt to confirm non-existent order
        restSaleOrderMockMvc.perform(post(ENTITY_API_URL_ID + "/confirm", 99999L)).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void confirmOrder_MultipleItems_ShouldReserveAllInventory() throws Exception {
        setupSecurityContext();

        // Create multiple products and inventories
        Product product1 = createAndSaveProduct(
            "Product 1",
            "PROD-001",
            new BigDecimal("100.00"),
            new BigDecimal("80.00"),
            false,
            clientAccount
        );
        Product product2 = createAndSaveProduct(
            "Product 2",
            "PROD-002",
            new BigDecimal("200.00"),
            new BigDecimal("160.00"),
            false,
            clientAccount
        );

        Inventory inventory1 = createAndSaveInventory(product1, new BigDecimal("50"), new BigDecimal("50"), clientAccount);
        Inventory inventory2 = createAndSaveInventory(product2, new BigDecimal("30"), new BigDecimal("30"), clientAccount);

        // Create order with multiple items
        saleOrder = createSaleOrder(
            DEFAULT_REFERENCE,
            DEFAULT_DATE,
            OrderStatus.DRAFTED,
            DEFAULT_ORDER_TYPE,
            new BigDecimal("1500.00"),
            new BigDecimal("1500.00"),
            false,
            false,
            clientAccount,
            customer
        );

        SaleOrderItem item1 = createSaleOrderItem(product1, new BigDecimal("5"), new BigDecimal("100.00"), saleOrder);
        SaleOrderItem item2 = createSaleOrderItem(product2, new BigDecimal("5"), new BigDecimal("200.00"), saleOrder);

        saleOrder.addOrderItem(item1);
        saleOrder.addOrderItem(item2);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Confirm the order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/confirm", saleOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.CONFIRMED.toString()));

        // Verify both inventories were reserved
        Inventory updatedInventory1 = inventoryRepository.findById(inventory1.getId()).orElse(null);
        assertThat(updatedInventory1).isNotNull();
        assertThat(updatedInventory1.getAvailableQuantity()).isEqualByComparingTo(new BigDecimal("45")); // 50 - 5

        Inventory updatedInventory2 = inventoryRepository.findById(inventory2.getId()).orElse(null);
        assertThat(updatedInventory2).isNotNull();
        assertThat(updatedInventory2.getAvailableQuantity()).isEqualByComparingTo(new BigDecimal("25")); // 30 - 5
    }

    @Test
    @Transactional
    void confirmOrder_PartialInventoryFailure_ShouldRollback() throws Exception {
        setupSecurityContext();

        // Create two products, one with sufficient inventory, one without
        Product product1 = createAndSaveProduct(
            "Product 1",
            "PROD-001",
            new BigDecimal("100.00"),
            new BigDecimal("80.00"),
            false,
            clientAccount
        );
        Product product2 = createAndSaveProduct(
            "Product 2",
            "PROD-002",
            new BigDecimal("200.00"),
            new BigDecimal("160.00"),
            false,
            clientAccount
        );

        // Product 1 has sufficient inventory, Product 2 has insufficient inventory
        Inventory inventory1 = createAndSaveInventory(product1, new BigDecimal("50"), new BigDecimal("50"), clientAccount);
        Inventory inventory2 = createAndSaveInventory(product2, new BigDecimal("2"), new BigDecimal("2"), clientAccount);

        // Create order requesting more than available for product2
        saleOrder = createSaleOrder(
            DEFAULT_REFERENCE,
            DEFAULT_DATE,
            OrderStatus.DRAFTED,
            DEFAULT_ORDER_TYPE,
            new BigDecimal("1500.00"),
            new BigDecimal("1500.00"),
            false,
            false,
            clientAccount,
            customer
        );

        SaleOrderItem item1 = createSaleOrderItem(product1, new BigDecimal("5"), new BigDecimal("100.00"), saleOrder); // This is fine
        SaleOrderItem item2 = createSaleOrderItem(product2, new BigDecimal("5"), new BigDecimal("200.00"), saleOrder); // This exceeds available

        saleOrder.addOrderItem(item1);
        saleOrder.addOrderItem(item2);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Attempt to confirm should fail
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/confirm", saleOrder.getId()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("has only 2 units available")));

        // Verify NO inventory was reserved (transaction rollback)
        Inventory unchangedInventory1 = inventoryRepository.findById(inventory1.getId()).orElse(null);
        assertThat(unchangedInventory1).isNotNull();
        assertThat(unchangedInventory1.getAvailableQuantity()).isEqualByComparingTo(new BigDecimal("50"));

        Inventory unchangedInventory2 = inventoryRepository.findById(inventory2.getId()).orElse(null);
        assertThat(unchangedInventory2).isNotNull();
        assertThat(unchangedInventory2.getAvailableQuantity()).isEqualByComparingTo(new BigDecimal("2"));

        // Verify order status remains DRAFTED
        SaleOrder unchangedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(unchangedOrder).isNotNull();
        assertThat(unchangedOrder.getStatus()).isEqualTo(OrderStatus.DRAFTED);
    }

    @Test
    @Transactional
    void confirmOrder_ReservationExpirationSet_ShouldHaveCorrectTimeout() throws Exception {
        setupSecurityContext();

        // Update client account with specific timeout
        clientAccount.setReservationTimeoutHours(48); // 48 hours
        clientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        createCompleteTestOrder();
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        ZonedDateTime beforeConfirm = ZonedDateTime.now();

        // Confirm the order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/confirm", saleOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.CONFIRMED.toString()))
            .andExpect(jsonPath("$.reservationExpiresAt").exists());

        ZonedDateTime afterConfirm = ZonedDateTime.now();

        // Verify reservation expiration is set correctly (48 hours from now)
        SaleOrder confirmedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(confirmedOrder).isNotNull();
        assertThat(confirmedOrder.getReservationExpiresAt()).isNotNull();

        ZonedDateTime expectedMin = beforeConfirm.plusHours(48);
        ZonedDateTime expectedMax = afterConfirm.plusHours(48);

        assertThat(confirmedOrder.getReservationExpiresAt()).isAfterOrEqualTo(expectedMin).isBeforeOrEqualTo(expectedMax);
    }

    @Test
    @Transactional
    void cancelOrder() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.CONFIRMED);
        saleOrder.setReservationExpiresAt(ZonedDateTime.now().plusHours(24));
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Reserve inventory
        inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(BigDecimal.TEN));
        inventoryRepository.saveAndFlush(inventory);

        CancelOrderDTO cancelRequest = new CancelOrderDTO();
        cancelRequest.setReason("Customer requested cancellation");
        cancelRequest.setNotes("Order cancelled by test");

        // Cancel the order
        restSaleOrderMockMvc
            .perform(
                post(ENTITY_API_URL_ID + "/cancel", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(cancelRequest))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.status").value(OrderStatus.CANCELLED.toString()));

        // Validate inventory was released
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElse(null);
        assertThat(updatedInventory).isNotNull();
    }

    @Test
    @Transactional
    void validateOrderAvailability() throws Exception {
        setupSecurityContext();

        product = createAndSaveProduct(clientAccount);
        inventory = createAndSaveInventory(product, clientAccount);

        // Create order items for validation
        OrderItemDTO orderItem = new OrderItemDTO();
        orderItem.setProductId(product.getId());
        orderItem.setQuantity(BigDecimal.valueOf(5));

        List<OrderItemDTO> items = List.of(orderItem);

        // Validate availability
        restSaleOrderMockMvc
            .perform(
                post(ENTITY_API_URL + "/validate-availability")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(items))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @Transactional
    void validateOrderAvailability_InsufficientStock() throws Exception {
        setupSecurityContext();

        product = createAndSaveProduct(clientAccount);
        inventory = createAndSaveInventory(product, clientAccount);

        // Create order items that exceed available stock
        OrderItemDTO orderItem = new OrderItemDTO();
        orderItem.setProductId(product.getId());
        orderItem.setQuantity(BigDecimal.valueOf(1000)); // More than available

        List<OrderItemDTO> items = List.of(orderItem);

        // Validate availability
        restSaleOrderMockMvc
            .perform(
                post(ENTITY_API_URL + "/validate-availability")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(items))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.valid").value(false))
            .andExpect(jsonPath("$.errors").isNotEmpty())
            .andExpect(jsonPath("$.errors[0].message").value("Insufficient inventory"));
    }

    @Test
    @Transactional
    void getOrderStatistics() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrderRepository.saveAndFlush(saleOrder);

        // Get statistics
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL + "/stats"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.totalOrders").value(greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.draftedOrders").value(greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.deliveryOrders").exists())
            .andExpect(jsonPath("$.pickupOrders").exists());
    }

    @Test
    @Transactional
    void searchSaleOrders() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setReference("SO-TEST-2024-001");
        saleOrder.setStatus(OrderStatus.DRAFTED);
        saleOrder.setOrderType(OrderType.STORE_PICKUP);
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search orders using criteria filtering via findAll endpoint
        restSaleOrderMockMvc
            .perform(
                get(ENTITY_API_URL)
                    .param("reference.contains", "TEST")
                    .param("status.equals", "DRAFTED")
                    .param("orderType.equals", "STORE_PICKUP")
                    .param("sort", "id,desc")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].reference").value(containsString("TEST")))
            .andExpect(jsonPath("$[0].status").value("DRAFTED"))
            .andExpect(jsonPath("$[0].orderType").value("STORE_PICKUP"));
    }

    @Test
    @Transactional
    void searchSaleOrders_ByReference() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setReference("SO-TEST-2024-001");
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search by reference using proper criteria parameters
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL).param("reference.contains", "TEST").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[*].reference").value(hasItem(containsString("TEST"))));
    }

    @Test
    @Transactional
    void searchSaleOrders_ByStatus() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.CONFIRMED);
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search by status
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL).param("status.equals", "CONFIRMED").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$[*].status").value(hasItem("CONFIRMED")));
    }

    @Test
    @Transactional
    void searchSaleOrders_ByOrderType() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setOrderType(OrderType.DELIVERY);
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search by order type
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL).param("orderType.equals", "DELIVERY").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$[*].orderType").value(hasItem("DELIVERY")));
    }

    @Test
    @Transactional
    void searchSaleOrders_ByCustomerName() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        customer.setFirstName("John");
        customer.setLastName("TestCustomer");
        customerRepository.saveAndFlush(customer);
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search by customer name using customerName criteria
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL).param("customerName.contains", "john").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Transactional
    void searchSaleOrders_ByCustomerId() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search by customer ID
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL).param("customerId.equals", customer.getId().toString()).param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[*].customer.id").value(hasItem(customer.getId().intValue())));
    }

    @Test
    @Transactional
    void searchSaleOrders_ByDateRange() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        ZonedDateTime testDate = ZonedDateTime.now().minusDays(1);
        saleOrder.setDate(testDate);
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search by date range using date criteria
        String fromDate = testDate.minusHours(1).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        String toDate = testDate.plusHours(1).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);

        restSaleOrderMockMvc
            .perform(
                get(ENTITY_API_URL)
                    .param("date.greaterThanOrEqual", fromDate)
                    .param("date.lessThanOrEqual", toDate)
                    .param("sort", "id,desc")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Transactional
    void searchSaleOrders_ByTotalRange() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setTotal(new BigDecimal("500.00"));
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search by total amount range
        restSaleOrderMockMvc
            .perform(
                get(ENTITY_API_URL).param("total.greaterThanOrEqual", "400").param("total.lessThanOrEqual", "600").param("sort", "id,desc")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Transactional
    void searchSaleOrders_MultipleCriteria() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.CONFIRMED);
        saleOrder.setOrderType(OrderType.STORE_PICKUP);
        saleOrder.setReference("SO-MULTI-TEST-001");
        saleOrder.setTotal(new BigDecimal("750.00"));
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search with multiple criteria
        restSaleOrderMockMvc
            .perform(
                get(ENTITY_API_URL)
                    .param("status.equals", "CONFIRMED")
                    .param("orderType.equals", "STORE_PICKUP")
                    .param("reference.contains", "MULTI")
                    .param("total.greaterThan", "500")
                    .param("sort", "id,desc")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$[*].status").value(hasItem("CONFIRMED")))
            .andExpect(jsonPath("$[*].orderType").value(hasItem("STORE_PICKUP")))
            .andExpect(jsonPath("$[*].reference").value(hasItem(containsString("MULTI"))));
    }

    @Test
    @Transactional
    void searchSaleOrders_EmptyResult() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search for non-existent reference
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL).param("reference.contains", "NONEXISTENT").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    void searchSaleOrders_ByMultipleStatuses() throws Exception {
        setupSecurityContext();

        // Create first order with DRAFTED status
        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.DRAFTED);
        saleOrder.setReference("SO-DRAFTED-001");
        saleOrderRepository.saveAndFlush(saleOrder);

        // Create a completely separate order with CONFIRMED status
        SaleOrder confirmedOrder = createCompleteTestOrder();
        confirmedOrder.setStatus(OrderStatus.CONFIRMED);
        confirmedOrder.setReference("SO-CONFIRMED-002");
        saleOrderRepository.saveAndFlush(confirmedOrder);

        // Search by multiple statuses using 'in' filter
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL).param("status.in", "DRAFTED,CONFIRMED").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)));
    }

    @Test
    @Transactional
    void searchSaleOrders_ExcludeStatus() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.CANCELLED);
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search excluding cancelled orders
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL).param("status.notEquals", "CANCELLED").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Transactional
    void searchSaleOrders_ByPaymentStatus() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search by payment status
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL).param("paymentStatus.equals", "PENDING").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Transactional
    void searchSaleOrders_OrdersWithoutPayment() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search for orders without payment
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL).param("hasPayment.equals", "false").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Transactional
    void searchSaleOrders_OrdersWithShipment() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search for orders with shipment
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL).param("hasShipment.equals", "true").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Transactional
    void cancelOrder_AlreadyCancelled_ShouldFail() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.CANCELLED);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        CancelOrderDTO cancelRequest = new CancelOrderDTO();
        cancelRequest.setReason("Already cancelled order");

        // Attempt to cancel already cancelled order should fail
        restSaleOrderMockMvc
            .perform(
                post(ENTITY_API_URL_ID + "/cancel", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(cancelRequest))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorKey").value(containsString(ErrorConstants.INVALID_ORDER_TRANSITION)));
    }

    @Test
    @Transactional
    void cancelOrder_CompletedOrder_ShouldFail() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.COMPLETED);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        CancelOrderDTO cancelRequest = new CancelOrderDTO();
        cancelRequest.setReason("Try to cancel completed order");

        // Attempt to cancel completed order should fail
        restSaleOrderMockMvc
            .perform(
                post(ENTITY_API_URL_ID + "/cancel", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(cancelRequest))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Cannot cancel completed order")));
    }

    @Test
    @Transactional
    void validateOrderAvailability_WithMixedResults() throws Exception {
        setupSecurityContext();

        // Create two products, one with sufficient stock, one without
        Product product1 = createAndSaveProduct(clientAccount);
        Product product2 = createAndSaveProduct(clientAccount);
        product2.setName("Product 2");
        productRepository.saveAndFlush(product2);

        Inventory inventory1 = createAndSaveInventory(product1, new BigDecimal("10"), new BigDecimal("10"), clientAccount);
        Inventory inventory2 = createAndSaveInventory(product2, new BigDecimal("2"), new BigDecimal("2"), clientAccount);

        // Create order items
        OrderItemDTO orderItem1 = new OrderItemDTO();
        orderItem1.setProductId(product1.getId());
        orderItem1.setQuantity(BigDecimal.valueOf(5)); // Available

        OrderItemDTO orderItem2 = new OrderItemDTO();
        orderItem2.setProductId(product2.getId());
        orderItem2.setQuantity(BigDecimal.valueOf(5)); // Not enough

        List<OrderItemDTO> items = List.of(orderItem1, orderItem2);

        // Validate availability
        restSaleOrderMockMvc
            .perform(
                post(ENTITY_API_URL + "/validate-availability")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(items))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.valid").value(false))
            .andExpect(jsonPath("$.errors").isNotEmpty())
            .andExpect(jsonPath("$.errors[0].productId").value(product2.getId()));
    }

    @Test
    @Transactional
    void getOrderStatistics_WithNoOrders() throws Exception {
        setupSecurityContext();

        // Get statistics when no orders exist
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL + "/stats"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.totalOrders").value(0))
            .andExpect(jsonPath("$.draftedOrders").value(0))
            .andExpect(jsonPath("$.confirmedOrders").value(0))
            .andExpect(jsonPath("$.deliveryOrders").value(0))
            .andExpect(jsonPath("$.pickupOrders").value(0));
    }

    @Test
    @Transactional
    void getOrderStatistics_WithMultipleOrderTypes() throws Exception {
        setupSecurityContext();

        // Create multiple orders with different statuses and types
        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.CONFIRMED);
        saleOrder.setOrderType(OrderType.DELIVERY);
        saleOrderRepository.saveAndFlush(saleOrder);

        // Create another order
        SaleOrder order2 = createSaleOrder(clientAccount, customer);
        order2.setStatus(OrderStatus.DRAFTED);
        order2.setOrderType(OrderType.STORE_PICKUP);
        order2.setReference("SO-TEST-2024-002");
        saleOrderRepository.saveAndFlush(order2);

        // Get statistics
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL + "/stats"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.totalOrders").value(greaterThanOrEqualTo(2)))
            .andExpect(jsonPath("$.draftedOrders").value(greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.confirmedOrders").value(greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.deliveryOrders").value(greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.pickupOrders").value(greaterThanOrEqualTo(1)));
    }

    @Test
    @Transactional
    void completeOrder_WithValidShippedOrder_ShouldSucceed() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setOrderType(OrderType.DELIVERY);
        saleOrder.setStatus(OrderStatus.SHIPPED);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Reserve inventory to simulate previous confirmation
        BigDecimal originalAvailable = inventory.getAvailableQuantity();
        BigDecimal orderQuantity = BigDecimal.TEN; // From createCompleteTestOrder
        inventory.setAvailableQuantity(originalAvailable.subtract(orderQuantity));
        inventoryRepository.saveAndFlush(inventory);

        em.detach(inventory);
        // Complete the order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/complete", saleOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.status").value(OrderStatus.COMPLETED.toString()))
            .andExpect(jsonPath("$.id").value(saleOrder.getId().intValue()));

        // Verify order status change
        SaleOrder completedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(completedOrder).isNotNull();
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);

        // Verify inventory was finally consumed (total quantity decreased)
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElse(null);
        assertThat(updatedInventory).isNotNull();
        assertThat(updatedInventory.getQuantity()).isEqualByComparingTo(inventory.getQuantity().subtract(orderQuantity));
        // Available quantity should remain the same (already reduced during confirmation)
        assertThat(updatedInventory.getAvailableQuantity()).isEqualByComparingTo(originalAvailable.subtract(orderQuantity));
    }

    @Test
    @Transactional
    void completeOrder_WithDraftedOrder_ShouldFail() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.DRAFTED);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Attempt to complete drafted order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/complete", saleOrder.getId()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Order must be CONFIRMED")));

        // Verify order status remains unchanged
        SaleOrder unchangedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(unchangedOrder).isNotNull();
        assertThat(unchangedOrder.getStatus()).isEqualTo(OrderStatus.DRAFTED);
    }

    @Test
    @Transactional
    void completeOrder_WithAlreadyCompletedOrder_ShouldFail() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.COMPLETED);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Attempt to complete already completed order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/complete", saleOrder.getId()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Order must be CONFIRMED")));

        // Verify order status remains unchanged
        SaleOrder unchangedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(unchangedOrder).isNotNull();
        assertThat(unchangedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @Transactional
    void completeOrder_WithCancelledOrder_ShouldFail() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.CANCELLED);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Attempt to complete cancelled order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/complete", saleOrder.getId()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Order must be CONFIRMED")));

        // Verify order status remains unchanged
        SaleOrder unchangedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(unchangedOrder).isNotNull();
        assertThat(unchangedOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @Transactional
    void completeOrder_OrderFromDifferentClientAccount_ShouldFail() throws Exception {
        setupSecurityContext();

        // Create another client account
        ClientAccount otherClientAccount = createAndSaveClientAccount("Other Company", "other@company.com", "0676841437", 24);

        createCompleteTestOrder();
        saleOrder.setStatus(OrderStatus.SHIPPED);
        saleOrder.setClientAccount(otherClientAccount); // Set to different client account
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Attempt to complete should fail (access denied)
        restSaleOrderMockMvc.perform(post(ENTITY_API_URL_ID + "/complete", saleOrder.getId())).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void completeOrder_NonExistentOrder_ShouldFail() throws Exception {
        setupSecurityContext();

        // Attempt to complete non-existent order
        restSaleOrderMockMvc.perform(post(ENTITY_API_URL_ID + "/complete", 99999L)).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void completeOrder_WithMultipleItems_ShouldCompleteAllInventoryTransactions() throws Exception {
        setupSecurityContext();

        // Create multiple products and inventories
        Product product1 = createAndSaveProduct(
            "Product 1",
            "PROD-001",
            new BigDecimal("100.00"),
            new BigDecimal("80.00"),
            false,
            clientAccount
        );
        Product product2 = createAndSaveProduct(
            "Product 2",
            "PROD-002",
            new BigDecimal("200.00"),
            new BigDecimal("160.00"),
            false,
            clientAccount
        );

        Inventory inventory1 = createAndSaveInventory(product1, new BigDecimal("50"), new BigDecimal("40"), clientAccount);
        Inventory inventory2 = createAndSaveInventory(product2, new BigDecimal("30"), new BigDecimal("25"), clientAccount);

        // Create order with multiple items
        saleOrder = createSaleOrder(
            DEFAULT_REFERENCE,
            DEFAULT_DATE,
            OrderStatus.SHIPPED, // Set to shipped so it can be completed
            UPDATED_ORDER_TYPE,
            new BigDecimal("1500.00"),
            new BigDecimal("1500.00"),
            false,
            false,
            clientAccount,
            customer
        );

        BigDecimal quantity1 = new BigDecimal("5");
        BigDecimal quantity2 = new BigDecimal("3");

        SaleOrderItem item1 = createSaleOrderItem(product1, quantity1, new BigDecimal("100.00"), saleOrder);
        SaleOrderItem item2 = createSaleOrderItem(product2, quantity2, new BigDecimal("200.00"), saleOrder);

        saleOrder.addOrderItem(item1);
        saleOrder.addOrderItem(item2);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Store original quantities for verification
        BigDecimal originalQuantity1 = inventory1.getQuantity();
        BigDecimal originalQuantity2 = inventory2.getQuantity();

        // Complete the order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/complete", saleOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(OrderStatus.COMPLETED.toString()));

        // Verify both inventories were updated (total quantities decreased)
        Inventory updatedInventory1 = inventoryRepository.findById(inventory1.getId()).orElse(null);
        assertThat(updatedInventory1).isNotNull();
        assertThat(updatedInventory1.getQuantity()).isEqualByComparingTo(originalQuantity1.subtract(quantity1));

        Inventory updatedInventory2 = inventoryRepository.findById(inventory2.getId()).orElse(null);
        assertThat(updatedInventory2).isNotNull();
        assertThat(updatedInventory2.getQuantity()).isEqualByComparingTo(originalQuantity2.subtract(quantity2));
    }

    @Test
    @Transactional
    void completeOrder_WithDeliveryOrderType_ShouldSucceed() throws Exception {
        setupSecurityContext();

        createCompleteTestOrder();
        saleOrder.setOrderType(OrderType.DELIVERY);
        saleOrder.setStatus(OrderStatus.SHIPPED);
        saleOrder.setShippingCost(new BigDecimal("25.00"));
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Reserve inventory
        BigDecimal originalQuantity = inventory.getQuantity();
        BigDecimal orderQuantity = BigDecimal.TEN;
        inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(orderQuantity));
        inventoryRepository.saveAndFlush(inventory);

        // Complete the order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/complete", saleOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.status").value(OrderStatus.COMPLETED.toString()))
            .andExpect(jsonPath("$.orderType").value(OrderType.DELIVERY.toString()));

        // Verify order completion
        SaleOrder completedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(completedOrder).isNotNull();
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(completedOrder.getOrderType()).isEqualTo(OrderType.DELIVERY);

        // Verify inventory transaction
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElse(null);
        assertThat(updatedInventory).isNotNull();
        assertThat(updatedInventory.getQuantity()).isEqualByComparingTo(originalQuantity.subtract(orderQuantity));
    }
}
