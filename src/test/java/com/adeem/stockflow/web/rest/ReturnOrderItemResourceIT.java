package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.ReturnOrderItemAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.ReturnOrderItem;
import com.adeem.stockflow.domain.enumeration.ItemCondition;
import com.adeem.stockflow.domain.enumeration.ReturnReason;
import com.adeem.stockflow.repository.ReturnOrderItemRepository;
import com.adeem.stockflow.service.dto.ReturnOrderItemDTO;
import com.adeem.stockflow.service.mapper.ReturnOrderItemMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ReturnOrderItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReturnOrderItemResourceIT {

    private static final BigDecimal DEFAULT_QUANTITY = new BigDecimal(1);
    private static final BigDecimal UPDATED_QUANTITY = new BigDecimal(2);

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(2);

    private static final BigDecimal DEFAULT_ALLOCATED_DISCOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_ALLOCATED_DISCOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(2);

    private static final ReturnReason DEFAULT_RETURN_REASON = ReturnReason.DAMAGED;
    private static final ReturnReason UPDATED_RETURN_REASON = ReturnReason.DEFECTIVE;

    private static final ItemCondition DEFAULT_CONDITION = ItemCondition.NEW;
    private static final ItemCondition UPDATED_CONDITION = ItemCondition.LIKE_NEW;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_REFUNDABLE = false;
    private static final Boolean UPDATED_IS_REFUNDABLE = true;

    private static final Boolean DEFAULT_IS_RESTOCKABLE = false;
    private static final Boolean UPDATED_IS_RESTOCKABLE = true;

    private static final String ENTITY_API_URL = "/api/return-order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReturnOrderItemRepository returnOrderItemRepository;

    @Autowired
    private ReturnOrderItemMapper returnOrderItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReturnOrderItemMockMvc;

    private ReturnOrderItem returnOrderItem;

    private ReturnOrderItem insertedReturnOrderItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnOrderItem createEntity() {
        return new ReturnOrderItem()
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .subtotal(DEFAULT_SUBTOTAL)
            .allocatedDiscount(DEFAULT_ALLOCATED_DISCOUNT)
            .total(DEFAULT_TOTAL)
            .returnReason(DEFAULT_RETURN_REASON)
            .condition(DEFAULT_CONDITION)
            .notes(DEFAULT_NOTES)
            .isRefundable(DEFAULT_IS_REFUNDABLE)
            .isRestockable(DEFAULT_IS_RESTOCKABLE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnOrderItem createUpdatedEntity() {
        return new ReturnOrderItem()
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .subtotal(UPDATED_SUBTOTAL)
            .allocatedDiscount(UPDATED_ALLOCATED_DISCOUNT)
            .total(UPDATED_TOTAL)
            .returnReason(UPDATED_RETURN_REASON)
            .condition(UPDATED_CONDITION)
            .notes(UPDATED_NOTES)
            .isRefundable(UPDATED_IS_REFUNDABLE)
            .isRestockable(UPDATED_IS_RESTOCKABLE);
    }

    @BeforeEach
    void initTest() {
        returnOrderItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedReturnOrderItem != null) {
            returnOrderItemRepository.delete(insertedReturnOrderItem);
            insertedReturnOrderItem = null;
        }
    }

    @Test
    @Transactional
    void createReturnOrderItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReturnOrderItem
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);
        var returnedReturnOrderItemDTO = om.readValue(
            restReturnOrderItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReturnOrderItemDTO.class
        );

        // Validate the ReturnOrderItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReturnOrderItem = returnOrderItemMapper.toEntity(returnedReturnOrderItemDTO);
        assertReturnOrderItemUpdatableFieldsEquals(returnedReturnOrderItem, getPersistedReturnOrderItem(returnedReturnOrderItem));

        insertedReturnOrderItem = returnedReturnOrderItem;
    }

    @Test
    @Transactional
    void createReturnOrderItemWithExistingId() throws Exception {
        // Create the ReturnOrderItem with an existing ID
        returnOrderItem.setId(1L);
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReturnOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ReturnOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrderItem.setQuantity(null);

        // Create the ReturnOrderItem, which fails.
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        restReturnOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrderItem.setUnitPrice(null);

        // Create the ReturnOrderItem, which fails.
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        restReturnOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubtotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrderItem.setSubtotal(null);

        // Create the ReturnOrderItem, which fails.
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        restReturnOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReturnReasonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrderItem.setReturnReason(null);

        // Create the ReturnOrderItem, which fails.
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        restReturnOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkConditionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrderItem.setCondition(null);

        // Create the ReturnOrderItem, which fails.
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        restReturnOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsRefundableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrderItem.setIsRefundable(null);

        // Create the ReturnOrderItem, which fails.
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        restReturnOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsRestockableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnOrderItem.setIsRestockable(null);

        // Create the ReturnOrderItem, which fails.
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        restReturnOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReturnOrderItems() throws Exception {
        // Initialize the database
        insertedReturnOrderItem = returnOrderItemRepository.saveAndFlush(returnOrderItem);

        // Get all the returnOrderItemList
        restReturnOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(returnOrderItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))))
            .andExpect(jsonPath("$.[*].allocatedDiscount").value(hasItem(sameNumber(DEFAULT_ALLOCATED_DISCOUNT))))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))))
            .andExpect(jsonPath("$.[*].returnReason").value(hasItem(DEFAULT_RETURN_REASON.toString())))
            .andExpect(jsonPath("$.[*].condition").value(hasItem(DEFAULT_CONDITION.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].isRefundable").value(hasItem(DEFAULT_IS_REFUNDABLE)))
            .andExpect(jsonPath("$.[*].isRestockable").value(hasItem(DEFAULT_IS_RESTOCKABLE)));
    }

    @Test
    @Transactional
    void getReturnOrderItem() throws Exception {
        // Initialize the database
        insertedReturnOrderItem = returnOrderItemRepository.saveAndFlush(returnOrderItem);

        // Get the returnOrderItem
        restReturnOrderItemMockMvc
            .perform(get(ENTITY_API_URL_ID, returnOrderItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(returnOrderItem.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(sameNumber(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.subtotal").value(sameNumber(DEFAULT_SUBTOTAL)))
            .andExpect(jsonPath("$.allocatedDiscount").value(sameNumber(DEFAULT_ALLOCATED_DISCOUNT)))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.returnReason").value(DEFAULT_RETURN_REASON.toString()))
            .andExpect(jsonPath("$.condition").value(DEFAULT_CONDITION.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.isRefundable").value(DEFAULT_IS_REFUNDABLE))
            .andExpect(jsonPath("$.isRestockable").value(DEFAULT_IS_RESTOCKABLE));
    }

    @Test
    @Transactional
    void getNonExistingReturnOrderItem() throws Exception {
        // Get the returnOrderItem
        restReturnOrderItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReturnOrderItem() throws Exception {
        // Initialize the database
        insertedReturnOrderItem = returnOrderItemRepository.saveAndFlush(returnOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnOrderItem
        ReturnOrderItem updatedReturnOrderItem = returnOrderItemRepository.findById(returnOrderItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReturnOrderItem are not directly saved in db
        em.detach(updatedReturnOrderItem);
        updatedReturnOrderItem
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .subtotal(UPDATED_SUBTOTAL)
            .allocatedDiscount(UPDATED_ALLOCATED_DISCOUNT)
            .total(UPDATED_TOTAL)
            .returnReason(UPDATED_RETURN_REASON)
            .condition(UPDATED_CONDITION)
            .notes(UPDATED_NOTES)
            .isRefundable(UPDATED_IS_REFUNDABLE)
            .isRestockable(UPDATED_IS_RESTOCKABLE);
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(updatedReturnOrderItem);

        restReturnOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnOrderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnOrderItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReturnOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReturnOrderItemToMatchAllProperties(updatedReturnOrderItem);
    }

    @Test
    @Transactional
    void putNonExistingReturnOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnOrderItem.setId(longCount.incrementAndGet());

        // Create the ReturnOrderItem
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnOrderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReturnOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnOrderItem.setId(longCount.incrementAndGet());

        // Create the ReturnOrderItem
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReturnOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnOrderItem.setId(longCount.incrementAndGet());

        // Create the ReturnOrderItem
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnOrderItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnOrderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReturnOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedReturnOrderItem = returnOrderItemRepository.saveAndFlush(returnOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnOrderItem using partial update
        ReturnOrderItem partialUpdatedReturnOrderItem = new ReturnOrderItem();
        partialUpdatedReturnOrderItem.setId(returnOrderItem.getId());

        partialUpdatedReturnOrderItem
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .allocatedDiscount(UPDATED_ALLOCATED_DISCOUNT)
            .total(UPDATED_TOTAL)
            .returnReason(UPDATED_RETURN_REASON)
            .notes(UPDATED_NOTES);

        restReturnOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the ReturnOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnOrderItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReturnOrderItem, returnOrderItem),
            getPersistedReturnOrderItem(returnOrderItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateReturnOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedReturnOrderItem = returnOrderItemRepository.saveAndFlush(returnOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnOrderItem using partial update
        ReturnOrderItem partialUpdatedReturnOrderItem = new ReturnOrderItem();
        partialUpdatedReturnOrderItem.setId(returnOrderItem.getId());

        partialUpdatedReturnOrderItem
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .subtotal(UPDATED_SUBTOTAL)
            .allocatedDiscount(UPDATED_ALLOCATED_DISCOUNT)
            .total(UPDATED_TOTAL)
            .returnReason(UPDATED_RETURN_REASON)
            .condition(UPDATED_CONDITION)
            .notes(UPDATED_NOTES)
            .isRefundable(UPDATED_IS_REFUNDABLE)
            .isRestockable(UPDATED_IS_RESTOCKABLE);

        restReturnOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the ReturnOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnOrderItemUpdatableFieldsEquals(
            partialUpdatedReturnOrderItem,
            getPersistedReturnOrderItem(partialUpdatedReturnOrderItem)
        );
    }

    @Test
    @Transactional
    void patchNonExistingReturnOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnOrderItem.setId(longCount.incrementAndGet());

        // Create the ReturnOrderItem
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, returnOrderItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReturnOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnOrderItem.setId(longCount.incrementAndGet());

        // Create the ReturnOrderItem
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReturnOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnOrderItem.setId(longCount.incrementAndGet());

        // Create the ReturnOrderItem
        ReturnOrderItemDTO returnOrderItemDTO = returnOrderItemMapper.toDto(returnOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnOrderItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(returnOrderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReturnOrderItem() throws Exception {
        // Initialize the database
        insertedReturnOrderItem = returnOrderItemRepository.saveAndFlush(returnOrderItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the returnOrderItem
        restReturnOrderItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, returnOrderItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return returnOrderItemRepository.count();
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

    protected ReturnOrderItem getPersistedReturnOrderItem(ReturnOrderItem returnOrderItem) {
        return returnOrderItemRepository.findById(returnOrderItem.getId()).orElseThrow();
    }

    protected void assertPersistedReturnOrderItemToMatchAllProperties(ReturnOrderItem expectedReturnOrderItem) {
        assertReturnOrderItemAllPropertiesEquals(expectedReturnOrderItem, getPersistedReturnOrderItem(expectedReturnOrderItem));
    }

    protected void assertPersistedReturnOrderItemToMatchUpdatableProperties(ReturnOrderItem expectedReturnOrderItem) {
        assertReturnOrderItemAllUpdatablePropertiesEquals(expectedReturnOrderItem, getPersistedReturnOrderItem(expectedReturnOrderItem));
    }
}
