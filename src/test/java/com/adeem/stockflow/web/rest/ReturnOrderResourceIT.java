package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.ReturnOrderAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameInstant;
import static com.adeem.stockflow.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.ReturnOrder;
import com.adeem.stockflow.domain.enumeration.DiscountAllocationMethod;
import com.adeem.stockflow.domain.enumeration.ReturnStatus;
import com.adeem.stockflow.domain.enumeration.ReturnType;
import com.adeem.stockflow.repository.ReturnOrderRepository;
import com.adeem.stockflow.service.dto.ReturnOrderDTO;
import com.adeem.stockflow.service.mapper.ReturnOrderMapper;
import com.adeem.stockflow.web.rest.uncostomized.ReturnOrderResource;
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
 * Integration tests for the {@link ReturnOrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReturnOrderResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_RETURN_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_RETURN_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_PROCESSED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_PROCESSED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ReturnStatus DEFAULT_STATUS = ReturnStatus.DRAFT;
    private static final ReturnStatus UPDATED_STATUS = ReturnStatus.PENDING;

    private static final ReturnType DEFAULT_RETURN_TYPE = ReturnType.CUSTOMER_RETURN;
    private static final ReturnType UPDATED_RETURN_TYPE = ReturnType.SUPPLIER_RETURN;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_REFUND_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_REFUND_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_ORIGINAL_ORDER_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_ORDER_REFERENCE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_PARTIAL_RETURN = false;
    private static final Boolean UPDATED_IS_PARTIAL_RETURN = true;

    private static final DiscountAllocationMethod DEFAULT_DISCOUNT_ALLOCATION_METHOD = DiscountAllocationMethod.PROPORTIONAL;
    private static final DiscountAllocationMethod UPDATED_DISCOUNT_ALLOCATION_METHOD = DiscountAllocationMethod.UNIT_PRICE;

    private static final String ENTITY_API_URL = "/api/return-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    @Autowired
    private ReturnOrderMapper returnOrderMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReturnOrderMockMvc;

    private ReturnOrder returnOrder;

    private ReturnOrder insertedReturnOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnOrder createEntity() {
        return new ReturnOrder()
            .reference(DEFAULT_REFERENCE)
            .returnDate(DEFAULT_RETURN_DATE)
            .processedDate(DEFAULT_PROCESSED_DATE)
            .status(DEFAULT_STATUS)
            .returnType(DEFAULT_RETURN_TYPE)
            .notes(DEFAULT_NOTES)
            .refundAmount(DEFAULT_REFUND_AMOUNT)
            .originalOrderReference(DEFAULT_ORIGINAL_ORDER_REFERENCE)
            .isPartialReturn(DEFAULT_IS_PARTIAL_RETURN)
            .discountAllocationMethod(DEFAULT_DISCOUNT_ALLOCATION_METHOD);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnOrder createUpdatedEntity() {
        return new ReturnOrder()
            .reference(UPDATED_REFERENCE)
            .returnDate(UPDATED_RETURN_DATE)
            .processedDate(UPDATED_PROCESSED_DATE)
            .status(UPDATED_STATUS)
            .returnType(UPDATED_RETURN_TYPE)
            .notes(UPDATED_NOTES)
            .refundAmount(UPDATED_REFUND_AMOUNT)
            .originalOrderReference(UPDATED_ORIGINAL_ORDER_REFERENCE)
            .isPartialReturn(UPDATED_IS_PARTIAL_RETURN)
            .discountAllocationMethod(UPDATED_DISCOUNT_ALLOCATION_METHOD);
    }

    @BeforeEach
    void initTest() {
        returnOrder = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedReturnOrder != null) {
            returnOrderRepository.delete(insertedReturnOrder);
            insertedReturnOrder = null;
        }
    }

    @Test
    @Transactional
    void createReturnOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReturnOrder
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);
        var returnedReturnOrderDTO = om.readValue(
            restReturnOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReturnOrderDTO.class
        );

        // Validate the ReturnOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReturnOrder = returnOrderMapper.toEntity(returnedReturnOrderDTO);
        assertReturnOrderUpdatableFieldsEquals(returnedReturnOrder, getPersistedReturnOrder(returnedReturnOrder));

        insertedReturnOrder = returnedReturnOrder;
    }

    @Test
    @Transactional
    void createReturnOrderWithExistingId() throws Exception {
        // Create the ReturnOrder with an existing ID
        returnOrder.setId(1L);
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReturnOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ReturnOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrder.setReference(null);

        // Create the ReturnOrder, which fails.
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        restReturnOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReturnDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrder.setReturnDate(null);

        // Create the ReturnOrder, which fails.
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        restReturnOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrder.setStatus(null);

        // Create the ReturnOrder, which fails.
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        restReturnOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReturnTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrder.setReturnType(null);

        // Create the ReturnOrder, which fails.
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        restReturnOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOriginalOrderReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrder.setOriginalOrderReference(null);

        // Create the ReturnOrder, which fails.
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        restReturnOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsPartialReturnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrder.setIsPartialReturn(null);

        // Create the ReturnOrder, which fails.
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        restReturnOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReturnOrders() throws Exception {
        // Initialize the database
        insertedReturnOrder = returnOrderRepository.saveAndFlush(returnOrder);

        // Get all the returnOrderList
        restReturnOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(returnOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(sameInstant(DEFAULT_RETURN_DATE))))
            .andExpect(jsonPath("$.[*].processedDate").value(hasItem(sameInstant(DEFAULT_PROCESSED_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].returnType").value(hasItem(DEFAULT_RETURN_TYPE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].refundAmount").value(hasItem(sameNumber(DEFAULT_REFUND_AMOUNT))))
            .andExpect(jsonPath("$.[*].originalOrderReference").value(hasItem(DEFAULT_ORIGINAL_ORDER_REFERENCE)))
            .andExpect(jsonPath("$.[*].isPartialReturn").value(hasItem(DEFAULT_IS_PARTIAL_RETURN)))
            .andExpect(jsonPath("$.[*].discountAllocationMethod").value(hasItem(DEFAULT_DISCOUNT_ALLOCATION_METHOD.toString())));
    }

    @Test
    @Transactional
    void getReturnOrder() throws Exception {
        // Initialize the database
        insertedReturnOrder = returnOrderRepository.saveAndFlush(returnOrder);

        // Get the returnOrder
        restReturnOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, returnOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(returnOrder.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.returnDate").value(sameInstant(DEFAULT_RETURN_DATE)))
            .andExpect(jsonPath("$.processedDate").value(sameInstant(DEFAULT_PROCESSED_DATE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.returnType").value(DEFAULT_RETURN_TYPE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.refundAmount").value(sameNumber(DEFAULT_REFUND_AMOUNT)))
            .andExpect(jsonPath("$.originalOrderReference").value(DEFAULT_ORIGINAL_ORDER_REFERENCE))
            .andExpect(jsonPath("$.isPartialReturn").value(DEFAULT_IS_PARTIAL_RETURN))
            .andExpect(jsonPath("$.discountAllocationMethod").value(DEFAULT_DISCOUNT_ALLOCATION_METHOD.toString()));
    }

    @Test
    @Transactional
    void getNonExistingReturnOrder() throws Exception {
        // Get the returnOrder
        restReturnOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReturnOrder() throws Exception {
        // Initialize the database
        insertedReturnOrder = returnOrderRepository.saveAndFlush(returnOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnOrder
        ReturnOrder updatedReturnOrder = returnOrderRepository.findById(returnOrder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReturnOrder are not directly saved in db
        em.detach(updatedReturnOrder);
        updatedReturnOrder
            .reference(UPDATED_REFERENCE)
            .returnDate(UPDATED_RETURN_DATE)
            .processedDate(UPDATED_PROCESSED_DATE)
            .status(UPDATED_STATUS)
            .returnType(UPDATED_RETURN_TYPE)
            .notes(UPDATED_NOTES)
            .refundAmount(UPDATED_REFUND_AMOUNT)
            .originalOrderReference(UPDATED_ORIGINAL_ORDER_REFERENCE)
            .isPartialReturn(UPDATED_IS_PARTIAL_RETURN)
            .discountAllocationMethod(UPDATED_DISCOUNT_ALLOCATION_METHOD);
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(updatedReturnOrder);

        restReturnOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnOrderDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReturnOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReturnOrderToMatchAllProperties(updatedReturnOrder);
    }

    @Test
    @Transactional
    void putNonExistingReturnOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnOrder.setId(longCount.incrementAndGet());

        // Create the ReturnOrder
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReturnOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnOrder.setId(longCount.incrementAndGet());

        // Create the ReturnOrder
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReturnOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnOrder.setId(longCount.incrementAndGet());

        // Create the ReturnOrder
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReturnOrderWithPatch() throws Exception {
        // Initialize the database
        insertedReturnOrder = returnOrderRepository.saveAndFlush(returnOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnOrder using partial update
        ReturnOrder partialUpdatedReturnOrder = new ReturnOrder();
        partialUpdatedReturnOrder.setId(returnOrder.getId());

        partialUpdatedReturnOrder
            .reference(UPDATED_REFERENCE)
            .status(UPDATED_STATUS)
            .returnType(UPDATED_RETURN_TYPE)
            .refundAmount(UPDATED_REFUND_AMOUNT)
            .isPartialReturn(UPDATED_IS_PARTIAL_RETURN)
            .discountAllocationMethod(UPDATED_DISCOUNT_ALLOCATION_METHOD);

        restReturnOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnOrder))
            )
            .andExpect(status().isOk());

        // Validate the ReturnOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnOrderUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReturnOrder, returnOrder),
            getPersistedReturnOrder(returnOrder)
        );
    }

    @Test
    @Transactional
    void fullUpdateReturnOrderWithPatch() throws Exception {
        // Initialize the database
        insertedReturnOrder = returnOrderRepository.saveAndFlush(returnOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnOrder using partial update
        ReturnOrder partialUpdatedReturnOrder = new ReturnOrder();
        partialUpdatedReturnOrder.setId(returnOrder.getId());

        partialUpdatedReturnOrder
            .reference(UPDATED_REFERENCE)
            .returnDate(UPDATED_RETURN_DATE)
            .processedDate(UPDATED_PROCESSED_DATE)
            .status(UPDATED_STATUS)
            .returnType(UPDATED_RETURN_TYPE)
            .notes(UPDATED_NOTES)
            .refundAmount(UPDATED_REFUND_AMOUNT)
            .originalOrderReference(UPDATED_ORIGINAL_ORDER_REFERENCE)
            .isPartialReturn(UPDATED_IS_PARTIAL_RETURN)
            .discountAllocationMethod(UPDATED_DISCOUNT_ALLOCATION_METHOD);

        restReturnOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnOrder))
            )
            .andExpect(status().isOk());

        // Validate the ReturnOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnOrderUpdatableFieldsEquals(partialUpdatedReturnOrder, getPersistedReturnOrder(partialUpdatedReturnOrder));
    }

    @Test
    @Transactional
    void patchNonExistingReturnOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnOrder.setId(longCount.incrementAndGet());

        // Create the ReturnOrder
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, returnOrderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReturnOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnOrder.setId(longCount.incrementAndGet());

        // Create the ReturnOrder
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReturnOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnOrder.setId(longCount.incrementAndGet());

        // Create the ReturnOrder
        ReturnOrderDTO returnOrderDTO = returnOrderMapper.toDto(returnOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(returnOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReturnOrder() throws Exception {
        // Initialize the database
        insertedReturnOrder = returnOrderRepository.saveAndFlush(returnOrder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the returnOrder
        restReturnOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, returnOrder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return returnOrderRepository.count();
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

    protected ReturnOrder getPersistedReturnOrder(ReturnOrder returnOrder) {
        return returnOrderRepository.findById(returnOrder.getId()).orElseThrow();
    }

    protected void assertPersistedReturnOrderToMatchAllProperties(ReturnOrder expectedReturnOrder) {
        assertReturnOrderAllPropertiesEquals(expectedReturnOrder, getPersistedReturnOrder(expectedReturnOrder));
    }

    protected void assertPersistedReturnOrderToMatchUpdatableProperties(ReturnOrder expectedReturnOrder) {
        assertReturnOrderAllUpdatablePropertiesEquals(expectedReturnOrder, getPersistedReturnOrder(expectedReturnOrder));
    }
}
