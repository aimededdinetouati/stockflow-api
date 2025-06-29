package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.PlanFormulaAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.PlanFormula;
import com.adeem.stockflow.domain.enumeration.BillingCycle;
import com.adeem.stockflow.repository.PlanFormulaRepository;
import com.adeem.stockflow.service.dto.PlanFormulaDTO;
import com.adeem.stockflow.service.mapper.PlanFormulaMapper;
import com.adeem.stockflow.web.rest.uncostomized.PlanFormulaResource;
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
 * Integration tests for the {@link PlanFormulaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PlanFormulaResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_BASE_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BASE_PRICE = new BigDecimal(2);

    private static final BillingCycle DEFAULT_BILLING_CYCLE = BillingCycle.MONTHLY;
    private static final BillingCycle UPDATED_BILLING_CYCLE = BillingCycle.QUARTERLY;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/plan-formulas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlanFormulaRepository planFormulaRepository;

    @Autowired
    private PlanFormulaMapper planFormulaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlanFormulaMockMvc;

    private PlanFormula planFormula;

    private PlanFormula insertedPlanFormula;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlanFormula createEntity() {
        return new PlanFormula()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .basePrice(DEFAULT_BASE_PRICE)
            .billingCycle(DEFAULT_BILLING_CYCLE)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlanFormula createUpdatedEntity() {
        return new PlanFormula()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .basePrice(UPDATED_BASE_PRICE)
            .billingCycle(UPDATED_BILLING_CYCLE)
            .isActive(UPDATED_IS_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        planFormula = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPlanFormula != null) {
            planFormulaRepository.delete(insertedPlanFormula);
            insertedPlanFormula = null;
        }
    }

    @Test
    @Transactional
    void createPlanFormula() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PlanFormula
        PlanFormulaDTO planFormulaDTO = planFormulaMapper.toDto(planFormula);
        var returnedPlanFormulaDTO = om.readValue(
            restPlanFormulaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFormulaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PlanFormulaDTO.class
        );

        // Validate the PlanFormula in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlanFormula = planFormulaMapper.toEntity(returnedPlanFormulaDTO);
        assertPlanFormulaUpdatableFieldsEquals(returnedPlanFormula, getPersistedPlanFormula(returnedPlanFormula));

        insertedPlanFormula = returnedPlanFormula;
    }

    @Test
    @Transactional
    void createPlanFormulaWithExistingId() throws Exception {
        // Create the PlanFormula with an existing ID
        planFormula.setId(1L);
        PlanFormulaDTO planFormulaDTO = planFormulaMapper.toDto(planFormula);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlanFormulaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFormulaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PlanFormula in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planFormula.setName(null);

        // Create the PlanFormula, which fails.
        PlanFormulaDTO planFormulaDTO = planFormulaMapper.toDto(planFormula);

        restPlanFormulaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFormulaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planFormula.setIsActive(null);

        // Create the PlanFormula, which fails.
        PlanFormulaDTO planFormulaDTO = planFormulaMapper.toDto(planFormula);

        restPlanFormulaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFormulaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlanFormulas() throws Exception {
        // Initialize the database
        insertedPlanFormula = planFormulaRepository.saveAndFlush(planFormula);

        // Get all the planFormulaList
        restPlanFormulaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(planFormula.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].basePrice").value(hasItem(sameNumber(DEFAULT_BASE_PRICE))))
            .andExpect(jsonPath("$.[*].billingCycle").value(hasItem(DEFAULT_BILLING_CYCLE.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getPlanFormula() throws Exception {
        // Initialize the database
        insertedPlanFormula = planFormulaRepository.saveAndFlush(planFormula);

        // Get the planFormula
        restPlanFormulaMockMvc
            .perform(get(ENTITY_API_URL_ID, planFormula.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(planFormula.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.basePrice").value(sameNumber(DEFAULT_BASE_PRICE)))
            .andExpect(jsonPath("$.billingCycle").value(DEFAULT_BILLING_CYCLE.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingPlanFormula() throws Exception {
        // Get the planFormula
        restPlanFormulaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPlanFormula() throws Exception {
        // Initialize the database
        insertedPlanFormula = planFormulaRepository.saveAndFlush(planFormula);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planFormula
        PlanFormula updatedPlanFormula = planFormulaRepository.findById(planFormula.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPlanFormula are not directly saved in db
        em.detach(updatedPlanFormula);
        updatedPlanFormula
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .basePrice(UPDATED_BASE_PRICE)
            .billingCycle(UPDATED_BILLING_CYCLE)
            .isActive(UPDATED_IS_ACTIVE);
        PlanFormulaDTO planFormulaDTO = planFormulaMapper.toDto(updatedPlanFormula);

        restPlanFormulaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, planFormulaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(planFormulaDTO))
            )
            .andExpect(status().isOk());

        // Validate the PlanFormula in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlanFormulaToMatchAllProperties(updatedPlanFormula);
    }

    @Test
    @Transactional
    void putNonExistingPlanFormula() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFormula.setId(longCount.incrementAndGet());

        // Create the PlanFormula
        PlanFormulaDTO planFormulaDTO = planFormulaMapper.toDto(planFormula);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlanFormulaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, planFormulaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(planFormulaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlanFormula in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlanFormula() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFormula.setId(longCount.incrementAndGet());

        // Create the PlanFormula
        PlanFormulaDTO planFormulaDTO = planFormulaMapper.toDto(planFormula);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanFormulaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(planFormulaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlanFormula in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlanFormula() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFormula.setId(longCount.incrementAndGet());

        // Create the PlanFormula
        PlanFormulaDTO planFormulaDTO = planFormulaMapper.toDto(planFormula);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanFormulaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFormulaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlanFormula in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlanFormulaWithPatch() throws Exception {
        // Initialize the database
        insertedPlanFormula = planFormulaRepository.saveAndFlush(planFormula);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planFormula using partial update
        PlanFormula partialUpdatedPlanFormula = new PlanFormula();
        partialUpdatedPlanFormula.setId(planFormula.getId());

        partialUpdatedPlanFormula.billingCycle(UPDATED_BILLING_CYCLE);

        restPlanFormulaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlanFormula.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlanFormula))
            )
            .andExpect(status().isOk());

        // Validate the PlanFormula in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanFormulaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPlanFormula, planFormula),
            getPersistedPlanFormula(planFormula)
        );
    }

    @Test
    @Transactional
    void fullUpdatePlanFormulaWithPatch() throws Exception {
        // Initialize the database
        insertedPlanFormula = planFormulaRepository.saveAndFlush(planFormula);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planFormula using partial update
        PlanFormula partialUpdatedPlanFormula = new PlanFormula();
        partialUpdatedPlanFormula.setId(planFormula.getId());

        partialUpdatedPlanFormula
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .basePrice(UPDATED_BASE_PRICE)
            .billingCycle(UPDATED_BILLING_CYCLE)
            .isActive(UPDATED_IS_ACTIVE);

        restPlanFormulaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlanFormula.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlanFormula))
            )
            .andExpect(status().isOk());

        // Validate the PlanFormula in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanFormulaUpdatableFieldsEquals(partialUpdatedPlanFormula, getPersistedPlanFormula(partialUpdatedPlanFormula));
    }

    @Test
    @Transactional
    void patchNonExistingPlanFormula() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFormula.setId(longCount.incrementAndGet());

        // Create the PlanFormula
        PlanFormulaDTO planFormulaDTO = planFormulaMapper.toDto(planFormula);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlanFormulaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, planFormulaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(planFormulaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlanFormula in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlanFormula() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFormula.setId(longCount.incrementAndGet());

        // Create the PlanFormula
        PlanFormulaDTO planFormulaDTO = planFormulaMapper.toDto(planFormula);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanFormulaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(planFormulaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlanFormula in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlanFormula() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFormula.setId(longCount.incrementAndGet());

        // Create the PlanFormula
        PlanFormulaDTO planFormulaDTO = planFormulaMapper.toDto(planFormula);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanFormulaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(planFormulaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlanFormula in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlanFormula() throws Exception {
        // Initialize the database
        insertedPlanFormula = planFormulaRepository.saveAndFlush(planFormula);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the planFormula
        restPlanFormulaMockMvc
            .perform(delete(ENTITY_API_URL_ID, planFormula.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return planFormulaRepository.count();
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

    protected PlanFormula getPersistedPlanFormula(PlanFormula planFormula) {
        return planFormulaRepository.findById(planFormula.getId()).orElseThrow();
    }

    protected void assertPersistedPlanFormulaToMatchAllProperties(PlanFormula expectedPlanFormula) {
        assertPlanFormulaAllPropertiesEquals(expectedPlanFormula, getPersistedPlanFormula(expectedPlanFormula));
    }

    protected void assertPersistedPlanFormulaToMatchUpdatableProperties(PlanFormula expectedPlanFormula) {
        assertPlanFormulaAllUpdatablePropertiesEquals(expectedPlanFormula, getPersistedPlanFormula(expectedPlanFormula));
    }
}
