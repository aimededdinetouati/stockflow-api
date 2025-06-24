package com.adeem.stockflow.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.SaleOrder;
import com.adeem.stockflow.domain.Shipment;
import com.adeem.stockflow.domain.enumeration.AccountStatus;
import com.adeem.stockflow.domain.enumeration.OrderStatus;
import com.adeem.stockflow.domain.enumeration.OrderType;
import com.adeem.stockflow.domain.enumeration.ShippingStatus;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.repository.SaleOrderRepository;
import com.adeem.stockflow.repository.ShipmentRepository;
import com.adeem.stockflow.service.dto.ShipmentRequestDTO;
import com.adeem.stockflow.service.dto.UpdateShipmentStatusDTO;
import com.adeem.stockflow.service.mapper.ShipmentMapper;
import com.adeem.stockflow.web.rest.ShipmentResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the enhanced {@link ShipmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = { "USER_ADMIN" })
class ShipmentResourceIT {

    private static final String DEFAULT_REFERENCE = "SH-TEST-2024-000001";
    private static final String DEFAULT_TRACKING_NUMBER = "TRACK123456";
    private static final String DEFAULT_CARRIER = "YALIDINE";
    private static final ShippingStatus DEFAULT_STATUS = ShippingStatus.PENDING;
    private static final BigDecimal DEFAULT_SHIPPING_COST = new BigDecimal("500.00");

    private static final String ENTITY_API_URL = "/api/shipments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentMapper shipmentMapper;

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private MockMvc restShipmentMockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Shipment shipment;
    private SaleOrder saleOrder;
    private ClientAccount clientAccount;

    @BeforeEach
    void initTest() {
        createTestData();
    }

    @Test
    @Transactional
    void createShipmentForOrder() throws Exception {
        // Prepare the sale order for shipping
        saleOrder.setStatus(OrderStatus.CONFIRMED);
        saleOrder.setOrderType(OrderType.DELIVERY);
        saleOrderRepository.saveAndFlush(saleOrder);

        ShipmentRequestDTO createShipmentDTO = new ShipmentRequestDTO();
        createShipmentDTO.setCarrier("DHL");
        createShipmentDTO.setNotes("Handle with care");
        createShipmentDTO.setWeight(2.5);

        int databaseSizeBeforeCreate = shipmentRepository.findAll().size();

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
            .andExpect(jsonPath("$.weight").value(2.5));

        // Validate the Shipment in the database
        List<Shipment> shipmentList = shipmentRepository.findAll();
        assertThat(shipmentList).hasSize(databaseSizeBeforeCreate + 1);

        // Validate the order status was updated
        SaleOrder updatedOrder = saleOrderRepository.findById(saleOrder.getId()).orElse(null);
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    @Transactional
    void updateShipmentStatus() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        UpdateShipmentStatusDTO updateRequest = new UpdateShipmentStatusDTO();
        updateRequest.setStatus(ShippingStatus.DELIVERED);

        // Update shipment status
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
    }

    @Test
    @Transactional
    void getShipmentTracking() throws Exception {
        // Initialize the database
        shipment.setTrackingNumber(DEFAULT_TRACKING_NUMBER);
        shipmentRepository.saveAndFlush(shipment);

        // Get tracking information
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL_ID + "/tracking", shipment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.trackingNumber").value(DEFAULT_TRACKING_NUMBER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.carrier").value(DEFAULT_CARRIER));
    }

    @Test
    @Transactional
    void searchShipments() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Search shipments
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/search").param("carrier", "YALIDINE").param("status", "PENDING"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Transactional
    void getPendingShipments() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get pending shipments
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/pending"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray());
    }

    private void createTestData() {
        // Create client account
        clientAccount = new ClientAccount();
        clientAccount.phone("0676811436");
        clientAccount.setEmail("mail@mail.com");
        clientAccount.setCompanyName("Test Company");
        clientAccount.setStatus(AccountStatus.ENABLED);
        clientAccount.setDefaultShippingCost(DEFAULT_SHIPPING_COST);
        clientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        // Create sale order
        saleOrder = new SaleOrder();
        saleOrder.setReference("SO-TEST-2024-000001");
        saleOrder.setDate(ZonedDateTime.now());
        saleOrder.setStatus(OrderStatus.CONFIRMED);
        saleOrder.setOrderType(OrderType.DELIVERY);
        saleOrder.setTotal(new BigDecimal("1000.00"));
        saleOrder.setClientAccount(clientAccount);
        saleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Create shipment
        shipment = new Shipment();
        shipment.setReference(DEFAULT_REFERENCE);
        shipment.setTrackingNumber(DEFAULT_TRACKING_NUMBER);
        shipment.setCarrier(DEFAULT_CARRIER);
        shipment.setStatus(DEFAULT_STATUS);
        shipment.setShippingCost(DEFAULT_SHIPPING_COST);
        shipment.setSaleOrder(saleOrder);
        shipment.setClientAccount(clientAccount);
    }
}
