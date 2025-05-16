package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.ProductFamilyAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.ProductFamily;
import com.adeem.stockflow.repository.ProductFamilyRepository;
import com.adeem.stockflow.service.dto.ProductFamilyDTO;
import com.adeem.stockflow.service.mapper.ProductFamilyMapper;
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
 * Integration tests for the {@link ProductFamilyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductFamilyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-families";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductFamilyRepository productFamilyRepository;

    @Autowired
    private ProductFamilyMapper productFamilyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductFamilyMockMvc;

    private ProductFamily productFamily;

    private ProductFamily insertedProductFamily;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductFamily createEntity() {
        return new ProductFamily().name(DEFAULT_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductFamily createUpdatedEntity() {
        return new ProductFamily().name(UPDATED_NAME);
    }

    @BeforeEach
    void initTest() {
        productFamily = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProductFamily != null) {
            productFamilyRepository.delete(insertedProductFamily);
            insertedProductFamily = null;
        }
    }

    @Test
    @Transactional
    void createProductFamily() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductFamily
        ProductFamilyDTO productFamilyDTO = productFamilyMapper.toDto(productFamily);
        var returnedProductFamilyDTO = om.readValue(
            restProductFamilyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productFamilyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductFamilyDTO.class
        );

        // Validate the ProductFamily in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductFamily = productFamilyMapper.toEntity(returnedProductFamilyDTO);
        assertProductFamilyUpdatableFieldsEquals(returnedProductFamily, getPersistedProductFamily(returnedProductFamily));

        insertedProductFamily = returnedProductFamily;
    }

    @Test
    @Transactional
    void createProductFamilyWithExistingId() throws Exception {
        // Create the ProductFamily with an existing ID
        productFamily.setId(1L);
        ProductFamilyDTO productFamilyDTO = productFamilyMapper.toDto(productFamily);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductFamilyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productFamilyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductFamily in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productFamily.setName(null);

        // Create the ProductFamily, which fails.
        ProductFamilyDTO productFamilyDTO = productFamilyMapper.toDto(productFamily);

        restProductFamilyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productFamilyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProductFamilies() throws Exception {
        // Initialize the database
        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        // Get all the productFamilyList
        restProductFamilyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productFamily.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getProductFamily() throws Exception {
        // Initialize the database
        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        // Get the productFamily
        restProductFamilyMockMvc
            .perform(get(ENTITY_API_URL_ID, productFamily.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productFamily.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingProductFamily() throws Exception {
        // Get the productFamily
        restProductFamilyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductFamily() throws Exception {
        // Initialize the database
        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productFamily
        ProductFamily updatedProductFamily = productFamilyRepository.findById(productFamily.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductFamily are not directly saved in db
        em.detach(updatedProductFamily);
        updatedProductFamily.name(UPDATED_NAME);
        ProductFamilyDTO productFamilyDTO = productFamilyMapper.toDto(updatedProductFamily);

        restProductFamilyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productFamilyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productFamilyDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductFamily in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductFamilyToMatchAllProperties(updatedProductFamily);
    }

    @Test
    @Transactional
    void putNonExistingProductFamily() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productFamily.setId(longCount.incrementAndGet());

        // Create the ProductFamily
        ProductFamilyDTO productFamilyDTO = productFamilyMapper.toDto(productFamily);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductFamilyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productFamilyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productFamilyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductFamily in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductFamily() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productFamily.setId(longCount.incrementAndGet());

        // Create the ProductFamily
        ProductFamilyDTO productFamilyDTO = productFamilyMapper.toDto(productFamily);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductFamilyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productFamilyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductFamily in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductFamily() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productFamily.setId(longCount.incrementAndGet());

        // Create the ProductFamily
        ProductFamilyDTO productFamilyDTO = productFamilyMapper.toDto(productFamily);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductFamilyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productFamilyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductFamily in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductFamilyWithPatch() throws Exception {
        // Initialize the database
        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productFamily using partial update
        ProductFamily partialUpdatedProductFamily = new ProductFamily();
        partialUpdatedProductFamily.setId(productFamily.getId());

        partialUpdatedProductFamily.name(UPDATED_NAME);

        restProductFamilyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductFamily.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductFamily))
            )
            .andExpect(status().isOk());

        // Validate the ProductFamily in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductFamilyUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductFamily, productFamily),
            getPersistedProductFamily(productFamily)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductFamilyWithPatch() throws Exception {
        // Initialize the database
        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productFamily using partial update
        ProductFamily partialUpdatedProductFamily = new ProductFamily();
        partialUpdatedProductFamily.setId(productFamily.getId());

        partialUpdatedProductFamily.name(UPDATED_NAME);

        restProductFamilyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductFamily.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductFamily))
            )
            .andExpect(status().isOk());

        // Validate the ProductFamily in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductFamilyUpdatableFieldsEquals(partialUpdatedProductFamily, getPersistedProductFamily(partialUpdatedProductFamily));
    }

    @Test
    @Transactional
    void patchNonExistingProductFamily() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productFamily.setId(longCount.incrementAndGet());

        // Create the ProductFamily
        ProductFamilyDTO productFamilyDTO = productFamilyMapper.toDto(productFamily);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductFamilyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productFamilyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productFamilyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductFamily in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductFamily() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productFamily.setId(longCount.incrementAndGet());

        // Create the ProductFamily
        ProductFamilyDTO productFamilyDTO = productFamilyMapper.toDto(productFamily);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductFamilyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productFamilyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductFamily in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductFamily() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productFamily.setId(longCount.incrementAndGet());

        // Create the ProductFamily
        ProductFamilyDTO productFamilyDTO = productFamilyMapper.toDto(productFamily);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductFamilyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productFamilyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductFamily in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductFamily() throws Exception {
        // Initialize the database
        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productFamily
        restProductFamilyMockMvc
            .perform(delete(ENTITY_API_URL_ID, productFamily.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productFamilyRepository.count();
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

    protected ProductFamily getPersistedProductFamily(ProductFamily productFamily) {
        return productFamilyRepository.findById(productFamily.getId()).orElseThrow();
    }

    protected void assertPersistedProductFamilyToMatchAllProperties(ProductFamily expectedProductFamily) {
        assertProductFamilyAllPropertiesEquals(expectedProductFamily, getPersistedProductFamily(expectedProductFamily));
    }

    protected void assertPersistedProductFamilyToMatchUpdatableProperties(ProductFamily expectedProductFamily) {
        assertProductFamilyAllUpdatablePropertiesEquals(expectedProductFamily, getPersistedProductFamily(expectedProductFamily));
    }
}
