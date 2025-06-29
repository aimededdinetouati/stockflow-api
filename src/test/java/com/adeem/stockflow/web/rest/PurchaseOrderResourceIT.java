package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.PurchaseOrderAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameInstant;
import static com.adeem.stockflow.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.PurchaseOrder;
import com.adeem.stockflow.domain.enumeration.OrderStatus;
import com.adeem.stockflow.repository.PurchaseOrderRepository;
import com.adeem.stockflow.service.dto.PurchaseOrderDTO;
import com.adeem.stockflow.service.mapper.PurchaseOrderMapper;
import com.adeem.stockflow.web.rest.uncostomized.PurchaseOrderResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PurchaseOrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PurchaseOrderResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.DRAFTED;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.CONFIRMED;

    private static final BigDecimal DEFAULT_SHIPPING = new BigDecimal(1);
    private static final BigDecimal UPDATED_SHIPPING = new BigDecimal(2);

    private static final BigDecimal DEFAULT_MISSION_FEE = new BigDecimal(1);
    private static final BigDecimal UPDATED_MISSION_FEE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_HANDLING = new BigDecimal(1);
    private static final BigDecimal UPDATED_HANDLING = new BigDecimal(2);

    private static final BigDecimal DEFAULT_COST_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_COST_TOTAL = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TVA_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_TVA_RATE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_STAMP_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_STAMP_RATE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_DISCOUNT_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT_RATE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TVA_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TVA_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_STAMP_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_STAMP_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_SUB_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_SUB_TOTAL = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/purchase-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchaseOrderMockMvc;

    private PurchaseOrder purchaseOrder;

    private PurchaseOrder insertedPurchaseOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseOrder createEntity() {
        return new PurchaseOrder()
            .reference(DEFAULT_REFERENCE)
            .date(DEFAULT_DATE)
            .notes(DEFAULT_NOTES)
            .status(DEFAULT_STATUS)
            .shipping(DEFAULT_SHIPPING)
            .missionFee(DEFAULT_MISSION_FEE)
            .handling(DEFAULT_HANDLING)
            .costTotal(DEFAULT_COST_TOTAL)
            .tvaRate(DEFAULT_TVA_RATE)
            .stampRate(DEFAULT_STAMP_RATE)
            .discountRate(DEFAULT_DISCOUNT_RATE)
            .tvaAmount(DEFAULT_TVA_AMOUNT)
            .stampAmount(DEFAULT_STAMP_AMOUNT)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT)
            .subTotal(DEFAULT_SUB_TOTAL)
            .total(DEFAULT_TOTAL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseOrder createUpdatedEntity() {
        return new PurchaseOrder()
            .reference(UPDATED_REFERENCE)
            .date(UPDATED_DATE)
            .notes(UPDATED_NOTES)
            .status(UPDATED_STATUS)
            .shipping(UPDATED_SHIPPING)
            .missionFee(UPDATED_MISSION_FEE)
            .handling(UPDATED_HANDLING)
            .costTotal(UPDATED_COST_TOTAL)
            .tvaRate(UPDATED_TVA_RATE)
            .stampRate(UPDATED_STAMP_RATE)
            .discountRate(UPDATED_DISCOUNT_RATE)
            .tvaAmount(UPDATED_TVA_AMOUNT)
            .stampAmount(UPDATED_STAMP_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .subTotal(UPDATED_SUB_TOTAL)
            .total(UPDATED_TOTAL);
    }

    @BeforeEach
    void initTest() {
        purchaseOrder = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPurchaseOrder != null) {
            purchaseOrderRepository.delete(insertedPurchaseOrder);
            insertedPurchaseOrder = null;
        }
    }

    @Test
    @Transactional
    void createPurchaseOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);
        var returnedPurchaseOrderDTO = om.readValue(
            restPurchaseOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PurchaseOrderDTO.class
        );

        // Validate the PurchaseOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPurchaseOrder = purchaseOrderMapper.toEntity(returnedPurchaseOrderDTO);
        assertPurchaseOrderUpdatableFieldsEquals(returnedPurchaseOrder, getPersistedPurchaseOrder(returnedPurchaseOrder));

        insertedPurchaseOrder = returnedPurchaseOrder;
    }

    @Test
    @Transactional
    void createPurchaseOrderWithExistingId() throws Exception {
        // Create the PurchaseOrder with an existing ID
        purchaseOrder.setId(1L);
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPurchaseOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchaseOrder.setReference(null);

        // Create the PurchaseOrder, which fails.
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        restPurchaseOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchaseOrder.setDate(null);

        // Create the PurchaseOrder, which fails.
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        restPurchaseOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchaseOrder.setStatus(null);

        // Create the PurchaseOrder, which fails.
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        restPurchaseOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPurchaseOrders() throws Exception {
        // Initialize the database
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get all the purchaseOrderList
        restPurchaseOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].shipping").value(hasItem(sameNumber(DEFAULT_SHIPPING))))
            .andExpect(jsonPath("$.[*].missionFee").value(hasItem(sameNumber(DEFAULT_MISSION_FEE))))
            .andExpect(jsonPath("$.[*].handling").value(hasItem(sameNumber(DEFAULT_HANDLING))))
            .andExpect(jsonPath("$.[*].costTotal").value(hasItem(sameNumber(DEFAULT_COST_TOTAL))))
            .andExpect(jsonPath("$.[*].tvaRate").value(hasItem(sameNumber(DEFAULT_TVA_RATE))))
            .andExpect(jsonPath("$.[*].stampRate").value(hasItem(sameNumber(DEFAULT_STAMP_RATE))))
            .andExpect(jsonPath("$.[*].discountRate").value(hasItem(sameNumber(DEFAULT_DISCOUNT_RATE))))
            .andExpect(jsonPath("$.[*].tvaAmount").value(hasItem(sameNumber(DEFAULT_TVA_AMOUNT))))
            .andExpect(jsonPath("$.[*].stampAmount").value(hasItem(sameNumber(DEFAULT_STAMP_AMOUNT))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].subTotal").value(hasItem(sameNumber(DEFAULT_SUB_TOTAL))))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))));
    }

    @Test
    @Transactional
    void getPurchaseOrder() throws Exception {
        // Initialize the database
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        // Get the purchaseOrder
        restPurchaseOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, purchaseOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchaseOrder.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.shipping").value(sameNumber(DEFAULT_SHIPPING)))
            .andExpect(jsonPath("$.missionFee").value(sameNumber(DEFAULT_MISSION_FEE)))
            .andExpect(jsonPath("$.handling").value(sameNumber(DEFAULT_HANDLING)))
            .andExpect(jsonPath("$.costTotal").value(sameNumber(DEFAULT_COST_TOTAL)))
            .andExpect(jsonPath("$.tvaRate").value(sameNumber(DEFAULT_TVA_RATE)))
            .andExpect(jsonPath("$.stampRate").value(sameNumber(DEFAULT_STAMP_RATE)))
            .andExpect(jsonPath("$.discountRate").value(sameNumber(DEFAULT_DISCOUNT_RATE)))
            .andExpect(jsonPath("$.tvaAmount").value(sameNumber(DEFAULT_TVA_AMOUNT)))
            .andExpect(jsonPath("$.stampAmount").value(sameNumber(DEFAULT_STAMP_AMOUNT)))
            .andExpect(jsonPath("$.discountAmount").value(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .andExpect(jsonPath("$.subTotal").value(sameNumber(DEFAULT_SUB_TOTAL)))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)));
    }

    @Test
    @Transactional
    void getNonExistingPurchaseOrder() throws Exception {
        // Get the purchaseOrder
        restPurchaseOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPurchaseOrder() throws Exception {
        // Initialize the database
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchaseOrder
        PurchaseOrder updatedPurchaseOrder = purchaseOrderRepository.findById(purchaseOrder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPurchaseOrder are not directly saved in db
        em.detach(updatedPurchaseOrder);
        updatedPurchaseOrder
            .reference(UPDATED_REFERENCE)
            .date(UPDATED_DATE)
            .notes(UPDATED_NOTES)
            .status(UPDATED_STATUS)
            .shipping(UPDATED_SHIPPING)
            .missionFee(UPDATED_MISSION_FEE)
            .handling(UPDATED_HANDLING)
            .costTotal(UPDATED_COST_TOTAL)
            .tvaRate(UPDATED_TVA_RATE)
            .stampRate(UPDATED_STAMP_RATE)
            .discountRate(UPDATED_DISCOUNT_RATE)
            .tvaAmount(UPDATED_TVA_AMOUNT)
            .stampAmount(UPDATED_STAMP_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .subTotal(UPDATED_SUB_TOTAL)
            .total(UPDATED_TOTAL);
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(updatedPurchaseOrder);

        restPurchaseOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseOrderDTO))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPurchaseOrderToMatchAllProperties(updatedPurchaseOrder);
    }

    @Test
    @Transactional
    void putNonExistingPurchaseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrder.setId(longCount.incrementAndGet());

        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchaseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrder.setId(longCount.incrementAndGet());

        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchaseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrder.setId(longCount.incrementAndGet());

        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePurchaseOrderWithPatch() throws Exception {
        // Initialize the database
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchaseOrder using partial update
        PurchaseOrder partialUpdatedPurchaseOrder = new PurchaseOrder();
        partialUpdatedPurchaseOrder.setId(purchaseOrder.getId());

        partialUpdatedPurchaseOrder
            .reference(UPDATED_REFERENCE)
            .date(UPDATED_DATE)
            .notes(UPDATED_NOTES)
            .status(UPDATED_STATUS)
            .stampRate(UPDATED_STAMP_RATE)
            .subTotal(UPDATED_SUB_TOTAL);

        restPurchaseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchaseOrder))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchaseOrderUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPurchaseOrder, purchaseOrder),
            getPersistedPurchaseOrder(purchaseOrder)
        );
    }

    @Test
    @Transactional
    void fullUpdatePurchaseOrderWithPatch() throws Exception {
        // Initialize the database
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchaseOrder using partial update
        PurchaseOrder partialUpdatedPurchaseOrder = new PurchaseOrder();
        partialUpdatedPurchaseOrder.setId(purchaseOrder.getId());

        partialUpdatedPurchaseOrder
            .reference(UPDATED_REFERENCE)
            .date(UPDATED_DATE)
            .notes(UPDATED_NOTES)
            .status(UPDATED_STATUS)
            .shipping(UPDATED_SHIPPING)
            .missionFee(UPDATED_MISSION_FEE)
            .handling(UPDATED_HANDLING)
            .costTotal(UPDATED_COST_TOTAL)
            .tvaRate(UPDATED_TVA_RATE)
            .stampRate(UPDATED_STAMP_RATE)
            .discountRate(UPDATED_DISCOUNT_RATE)
            .tvaAmount(UPDATED_TVA_AMOUNT)
            .stampAmount(UPDATED_STAMP_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .subTotal(UPDATED_SUB_TOTAL)
            .total(UPDATED_TOTAL);

        restPurchaseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchaseOrder))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchaseOrderUpdatableFieldsEquals(partialUpdatedPurchaseOrder, getPersistedPurchaseOrder(partialUpdatedPurchaseOrder));
    }

    @Test
    @Transactional
    void patchNonExistingPurchaseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrder.setId(longCount.incrementAndGet());

        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, purchaseOrderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchaseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPurchaseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrder.setId(longCount.incrementAndGet());

        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchaseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPurchaseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrder.setId(longCount.incrementAndGet());

        // Create the PurchaseOrder
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(purchaseOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchaseOrder() throws Exception {
        // Initialize the database
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the purchaseOrder
        restPurchaseOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchaseOrder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return purchaseOrderRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected PurchaseOrder getPersistedPurchaseOrder(PurchaseOrder purchaseOrder) {
        return purchaseOrderRepository.findById(purchaseOrder.getId()).orElseThrow();
    }

    protected void assertPersistedPurchaseOrderToMatchAllProperties(PurchaseOrder expectedPurchaseOrder) {
        assertPurchaseOrderAllPropertiesEquals(expectedPurchaseOrder, getPersistedPurchaseOrder(expectedPurchaseOrder));
    }

    protected void assertPersistedPurchaseOrderToMatchUpdatableProperties(PurchaseOrder expectedPurchaseOrder) {
        assertPurchaseOrderAllUpdatablePropertiesEquals(expectedPurchaseOrder, getPersistedPurchaseOrder(expectedPurchaseOrder));
    }
}
