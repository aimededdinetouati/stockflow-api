package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.SubscriptionAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameInstant;
import static com.adeem.stockflow.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.Subscription;
import com.adeem.stockflow.domain.enumeration.SubscriptionStatus;
import com.adeem.stockflow.repository.SubscriptionRepository;
import com.adeem.stockflow.service.dto.SubscriptionDTO;
import com.adeem.stockflow.service.mapper.SubscriptionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link SubscriptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubscriptionResourceIT {

    private static final ZonedDateTime DEFAULT_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final SubscriptionStatus DEFAULT_STATUS = SubscriptionStatus.ACTIVE;
    private static final SubscriptionStatus UPDATED_STATUS = SubscriptionStatus.PENDING;

    private static final String DEFAULT_PAYMENT_METHOD = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_METHOD = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_ACTUAL_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_ACTUAL_PRICE = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/subscriptiones";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubscriptionMockMvc;

    private Subscription subscription;

    private Subscription insertedSubscription;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subscription createEntity() {
        return new Subscription()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .status(DEFAULT_STATUS)
            .paymentMethod(DEFAULT_PAYMENT_METHOD)
            .actualPrice(DEFAULT_ACTUAL_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subscription createUpdatedEntity() {
        return new Subscription()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .actualPrice(UPDATED_ACTUAL_PRICE);
    }

    @BeforeEach
    void initTest() {
        subscription = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSubscription != null) {
            subscriptionRepository.delete(insertedSubscription);
            insertedSubscription = null;
        }
    }

    @Test
    @Transactional
    void createSubscription() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);
        var returnedSubscriptionDTO = om.readValue(
            restSubscriptionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SubscriptionDTO.class
        );

        // Validate the Subscription in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSubscription = subscriptionMapper.toEntity(returnedSubscriptionDTO);
        assertSubscriptionUpdatableFieldsEquals(returnedSubscription, getPersistedSubscription(returnedSubscription));

        insertedSubscription = returnedSubscription;
    }

    @Test
    @Transactional
    void createSubscriptionWithExistingId() throws Exception {
        // Create the Subscription with an existing ID
        subscription.setId(1L);
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subscription.setStartDate(null);

        // Create the Subscription, which fails.
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        restSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subscription.setEndDate(null);

        // Create the Subscription, which fails.
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        restSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subscription.setStatus(null);

        // Create the Subscription, which fails.
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        restSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentMethodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subscription.setPaymentMethod(null);

        // Create the Subscription, which fails.
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        restSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActualPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subscription.setActualPrice(null);

        // Create the Subscription, which fails.
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        restSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSubscriptiones() throws Exception {
        // Initialize the database
        insertedSubscription = subscriptionRepository.saveAndFlush(subscription);

        // Get all the subscriptionList
        restSubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subscription.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD)))
            .andExpect(jsonPath("$.[*].actualPrice").value(hasItem(sameNumber(DEFAULT_ACTUAL_PRICE))));
    }

    @Test
    @Transactional
    void getSubscription() throws Exception {
        // Initialize the database
        insertedSubscription = subscriptionRepository.saveAndFlush(subscription);

        // Get the subscription
        restSubscriptionMockMvc
            .perform(get(ENTITY_API_URL_ID, subscription.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subscription.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
            .andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.paymentMethod").value(DEFAULT_PAYMENT_METHOD))
            .andExpect(jsonPath("$.actualPrice").value(sameNumber(DEFAULT_ACTUAL_PRICE)));
    }

    @Test
    @Transactional
    void getNonExistingSubscription() throws Exception {
        // Get the subscription
        restSubscriptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSubscription() throws Exception {
        // Initialize the database
        insertedSubscription = subscriptionRepository.saveAndFlush(subscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subscription
        Subscription updatedSubscription = subscriptionRepository.findById(subscription.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSubscription are not directly saved in db
        em.detach(updatedSubscription);
        updatedSubscription
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .actualPrice(UPDATED_ACTUAL_PRICE);
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(updatedSubscription);

        restSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subscriptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSubscriptionToMatchAllProperties(updatedSubscription);
    }

    @Test
    @Transactional
    void putNonExistingSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscription.setId(longCount.incrementAndGet());

        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subscriptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscription.setId(longCount.incrementAndGet());

        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscription.setId(longCount.incrementAndGet());

        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedSubscription = subscriptionRepository.saveAndFlush(subscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subscription using partial update
        Subscription partialUpdatedSubscription = new Subscription();
        partialUpdatedSubscription.setId(subscription.getId());

        partialUpdatedSubscription.startDate(UPDATED_START_DATE).status(UPDATED_STATUS).paymentMethod(UPDATED_PAYMENT_METHOD);

        restSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubscription.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubscription))
            )
            .andExpect(status().isOk());

        // Validate the Subscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubscriptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSubscription, subscription),
            getPersistedSubscription(subscription)
        );
    }

    @Test
    @Transactional
    void fullUpdateSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedSubscription = subscriptionRepository.saveAndFlush(subscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subscription using partial update
        Subscription partialUpdatedSubscription = new Subscription();
        partialUpdatedSubscription.setId(subscription.getId());

        partialUpdatedSubscription
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .actualPrice(UPDATED_ACTUAL_PRICE);

        restSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubscription.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubscription))
            )
            .andExpect(status().isOk());

        // Validate the Subscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubscriptionUpdatableFieldsEquals(partialUpdatedSubscription, getPersistedSubscription(partialUpdatedSubscription));
    }

    @Test
    @Transactional
    void patchNonExistingSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscription.setId(longCount.incrementAndGet());

        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subscriptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscription.setId(longCount.incrementAndGet());

        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscription.setId(longCount.incrementAndGet());

        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(subscriptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSubscription() throws Exception {
        // Initialize the database
        insertedSubscription = subscriptionRepository.saveAndFlush(subscription);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the subscription
        restSubscriptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, subscription.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return subscriptionRepository.count();
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

    protected Subscription getPersistedSubscription(Subscription subscription) {
        return subscriptionRepository.findById(subscription.getId()).orElseThrow();
    }

    protected void assertPersistedSubscriptionToMatchAllProperties(Subscription expectedSubscription) {
        assertSubscriptionAllPropertiesEquals(expectedSubscription, getPersistedSubscription(expectedSubscription));
    }

    protected void assertPersistedSubscriptionToMatchUpdatableProperties(Subscription expectedSubscription) {
        assertSubscriptionAllUpdatablePropertiesEquals(expectedSubscription, getPersistedSubscription(expectedSubscription));
    }
}
