package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.PaymentReceiptAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.PaymentReceipt;
import com.adeem.stockflow.domain.enumeration.ReceiptStatus;
import com.adeem.stockflow.repository.PaymentReceiptRepository;
import com.adeem.stockflow.service.dto.PaymentReceiptDTO;
import com.adeem.stockflow.service.mapper.PaymentReceiptMapper;
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
 * Integration tests for the {@link PaymentReceiptResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentReceiptResourceIT {

    private static final String DEFAULT_RECEIPT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_RECEIPT_NUMBER = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_SUBMISSION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SUBMISSION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ReceiptStatus DEFAULT_STATUS = ReceiptStatus.SUBMITTED;
    private static final ReceiptStatus UPDATED_STATUS = ReceiptStatus.UNDER_REVIEW;

    private static final ZonedDateTime DEFAULT_REVIEW_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_REVIEW_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_REVIEW_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_REVIEW_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/payment-receipts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaymentReceiptRepository paymentReceiptRepository;

    @Autowired
    private PaymentReceiptMapper paymentReceiptMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentReceiptMockMvc;

    private PaymentReceipt paymentReceipt;

    private PaymentReceipt insertedPaymentReceipt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentReceipt createEntity() {
        return new PaymentReceipt()
            .receiptNumber(DEFAULT_RECEIPT_NUMBER)
            .submissionDate(DEFAULT_SUBMISSION_DATE)
            .status(DEFAULT_STATUS)
            .reviewDate(DEFAULT_REVIEW_DATE)
            .reviewNotes(DEFAULT_REVIEW_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentReceipt createUpdatedEntity() {
        return new PaymentReceipt()
            .receiptNumber(UPDATED_RECEIPT_NUMBER)
            .submissionDate(UPDATED_SUBMISSION_DATE)
            .status(UPDATED_STATUS)
            .reviewDate(UPDATED_REVIEW_DATE)
            .reviewNotes(UPDATED_REVIEW_NOTES);
    }

    @BeforeEach
    void initTest() {
        paymentReceipt = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPaymentReceipt != null) {
            paymentReceiptRepository.delete(insertedPaymentReceipt);
            insertedPaymentReceipt = null;
        }
    }

    @Test
    @Transactional
    void createPaymentReceipt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PaymentReceipt
        PaymentReceiptDTO paymentReceiptDTO = paymentReceiptMapper.toDto(paymentReceipt);
        var returnedPaymentReceiptDTO = om.readValue(
            restPaymentReceiptMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentReceiptDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PaymentReceiptDTO.class
        );

        // Validate the PaymentReceipt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPaymentReceipt = paymentReceiptMapper.toEntity(returnedPaymentReceiptDTO);
        assertPaymentReceiptUpdatableFieldsEquals(returnedPaymentReceipt, getPersistedPaymentReceipt(returnedPaymentReceipt));

        insertedPaymentReceipt = returnedPaymentReceipt;
    }

    @Test
    @Transactional
    void createPaymentReceiptWithExistingId() throws Exception {
        // Create the PaymentReceipt with an existing ID
        paymentReceipt.setId(1L);
        PaymentReceiptDTO paymentReceiptDTO = paymentReceiptMapper.toDto(paymentReceipt);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentReceiptDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PaymentReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReceiptNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentReceipt.setReceiptNumber(null);

        // Create the PaymentReceipt, which fails.
        PaymentReceiptDTO paymentReceiptDTO = paymentReceiptMapper.toDto(paymentReceipt);

        restPaymentReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentReceiptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubmissionDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentReceipt.setSubmissionDate(null);

        // Create the PaymentReceipt, which fails.
        PaymentReceiptDTO paymentReceiptDTO = paymentReceiptMapper.toDto(paymentReceipt);

        restPaymentReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentReceiptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentReceipt.setStatus(null);

        // Create the PaymentReceipt, which fails.
        PaymentReceiptDTO paymentReceiptDTO = paymentReceiptMapper.toDto(paymentReceipt);

        restPaymentReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentReceiptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPaymentReceipts() throws Exception {
        // Initialize the database
        insertedPaymentReceipt = paymentReceiptRepository.saveAndFlush(paymentReceipt);

        // Get all the paymentReceiptList
        restPaymentReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentReceipt.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiptNumber").value(hasItem(DEFAULT_RECEIPT_NUMBER)))
            .andExpect(jsonPath("$.[*].submissionDate").value(hasItem(sameInstant(DEFAULT_SUBMISSION_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].reviewDate").value(hasItem(sameInstant(DEFAULT_REVIEW_DATE))))
            .andExpect(jsonPath("$.[*].reviewNotes").value(hasItem(DEFAULT_REVIEW_NOTES)));
    }

    @Test
    @Transactional
    void getPaymentReceipt() throws Exception {
        // Initialize the database
        insertedPaymentReceipt = paymentReceiptRepository.saveAndFlush(paymentReceipt);

        // Get the paymentReceipt
        restPaymentReceiptMockMvc
            .perform(get(ENTITY_API_URL_ID, paymentReceipt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paymentReceipt.getId().intValue()))
            .andExpect(jsonPath("$.receiptNumber").value(DEFAULT_RECEIPT_NUMBER))
            .andExpect(jsonPath("$.submissionDate").value(sameInstant(DEFAULT_SUBMISSION_DATE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.reviewDate").value(sameInstant(DEFAULT_REVIEW_DATE)))
            .andExpect(jsonPath("$.reviewNotes").value(DEFAULT_REVIEW_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingPaymentReceipt() throws Exception {
        // Get the paymentReceipt
        restPaymentReceiptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaymentReceipt() throws Exception {
        // Initialize the database
        insertedPaymentReceipt = paymentReceiptRepository.saveAndFlush(paymentReceipt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentReceipt
        PaymentReceipt updatedPaymentReceipt = paymentReceiptRepository.findById(paymentReceipt.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPaymentReceipt are not directly saved in db
        em.detach(updatedPaymentReceipt);
        updatedPaymentReceipt
            .receiptNumber(UPDATED_RECEIPT_NUMBER)
            .submissionDate(UPDATED_SUBMISSION_DATE)
            .status(UPDATED_STATUS)
            .reviewDate(UPDATED_REVIEW_DATE)
            .reviewNotes(UPDATED_REVIEW_NOTES);
        PaymentReceiptDTO paymentReceiptDTO = paymentReceiptMapper.toDto(updatedPaymentReceipt);

        restPaymentReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentReceiptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentReceiptDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaymentReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaymentReceiptToMatchAllProperties(updatedPaymentReceipt);
    }

    @Test
    @Transactional
    void putNonExistingPaymentReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentReceipt.setId(longCount.incrementAndGet());

        // Create the PaymentReceipt
        PaymentReceiptDTO paymentReceiptDTO = paymentReceiptMapper.toDto(paymentReceipt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentReceiptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaymentReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentReceipt.setId(longCount.incrementAndGet());

        // Create the PaymentReceipt
        PaymentReceiptDTO paymentReceiptDTO = paymentReceiptMapper.toDto(paymentReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaymentReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentReceipt.setId(longCount.incrementAndGet());

        // Create the PaymentReceipt
        PaymentReceiptDTO paymentReceiptDTO = paymentReceiptMapper.toDto(paymentReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentReceiptMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentReceiptDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaymentReceiptWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentReceipt = paymentReceiptRepository.saveAndFlush(paymentReceipt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentReceipt using partial update
        PaymentReceipt partialUpdatedPaymentReceipt = new PaymentReceipt();
        partialUpdatedPaymentReceipt.setId(paymentReceipt.getId());

        partialUpdatedPaymentReceipt.receiptNumber(UPDATED_RECEIPT_NUMBER).status(UPDATED_STATUS).reviewDate(UPDATED_REVIEW_DATE);

        restPaymentReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentReceipt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentReceipt))
            )
            .andExpect(status().isOk());

        // Validate the PaymentReceipt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentReceiptUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPaymentReceipt, paymentReceipt),
            getPersistedPaymentReceipt(paymentReceipt)
        );
    }

    @Test
    @Transactional
    void fullUpdatePaymentReceiptWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentReceipt = paymentReceiptRepository.saveAndFlush(paymentReceipt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentReceipt using partial update
        PaymentReceipt partialUpdatedPaymentReceipt = new PaymentReceipt();
        partialUpdatedPaymentReceipt.setId(paymentReceipt.getId());

        partialUpdatedPaymentReceipt
            .receiptNumber(UPDATED_RECEIPT_NUMBER)
            .submissionDate(UPDATED_SUBMISSION_DATE)
            .status(UPDATED_STATUS)
            .reviewDate(UPDATED_REVIEW_DATE)
            .reviewNotes(UPDATED_REVIEW_NOTES);

        restPaymentReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentReceipt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentReceipt))
            )
            .andExpect(status().isOk());

        // Validate the PaymentReceipt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentReceiptUpdatableFieldsEquals(partialUpdatedPaymentReceipt, getPersistedPaymentReceipt(partialUpdatedPaymentReceipt));
    }

    @Test
    @Transactional
    void patchNonExistingPaymentReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentReceipt.setId(longCount.incrementAndGet());

        // Create the PaymentReceipt
        PaymentReceiptDTO paymentReceiptDTO = paymentReceiptMapper.toDto(paymentReceipt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentReceiptDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaymentReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentReceipt.setId(longCount.incrementAndGet());

        // Create the PaymentReceipt
        PaymentReceiptDTO paymentReceiptDTO = paymentReceiptMapper.toDto(paymentReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaymentReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentReceipt.setId(longCount.incrementAndGet());

        // Create the PaymentReceipt
        PaymentReceiptDTO paymentReceiptDTO = paymentReceiptMapper.toDto(paymentReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentReceiptMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(paymentReceiptDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaymentReceipt() throws Exception {
        // Initialize the database
        insertedPaymentReceipt = paymentReceiptRepository.saveAndFlush(paymentReceipt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paymentReceipt
        restPaymentReceiptMockMvc
            .perform(delete(ENTITY_API_URL_ID, paymentReceipt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paymentReceiptRepository.count();
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

    protected PaymentReceipt getPersistedPaymentReceipt(PaymentReceipt paymentReceipt) {
        return paymentReceiptRepository.findById(paymentReceipt.getId()).orElseThrow();
    }

    protected void assertPersistedPaymentReceiptToMatchAllProperties(PaymentReceipt expectedPaymentReceipt) {
        assertPaymentReceiptAllPropertiesEquals(expectedPaymentReceipt, getPersistedPaymentReceipt(expectedPaymentReceipt));
    }

    protected void assertPersistedPaymentReceiptToMatchUpdatableProperties(PaymentReceipt expectedPaymentReceipt) {
        assertPaymentReceiptAllUpdatablePropertiesEquals(expectedPaymentReceipt, getPersistedPaymentReceipt(expectedPaymentReceipt));
    }
}
