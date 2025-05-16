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
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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

    private static final String DEFAULT_RESOURCE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_RESOURCE_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_USED_AMOUNT = 1;
    private static final Integer UPDATED_USED_AMOUNT = 2;

    private static final Integer DEFAULT_MAX_AMOUNT = 1;
    private static final Integer UPDATED_MAX_AMOUNT = 2;

    private static final ZonedDateTime DEFAULT_RESET_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_RESET_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Instant DEFAULT_LAST_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

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
            .resourceType(DEFAULT_RESOURCE_TYPE)
            .usedAmount(DEFAULT_USED_AMOUNT)
            .maxAmount(DEFAULT_MAX_AMOUNT)
            .resetDate(DEFAULT_RESET_DATE)
            .lastUpdated(DEFAULT_LAST_UPDATED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quota createUpdatedEntity() {
        return new Quota()
            .resourceType(UPDATED_RESOURCE_TYPE)
            .usedAmount(UPDATED_USED_AMOUNT)
            .maxAmount(UPDATED_MAX_AMOUNT)
            .resetDate(UPDATED_RESET_DATE)
            .lastUpdated(UPDATED_LAST_UPDATED);
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
    void checkResourceTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quota.setResourceType(null);

        // Create the Quota, which fails.
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);

        restQuotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quotaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUsedAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quota.setUsedAmount(null);

        // Create the Quota, which fails.
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);

        restQuotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quotaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quota.setMaxAmount(null);

        // Create the Quota, which fails.
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);

        restQuotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quotaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkResetDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quota.setResetDate(null);

        // Create the Quota, which fails.
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);

        restQuotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quotaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastUpdatedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quota.setLastUpdated(null);

        // Create the Quota, which fails.
        QuotaDTO quotaDTO = quotaMapper.toDto(quota);

        restQuotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quotaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
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
            .andExpect(jsonPath("$.[*].resourceType").value(hasItem(DEFAULT_RESOURCE_TYPE)))
            .andExpect(jsonPath("$.[*].usedAmount").value(hasItem(DEFAULT_USED_AMOUNT)))
            .andExpect(jsonPath("$.[*].maxAmount").value(hasItem(DEFAULT_MAX_AMOUNT)))
            .andExpect(jsonPath("$.[*].resetDate").value(hasItem(sameInstant(DEFAULT_RESET_DATE))))
            .andExpect(jsonPath("$.[*].lastUpdated").value(hasItem(DEFAULT_LAST_UPDATED.toString())));
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
            .andExpect(jsonPath("$.resourceType").value(DEFAULT_RESOURCE_TYPE))
            .andExpect(jsonPath("$.usedAmount").value(DEFAULT_USED_AMOUNT))
            .andExpect(jsonPath("$.maxAmount").value(DEFAULT_MAX_AMOUNT))
            .andExpect(jsonPath("$.resetDate").value(sameInstant(DEFAULT_RESET_DATE)))
            .andExpect(jsonPath("$.lastUpdated").value(DEFAULT_LAST_UPDATED.toString()));
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
            .resourceType(UPDATED_RESOURCE_TYPE)
            .usedAmount(UPDATED_USED_AMOUNT)
            .maxAmount(UPDATED_MAX_AMOUNT)
            .resetDate(UPDATED_RESET_DATE)
            .lastUpdated(UPDATED_LAST_UPDATED);
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

        partialUpdatedQuota.usedAmount(UPDATED_USED_AMOUNT).maxAmount(UPDATED_MAX_AMOUNT);

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
            .resourceType(UPDATED_RESOURCE_TYPE)
            .usedAmount(UPDATED_USED_AMOUNT)
            .maxAmount(UPDATED_MAX_AMOUNT)
            .resetDate(UPDATED_RESET_DATE)
            .lastUpdated(UPDATED_LAST_UPDATED);

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
