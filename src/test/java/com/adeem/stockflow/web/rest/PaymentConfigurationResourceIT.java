package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.PaymentConfigurationAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.PaymentConfiguration;
import com.adeem.stockflow.repository.PaymentConfigurationRepository;
import com.adeem.stockflow.service.dto.PaymentConfigurationDTO;
import com.adeem.stockflow.service.mapper.PaymentConfigurationMapper;
import com.adeem.stockflow.web.rest.uncostomized.PaymentConfigurationResource;
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
 * Integration tests for the {@link PaymentConfigurationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentConfigurationResourceIT {

    private static final Boolean DEFAULT_ONLINE_PAYMENT_ENABLED = false;
    private static final Boolean UPDATED_ONLINE_PAYMENT_ENABLED = true;

    private static final String DEFAULT_CCP = "AAAAAAAAAA";
    private static final String UPDATED_CCP = "BBBBBBBBBB";

    private static final String DEFAULT_RIP = "AAAAAAAAAA";
    private static final String UPDATED_RIP = "BBBBBBBBBB";

    private static final String DEFAULT_RIB = "AAAAAAAAAA";
    private static final String UPDATED_RIB = "BBBBBBBBBB";

    private static final String DEFAULT_IBAN = "AAAAAAAAAA";
    private static final String UPDATED_IBAN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/payment-configurations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaymentConfigurationRepository paymentConfigurationRepository;

    @Autowired
    private PaymentConfigurationMapper paymentConfigurationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentConfigurationMockMvc;

    private PaymentConfiguration paymentConfiguration;

    private PaymentConfiguration insertedPaymentConfiguration;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentConfiguration createEntity() {
        return new PaymentConfiguration()
            .onlinePaymentEnabled(DEFAULT_ONLINE_PAYMENT_ENABLED)
            .ccp(DEFAULT_CCP)
            .rip(DEFAULT_RIP)
            .rib(DEFAULT_RIB)
            .iban(DEFAULT_IBAN);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentConfiguration createUpdatedEntity() {
        return new PaymentConfiguration()
            .onlinePaymentEnabled(UPDATED_ONLINE_PAYMENT_ENABLED)
            .ccp(UPDATED_CCP)
            .rip(UPDATED_RIP)
            .rib(UPDATED_RIB)
            .iban(UPDATED_IBAN);
    }

    @BeforeEach
    void initTest() {
        paymentConfiguration = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPaymentConfiguration != null) {
            paymentConfigurationRepository.delete(insertedPaymentConfiguration);
            insertedPaymentConfiguration = null;
        }
    }

    @Test
    @Transactional
    void createPaymentConfiguration() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PaymentConfiguration
        PaymentConfigurationDTO paymentConfigurationDTO = paymentConfigurationMapper.toDto(paymentConfiguration);
        var returnedPaymentConfigurationDTO = om.readValue(
            restPaymentConfigurationMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentConfigurationDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PaymentConfigurationDTO.class
        );

        // Validate the PaymentConfiguration in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPaymentConfiguration = paymentConfigurationMapper.toEntity(returnedPaymentConfigurationDTO);
        assertPaymentConfigurationUpdatableFieldsEquals(
            returnedPaymentConfiguration,
            getPersistedPaymentConfiguration(returnedPaymentConfiguration)
        );

        insertedPaymentConfiguration = returnedPaymentConfiguration;
    }

    @Test
    @Transactional
    void createPaymentConfigurationWithExistingId() throws Exception {
        // Create the PaymentConfiguration with an existing ID
        paymentConfiguration.setId(1L);
        PaymentConfigurationDTO paymentConfigurationDTO = paymentConfigurationMapper.toDto(paymentConfiguration);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentConfigurationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentConfigurationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PaymentConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPaymentConfigurations() throws Exception {
        // Initialize the database
        insertedPaymentConfiguration = paymentConfigurationRepository.saveAndFlush(paymentConfiguration);

        // Get all the paymentConfigurationList
        restPaymentConfigurationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentConfiguration.getId().intValue())))
            .andExpect(jsonPath("$.[*].onlinePaymentEnabled").value(hasItem(DEFAULT_ONLINE_PAYMENT_ENABLED)))
            .andExpect(jsonPath("$.[*].ccp").value(hasItem(DEFAULT_CCP)))
            .andExpect(jsonPath("$.[*].rip").value(hasItem(DEFAULT_RIP)))
            .andExpect(jsonPath("$.[*].rib").value(hasItem(DEFAULT_RIB)))
            .andExpect(jsonPath("$.[*].iban").value(hasItem(DEFAULT_IBAN)));
    }

    @Test
    @Transactional
    void getPaymentConfiguration() throws Exception {
        // Initialize the database
        insertedPaymentConfiguration = paymentConfigurationRepository.saveAndFlush(paymentConfiguration);

        // Get the paymentConfiguration
        restPaymentConfigurationMockMvc
            .perform(get(ENTITY_API_URL_ID, paymentConfiguration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paymentConfiguration.getId().intValue()))
            .andExpect(jsonPath("$.onlinePaymentEnabled").value(DEFAULT_ONLINE_PAYMENT_ENABLED))
            .andExpect(jsonPath("$.ccp").value(DEFAULT_CCP))
            .andExpect(jsonPath("$.rip").value(DEFAULT_RIP))
            .andExpect(jsonPath("$.rib").value(DEFAULT_RIB))
            .andExpect(jsonPath("$.iban").value(DEFAULT_IBAN));
    }

    @Test
    @Transactional
    void getNonExistingPaymentConfiguration() throws Exception {
        // Get the paymentConfiguration
        restPaymentConfigurationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaymentConfiguration() throws Exception {
        // Initialize the database
        insertedPaymentConfiguration = paymentConfigurationRepository.saveAndFlush(paymentConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentConfiguration
        PaymentConfiguration updatedPaymentConfiguration = paymentConfigurationRepository
            .findById(paymentConfiguration.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedPaymentConfiguration are not directly saved in db
        em.detach(updatedPaymentConfiguration);
        updatedPaymentConfiguration
            .onlinePaymentEnabled(UPDATED_ONLINE_PAYMENT_ENABLED)
            .ccp(UPDATED_CCP)
            .rip(UPDATED_RIP)
            .rib(UPDATED_RIB)
            .iban(UPDATED_IBAN);
        PaymentConfigurationDTO paymentConfigurationDTO = paymentConfigurationMapper.toDto(updatedPaymentConfiguration);

        restPaymentConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentConfigurationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentConfigurationDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaymentConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaymentConfigurationToMatchAllProperties(updatedPaymentConfiguration);
    }

    @Test
    @Transactional
    void putNonExistingPaymentConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentConfiguration.setId(longCount.incrementAndGet());

        // Create the PaymentConfiguration
        PaymentConfigurationDTO paymentConfigurationDTO = paymentConfigurationMapper.toDto(paymentConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentConfigurationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaymentConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentConfiguration.setId(longCount.incrementAndGet());

        // Create the PaymentConfiguration
        PaymentConfigurationDTO paymentConfigurationDTO = paymentConfigurationMapper.toDto(paymentConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaymentConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentConfiguration.setId(longCount.incrementAndGet());

        // Create the PaymentConfiguration
        PaymentConfigurationDTO paymentConfigurationDTO = paymentConfigurationMapper.toDto(paymentConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentConfigurationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentConfigurationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaymentConfigurationWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentConfiguration = paymentConfigurationRepository.saveAndFlush(paymentConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentConfiguration using partial update
        PaymentConfiguration partialUpdatedPaymentConfiguration = new PaymentConfiguration();
        partialUpdatedPaymentConfiguration.setId(paymentConfiguration.getId());

        partialUpdatedPaymentConfiguration.ccp(UPDATED_CCP).rib(UPDATED_RIB).iban(UPDATED_IBAN);

        restPaymentConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentConfiguration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentConfiguration))
            )
            .andExpect(status().isOk());

        // Validate the PaymentConfiguration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentConfigurationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPaymentConfiguration, paymentConfiguration),
            getPersistedPaymentConfiguration(paymentConfiguration)
        );
    }

    @Test
    @Transactional
    void fullUpdatePaymentConfigurationWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentConfiguration = paymentConfigurationRepository.saveAndFlush(paymentConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentConfiguration using partial update
        PaymentConfiguration partialUpdatedPaymentConfiguration = new PaymentConfiguration();
        partialUpdatedPaymentConfiguration.setId(paymentConfiguration.getId());

        partialUpdatedPaymentConfiguration
            .onlinePaymentEnabled(UPDATED_ONLINE_PAYMENT_ENABLED)
            .ccp(UPDATED_CCP)
            .rip(UPDATED_RIP)
            .rib(UPDATED_RIB)
            .iban(UPDATED_IBAN);

        restPaymentConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentConfiguration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentConfiguration))
            )
            .andExpect(status().isOk());

        // Validate the PaymentConfiguration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentConfigurationUpdatableFieldsEquals(
            partialUpdatedPaymentConfiguration,
            getPersistedPaymentConfiguration(partialUpdatedPaymentConfiguration)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPaymentConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentConfiguration.setId(longCount.incrementAndGet());

        // Create the PaymentConfiguration
        PaymentConfigurationDTO paymentConfigurationDTO = paymentConfigurationMapper.toDto(paymentConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentConfigurationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaymentConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentConfiguration.setId(longCount.incrementAndGet());

        // Create the PaymentConfiguration
        PaymentConfigurationDTO paymentConfigurationDTO = paymentConfigurationMapper.toDto(paymentConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaymentConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentConfiguration.setId(longCount.incrementAndGet());

        // Create the PaymentConfiguration
        PaymentConfigurationDTO paymentConfigurationDTO = paymentConfigurationMapper.toDto(paymentConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(paymentConfigurationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaymentConfiguration() throws Exception {
        // Initialize the database
        insertedPaymentConfiguration = paymentConfigurationRepository.saveAndFlush(paymentConfiguration);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paymentConfiguration
        restPaymentConfigurationMockMvc
            .perform(delete(ENTITY_API_URL_ID, paymentConfiguration.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paymentConfigurationRepository.count();
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

    protected PaymentConfiguration getPersistedPaymentConfiguration(PaymentConfiguration paymentConfiguration) {
        return paymentConfigurationRepository.findById(paymentConfiguration.getId()).orElseThrow();
    }

    protected void assertPersistedPaymentConfigurationToMatchAllProperties(PaymentConfiguration expectedPaymentConfiguration) {
        assertPaymentConfigurationAllPropertiesEquals(
            expectedPaymentConfiguration,
            getPersistedPaymentConfiguration(expectedPaymentConfiguration)
        );
    }

    protected void assertPersistedPaymentConfigurationToMatchUpdatableProperties(PaymentConfiguration expectedPaymentConfiguration) {
        assertPaymentConfigurationAllUpdatablePropertiesEquals(
            expectedPaymentConfiguration,
            getPersistedPaymentConfiguration(expectedPaymentConfiguration)
        );
    }
}
