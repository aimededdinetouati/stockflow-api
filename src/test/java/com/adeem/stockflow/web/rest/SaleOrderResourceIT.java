package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.SaleOrderAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameInstant;
import static com.adeem.stockflow.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.SaleOrder;
import com.adeem.stockflow.domain.enumeration.OrderStatus;
import com.adeem.stockflow.domain.enumeration.SaleType;
import com.adeem.stockflow.repository.SaleOrderRepository;
import com.adeem.stockflow.service.dto.SaleOrderDTO;
import com.adeem.stockflow.service.mapper.SaleOrderMapper;
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
 * Integration tests for the {@link SaleOrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SaleOrderResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_DUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DUE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.DRAFTED;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.CONFIRMED;

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

    private static final SaleType DEFAULT_SALE_TYPE = SaleType.RECEIPT;
    private static final SaleType UPDATED_SALE_TYPE = SaleType.INVOICE;

    private static final String ENTITY_API_URL = "/api/sale-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private SaleOrderMapper saleOrderMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSaleOrderMockMvc;

    private SaleOrder saleOrder;

    private SaleOrder insertedSaleOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleOrder createEntity() {
        return new SaleOrder()
            .reference(DEFAULT_REFERENCE)
            .date(DEFAULT_DATE)
            .dueDate(DEFAULT_DUE_DATE)
            .notes(DEFAULT_NOTES)
            .status(DEFAULT_STATUS)
            .tvaRate(DEFAULT_TVA_RATE)
            .stampRate(DEFAULT_STAMP_RATE)
            .discountRate(DEFAULT_DISCOUNT_RATE)
            .tvaAmount(DEFAULT_TVA_AMOUNT)
            .stampAmount(DEFAULT_STAMP_AMOUNT)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT)
            .subTotal(DEFAULT_SUB_TOTAL)
            .total(DEFAULT_TOTAL)
            .saleType(DEFAULT_SALE_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleOrder createUpdatedEntity() {
        return new SaleOrder()
            .reference(UPDATED_REFERENCE)
            .date(UPDATED_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .notes(UPDATED_NOTES)
            .status(UPDATED_STATUS)
            .tvaRate(UPDATED_TVA_RATE)
            .stampRate(UPDATED_STAMP_RATE)
            .discountRate(UPDATED_DISCOUNT_RATE)
            .tvaAmount(UPDATED_TVA_AMOUNT)
            .stampAmount(UPDATED_STAMP_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .subTotal(UPDATED_SUB_TOTAL)
            .total(UPDATED_TOTAL)
            .saleType(UPDATED_SALE_TYPE);
    }

    @BeforeEach
    void initTest() {
        saleOrder = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSaleOrder != null) {
            saleOrderRepository.delete(insertedSaleOrder);
            insertedSaleOrder = null;
        }
    }

    @Test
    @Transactional
    void createSaleOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SaleOrder
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);
        var returnedSaleOrderDTO = om.readValue(
            restSaleOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOrderDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SaleOrderDTO.class
        );

        // Validate the SaleOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSaleOrder = saleOrderMapper.toEntity(returnedSaleOrderDTO);
        assertSaleOrderUpdatableFieldsEquals(returnedSaleOrder, getPersistedSaleOrder(returnedSaleOrder));

        insertedSaleOrder = returnedSaleOrder;
    }

    @Test
    @Transactional
    void createSaleOrderWithExistingId() throws Exception {
        // Create the SaleOrder with an existing ID
        saleOrder.setId(1L);
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SaleOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleOrder.setReference(null);

        // Create the SaleOrder, which fails.
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleOrder.setDate(null);

        // Create the SaleOrder, which fails.
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleOrder.setStatus(null);

        // Create the SaleOrder, which fails.
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);

        restSaleOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSaleOrders() throws Exception {
        // Initialize the database
        insertedSaleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Get all the saleOrderList
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(sameInstant(DEFAULT_DUE_DATE))))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].tvaRate").value(hasItem(sameNumber(DEFAULT_TVA_RATE))))
            .andExpect(jsonPath("$.[*].stampRate").value(hasItem(sameNumber(DEFAULT_STAMP_RATE))))
            .andExpect(jsonPath("$.[*].discountRate").value(hasItem(sameNumber(DEFAULT_DISCOUNT_RATE))))
            .andExpect(jsonPath("$.[*].tvaAmount").value(hasItem(sameNumber(DEFAULT_TVA_AMOUNT))))
            .andExpect(jsonPath("$.[*].stampAmount").value(hasItem(sameNumber(DEFAULT_STAMP_AMOUNT))))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].subTotal").value(hasItem(sameNumber(DEFAULT_SUB_TOTAL))))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))))
            .andExpect(jsonPath("$.[*].saleType").value(hasItem(DEFAULT_SALE_TYPE.toString())));
    }

    @Test
    @Transactional
    void getSaleOrder() throws Exception {
        // Initialize the database
        insertedSaleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        // Get the saleOrder
        restSaleOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, saleOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(saleOrder.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.dueDate").value(sameInstant(DEFAULT_DUE_DATE)))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.tvaRate").value(sameNumber(DEFAULT_TVA_RATE)))
            .andExpect(jsonPath("$.stampRate").value(sameNumber(DEFAULT_STAMP_RATE)))
            .andExpect(jsonPath("$.discountRate").value(sameNumber(DEFAULT_DISCOUNT_RATE)))
            .andExpect(jsonPath("$.tvaAmount").value(sameNumber(DEFAULT_TVA_AMOUNT)))
            .andExpect(jsonPath("$.stampAmount").value(sameNumber(DEFAULT_STAMP_AMOUNT)))
            .andExpect(jsonPath("$.discountAmount").value(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .andExpect(jsonPath("$.subTotal").value(sameNumber(DEFAULT_SUB_TOTAL)))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.saleType").value(DEFAULT_SALE_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSaleOrder() throws Exception {
        // Get the saleOrder
        restSaleOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSaleOrder() throws Exception {
        // Initialize the database
        insertedSaleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleOrder
        SaleOrder updatedSaleOrder = saleOrderRepository.findById(saleOrder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSaleOrder are not directly saved in db
        em.detach(updatedSaleOrder);
        updatedSaleOrder
            .reference(UPDATED_REFERENCE)
            .date(UPDATED_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .notes(UPDATED_NOTES)
            .status(UPDATED_STATUS)
            .tvaRate(UPDATED_TVA_RATE)
            .stampRate(UPDATED_STAMP_RATE)
            .discountRate(UPDATED_DISCOUNT_RATE)
            .tvaAmount(UPDATED_TVA_AMOUNT)
            .stampAmount(UPDATED_STAMP_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .subTotal(UPDATED_SUB_TOTAL)
            .total(UPDATED_TOTAL)
            .saleType(UPDATED_SALE_TYPE);
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(updatedSaleOrder);

        restSaleOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleOrderDTO))
            )
            .andExpect(status().isOk());

        // Validate the SaleOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSaleOrderToMatchAllProperties(updatedSaleOrder);
    }

    @Test
    @Transactional
    void putNonExistingSaleOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOrder.setId(longCount.incrementAndGet());

        // Create the SaleOrder
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSaleOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOrder.setId(longCount.incrementAndGet());

        // Create the SaleOrder
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSaleOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOrder.setId(longCount.incrementAndGet());

        // Create the SaleOrder
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSaleOrderWithPatch() throws Exception {
        // Initialize the database
        insertedSaleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleOrder using partial update
        SaleOrder partialUpdatedSaleOrder = new SaleOrder();
        partialUpdatedSaleOrder.setId(saleOrder.getId());

        partialUpdatedSaleOrder
            .date(UPDATED_DATE)
            .notes(UPDATED_NOTES)
            .tvaRate(UPDATED_TVA_RATE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .saleType(UPDATED_SALE_TYPE);

        restSaleOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSaleOrder))
            )
            .andExpect(status().isOk());

        // Validate the SaleOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleOrderUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSaleOrder, saleOrder),
            getPersistedSaleOrder(saleOrder)
        );
    }

    @Test
    @Transactional
    void fullUpdateSaleOrderWithPatch() throws Exception {
        // Initialize the database
        insertedSaleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleOrder using partial update
        SaleOrder partialUpdatedSaleOrder = new SaleOrder();
        partialUpdatedSaleOrder.setId(saleOrder.getId());

        partialUpdatedSaleOrder
            .reference(UPDATED_REFERENCE)
            .date(UPDATED_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .notes(UPDATED_NOTES)
            .status(UPDATED_STATUS)
            .tvaRate(UPDATED_TVA_RATE)
            .stampRate(UPDATED_STAMP_RATE)
            .discountRate(UPDATED_DISCOUNT_RATE)
            .tvaAmount(UPDATED_TVA_AMOUNT)
            .stampAmount(UPDATED_STAMP_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .subTotal(UPDATED_SUB_TOTAL)
            .total(UPDATED_TOTAL)
            .saleType(UPDATED_SALE_TYPE);

        restSaleOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSaleOrder))
            )
            .andExpect(status().isOk());

        // Validate the SaleOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleOrderUpdatableFieldsEquals(partialUpdatedSaleOrder, getPersistedSaleOrder(partialUpdatedSaleOrder));
    }

    @Test
    @Transactional
    void patchNonExistingSaleOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOrder.setId(longCount.incrementAndGet());

        // Create the SaleOrder
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, saleOrderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSaleOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOrder.setId(longCount.incrementAndGet());

        // Create the SaleOrder
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSaleOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOrder.setId(longCount.incrementAndGet());

        // Create the SaleOrder
        SaleOrderDTO saleOrderDTO = saleOrderMapper.toDto(saleOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(saleOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSaleOrder() throws Exception {
        // Initialize the database
        insertedSaleOrder = saleOrderRepository.saveAndFlush(saleOrder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the saleOrder
        restSaleOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, saleOrder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return saleOrderRepository.count();
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

    protected SaleOrder getPersistedSaleOrder(SaleOrder saleOrder) {
        return saleOrderRepository.findById(saleOrder.getId()).orElseThrow();
    }

    protected void assertPersistedSaleOrderToMatchAllProperties(SaleOrder expectedSaleOrder) {
        assertSaleOrderAllPropertiesEquals(expectedSaleOrder, getPersistedSaleOrder(expectedSaleOrder));
    }

    protected void assertPersistedSaleOrderToMatchUpdatableProperties(SaleOrder expectedSaleOrder) {
        assertSaleOrderAllUpdatablePropertiesEquals(expectedSaleOrder, getPersistedSaleOrder(expectedSaleOrder));
    }
}
