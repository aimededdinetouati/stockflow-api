package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.PlanFeatureAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.PlanFeature;
import com.adeem.stockflow.repository.PlanFeatureRepository;
import com.adeem.stockflow.service.dto.PlanFeatureDTO;
import com.adeem.stockflow.service.mapper.PlanFeatureMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PlanFeatureResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PlanFeatureResourceIT {

    private static final String DEFAULT_FEATURE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FEATURE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_INCLUDED = false;
    private static final Boolean UPDATED_IS_INCLUDED = true;

    private static final String ENTITY_API_URL = "/api/plan-features";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlanFeatureRepository planFeatureRepository;

    @Autowired
    private PlanFeatureMapper planFeatureMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlanFeatureMockMvc;

    private PlanFeature planFeature;

    private PlanFeature insertedPlanFeature;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlanFeature createEntity() {
        return new PlanFeature().featureName(DEFAULT_FEATURE_NAME).description(DEFAULT_DESCRIPTION).isIncluded(DEFAULT_IS_INCLUDED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlanFeature createUpdatedEntity() {
        return new PlanFeature().featureName(UPDATED_FEATURE_NAME).description(UPDATED_DESCRIPTION).isIncluded(UPDATED_IS_INCLUDED);
    }

    @BeforeEach
    void initTest() {
        planFeature = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPlanFeature != null) {
            planFeatureRepository.delete(insertedPlanFeature);
            insertedPlanFeature = null;
        }
    }

    @Test
    @Transactional
    void createPlanFeature() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);
        var returnedPlanFeatureDTO = om.readValue(
            restPlanFeatureMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFeatureDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PlanFeatureDTO.class
        );

        // Validate the PlanFeature in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlanFeature = planFeatureMapper.toEntity(returnedPlanFeatureDTO);
        assertPlanFeatureUpdatableFieldsEquals(returnedPlanFeature, getPersistedPlanFeature(returnedPlanFeature));

        insertedPlanFeature = returnedPlanFeature;
    }

    @Test
    @Transactional
    void createPlanFeatureWithExistingId() throws Exception {
        // Create the PlanFeature with an existing ID
        planFeature.setId(1L);
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlanFeatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFeatureDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFeatureNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planFeature.setFeatureName(null);

        // Create the PlanFeature, which fails.
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        restPlanFeatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFeatureDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsIncludedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planFeature.setIsIncluded(null);

        // Create the PlanFeature, which fails.
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        restPlanFeatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFeatureDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlanFeatures() throws Exception {
        // Initialize the database
        insertedPlanFeature = planFeatureRepository.saveAndFlush(planFeature);

        // Get all the planFeatureList
        restPlanFeatureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(planFeature.getId().intValue())))
            .andExpect(jsonPath("$.[*].featureName").value(hasItem(DEFAULT_FEATURE_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isIncluded").value(hasItem(DEFAULT_IS_INCLUDED)));
    }

    @Test
    @Transactional
    void getPlanFeature() throws Exception {
        // Initialize the database
        insertedPlanFeature = planFeatureRepository.saveAndFlush(planFeature);

        // Get the planFeature
        restPlanFeatureMockMvc
            .perform(get(ENTITY_API_URL_ID, planFeature.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(planFeature.getId().intValue()))
            .andExpect(jsonPath("$.featureName").value(DEFAULT_FEATURE_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.isIncluded").value(DEFAULT_IS_INCLUDED));
    }

    @Test
    @Transactional
    void getNonExistingPlanFeature() throws Exception {
        // Get the planFeature
        restPlanFeatureMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPlanFeature() throws Exception {
        // Initialize the database
        insertedPlanFeature = planFeatureRepository.saveAndFlush(planFeature);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planFeature
        PlanFeature updatedPlanFeature = planFeatureRepository.findById(planFeature.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPlanFeature are not directly saved in db
        em.detach(updatedPlanFeature);
        updatedPlanFeature.featureName(UPDATED_FEATURE_NAME).description(UPDATED_DESCRIPTION).isIncluded(UPDATED_IS_INCLUDED);
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(updatedPlanFeature);

        restPlanFeatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, planFeatureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(planFeatureDTO))
            )
            .andExpect(status().isOk());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlanFeatureToMatchAllProperties(updatedPlanFeature);
    }

    @Test
    @Transactional
    void putNonExistingPlanFeature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFeature.setId(longCount.incrementAndGet());

        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlanFeatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, planFeatureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(planFeatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlanFeature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFeature.setId(longCount.incrementAndGet());

        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanFeatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(planFeatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlanFeature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFeature.setId(longCount.incrementAndGet());

        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanFeatureMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFeatureDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlanFeatureWithPatch() throws Exception {
        // Initialize the database
        insertedPlanFeature = planFeatureRepository.saveAndFlush(planFeature);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planFeature using partial update
        PlanFeature partialUpdatedPlanFeature = new PlanFeature();
        partialUpdatedPlanFeature.setId(planFeature.getId());

        partialUpdatedPlanFeature.isIncluded(UPDATED_IS_INCLUDED);

        restPlanFeatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlanFeature.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlanFeature))
            )
            .andExpect(status().isOk());

        // Validate the PlanFeature in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanFeatureUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPlanFeature, planFeature),
            getPersistedPlanFeature(planFeature)
        );
    }

    @Test
    @Transactional
    void fullUpdatePlanFeatureWithPatch() throws Exception {
        // Initialize the database
        insertedPlanFeature = planFeatureRepository.saveAndFlush(planFeature);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planFeature using partial update
        PlanFeature partialUpdatedPlanFeature = new PlanFeature();
        partialUpdatedPlanFeature.setId(planFeature.getId());

        partialUpdatedPlanFeature.featureName(UPDATED_FEATURE_NAME).description(UPDATED_DESCRIPTION).isIncluded(UPDATED_IS_INCLUDED);

        restPlanFeatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlanFeature.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlanFeature))
            )
            .andExpect(status().isOk());

        // Validate the PlanFeature in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanFeatureUpdatableFieldsEquals(partialUpdatedPlanFeature, getPersistedPlanFeature(partialUpdatedPlanFeature));
    }

    @Test
    @Transactional
    void patchNonExistingPlanFeature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFeature.setId(longCount.incrementAndGet());

        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlanFeatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, planFeatureDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(planFeatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlanFeature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFeature.setId(longCount.incrementAndGet());

        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanFeatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(planFeatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlanFeature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFeature.setId(longCount.incrementAndGet());

        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanFeatureMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(planFeatureDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlanFeature() throws Exception {
        // Initialize the database
        insertedPlanFeature = planFeatureRepository.saveAndFlush(planFeature);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the planFeature
        restPlanFeatureMockMvc
            .perform(delete(ENTITY_API_URL_ID, planFeature.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return planFeatureRepository.count();
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

    protected PlanFeature getPersistedPlanFeature(PlanFeature planFeature) {
        return planFeatureRepository.findById(planFeature.getId()).orElseThrow();
    }

    protected void assertPersistedPlanFeatureToMatchAllProperties(PlanFeature expectedPlanFeature) {
        assertPlanFeatureAllPropertiesEquals(expectedPlanFeature, getPersistedPlanFeature(expectedPlanFeature));
    }

    protected void assertPersistedPlanFeatureToMatchUpdatableProperties(PlanFeature expectedPlanFeature) {
        assertPlanFeatureAllUpdatablePropertiesEquals(expectedPlanFeature, getPersistedPlanFeature(expectedPlanFeature));
    }
}
