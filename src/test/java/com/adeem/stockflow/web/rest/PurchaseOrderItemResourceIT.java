package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.PurchaseOrderItemAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.PurchaseOrderItem;
import com.adeem.stockflow.repository.PurchaseOrderItemRepository;
import com.adeem.stockflow.service.dto.PurchaseOrderItemDTO;
import com.adeem.stockflow.service.mapper.PurchaseOrderItemMapper;
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
 * Integration tests for the {@link PurchaseOrderItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PurchaseOrderItemResourceIT {

    private static final BigDecimal DEFAULT_QUANTITY = new BigDecimal(1);
    private static final BigDecimal UPDATED_QUANTITY = new BigDecimal(2);

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/purchase-order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Autowired
    private PurchaseOrderItemMapper purchaseOrderItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchaseOrderItemMockMvc;

    private PurchaseOrderItem purchaseOrderItem;

    private PurchaseOrderItem insertedPurchaseOrderItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseOrderItem createEntity() {
        return new PurchaseOrderItem().quantity(DEFAULT_QUANTITY).unitPrice(DEFAULT_UNIT_PRICE).total(DEFAULT_TOTAL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseOrderItem createUpdatedEntity() {
        return new PurchaseOrderItem().quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).total(UPDATED_TOTAL);
    }

    @BeforeEach
    void initTest() {
        purchaseOrderItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPurchaseOrderItem != null) {
            purchaseOrderItemRepository.delete(insertedPurchaseOrderItem);
            insertedPurchaseOrderItem = null;
        }
    }

    @Test
    @Transactional
    void createPurchaseOrderItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PurchaseOrderItem
        PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemMapper.toDto(purchaseOrderItem);
        var returnedPurchaseOrderItemDTO = om.readValue(
            restPurchaseOrderItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PurchaseOrderItemDTO.class
        );

        // Validate the PurchaseOrderItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPurchaseOrderItem = purchaseOrderItemMapper.toEntity(returnedPurchaseOrderItemDTO);
        assertPurchaseOrderItemUpdatableFieldsEquals(returnedPurchaseOrderItem, getPersistedPurchaseOrderItem(returnedPurchaseOrderItem));

        insertedPurchaseOrderItem = returnedPurchaseOrderItem;
    }

    @Test
    @Transactional
    void createPurchaseOrderItemWithExistingId() throws Exception {
        // Create the PurchaseOrderItem with an existing ID
        purchaseOrderItem.setId(1L);
        PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemMapper.toDto(purchaseOrderItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPurchaseOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchaseOrderItem.setQuantity(null);

        // Create the PurchaseOrderItem, which fails.
        PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemMapper.toDto(purchaseOrderItem);

        restPurchaseOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchaseOrderItem.setUnitPrice(null);

        // Create the PurchaseOrderItem, which fails.
        PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemMapper.toDto(purchaseOrderItem);

        restPurchaseOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        purchaseOrderItem.setTotal(null);

        // Create the PurchaseOrderItem, which fails.
        PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemMapper.toDto(purchaseOrderItem);

        restPurchaseOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPurchaseOrderItems() throws Exception {
        // Initialize the database
        insertedPurchaseOrderItem = purchaseOrderItemRepository.saveAndFlush(purchaseOrderItem);

        // Get all the purchaseOrderItemList
        restPurchaseOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseOrderItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))));
    }

    @Test
    @Transactional
    void getPurchaseOrderItem() throws Exception {
        // Initialize the database
        insertedPurchaseOrderItem = purchaseOrderItemRepository.saveAndFlush(purchaseOrderItem);

        // Get the purchaseOrderItem
        restPurchaseOrderItemMockMvc
            .perform(get(ENTITY_API_URL_ID, purchaseOrderItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchaseOrderItem.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(sameNumber(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)));
    }

    @Test
    @Transactional
    void getNonExistingPurchaseOrderItem() throws Exception {
        // Get the purchaseOrderItem
        restPurchaseOrderItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPurchaseOrderItem() throws Exception {
        // Initialize the database
        insertedPurchaseOrderItem = purchaseOrderItemRepository.saveAndFlush(purchaseOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchaseOrderItem
        PurchaseOrderItem updatedPurchaseOrderItem = purchaseOrderItemRepository.findById(purchaseOrderItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPurchaseOrderItem are not directly saved in db
        em.detach(updatedPurchaseOrderItem);
        updatedPurchaseOrderItem.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).total(UPDATED_TOTAL);
        PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemMapper.toDto(updatedPurchaseOrderItem);

        restPurchaseOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseOrderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseOrderItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPurchaseOrderItemToMatchAllProperties(updatedPurchaseOrderItem);
    }

    @Test
    @Transactional
    void putNonExistingPurchaseOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrderItem.setId(longCount.incrementAndGet());

        // Create the PurchaseOrderItem
        PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemMapper.toDto(purchaseOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseOrderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchaseOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrderItem.setId(longCount.incrementAndGet());

        // Create the PurchaseOrderItem
        PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemMapper.toDto(purchaseOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchaseOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrderItem.setId(longCount.incrementAndGet());

        // Create the PurchaseOrderItem
        PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemMapper.toDto(purchaseOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOrderItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePurchaseOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedPurchaseOrderItem = purchaseOrderItemRepository.saveAndFlush(purchaseOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchaseOrderItem using partial update
        PurchaseOrderItem partialUpdatedPurchaseOrderItem = new PurchaseOrderItem();
        partialUpdatedPurchaseOrderItem.setId(purchaseOrderItem.getId());

        partialUpdatedPurchaseOrderItem.unitPrice(UPDATED_UNIT_PRICE);

        restPurchaseOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchaseOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchaseOrderItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPurchaseOrderItem, purchaseOrderItem),
            getPersistedPurchaseOrderItem(purchaseOrderItem)
        );
    }

    @Test
    @Transactional
    void fullUpdatePurchaseOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedPurchaseOrderItem = purchaseOrderItemRepository.saveAndFlush(purchaseOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchaseOrderItem using partial update
        PurchaseOrderItem partialUpdatedPurchaseOrderItem = new PurchaseOrderItem();
        partialUpdatedPurchaseOrderItem.setId(purchaseOrderItem.getId());

        partialUpdatedPurchaseOrderItem.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).total(UPDATED_TOTAL);

        restPurchaseOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchaseOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchaseOrderItemUpdatableFieldsEquals(
            partialUpdatedPurchaseOrderItem,
            getPersistedPurchaseOrderItem(partialUpdatedPurchaseOrderItem)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPurchaseOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrderItem.setId(longCount.incrementAndGet());

        // Create the PurchaseOrderItem
        PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemMapper.toDto(purchaseOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, purchaseOrderItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchaseOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPurchaseOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrderItem.setId(longCount.incrementAndGet());

        // Create the PurchaseOrderItem
        PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemMapper.toDto(purchaseOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchaseOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPurchaseOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrderItem.setId(longCount.incrementAndGet());

        // Create the PurchaseOrderItem
        PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemMapper.toDto(purchaseOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseOrderItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(purchaseOrderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchaseOrderItem() throws Exception {
        // Initialize the database
        insertedPurchaseOrderItem = purchaseOrderItemRepository.saveAndFlush(purchaseOrderItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the purchaseOrderItem
        restPurchaseOrderItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchaseOrderItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return purchaseOrderItemRepository.count();
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

    protected PurchaseOrderItem getPersistedPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
        return purchaseOrderItemRepository.findById(purchaseOrderItem.getId()).orElseThrow();
    }

    protected void assertPersistedPurchaseOrderItemToMatchAllProperties(PurchaseOrderItem expectedPurchaseOrderItem) {
        assertPurchaseOrderItemAllPropertiesEquals(expectedPurchaseOrderItem, getPersistedPurchaseOrderItem(expectedPurchaseOrderItem));
    }

    protected void assertPersistedPurchaseOrderItemToMatchUpdatableProperties(PurchaseOrderItem expectedPurchaseOrderItem) {
        assertPurchaseOrderItemAllUpdatablePropertiesEquals(
            expectedPurchaseOrderItem,
            getPersistedPurchaseOrderItem(expectedPurchaseOrderItem)
        );
    }
}
