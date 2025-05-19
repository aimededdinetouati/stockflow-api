package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.ResourceLimitAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.ResourceLimit;
import com.adeem.stockflow.repository.ResourceLimitRepository;
import com.adeem.stockflow.service.dto.ResourceLimitDTO;
import com.adeem.stockflow.service.mapper.ResourceLimitMapper;
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
 * Integration tests for the {@link ResourceLimitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ResourceLimitResourceIT {

    private static final String DEFAULT_RESOURCE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_RESOURCE_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_MAX_AMOUNT = 1;
    private static final Integer UPDATED_MAX_AMOUNT = 2;

    private static final Boolean DEFAULT_IS_UNLIMITED = false;
    private static final Boolean UPDATED_IS_UNLIMITED = true;

    private static final String ENTITY_API_URL = "/api/resource-limits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ResourceLimitRepository resourceLimitRepository;

    @Autowired
    private ResourceLimitMapper resourceLimitMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restResourceLimitMockMvc;

    private ResourceLimit resourceLimit;

    private ResourceLimit insertedResourceLimit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResourceLimit createEntity() {
        return new ResourceLimit().resourceType(DEFAULT_RESOURCE_TYPE).maxAmount(DEFAULT_MAX_AMOUNT).isUnlimited(DEFAULT_IS_UNLIMITED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResourceLimit createUpdatedEntity() {
        return new ResourceLimit().resourceType(UPDATED_RESOURCE_TYPE).maxAmount(UPDATED_MAX_AMOUNT).isUnlimited(UPDATED_IS_UNLIMITED);
    }

    @BeforeEach
    void initTest() {
        resourceLimit = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedResourceLimit != null) {
            resourceLimitRepository.delete(insertedResourceLimit);
            insertedResourceLimit = null;
        }
    }

    @Test
    @Transactional
    void createResourceLimit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ResourceLimit
        ResourceLimitDTO resourceLimitDTO = resourceLimitMapper.toDto(resourceLimit);
        var returnedResourceLimitDTO = om.readValue(
            restResourceLimitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(resourceLimitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ResourceLimitDTO.class
        );

        // Validate the ResourceLimit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedResourceLimit = resourceLimitMapper.toEntity(returnedResourceLimitDTO);
        assertResourceLimitUpdatableFieldsEquals(returnedResourceLimit, getPersistedResourceLimit(returnedResourceLimit));

        insertedResourceLimit = returnedResourceLimit;
    }

    @Test
    @Transactional
    void createResourceLimitWithExistingId() throws Exception {
        // Create the ResourceLimit with an existing ID
        resourceLimit.setId(1L);
        ResourceLimitDTO resourceLimitDTO = resourceLimitMapper.toDto(resourceLimit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restResourceLimitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(resourceLimitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ResourceLimit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkResourceTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        resourceLimit.setResourceType(null);

        // Create the ResourceLimit, which fails.
        ResourceLimitDTO resourceLimitDTO = resourceLimitMapper.toDto(resourceLimit);

        restResourceLimitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(resourceLimitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsUnlimitedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        resourceLimit.setIsUnlimited(null);

        // Create the ResourceLimit, which fails.
        ResourceLimitDTO resourceLimitDTO = resourceLimitMapper.toDto(resourceLimit);

        restResourceLimitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(resourceLimitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllResourceLimits() throws Exception {
        // Initialize the database
        insertedResourceLimit = resourceLimitRepository.saveAndFlush(resourceLimit);

        // Get all the resourceLimitList
        restResourceLimitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resourceLimit.getId().intValue())))
            .andExpect(jsonPath("$.[*].resourceType").value(hasItem(DEFAULT_RESOURCE_TYPE)))
            .andExpect(jsonPath("$.[*].maxAmount").value(hasItem(DEFAULT_MAX_AMOUNT)))
            .andExpect(jsonPath("$.[*].isUnlimited").value(hasItem(DEFAULT_IS_UNLIMITED)));
    }

    @Test
    @Transactional
    void getResourceLimit() throws Exception {
        // Initialize the database
        insertedResourceLimit = resourceLimitRepository.saveAndFlush(resourceLimit);

        // Get the resourceLimit
        restResourceLimitMockMvc
            .perform(get(ENTITY_API_URL_ID, resourceLimit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(resourceLimit.getId().intValue()))
            .andExpect(jsonPath("$.resourceType").value(DEFAULT_RESOURCE_TYPE))
            .andExpect(jsonPath("$.maxAmount").value(DEFAULT_MAX_AMOUNT))
            .andExpect(jsonPath("$.isUnlimited").value(DEFAULT_IS_UNLIMITED));
    }

    @Test
    @Transactional
    void getNonExistingResourceLimit() throws Exception {
        // Get the resourceLimit
        restResourceLimitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingResourceLimit() throws Exception {
        // Initialize the database
        insertedResourceLimit = resourceLimitRepository.saveAndFlush(resourceLimit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the resourceLimit
        ResourceLimit updatedResourceLimit = resourceLimitRepository.findById(resourceLimit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedResourceLimit are not directly saved in db
        em.detach(updatedResourceLimit);
        updatedResourceLimit.resourceType(UPDATED_RESOURCE_TYPE).maxAmount(UPDATED_MAX_AMOUNT).isUnlimited(UPDATED_IS_UNLIMITED);
        ResourceLimitDTO resourceLimitDTO = resourceLimitMapper.toDto(updatedResourceLimit);

        restResourceLimitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, resourceLimitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(resourceLimitDTO))
            )
            .andExpect(status().isOk());

        // Validate the ResourceLimit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedResourceLimitToMatchAllProperties(updatedResourceLimit);
    }

    @Test
    @Transactional
    void putNonExistingResourceLimit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        resourceLimit.setId(longCount.incrementAndGet());

        // Create the ResourceLimit
        ResourceLimitDTO resourceLimitDTO = resourceLimitMapper.toDto(resourceLimit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourceLimitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, resourceLimitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(resourceLimitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceLimit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchResourceLimit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        resourceLimit.setId(longCount.incrementAndGet());

        // Create the ResourceLimit
        ResourceLimitDTO resourceLimitDTO = resourceLimitMapper.toDto(resourceLimit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceLimitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(resourceLimitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceLimit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamResourceLimit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        resourceLimit.setId(longCount.incrementAndGet());

        // Create the ResourceLimit
        ResourceLimitDTO resourceLimitDTO = resourceLimitMapper.toDto(resourceLimit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceLimitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(resourceLimitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResourceLimit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateResourceLimitWithPatch() throws Exception {
        // Initialize the database
        insertedResourceLimit = resourceLimitRepository.saveAndFlush(resourceLimit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the resourceLimit using partial update
        ResourceLimit partialUpdatedResourceLimit = new ResourceLimit();
        partialUpdatedResourceLimit.setId(resourceLimit.getId());

        partialUpdatedResourceLimit.resourceType(UPDATED_RESOURCE_TYPE).maxAmount(UPDATED_MAX_AMOUNT);

        restResourceLimitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResourceLimit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedResourceLimit))
            )
            .andExpect(status().isOk());

        // Validate the ResourceLimit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertResourceLimitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedResourceLimit, resourceLimit),
            getPersistedResourceLimit(resourceLimit)
        );
    }

    @Test
    @Transactional
    void fullUpdateResourceLimitWithPatch() throws Exception {
        // Initialize the database
        insertedResourceLimit = resourceLimitRepository.saveAndFlush(resourceLimit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the resourceLimit using partial update
        ResourceLimit partialUpdatedResourceLimit = new ResourceLimit();
        partialUpdatedResourceLimit.setId(resourceLimit.getId());

        partialUpdatedResourceLimit.resourceType(UPDATED_RESOURCE_TYPE).maxAmount(UPDATED_MAX_AMOUNT).isUnlimited(UPDATED_IS_UNLIMITED);

        restResourceLimitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResourceLimit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedResourceLimit))
            )
            .andExpect(status().isOk());

        // Validate the ResourceLimit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertResourceLimitUpdatableFieldsEquals(partialUpdatedResourceLimit, getPersistedResourceLimit(partialUpdatedResourceLimit));
    }

    @Test
    @Transactional
    void patchNonExistingResourceLimit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        resourceLimit.setId(longCount.incrementAndGet());

        // Create the ResourceLimit
        ResourceLimitDTO resourceLimitDTO = resourceLimitMapper.toDto(resourceLimit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourceLimitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, resourceLimitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(resourceLimitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceLimit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchResourceLimit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        resourceLimit.setId(longCount.incrementAndGet());

        // Create the ResourceLimit
        ResourceLimitDTO resourceLimitDTO = resourceLimitMapper.toDto(resourceLimit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceLimitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(resourceLimitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResourceLimit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamResourceLimit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        resourceLimit.setId(longCount.incrementAndGet());

        // Create the ResourceLimit
        ResourceLimitDTO resourceLimitDTO = resourceLimitMapper.toDto(resourceLimit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceLimitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(resourceLimitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResourceLimit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteResourceLimit() throws Exception {
        // Initialize the database
        insertedResourceLimit = resourceLimitRepository.saveAndFlush(resourceLimit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the resourceLimit
        restResourceLimitMockMvc
            .perform(delete(ENTITY_API_URL_ID, resourceLimit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return resourceLimitRepository.count();
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

    protected ResourceLimit getPersistedResourceLimit(ResourceLimit resourceLimit) {
        return resourceLimitRepository.findById(resourceLimit.getId()).orElseThrow();
    }

    protected void assertPersistedResourceLimitToMatchAllProperties(ResourceLimit expectedResourceLimit) {
        assertResourceLimitAllPropertiesEquals(expectedResourceLimit, getPersistedResourceLimit(expectedResourceLimit));
    }

    protected void assertPersistedResourceLimitToMatchUpdatableProperties(ResourceLimit expectedResourceLimit) {
        assertResourceLimitAllUpdatablePropertiesEquals(expectedResourceLimit, getPersistedResourceLimit(expectedResourceLimit));
    }
}
