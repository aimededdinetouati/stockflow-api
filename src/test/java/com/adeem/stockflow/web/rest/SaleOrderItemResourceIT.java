package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.SaleOrderItemAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.SaleOrderItem;
import com.adeem.stockflow.repository.SaleOrderItemRepository;
import com.adeem.stockflow.service.dto.SaleOrderItemDTO;
import com.adeem.stockflow.service.mapper.SaleOrderItemMapper;
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
 * Integration tests for the {@link SaleOrderItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SaleOrderItemResourceIT {

    private static final BigDecimal DEFAULT_QUANTITY = new BigDecimal(1);
    private static final BigDecimal UPDATED_QUANTITY = new BigDecimal(2);

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/sale-order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SaleOrderItemRepository saleOrderItemRepository;

    @Autowired
    private SaleOrderItemMapper saleOrderItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSaleOrderItemMockMvc;

    private SaleOrderItem saleOrderItem;

    private SaleOrderItem insertedSaleOrderItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleOrderItem createEntity() {
        return new SaleOrderItem().quantity(DEFAULT_QUANTITY).unitPrice(DEFAULT_UNIT_PRICE).total(DEFAULT_TOTAL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleOrderItem createUpdatedEntity() {
        return new SaleOrderItem().quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).total(UPDATED_TOTAL);
    }

    @BeforeEach
    void initTest() {
        saleOrderItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSaleOrderItem != null) {
            saleOrderItemRepository.delete(insertedSaleOrderItem);
            insertedSaleOrderItem = null;
        }
    }

    @Test
    @Transactional
    void createSaleOrderItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SaleOrderItem
        SaleOrderItemDTO saleOrderItemDTO = saleOrderItemMapper.toDto(saleOrderItem);
        var returnedSaleOrderItemDTO = om.readValue(
            restSaleOrderItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOrderItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SaleOrderItemDTO.class
        );

        // Validate the SaleOrderItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSaleOrderItem = saleOrderItemMapper.toEntity(returnedSaleOrderItemDTO);
        assertSaleOrderItemUpdatableFieldsEquals(returnedSaleOrderItem, getPersistedSaleOrderItem(returnedSaleOrderItem));

        insertedSaleOrderItem = returnedSaleOrderItem;
    }

    @Test
    @Transactional
    void createSaleOrderItemWithExistingId() throws Exception {
        // Create the SaleOrderItem with an existing ID
        saleOrderItem.setId(1L);
        SaleOrderItemDTO saleOrderItemDTO = saleOrderItemMapper.toDto(saleOrderItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaleOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOrderItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SaleOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleOrderItem.setQuantity(null);

        // Create the SaleOrderItem, which fails.
        SaleOrderItemDTO saleOrderItemDTO = saleOrderItemMapper.toDto(saleOrderItem);

        restSaleOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleOrderItem.setUnitPrice(null);

        // Create the SaleOrderItem, which fails.
        SaleOrderItemDTO saleOrderItemDTO = saleOrderItemMapper.toDto(saleOrderItem);

        restSaleOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleOrderItem.setTotal(null);

        // Create the SaleOrderItem, which fails.
        SaleOrderItemDTO saleOrderItemDTO = saleOrderItemMapper.toDto(saleOrderItem);

        restSaleOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSaleOrderItems() throws Exception {
        // Initialize the database
        insertedSaleOrderItem = saleOrderItemRepository.saveAndFlush(saleOrderItem);

        // Get all the saleOrderItemList
        restSaleOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleOrderItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))));
    }

    @Test
    @Transactional
    void getSaleOrderItem() throws Exception {
        // Initialize the database
        insertedSaleOrderItem = saleOrderItemRepository.saveAndFlush(saleOrderItem);

        // Get the saleOrderItem
        restSaleOrderItemMockMvc
            .perform(get(ENTITY_API_URL_ID, saleOrderItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(saleOrderItem.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(sameNumber(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)));
    }

    @Test
    @Transactional
    void getNonExistingSaleOrderItem() throws Exception {
        // Get the saleOrderItem
        restSaleOrderItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSaleOrderItem() throws Exception {
        // Initialize the database
        insertedSaleOrderItem = saleOrderItemRepository.saveAndFlush(saleOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleOrderItem
        SaleOrderItem updatedSaleOrderItem = saleOrderItemRepository.findById(saleOrderItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSaleOrderItem are not directly saved in db
        em.detach(updatedSaleOrderItem);
        updatedSaleOrderItem.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).total(UPDATED_TOTAL);
        SaleOrderItemDTO saleOrderItemDTO = saleOrderItemMapper.toDto(updatedSaleOrderItem);

        restSaleOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleOrderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleOrderItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the SaleOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSaleOrderItemToMatchAllProperties(updatedSaleOrderItem);
    }

    @Test
    @Transactional
    void putNonExistingSaleOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOrderItem.setId(longCount.incrementAndGet());

        // Create the SaleOrderItem
        SaleOrderItemDTO saleOrderItemDTO = saleOrderItemMapper.toDto(saleOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleOrderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSaleOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOrderItem.setId(longCount.incrementAndGet());

        // Create the SaleOrderItem
        SaleOrderItemDTO saleOrderItemDTO = saleOrderItemMapper.toDto(saleOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSaleOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOrderItem.setId(longCount.incrementAndGet());

        // Create the SaleOrderItem
        SaleOrderItemDTO saleOrderItemDTO = saleOrderItemMapper.toDto(saleOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleOrderItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleOrderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSaleOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedSaleOrderItem = saleOrderItemRepository.saveAndFlush(saleOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleOrderItem using partial update
        SaleOrderItem partialUpdatedSaleOrderItem = new SaleOrderItem();
        partialUpdatedSaleOrderItem.setId(saleOrderItem.getId());

        partialUpdatedSaleOrderItem.total(UPDATED_TOTAL);

        restSaleOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSaleOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the SaleOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleOrderItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSaleOrderItem, saleOrderItem),
            getPersistedSaleOrderItem(saleOrderItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateSaleOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedSaleOrderItem = saleOrderItemRepository.saveAndFlush(saleOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleOrderItem using partial update
        SaleOrderItem partialUpdatedSaleOrderItem = new SaleOrderItem();
        partialUpdatedSaleOrderItem.setId(saleOrderItem.getId());

        partialUpdatedSaleOrderItem.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).total(UPDATED_TOTAL);

        restSaleOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSaleOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the SaleOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleOrderItemUpdatableFieldsEquals(partialUpdatedSaleOrderItem, getPersistedSaleOrderItem(partialUpdatedSaleOrderItem));
    }

    @Test
    @Transactional
    void patchNonExistingSaleOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOrderItem.setId(longCount.incrementAndGet());

        // Create the SaleOrderItem
        SaleOrderItemDTO saleOrderItemDTO = saleOrderItemMapper.toDto(saleOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, saleOrderItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSaleOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOrderItem.setId(longCount.incrementAndGet());

        // Create the SaleOrderItem
        SaleOrderItemDTO saleOrderItemDTO = saleOrderItemMapper.toDto(saleOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSaleOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleOrderItem.setId(longCount.incrementAndGet());

        // Create the SaleOrderItem
        SaleOrderItemDTO saleOrderItemDTO = saleOrderItemMapper.toDto(saleOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleOrderItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(saleOrderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSaleOrderItem() throws Exception {
        // Initialize the database
        insertedSaleOrderItem = saleOrderItemRepository.saveAndFlush(saleOrderItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the saleOrderItem
        restSaleOrderItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, saleOrderItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return saleOrderItemRepository.count();
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

    protected SaleOrderItem getPersistedSaleOrderItem(SaleOrderItem saleOrderItem) {
        return saleOrderItemRepository.findById(saleOrderItem.getId()).orElseThrow();
    }

    protected void assertPersistedSaleOrderItemToMatchAllProperties(SaleOrderItem expectedSaleOrderItem) {
        assertSaleOrderItemAllPropertiesEquals(expectedSaleOrderItem, getPersistedSaleOrderItem(expectedSaleOrderItem));
    }

    protected void assertPersistedSaleOrderItemToMatchUpdatableProperties(SaleOrderItem expectedSaleOrderItem) {
        assertSaleOrderItemAllUpdatablePropertiesEquals(expectedSaleOrderItem, getPersistedSaleOrderItem(expectedSaleOrderItem));
    }
}
