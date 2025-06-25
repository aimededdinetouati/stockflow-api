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
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.mapper.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Comprehensive integration tests for the {@link ShipmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class ShipmentResourceIT {

    // Constants
    private static final String DEFAULT_REFERENCE = "SH-2025-000001";
    private static final String DEFAULT_TRACKING_NUMBER = "TRACK123456789";
    private static final String DEFAULT_CARRIER = "YALIDINE";
    private static final String UPDATED_CARRIER = "DHL";
    private static final ShippingStatus DEFAULT_STATUS = ShippingStatus.PENDING;
    private static final ShippingStatus UPDATED_STATUS = ShippingStatus.SHIPPED;
    private static final BigDecimal DEFAULT_SHIPPING_COST = new BigDecimal("500.00");
    private static final BigDecimal UPDATED_SHIPPING_COST = new BigDecimal("750.00");
    private static final Double DEFAULT_WEIGHT = 2.5;
    private static final Double UPDATED_WEIGHT = 3.5;
    private static final String DEFAULT_NOTES = "Handle with care";
    private static final String UPDATED_NOTES = "Fragile items - priority delivery";

    private static final String ENTITY_API_URL = "/api/shipments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    // Dependencies
    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentMapper shipmentMapper;

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private MockMvc restShipmentMockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    // Test data
    private Shipment shipment;
    private SaleOrder saleOrder;
    private ClientAccount clientAccount;
    private Customer customer;
    private Address address;
    private Product product;
    private Inventory inventory;

    @BeforeEach
    void initTest() {
        clientAccount = createAndSaveClientAccount();
        customer = createAndSaveCustomer(clientAccount);
        address = createAndSaveAddress(customer);
        product = createAndSaveProduct(clientAccount);
        inventory = createAndSaveInventory(product, clientAccount);
        saleOrder = createAndSaveSaleOrder(clientAccount, customer);
    }

    // ===============================
    // HELPER METHODS FOR TEST DATA CREATION
    // ===============================

    private ClientAccount createAndSaveClientAccount() {
        ClientAccount account = new ClientAccount();
        account.setCompanyName("Test Shipping Company");
        account.email("shipping@test.com");
        account.setPhone("0555123456");
        account.setStatus(AccountStatus.ENABLED);
        account.setDefaultShippingCost(DEFAULT_SHIPPING_COST);
        account.setReservationTimeoutHours(24);
        account.setYalidineEnabled(true);
        return clientAccountRepository.saveAndFlush(account);
    }

    private Customer createAndSaveCustomer(ClientAccount clientAccount) {
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setPhone("+213555123456");
        customer.setCreatedByClientAccount(clientAccount);
        return customerRepository.saveAndFlush(customer);
    }

    private Address createAndSaveAddress(Customer customer) {
        Address address = new Address();
        address.setIsDefault(true);
        address.setAddressType(AddressType.PRIMARY);
        address.streetAddress("keda");
        address.setCity("Algiers");
        address.setState("Algiers");
        address.setPostalCode("16000");
        address.setCountry("Algeria");
        address.setCustomer(customer);
        return addressRepository.saveAndFlush(address);
    }

    private Product createAndSaveProduct(ClientAccount clientAccount) {
        Product product = new Product();
        product.setName("Test Product");
        product.setCode("TEST-001");
        product.setSellingPrice(new BigDecimal("100.00"));
        product.setCostPrice(new BigDecimal("80.00"));
        product.setCategory(ProductCategory.ELECTRONICS);
        product.setApplyTva(true);
        product.setClientAccount(clientAccount);
        return productRepository.saveAndFlush(product);
    }

    private Inventory createAndSaveInventory(Product product, ClientAccount clientAccount) {
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(new BigDecimal("100"));
        inventory.setAvailableQuantity(new BigDecimal("100"));
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventory.setClientAccount(clientAccount);
        return inventoryRepository.saveAndFlush(inventory);
    }

    private SaleOrder createAndSaveSaleOrder(ClientAccount clientAccount, Customer customer) {
        SaleOrder order = new SaleOrder();
        order.setReference("SO-TEST-2025-001");
        order.setDate(ZonedDateTime.now());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setOrderType(OrderType.DELIVERY);
        order.setSubTotal(new BigDecimal("1000.00"));
        order.setTotal(new BigDecimal("1000.00"));
        order.setClientAccount(clientAccount);
        order.setCustomer(customer);

        // Add order item
        SaleOrderItem item = new SaleOrderItem();
        item.setProduct(product);
        item.setQuantity(BigDecimal.TEN);
        item.setUnitPrice(new BigDecimal("100.00"));
        item.setTotal(new BigDecimal("1000.00"));
        item.setSaleOrder(order);
        order.addOrderItem(item);

        return saleOrderRepository.saveAndFlush(order);
    }

    private Shipment createShipment() {
        Shipment shipment = new Shipment();
        shipment.setReference(DEFAULT_REFERENCE);
        shipment.setTrackingNumber(String.valueOf(new Random().nextInt()));
        shipment.setCarrier(DEFAULT_CARRIER);
        shipment.setStatus(DEFAULT_STATUS);
        shipment.setWeight(DEFAULT_WEIGHT);
        shipment.setNotes(DEFAULT_NOTES);
        shipment.setSaleOrder(saleOrder);
        shipment.setAddress(address);
        shipment.setClientAccount(clientAccount);
        shipment.setShippingDate(LocalDateTime.now());
        return shipment;
    }

    private void setupSecurityContext() {
        setSecurityContextWithClientAccountId(clientAccount.getId());
    }

    // ===============================
    // SHIPMENT CREATION TESTS
    // ===============================

    @Test
    @Transactional
    void createShipmentForOrder_Success() throws Exception {
        setupSecurityContext();
        int databaseSizeBeforeCreate = shipmentRepository.findAll().size();

        ShipmentRequestDTO createShipmentDTO = new ShipmentRequestDTO();
        createShipmentDTO.setCarrier("DHL");
        createShipmentDTO.setNotes("Handle with care");
        createShipmentDTO.setWeight(2.5);
        createShipmentDTO.setAddressId(address.getId());
        createShipmentDTO.setStatus(ShippingStatus.PENDING);

        // Create shipment for order
        restShipmentMockMvc
            .perform(
                post(ENTITY_API_URL + "/create-for-order/{orderId}", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(createShipmentDTO))
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.carrier").value("DHL"))
            .andExpect(jsonPath("$.status").value(ShippingStatus.PENDING.toString()))
            .andExpect(jsonPath("$.weight").value(2.5))
            .andExpect(jsonPath("$.notes").value("Handle with care"))
            .andExpect(jsonPath("$.reference").exists())
            .andExpect(jsonPath("$.shippingDate").exists());

        // Validate the Shipment in the database
        List<Shipment> shipmentList = shipmentRepository.findAll();
        assertThat(shipmentList).hasSize(databaseSizeBeforeCreate + 1);

        Shipment testShipment = shipmentList.get(shipmentList.size() - 1);
        assertThat(testShipment.getCarrier()).isEqualTo("DHL");
        assertThat(testShipment.getStatus()).isEqualTo(ShippingStatus.PENDING);
        assertThat(testShipment.getWeight()).isEqualTo(2.5);
        assertThat(testShipment.getNotes()).isEqualTo("Handle with care");
        assertThat(testShipment.getSaleOrder().getId()).isEqualTo(saleOrder.getId());
        assertThat(testShipment.getAddress().getId()).isEqualTo(address.getId());

        // Validate the order status was updated to SHIPPED
        SaleOrder updatedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    @Transactional
    void createShipmentForOrder_OrderNotFound_ShouldFail() throws Exception {
        setupSecurityContext();

        ShipmentRequestDTO createShipmentDTO = new ShipmentRequestDTO();
        createShipmentDTO.setCarrier("DHL");
        createShipmentDTO.setAddressId(address.getId());
        createShipmentDTO.setStatus(ShippingStatus.PENDING);

        restShipmentMockMvc
            .perform(
                post(ENTITY_API_URL + "/create-for-order/{orderId}", 99999L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(createShipmentDTO))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void createShipmentForOrder_OrderNotDelivery_ShouldFail() throws Exception {
        setupSecurityContext();

        // Change order type to STORE_PICKUP
        saleOrder.setOrderType(OrderType.STORE_PICKUP);
        saleOrderRepository.saveAndFlush(saleOrder);

        ShipmentRequestDTO createShipmentDTO = new ShipmentRequestDTO();
        createShipmentDTO.setCarrier("DHL");
        createShipmentDTO.setAddressId(address.getId());
        createShipmentDTO.setStatus(ShippingStatus.PENDING);

        restShipmentMockMvc
            .perform(
                post(ENTITY_API_URL + "/create-for-order/{orderId}", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(createShipmentDTO))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("not a delivery order")));
    }

    @Test
    @Transactional
    void createShipmentForOrder_OrderNotConfirmed_ShouldFail() throws Exception {
        setupSecurityContext();

        // Change order status to DRAFTED
        saleOrder.setStatus(OrderStatus.DRAFTED);
        saleOrderRepository.saveAndFlush(saleOrder);

        ShipmentRequestDTO createShipmentDTO = new ShipmentRequestDTO();
        createShipmentDTO.setCarrier("DHL");
        createShipmentDTO.setAddressId(address.getId());
        createShipmentDTO.setStatus(ShippingStatus.PENDING);
        createShipmentDTO.setStatus(ShippingStatus.PENDING);

        restShipmentMockMvc
            .perform(
                post(ENTITY_API_URL + "/create-for-order/{orderId}", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(createShipmentDTO))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("must be confirmed before shipping")));
    }

    @Test
    @Transactional
    void createShipmentForOrder_ShipmentAlreadyExists_ShouldFail() throws Exception {
        setupSecurityContext();

        // Create existing shipment
        shipment = createShipment();
        shipment = shipmentRepository.saveAndFlush(shipment);
        saleOrder.setShipment(shipment);
        saleOrderRepository.saveAndFlush(saleOrder);

        ShipmentRequestDTO createShipmentDTO = new ShipmentRequestDTO();
        createShipmentDTO.setCarrier("DHL");
        createShipmentDTO.setAddressId(address.getId());
        createShipmentDTO.setStatus(ShippingStatus.PENDING);

        restShipmentMockMvc
            .perform(
                post(ENTITY_API_URL + "/create-for-order/{orderId}", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(createShipmentDTO))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("already exists for this order")));
    }

    @Test
    @Transactional
    void createShipmentForOrder_InvalidAddress_ShouldFail() throws Exception {
        setupSecurityContext();

        ShipmentRequestDTO createShipmentDTO = new ShipmentRequestDTO();
        createShipmentDTO.setCarrier("DHL");
        createShipmentDTO.setAddressId(99999L); // Non-existent address

        restShipmentMockMvc
            .perform(
                post(ENTITY_API_URL + "/create-for-order/{orderId}", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(createShipmentDTO))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("validation")));
    }

    @Test
    @Transactional
    void createShipmentForOrder_AddressFromDifferentCustomer_ShouldFail() throws Exception {
        setupSecurityContext();

        // Create another customer and address
        Customer otherCustomer = new Customer();
        otherCustomer.setFirstName("Jane");
        otherCustomer.setLastName("Smith");
        otherCustomer.setPhone("+213555654321");
        otherCustomer.setCreatedByClientAccount(clientAccount);
        otherCustomer = customerRepository.saveAndFlush(otherCustomer);

        Address otherAddress = new Address();
        otherAddress.setAddressType(AddressType.PRIMARY);
        otherAddress.setIsDefault(true);
        otherAddress.setStreetAddress("street");
        otherAddress.setCity("Oran");
        otherAddress.setState("Oran");
        otherAddress.setPostalCode("31000");
        otherAddress.setCountry("Algeria");
        otherAddress.setCustomer(otherCustomer);
        otherAddress = addressRepository.saveAndFlush(otherAddress);

        ShipmentRequestDTO createShipmentDTO = new ShipmentRequestDTO();
        createShipmentDTO.setCarrier("DHL");
        createShipmentDTO.setAddressId(otherAddress.getId());
        createShipmentDTO.setStatus(ShippingStatus.PENDING);

        restShipmentMockMvc
            .perform(
                post(ENTITY_API_URL + "/create-for-order/{orderId}", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(createShipmentDTO))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Address not found")));
    }

    @Test
    @Transactional
    void createShipmentForOrder_DifferentClientAccount_ShouldFail() throws Exception {
        setupSecurityContext();

        // Create another client account and order
        ClientAccount otherAccount = new ClientAccount();
        otherAccount.setCompanyName("Other Company");
        otherAccount.email("other@test.com");
        otherAccount.setPhone("0555987654");
        otherAccount.setStatus(AccountStatus.ENABLED);
        otherAccount = clientAccountRepository.saveAndFlush(otherAccount);

        saleOrder.setClientAccount(otherAccount);
        saleOrderRepository.saveAndFlush(saleOrder);

        ShipmentRequestDTO createShipmentDTO = new ShipmentRequestDTO();
        createShipmentDTO.setCarrier("DHL");
        createShipmentDTO.setAddressId(address.getId());
        createShipmentDTO.setStatus(ShippingStatus.PENDING);

        restShipmentMockMvc
            .perform(
                post(ENTITY_API_URL + "/create-for-order/{orderId}", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(createShipmentDTO))
            )
            .andExpect(status().isForbidden());
    }

    // ===============================
    // SHIPMENT UPDATE TESTS
    // ===============================

    @Test
    @Transactional
    void updateShipment_Success() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment = shipmentRepository.saveAndFlush(shipment);

        ShipmentRequestDTO updateDTO = new ShipmentRequestDTO();
        updateDTO.setId(shipment.getId());
        updateDTO.setCarrier(UPDATED_CARRIER);
        updateDTO.setNotes(UPDATED_NOTES);
        updateDTO.setWeight(UPDATED_WEIGHT);
        updateDTO.setStatus(UPDATED_STATUS);
        updateDTO.setAddressId(address.getId());

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(updateDTO))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shipment.getId()))
            .andExpect(jsonPath("$.carrier").value(UPDATED_CARRIER))
            .andExpect(jsonPath("$.notes").value(UPDATED_NOTES))
            .andExpect(jsonPath("$.weight").value(UPDATED_WEIGHT))
            .andExpect(jsonPath("$.status").value(UPDATED_STATUS.toString()));

        // Verify the shipment in the database
        Shipment testShipment = shipmentRepository.findById(shipment.getId()).orElse(null);
        assertThat(testShipment).isNotNull();
        assertThat(testShipment.getCarrier()).isEqualTo(UPDATED_CARRIER);
        assertThat(testShipment.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testShipment.getWeight()).isEqualTo(UPDATED_WEIGHT);
        assertThat(testShipment.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void updateShipment_InvalidId_ShouldFail() throws Exception {
        setupSecurityContext();

        ShipmentRequestDTO updateDTO = new ShipmentRequestDTO();
        updateDTO.setId(99999L);
        updateDTO.setCarrier(UPDATED_CARRIER);

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, 99999L).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(updateDTO))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void updateShipment_DifferentClientAccount_ShouldFail() throws Exception {
        setupSecurityContext();

        // Create shipment for different client account
        ClientAccount otherAccount = new ClientAccount();
        otherAccount.setCompanyName("Other Company");
        otherAccount.email("other@test.com");
        otherAccount.setPhone("0555987654");
        otherAccount.setStatus(AccountStatus.ENABLED);
        otherAccount = clientAccountRepository.saveAndFlush(otherAccount);

        shipment = createShipment();
        shipment.setClientAccount(otherAccount);
        shipment = shipmentRepository.saveAndFlush(shipment);

        ShipmentRequestDTO updateDTO = new ShipmentRequestDTO();
        updateDTO.setId(shipment.getId());
        updateDTO.setCarrier(UPDATED_CARRIER);
        updateDTO.setStatus(ShippingStatus.PENDING);
        updateDTO.setAddressId(address.getId());

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(updateDTO))
            )
            .andExpect(status().isForbidden());
    }

    // ===============================
    // STATUS UPDATE TESTS
    // ===============================

    @Test
    @Transactional
    void updateShipmentStatus_Success() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment = shipmentRepository.saveAndFlush(shipment);

        UpdateShipmentStatusDTO updateRequest = new UpdateShipmentStatusDTO();
        updateRequest.setStatus(ShippingStatus.DELIVERED);
        updateRequest.setActualDeliveryDate(LocalDateTime.now());

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID + "/status", shipment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(updateRequest))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.status").value(ShippingStatus.DELIVERED.toString()))
            .andExpect(jsonPath("$.actualDeliveryDate").exists());

        // Validate the Shipment in the database
        Shipment testShipment = shipmentRepository.findById(shipment.getId()).orElse(null);
        assertThat(testShipment).isNotNull();
        assertThat(testShipment.getStatus()).isEqualTo(ShippingStatus.DELIVERED);
        assertThat(testShipment.getActualDeliveryDate()).isNotNull();

        // Verify order status was updated to COMPLETED
        SaleOrder updatedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @Transactional
    void updateShipmentStatus_InvalidTransition_ShouldFail() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setStatus(ShippingStatus.DELIVERED);
        shipment = shipmentRepository.saveAndFlush(shipment);

        UpdateShipmentStatusDTO updateRequest = new UpdateShipmentStatusDTO();
        updateRequest.setStatus(ShippingStatus.PENDING);

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID + "/status", shipment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(updateRequest))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Cannot change status from DELIVERED")));
    }

    @Test
    @Transactional
    void updateShipmentStatus_FromFailedToProcessing_ShouldFail() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setStatus(ShippingStatus.FAILED);
        shipment = shipmentRepository.saveAndFlush(shipment);

        UpdateShipmentStatusDTO updateRequest = new UpdateShipmentStatusDTO();
        updateRequest.setStatus(ShippingStatus.PROCESSING);

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID + "/status", shipment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(updateRequest))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Failed shipments can only be reset to PENDING")));
    }

    @Test
    @Transactional
    void updateShipmentStatus_FromFailedToPending_ShouldSucceed() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setStatus(ShippingStatus.FAILED);
        shipment = shipmentRepository.saveAndFlush(shipment);

        UpdateShipmentStatusDTO updateRequest = new UpdateShipmentStatusDTO();
        updateRequest.setStatus(ShippingStatus.PENDING);

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID + "/status", shipment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(updateRequest))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ShippingStatus.PENDING.toString()));
    }

    // ===============================
    // CANCEL SHIPMENT TESTS
    // ===============================

    @Test
    @Transactional
    void cancelShipment_Success() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setStatus(ShippingStatus.PROCESSING);
        shipment = shipmentRepository.saveAndFlush(shipment);

        restShipmentMockMvc
            .perform(post(ENTITY_API_URL_ID + "/cancel", shipment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.status").value(ShippingStatus.FAILED.toString()));

        // Verify the shipment status was updated
        Shipment cancelledShipment = shipmentRepository.findById(shipment.getId()).orElse(null);
        assertThat(cancelledShipment).isNotNull();
        assertThat(cancelledShipment.getStatus()).isEqualTo(ShippingStatus.FAILED);
    }

    @Test
    @Transactional
    void cancelShipment_AlreadyDelivered_ShouldFail() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setStatus(ShippingStatus.DELIVERED);
        shipment = shipmentRepository.saveAndFlush(shipment);

        restShipmentMockMvc
            .perform(post(ENTITY_API_URL_ID + "/cancel", shipment.getId()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Cannot cancel delivered shipments")));
    }

    // ===============================
    // SEARCH AND FILTERING TESTS
    // ===============================

    @Test
    @Transactional
    void searchShipments_ByCarrier() throws Exception {
        setupSecurityContext();

        // Create multiple shipments with different carriers
        shipment = createShipment();
        shipment.setCarrier("YALIDINE");
        shipmentRepository.saveAndFlush(shipment);

        Shipment shipment2 = createShipment();
        shipment2.setCarrier("DHL");
        shipment2.setReference("SH-2025-000002");
        shipment2.setSaleOrder(createAndSaveSaleOrder(clientAccount, customer));
        shipmentRepository.saveAndFlush(shipment2);

        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/search").param("carrier", "YALIDINE").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[*].carrier").value(hasItem("YALIDINE")));
    }

    @Test
    @Transactional
    void searchShipments_ByStatus() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setStatus(ShippingStatus.DELIVERED);
        shipmentRepository.saveAndFlush(shipment);

        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/search").param("status", "DELIVERED").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[*].status").value(hasItem("DELIVERED")));
    }

    @Test
    @Transactional
    void searchShipments_ByTrackingNumber() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setTrackingNumber("TRACK999888777");
        shipmentRepository.saveAndFlush(shipment);

        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/search").param("trackingNumber", "TRACK999888777").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[*].trackingNumber").value(hasItem("TRACK999888777")));
    }

    @Test
    @Transactional
    void searchShipments_ByReference() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setReference("SH-SEARCH-001");
        shipmentRepository.saveAndFlush(shipment);

        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/search").param("query", "SEARCH").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[*].reference").value(hasItem(containsString("SEARCH"))));
    }

    @Test
    @Transactional
    void searchShipments_MultipleCriteria() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setCarrier("DHL");
        shipment.setStatus(ShippingStatus.SHIPPED);
        shipment.setReference("SH-MULTI-001");
        shipmentRepository.saveAndFlush(shipment);

        restShipmentMockMvc
            .perform(
                get(ENTITY_API_URL + "/search")
                    .param("carrier", "DHL")
                    .param("status", "SHIPPED")
                    .param("query", "MULTI")
                    .param("sort", "id,desc")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[*].carrier").value(hasItem("DHL")))
            .andExpect(jsonPath("$[*].status").value(hasItem("SHIPPED")))
            .andExpect(jsonPath("$[*].reference").value(hasItem(containsString("MULTI"))));
    }

    @Test
    @Transactional
    void searchShipments_InvalidStatus_ShouldFail() throws Exception {
        setupSecurityContext();

        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/search").param("status", "INVALID_STATUS"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Invalid status value")));
    }

    @Test
    @Transactional
    void searchShipments_InvalidDateFormat_ShouldFail() throws Exception {
        setupSecurityContext();

        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/search").param("fromDate", "invalid-date").param("toDate", "also-invalid"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Invalid date format")));
    }

    @Test
    @Transactional
    void getPendingShipments() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setStatus(ShippingStatus.PENDING);
        shipmentRepository.saveAndFlush(shipment);

        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/pending"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[*].status").value(hasItem("PENDING")));
    }

    // ===============================
    // CRUD OPERATIONS TESTS
    // ===============================

    @Test
    @Transactional
    void getAllShipments() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipmentRepository.saveAndFlush(shipment);

        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[*].id").value(hasItem(shipment.getId().intValue())));
    }

    @Test
    @Transactional
    void getShipment() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment = shipmentRepository.saveAndFlush(shipment);

        restShipmentMockMvc
            .perform(get(ENTITY_API_URL_ID, shipment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shipment.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.carrier").value(DEFAULT_CARRIER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getShipment_NonExistent_ShouldReturn404() throws Exception {
        setupSecurityContext();

        restShipmentMockMvc.perform(get(ENTITY_API_URL_ID, 99999L)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void getShipment_DifferentClientAccount_ShouldReturn404() throws Exception {
        setupSecurityContext();

        // Create shipment for different client account
        ClientAccount otherAccount = new ClientAccount();
        otherAccount.setCompanyName("Other Company");
        otherAccount.email("other@test.com");
        otherAccount.setPhone("0555987654");
        otherAccount.setStatus(AccountStatus.ENABLED);
        otherAccount = clientAccountRepository.saveAndFlush(otherAccount);

        shipment = createShipment();
        shipment.setClientAccount(otherAccount);
        shipment = shipmentRepository.saveAndFlush(shipment);

        restShipmentMockMvc.perform(get(ENTITY_API_URL_ID, shipment.getId())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deleteShipment_Success() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setStatus(ShippingStatus.PENDING);
        shipment = shipmentRepository.saveAndFlush(shipment);

        int databaseSizeBeforeDelete = shipmentRepository.findAll().size();

        restShipmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Shipment> shipmentList = shipmentRepository.findAll();
        assertThat(shipmentList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void deleteShipment_ProcessedShipment_ShouldFail() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setStatus(ShippingStatus.PROCESSING);
        shipment = shipmentRepository.saveAndFlush(shipment);

        restShipmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Cannot delete processed shipments")));
    }

    @Test
    @Transactional
    void deleteShipment_NonExistent_ShouldFail() throws Exception {
        setupSecurityContext();

        restShipmentMockMvc.perform(delete(ENTITY_API_URL_ID, 99999L).accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    // ===============================
    // EDGE CASES AND ERROR SCENARIOS
    // ===============================

    @Test
    @Transactional
    void createShipmentForOrder_WithMaxWeightLimit() throws Exception {
        setupSecurityContext();

        ShipmentRequestDTO createShipmentDTO = new ShipmentRequestDTO();
        createShipmentDTO.setCarrier("DHL");
        createShipmentDTO.setWeight(999999.99); // Very large weight
        createShipmentDTO.setAddressId(address.getId());
        createShipmentDTO.setStatus(ShippingStatus.PENDING);

        restShipmentMockMvc
            .perform(
                post(ENTITY_API_URL + "/create-for-order/{orderId}", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(createShipmentDTO))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.weight").value(999999.99));
    }

    @Test
    @Transactional
    void createShipmentForOrder_WithVeryLongNotes() throws Exception {
        setupSecurityContext();

        String longNotes = "A".repeat(1000); // Very long notes
        ShipmentRequestDTO createShipmentDTO = new ShipmentRequestDTO();
        createShipmentDTO.setCarrier("DHL");
        createShipmentDTO.setNotes(longNotes);
        createShipmentDTO.setAddressId(address.getId());
        createShipmentDTO.setStatus(ShippingStatus.PENDING);

        restShipmentMockMvc
            .perform(
                post(ENTITY_API_URL + "/create-for-order/{orderId}", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(createShipmentDTO))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.notes").value(longNotes));
    }

    @Test
    @Transactional
    void searchShipments_EmptyResults() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipmentRepository.saveAndFlush(shipment);

        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/search").param("query", "NONEXISTENT_REFERENCE"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    void updateShipmentStatus_ToDeliveredWithoutDeliveryDate_ShouldSetCurrentTime() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setStatus(ShippingStatus.SHIPPED);
        shipment = shipmentRepository.saveAndFlush(shipment);

        UpdateShipmentStatusDTO updateRequest = new UpdateShipmentStatusDTO();
        updateRequest.setStatus(ShippingStatus.DELIVERED);
        // Note: Not setting actualDeliveryDate

        LocalDateTime beforeUpdate = LocalDateTime.now();

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID + "/status", shipment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(updateRequest))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ShippingStatus.DELIVERED.toString()))
            .andExpect(jsonPath("$.actualDeliveryDate").exists());

        LocalDateTime afterUpdate = LocalDateTime.now();

        // Verify actualDeliveryDate was set to current time
        Shipment updatedShipment = shipmentRepository.findById(shipment.getId()).orElse(null);
        assertThat(updatedShipment).isNotNull();
        assertThat(updatedShipment.getActualDeliveryDate()).isNotNull();
        assertThat(updatedShipment.getActualDeliveryDate()).isAfterOrEqualTo(beforeUpdate).isBeforeOrEqualTo(afterUpdate);
    }

    @Test
    @Transactional
    void getAllShipments_WithPagination() throws Exception {
        setupSecurityContext();

        // Create multiple shipments
        for (int i = 0; i < 15; i++) {
            Shipment shipment = createShipment();
            shipment.setSaleOrder(createAndSaveSaleOrder(clientAccount, customer));
            shipment.setReference("SH-2025-" + String.format("%06d", i + 1));
            shipmentRepository.saveAndFlush(shipment);
        }

        // Test pagination
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "?page=0&size=5&sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    @Transactional
    void createShipmentForOrder_CustomerWithoutAddress_ShouldFail() throws Exception {
        setupSecurityContext();

        // Delete the customer's address
        addressRepository.delete(address);

        ShipmentRequestDTO createShipmentDTO = new ShipmentRequestDTO();
        createShipmentDTO.setCarrier("DHL");
        createShipmentDTO.setAddressId(address.getId());
        createShipmentDTO.setStatus(ShippingStatus.PENDING); // Now non-existent

        restShipmentMockMvc
            .perform(
                post(ENTITY_API_URL + "/create-for-order/{orderId}", saleOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(createShipmentDTO))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Address not found")));
    }

    @Test
    @Transactional
    void updateShipmentStatus_ComplexStatusFlow() throws Exception {
        setupSecurityContext();

        shipment = createShipment();
        shipment.setStatus(ShippingStatus.PENDING);
        shipment = shipmentRepository.saveAndFlush(shipment);

        // Test status progression: PENDING -> PROCESSING -> SHIPPED -> DELIVERED

        // PENDING -> PROCESSING
        UpdateShipmentStatusDTO updateRequest = new UpdateShipmentStatusDTO();
        updateRequest.setStatus(ShippingStatus.PROCESSING);

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID + "/status", shipment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(updateRequest))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ShippingStatus.PROCESSING.toString()));

        // PROCESSING -> SHIPPED
        updateRequest.setStatus(ShippingStatus.SHIPPED);

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID + "/status", shipment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(updateRequest))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ShippingStatus.SHIPPED.toString()));

        // SHIPPED -> DELIVERED
        updateRequest.setStatus(ShippingStatus.DELIVERED);
        updateRequest.setActualDeliveryDate(LocalDateTime.now());

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID + "/status", shipment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(updateRequest))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ShippingStatus.DELIVERED.toString()))
            .andExpect(jsonPath("$.actualDeliveryDate").exists());

        // Verify final state
        Shipment finalShipment = shipmentRepository.findById(shipment.getId()).orElse(null);
        assertThat(finalShipment).isNotNull();
        assertThat(finalShipment.getStatus()).isEqualTo(ShippingStatus.DELIVERED);
        assertThat(finalShipment.getActualDeliveryDate()).isNotNull();

        // Verify order was completed
        SaleOrder finalOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(finalOrder).isNotNull();
        assertThat(finalOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
}
