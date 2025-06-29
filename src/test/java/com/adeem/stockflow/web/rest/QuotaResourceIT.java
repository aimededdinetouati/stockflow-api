package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.QuotaAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.Quota;
import com.adeem.stockflow.repository.QuotaRepository;
import com.adeem.stockflow.service.dto.QuotaDTO;
import com.adeem.stockflow.service.mapper.QuotaMapper;
import com.adeem.stockflow.web.rest.uncostomized.QuotaResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link QuotaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class QuotaResourceIT {

    private static final Integer DEFAULT_USERS = 1;
    private static final Integer UPDATED_USERS = 2;

    private static final Integer DEFAULT_PRODUCTS = 1;
    private static final Integer UPDATED_PRODUCTS = 2;

    private static final Integer DEFAULT_PRODUCT_FAMILIES = 1;
    private static final Integer UPDATED_PRODUCT_FAMILIES = 2;

    private static final Integer DEFAULT_SHOWCASED_PRODUCTS = 1;
    private static final Integer UPDATED_SHOWCASED_PRODUCTS = 2;

    private static final Integer DEFAULT_SALE_ORDERS = 1;
    private static final Integer UPDATED_SALE_ORDERS = 2;

    private static final Integer DEFAULT_PURCHASE_ORDERS = 1;
    private static final Integer UPDATED_PURCHASE_ORDERS = 2;

    private static final Integer DEFAULT_CUSTOMERS = 1;
    private static final Integer UPDATED_CUSTOMERS = 2;

    private static final Integer DEFAULT_SUPPLIERS = 1;
    private static final Integer UPDATED_SUPPLIERS = 2;

    private static final Integer DEFAULT_SHIPMENTS = 1;
    private static final Integer UPDATED_SHIPMENTS = 2;

    private static final ZonedDateTime DEFAULT_RESET_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_RESET_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/quotas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuotaRepository quotaRepository;

    @Autowired
    private QuotaMapper quotaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuotaMockMvc;

    private Quota quota;

    private Quota insertedQuota;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quota createEntity() {
        return new Quota()
            .users(DEFAULT_USERS)
            .products(DEFAULT_PRODUCTS)
            .productFamilies(DEFAULT_PRODUCT_FAMILIES)
            .showcasedProducts(DEFAULT_SHOWCASED_PRODUCTS)
            .saleOrders(DEFAULT_SALE_ORDERS)
            .purchaseOrders(DEFAULT_PURCHASE_ORDERS)
            .customers(DEFAULT_CUSTOMERS)
            .suppliers(DEFAULT_SUPPLIERS)
            .shipments(DEFAULT_SHIPMENTS)
            .resetDate(DEFAULT_RESET_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quota createUpdatedEntity() {
        return new Quota()
            .users(UPDATED_USERS)
            .products(UPDATED_PRODUCTS)
            .productFamilies(UPDATED_PRODUCT_FAMILIES)
            .showcasedProducts(UPDATED_SHOWCASED_PRODUCTS)
            .saleOrders(UPDATED_SALE_ORDERS)
            .purchaseOrders(UPDATED_PURCHASE_ORDERS)
            .customers(UPDATED_CUSTOMERS)
            .suppliers(UPDATED_SUPPLIERS)
            .shipments(UPDATED_SHIPMENTS)
            .resetDate(UPDATED_RESET_DATE);
    }

    @BeforeEach
    void initTest() {
        quota = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedQuota != null) {
            quotaRepository.delete(insertedQuota);
            insertedQuota = null;
        }
    }

    @Test
    @Transactional
    void createQuota() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Quota
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);
        var returnedQuotaDTO = om.readValue(
            restQuotaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quotaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuotaDTO.class
        );

        // Validate the Quota in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedQuota = quotaMapper.toEntity(returnedQuotaDTO);
        assertQuotaUpdatableFieldsEquals(returnedQuota, getPersistedQuota(returnedQuota));

        insertedQuota = returnedQuota;
    }

    @Test
    @Transactional
    void createQuotaWithExistingId() throws Exception {
        // Create the Quota with an existing ID
        quota.setId(1L);
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quotaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Quota in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllQuotas() throws Exception {
        // Initialize the database
        insertedQuota = quotaRepository.saveAndFlush(quota);

        // Get all the quotaList
        restQuotaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quota.getId().intValue())))
            .andExpect(jsonPath("$.[*].users").value(hasItem(DEFAULT_USERS)))
            .andExpect(jsonPath("$.[*].products").value(hasItem(DEFAULT_PRODUCTS)))
            .andExpect(jsonPath("$.[*].productFamilies").value(hasItem(DEFAULT_PRODUCT_FAMILIES)))
            .andExpect(jsonPath("$.[*].showcasedProducts").value(hasItem(DEFAULT_SHOWCASED_PRODUCTS)))
            .andExpect(jsonPath("$.[*].saleOrders").value(hasItem(DEFAULT_SALE_ORDERS)))
            .andExpect(jsonPath("$.[*].purchaseOrders").value(hasItem(DEFAULT_PURCHASE_ORDERS)))
            .andExpect(jsonPath("$.[*].customers").value(hasItem(DEFAULT_CUSTOMERS)))
            .andExpect(jsonPath("$.[*].suppliers").value(hasItem(DEFAULT_SUPPLIERS)))
            .andExpect(jsonPath("$.[*].shipments").value(hasItem(DEFAULT_SHIPMENTS)))
            .andExpect(jsonPath("$.[*].resetDate").value(hasItem(sameInstant(DEFAULT_RESET_DATE))));
    }

    @Test
    @Transactional
    void getQuota() throws Exception {
        // Initialize the database
        insertedQuota = quotaRepository.saveAndFlush(quota);

        // Get the quota
        restQuotaMockMvc
            .perform(get(ENTITY_API_URL_ID, quota.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(quota.getId().intValue()))
            .andExpect(jsonPath("$.users").value(DEFAULT_USERS))
            .andExpect(jsonPath("$.products").value(DEFAULT_PRODUCTS))
            .andExpect(jsonPath("$.productFamilies").value(DEFAULT_PRODUCT_FAMILIES))
            .andExpect(jsonPath("$.showcasedProducts").value(DEFAULT_SHOWCASED_PRODUCTS))
            .andExpect(jsonPath("$.saleOrders").value(DEFAULT_SALE_ORDERS))
            .andExpect(jsonPath("$.purchaseOrders").value(DEFAULT_PURCHASE_ORDERS))
            .andExpect(jsonPath("$.customers").value(DEFAULT_CUSTOMERS))
            .andExpect(jsonPath("$.suppliers").value(DEFAULT_SUPPLIERS))
            .andExpect(jsonPath("$.shipments").value(DEFAULT_SHIPMENTS))
            .andExpect(jsonPath("$.resetDate").value(sameInstant(DEFAULT_RESET_DATE)));
    }

    @Test
    @Transactional
    void getNonExistingQuota() throws Exception {
        // Get the quota
        restQuotaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuota() throws Exception {
        // Initialize the database
        insertedQuota = quotaRepository.saveAndFlush(quota);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quota
        Quota updatedQuota = quotaRepository.findById(quota.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuota are not directly saved in db
        em.detach(updatedQuota);
        updatedQuota
            .users(UPDATED_USERS)
            .products(UPDATED_PRODUCTS)
            .productFamilies(UPDATED_PRODUCT_FAMILIES)
            .showcasedProducts(UPDATED_SHOWCASED_PRODUCTS)
            .saleOrders(UPDATED_SALE_ORDERS)
            .purchaseOrders(UPDATED_PURCHASE_ORDERS)
            .customers(UPDATED_CUSTOMERS)
            .suppliers(UPDATED_SUPPLIERS)
            .shipments(UPDATED_SHIPMENTS)
            .resetDate(UPDATED_RESET_DATE);
        QuotaDTO quotaDTO = quotaMapper.toDto(updatedQuota);

        restQuotaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quotaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quotaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Quota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuotaToMatchAllProperties(updatedQuota);
    }

    @Test
    @Transactional
    void putNonExistingQuota() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quota.setId(longCount.incrementAndGet());

        // Create the Quota
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuotaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quotaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quotaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuota() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quota.setId(longCount.incrementAndGet());

        // Create the Quota
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuotaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quotaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuota() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quota.setId(longCount.incrementAndGet());

        // Create the Quota
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuotaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quotaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Quota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuotaWithPatch() throws Exception {
        // Initialize the database
        insertedQuota = quotaRepository.saveAndFlush(quota);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quota using partial update
        Quota partialUpdatedQuota = new Quota();
        partialUpdatedQuota.setId(quota.getId());

        partialUpdatedQuota
            .products(UPDATED_PRODUCTS)
            .productFamilies(UPDATED_PRODUCT_FAMILIES)
            .purchaseOrders(UPDATED_PURCHASE_ORDERS)
            .customers(UPDATED_CUSTOMERS)
            .suppliers(UPDATED_SUPPLIERS);

        restQuotaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuota.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuota))
            )
            .andExpect(status().isOk());

        // Validate the Quota in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuotaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedQuota, quota), getPersistedQuota(quota));
    }

    @Test
    @Transactional
    void fullUpdateQuotaWithPatch() throws Exception {
        // Initialize the database
        insertedQuota = quotaRepository.saveAndFlush(quota);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quota using partial update
        Quota partialUpdatedQuota = new Quota();
        partialUpdatedQuota.setId(quota.getId());

        partialUpdatedQuota
            .users(UPDATED_USERS)
            .products(UPDATED_PRODUCTS)
            .productFamilies(UPDATED_PRODUCT_FAMILIES)
            .showcasedProducts(UPDATED_SHOWCASED_PRODUCTS)
            .saleOrders(UPDATED_SALE_ORDERS)
            .purchaseOrders(UPDATED_PURCHASE_ORDERS)
            .customers(UPDATED_CUSTOMERS)
            .suppliers(UPDATED_SUPPLIERS)
            .shipments(UPDATED_SHIPMENTS)
            .resetDate(UPDATED_RESET_DATE);

        restQuotaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuota.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuota))
            )
            .andExpect(status().isOk());

        // Validate the Quota in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuotaUpdatableFieldsEquals(partialUpdatedQuota, getPersistedQuota(partialUpdatedQuota));
    }

    @Test
    @Transactional
    void patchNonExistingQuota() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quota.setId(longCount.incrementAndGet());

        // Create the Quota
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuotaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, quotaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quotaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuota() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quota.setId(longCount.incrementAndGet());

        // Create the Quota
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuotaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quotaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuota() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quota.setId(longCount.incrementAndGet());

        // Create the Quota
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuotaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(quotaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Quota in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuota() throws Exception {
        // Initialize the database
        insertedQuota = quotaRepository.saveAndFlush(quota);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the quota
        restQuotaMockMvc
            .perform(delete(ENTITY_API_URL_ID, quota.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return quotaRepository.count();
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

    protected Quota getPersistedQuota(Quota quota) {
        return quotaRepository.findById(quota.getId()).orElseThrow();
    }

    protected void assertPersistedQuotaToMatchAllProperties(Quota expectedQuota) {
        assertQuotaAllPropertiesEquals(expectedQuota, getPersistedQuota(expectedQuota));
    }

    protected void assertPersistedQuotaToMatchUpdatableProperties(Quota expectedQuota) {
        assertQuotaAllUpdatablePropertiesEquals(expectedQuota, getPersistedQuota(expectedQuota));
    }
}
