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
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the enhanced {@link SaleOrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class SaleOrderResourceIT {

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

    private SaleOrder saleOrder;
    private ClientAccount clientAccount;
    private Customer customer;
    private Product product;
    private Inventory inventory;

    @BeforeEach
    void initTest() {
        // Create test data
        createTestClientAccount();
        createTestCustomer();
        //        createTestProduct();
        //        createTestInventory();
        //        createTestSaleOrder();
    }

    @Test
    @Transactional
    void createSaleOrder() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        int databaseSizeBeforeCreate = saleOrderRepository.findAll().size();

        product = new Product();
        product.setName("Test Product");
        product.setCode("TEST-001");
        product.applyTva(false);
        product.setSellingPrice(new BigDecimal("100.00"));
        product.setCostPrice(new BigDecimal("80.00"));
        product.setCategory(ProductCategory.ELECTRONICS);
        product.setClientAccount(clientAccount);
        product = productRepository.saveAndFlush(product);

        inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(new BigDecimal("100"));
        inventory.setAvailableQuantity(new BigDecimal("100"));
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventory.setClientAccount(clientAccount);
        inventory = inventoryRepository.saveAndFlush(inventory);

        saleOrder = new SaleOrder();
        saleOrder.setReference(DEFAULT_REFERENCE);
        saleOrder.setDate(DEFAULT_DATE);
        saleOrder.setStatus(DEFAULT_STATUS);
        saleOrder.setTvaApplied(false);
        saleOrder.setStampApplied(false);
        saleOrder.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrder.setSubTotal(DEFAULT_TOTAL);
        saleOrder.setTotal(DEFAULT_TOTAL);
        saleOrder.setClientAccount(clientAccount);
        saleOrder.setCustomer(customer);

        // Add order items
        SaleOrderItem item = new SaleOrderItem();
        item.setProduct(product);
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));
        item.setTotal(new BigDecimal("1000.00"));
        item.setSaleOrder(saleOrder);

        saleOrder.addOrderItem(item);

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
        setSecurityContextWithClientAccountId(clientAccount.getId());
        int databaseSizeBeforeCreate = saleOrderRepository.findAll().size();

        product = new Product();
        product.setName("Test Product");
        product.setCode("TEST-001");
        product.applyTva(false);
        product.setSellingPrice(new BigDecimal("100.00"));
        product.setCostPrice(new BigDecimal("80.00"));
        product.setCategory(ProductCategory.ELECTRONICS);
        product.setClientAccount(clientAccount);
        product = productRepository.saveAndFlush(product);

        inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(new BigDecimal("100"));
        inventory.setAvailableQuantity(new BigDecimal("100"));
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventory.setClientAccount(clientAccount);
        inventory = inventoryRepository.saveAndFlush(inventory);

        saleOrder = new SaleOrder();
        saleOrder.setReference(DEFAULT_REFERENCE);
        saleOrder.setDate(DEFAULT_DATE);
        saleOrder.setStatus(DEFAULT_STATUS);
        saleOrder.setTvaApplied(true);
        saleOrder.setStampApplied(true);
        saleOrder.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrder.setSubTotal(DEFAULT_TOTAL);
        saleOrder.setTotal(DEFAULT_TOTAL);
        saleOrder.setClientAccount(clientAccount);
        saleOrder.setCustomer(customer);

        // Add order items
        SaleOrderItem item = new SaleOrderItem();
        item.setProduct(product);
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));
        item.setTotal(new BigDecimal("1000.00"));
        item.setSaleOrder(saleOrder);

        saleOrder.addOrderItem(item);

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

    // Additional test cases for SaleOrder create functionality - add these to SaleOrderResourceIT class

    @Test
    @Transactional
    void createSaleOrder_WithNullOrderItems_ShouldThrowException() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
        saleOrderDTO.setReference(DEFAULT_REFERENCE);
        saleOrderDTO.setDate(DEFAULT_DATE);
        saleOrderDTO.setStatus(DEFAULT_STATUS);
        saleOrderDTO.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrderDTO.setCustomer(customerMapper.toDto(customer));
        saleOrderDTO.setOrderItems(null); // Null order items

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString(ErrorConstants.REQUIRED_ORDER_ITEMS)));
    }

    @Test
    @Transactional
    void createSaleOrder_WithEmptyOrderItems_ShouldThrowException() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
        saleOrderDTO.setReference(DEFAULT_REFERENCE);
        saleOrderDTO.setDate(DEFAULT_DATE);
        saleOrderDTO.setStatus(DEFAULT_STATUS);
        saleOrderDTO.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrderDTO.setCustomer(customerMapper.toDto(customer));
        saleOrderDTO.setOrderItems(new HashSet<>()); // Empty order items

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString(ErrorConstants.REQUIRED_ORDER_ITEMS)));
    }

    @Test
    @Transactional
    void createSaleOrder_WithNonExistentCustomer_ShouldThrowException() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        CustomerDTO nonExistentCustomer = new CustomerDTO();
        nonExistentCustomer.setId(99999L); // Non-existent customer ID

        SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
        saleOrderDTO.setReference(DEFAULT_REFERENCE);
        saleOrderDTO.setDate(DEFAULT_DATE);
        saleOrderDTO.setStatus(DEFAULT_STATUS);
        saleOrderDTO.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrderDTO.setCustomer(nonExistentCustomer);
        saleOrderDTO.setOrderItems(createValidOrderItems());

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void createSaleOrder_WithNonExistentProduct_ShouldThrowException() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create order item with non-existent product
        SaleOrderItemDTO item = new SaleOrderItemDTO();
        ProductDTO nonExistentProduct = new ProductDTO();
        nonExistentProduct.setId(99999L); // Non-existent product ID
        item.setProduct(nonExistentProduct);
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
        saleOrderDTO.setReference(DEFAULT_REFERENCE);
        saleOrderDTO.setDate(DEFAULT_DATE);
        saleOrderDTO.setStatus(DEFAULT_STATUS);
        saleOrderDTO.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrderDTO.setCustomer(customerMapper.toDto(customer));
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void createSaleOrder_WithProductFromDifferentClientAccount_ShouldThrowException() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create a product for a different client account
        ClientAccount otherClientAccount = new ClientAccount();
        otherClientAccount.setCompanyName("Other Company");
        otherClientAccount.email("other@company.com");
        otherClientAccount.setPhone("0676841437");
        otherClientAccount.setStatus(AccountStatus.ENABLED);
        otherClientAccount = clientAccountRepository.saveAndFlush(otherClientAccount);

        Product otherProduct = new Product();
        otherProduct.setName("Other Product");
        otherProduct.setCode("OTHER-001");
        otherProduct.applyTva(false);
        otherProduct.setSellingPrice(new BigDecimal("100.00"));
        otherProduct.setCostPrice(new BigDecimal("80.00"));
        otherProduct.setCategory(ProductCategory.ELECTRONICS);
        otherProduct.setClientAccount(otherClientAccount);
        otherProduct = productRepository.saveAndFlush(otherProduct);

        // Create order item with product from different client account
        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(otherProduct));
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
        saleOrderDTO.setReference(DEFAULT_REFERENCE);
        saleOrderDTO.setDate(DEFAULT_DATE);
        saleOrderDTO.setStatus(DEFAULT_STATUS);
        saleOrderDTO.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrderDTO.setCustomer(customerMapper.toDto(customer));
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void createSaleOrder_WithProductWithoutPrice_ShouldThrowException() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create product without selling price
        Product productWithoutPrice = new Product();
        productWithoutPrice.setName("No Price Product");
        productWithoutPrice.setCode("NOPRICE-001");
        productWithoutPrice.applyTva(false);
        productWithoutPrice.setSellingPrice(null); // No selling price
        productWithoutPrice.setCostPrice(new BigDecimal("80.00"));
        productWithoutPrice.setCategory(ProductCategory.ELECTRONICS);
        productWithoutPrice.setClientAccount(clientAccount);
        productWithoutPrice = productRepository.saveAndFlush(productWithoutPrice);

        // Create order item without unit price (should use product price which is null)
        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(productWithoutPrice));
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(null); // No unit price, should use product price

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
        saleOrderDTO.setReference(DEFAULT_REFERENCE);
        saleOrderDTO.setDate(DEFAULT_DATE);
        saleOrderDTO.setStatus(DEFAULT_STATUS);
        saleOrderDTO.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrderDTO.setCustomer(customerMapper.toDto(customer));
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString(ErrorConstants.REQUIRED_UNIT_PRICE)));
    }

    @Test
    @Transactional
    void createSaleOrder_WithZeroQuantity_ShouldSucceed() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        int databaseSizeBeforeCreate = saleOrderRepository.findAll().size();

        createTestProduct();

        // Create order item with zero quantity
        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(product));
        item.setQuantity(BigDecimal.ZERO); // Zero quantity
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
        saleOrderDTO.setReference(DEFAULT_REFERENCE);
        saleOrderDTO.setDate(DEFAULT_DATE);
        saleOrderDTO.setStatus(DEFAULT_STATUS);
        saleOrderDTO.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrderDTO.setSubTotal(BigDecimal.ZERO);
        saleOrderDTO.setTotal(BigDecimal.ZERO);
        saleOrderDTO.setCustomer(customerMapper.toDto(customer));
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.total").value(0.0));

        List<SaleOrder> saleOrderList = saleOrderRepository.findAll();
        assertThat(saleOrderList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void createSaleOrder_WithNegativeQuantity_ShouldSucceed() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        createTestProduct();

        // Create order item with negative quantity
        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(product));
        item.setQuantity(new BigDecimal("-5")); // Negative quantity
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
        saleOrderDTO.setReference(DEFAULT_REFERENCE);
        saleOrderDTO.setDate(DEFAULT_DATE);
        saleOrderDTO.setStatus(DEFAULT_STATUS);
        saleOrderDTO.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrderDTO.setSubTotal(new BigDecimal("-500.00"));
        saleOrderDTO.setTotal(new BigDecimal("-500.00"));
        saleOrderDTO.setCustomer(customerMapper.toDto(customer));
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.total").value(-500.0));
    }

    @Test
    @Transactional
    void createSaleOrder_WithMultipleItemsSameProduct_ShouldSucceed() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        int databaseSizeBeforeCreate = saleOrderRepository.findAll().size();

        createTestProduct();

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

        SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
        saleOrderDTO.setReference(DEFAULT_REFERENCE);
        saleOrderDTO.setDate(DEFAULT_DATE);
        saleOrderDTO.setStatus(DEFAULT_STATUS);
        saleOrderDTO.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrderDTO.setSubTotal(new BigDecimal("860.00")); // 500 + 360
        saleOrderDTO.setTotal(new BigDecimal("860.00"));
        saleOrderDTO.setCustomer(customerMapper.toDto(customer));
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
        setSecurityContextWithClientAccountId(clientAccount.getId());

        createTestProduct();

        // Create order item with very large quantity and price
        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(product));
        item.setQuantity(new BigDecimal("999999999.99"));
        item.setUnitPrice(new BigDecimal("999999999.99"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        BigDecimal expectedTotal = new BigDecimal("999999999.99").multiply(new BigDecimal("999999999.99"));

        SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
        saleOrderDTO.setReference(DEFAULT_REFERENCE);
        saleOrderDTO.setDate(DEFAULT_DATE);
        saleOrderDTO.setStatus(DEFAULT_STATUS);
        saleOrderDTO.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrderDTO.setSubTotal(expectedTotal);
        saleOrderDTO.setTotal(expectedTotal);
        saleOrderDTO.setCustomer(customerMapper.toDto(customer));
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
        setSecurityContextWithClientAccountId(clientAccount.getId());

        createTestProduct();

        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(product));
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
        saleOrderDTO.setReference(DEFAULT_REFERENCE);
        saleOrderDTO.setDate(DEFAULT_DATE);
        saleOrderDTO.setStatus(DEFAULT_STATUS);
        saleOrderDTO.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrderDTO.setSubTotal(new BigDecimal("1000.00"));
        saleOrderDTO.setTotal(new BigDecimal("1000.00"));
        saleOrderDTO.setCustomer(customerMapper.toDto(customer));
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

        createTestProduct();

        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(product));
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);

        SaleOrderDTO saleOrderDTO = new SaleOrderDTO();
        saleOrderDTO.setReference(DEFAULT_REFERENCE);
        saleOrderDTO.setDate(DEFAULT_DATE);
        saleOrderDTO.setStatus(DEFAULT_STATUS);
        saleOrderDTO.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrderDTO.setCustomer(customerMapper.toDto(customer));
        saleOrderDTO.setOrderItems(orderItems);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isForbidden());
    }

    // Helper method to create valid order items
    private Set<SaleOrderItemDTO> createValidOrderItems() {
        if (product == null) {
            createTestProduct();
        }

        SaleOrderItemDTO item = new SaleOrderItemDTO();
        item.setProduct(productMapper.toDto(product));
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));

        Set<SaleOrderItemDTO> orderItems = new HashSet<>();
        orderItems.add(item);
        return orderItems;
    }

    @Test
    @Transactional
    void confirmOrder() throws Exception {
        // Initialize the database
        saleOrderRepository.saveAndFlush(saleOrder);

        int databaseSizeBeforeUpdate = saleOrderRepository.findAll().size();

        // Confirm the order
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/confirm", saleOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.status").value(OrderStatus.CONFIRMED.toString()))
            .andExpect(jsonPath("$.reservationExpiresAt").exists());

        // Validate the SaleOrder in the database
        List<SaleOrder> saleOrderList = saleOrderRepository.findAll();
        assertThat(saleOrderList).hasSize(databaseSizeBeforeUpdate);
        SaleOrder testSaleOrder = saleOrderList.get(saleOrderList.size() - 1);
        assertThat(testSaleOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(testSaleOrder.getReservationExpiresAt()).isNotNull();

        // Validate inventory reservation
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElse(null);
        assertThat(updatedInventory).isNotNull();
        //assertThat(updatedInventory.getReservedQuantity()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @Transactional
    void cancelOrder() throws Exception {
        // Initialize the database with confirmed order
        saleOrder.setStatus(OrderStatus.CONFIRMED);
        saleOrder.setReservationExpiresAt(ZonedDateTime.now().plusHours(24));
        saleOrderRepository.saveAndFlush(saleOrder);

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
    void markOrderPickedUp() throws Exception {
        // Initialize the database with confirmed pickup order
        saleOrder.setStatus(OrderStatus.CONFIRMED);
        saleOrder.setOrderType(OrderType.STORE_PICKUP);
        saleOrderRepository.saveAndFlush(saleOrder);

        // Mark as picked up
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/mark-picked-up", saleOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.status").value(OrderStatus.PICKED_UP.toString()));

        // Validate the SaleOrder in the database
        SaleOrder testSaleOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(testSaleOrder).isNotNull();
        assertThat(testSaleOrder.getStatus()).isEqualTo(OrderStatus.PICKED_UP);
    }

    @Test
    @Transactional
    void validateOrderAvailability() throws Exception {
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
        // Initialize the database with test orders
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
        // Initialize the database
        saleOrderRepository.saveAndFlush(saleOrder);

        // Search orders
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL + "/search").param("query", "TEST").param("status", "DRAFTED").param("orderType", "STORE_PICKUP"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].reference").value(containsString("TEST")));
    }

    // Helper methods to create test data

    private void createTestClientAccount() {
        clientAccount = new ClientAccount();
        clientAccount.setCompanyName("Test Company");
        clientAccount.email("test@company.com");
        clientAccount.setPhone("0676841436");
        clientAccount.setStatus(AccountStatus.ENABLED);
        clientAccount.setDefaultShippingCost(DEFAULT_SHIPPING_COST);
        clientAccount.setReservationTimeoutHours(24);
        clientAccount.setYalidineEnabled(false);
        clientAccount = clientAccountRepository.saveAndFlush(clientAccount);
    }

    private void createTestCustomer() {
        customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setPhone("+213555123456");
        customer.setCreatedByClientAccount(clientAccount);
        customer = customerRepository.saveAndFlush(customer);
    }

    private void createTestProduct() {
        product = new Product();
        product.setName("Test Product");
        product.setCode("TEST-001");
        product.applyTva(false);
        product.setSellingPrice(new BigDecimal("100.00"));
        product.setCostPrice(new BigDecimal("80.00"));
        product.setCategory(ProductCategory.ELECTRONICS);
        product.setClientAccount(clientAccount);
        product = productRepository.saveAndFlush(product);
    }

    private void createTestInventory() {
        inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(new BigDecimal("100"));
        inventory.setAvailableQuantity(new BigDecimal("100"));
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventory.setClientAccount(clientAccount);
        inventory = inventoryRepository.saveAndFlush(inventory);
    }

    private void createTestSaleOrder() {
        saleOrder = new SaleOrder();
        saleOrder.setReference(DEFAULT_REFERENCE);
        saleOrder.setDate(DEFAULT_DATE);
        saleOrder.setStatus(DEFAULT_STATUS);
        saleOrder.setOrderType(DEFAULT_ORDER_TYPE);
        saleOrder.setSubTotal(DEFAULT_TOTAL);
        saleOrder.setTotal(DEFAULT_TOTAL);
        saleOrder.setClientAccount(clientAccount);
        saleOrder.setCustomer(customer);

        // Add order items
        SaleOrderItem item = new SaleOrderItem();
        item.setProduct(product);
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));
        item.setTotal(new BigDecimal("1000.00"));
        item.setSaleOrder(saleOrder);

        saleOrder.addOrderItem(item);
    }
}
