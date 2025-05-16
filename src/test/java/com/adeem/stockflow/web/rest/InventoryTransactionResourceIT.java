package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.InventoryTransactionAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameInstant;
import static com.adeem.stockflow.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.InventoryTransaction;
import com.adeem.stockflow.domain.enumeration.TransactionType;
import com.adeem.stockflow.repository.InventoryTransactionRepository;
import com.adeem.stockflow.service.dto.InventoryTransactionDTO;
import com.adeem.stockflow.service.mapper.InventoryTransactionMapper;
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
 * Integration tests for the {@link InventoryTransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InventoryTransactionResourceIT {

    private static final TransactionType DEFAULT_TRANSACTION_TYPE = TransactionType.PURCHASE;
    private static final TransactionType UPDATED_TRANSACTION_TYPE = TransactionType.SALE;

    private static final BigDecimal DEFAULT_QUANTITY = new BigDecimal(1);
    private static final BigDecimal UPDATED_QUANTITY = new BigDecimal(2);

    private static final ZonedDateTime DEFAULT_TRANSACTION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TRANSACTION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_REFERENCE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/inventory-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Autowired
    private InventoryTransactionMapper inventoryTransactionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventoryTransactionMockMvc;

    private InventoryTransaction inventoryTransaction;

    private InventoryTransaction insertedInventoryTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryTransaction createEntity() {
        return new InventoryTransaction()
            .transactionType(DEFAULT_TRANSACTION_TYPE)
            .quantity(DEFAULT_QUANTITY)
            .transactionDate(DEFAULT_TRANSACTION_DATE)
            .referenceNumber(DEFAULT_REFERENCE_NUMBER)
            .notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryTransaction createUpdatedEntity() {
        return new InventoryTransaction()
            .transactionType(UPDATED_TRANSACTION_TYPE)
            .quantity(UPDATED_QUANTITY)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .referenceNumber(UPDATED_REFERENCE_NUMBER)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    void initTest() {
        inventoryTransaction = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInventoryTransaction != null) {
            inventoryTransactionRepository.delete(insertedInventoryTransaction);
            insertedInventoryTransaction = null;
        }
    }

    @Test
    @Transactional
    void createInventoryTransaction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);
        var returnedInventoryTransactionDTO = om.readValue(
            restInventoryTransactionMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InventoryTransactionDTO.class
        );

        // Validate the InventoryTransaction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInventoryTransaction = inventoryTransactionMapper.toEntity(returnedInventoryTransactionDTO);
        assertInventoryTransactionUpdatableFieldsEquals(
            returnedInventoryTransaction,
            getPersistedInventoryTransaction(returnedInventoryTransaction)
        );

        insertedInventoryTransaction = returnedInventoryTransaction;
    }

    @Test
    @Transactional
    void createInventoryTransactionWithExistingId() throws Exception {
        // Create the InventoryTransaction with an existing ID
        inventoryTransaction.setId(1L);
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventoryTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTransactionTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryTransaction.setTransactionType(null);

        // Create the InventoryTransaction, which fails.
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        restInventoryTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryTransaction.setQuantity(null);

        // Create the InventoryTransaction, which fails.
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        restInventoryTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTransactionDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryTransaction.setTransactionDate(null);

        // Create the InventoryTransaction, which fails.
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        restInventoryTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReferenceNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryTransaction.setReferenceNumber(null);

        // Create the InventoryTransaction, which fails.
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        restInventoryTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInventoryTransactions() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get all the inventoryTransactionList
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventoryTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionType").value(hasItem(DEFAULT_TRANSACTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].transactionDate").value(hasItem(sameInstant(DEFAULT_TRANSACTION_DATE))))
            .andExpect(jsonPath("$.[*].referenceNumber").value(hasItem(DEFAULT_REFERENCE_NUMBER)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @Test
    @Transactional
    void getInventoryTransaction() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Get the inventoryTransaction
        restInventoryTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, inventoryTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventoryTransaction.getId().intValue()))
            .andExpect(jsonPath("$.transactionType").value(DEFAULT_TRANSACTION_TYPE.toString()))
            .andExpect(jsonPath("$.quantity").value(sameNumber(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.transactionDate").value(sameInstant(DEFAULT_TRANSACTION_DATE)))
            .andExpect(jsonPath("$.referenceNumber").value(DEFAULT_REFERENCE_NUMBER))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingInventoryTransaction() throws Exception {
        // Get the inventoryTransaction
        restInventoryTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInventoryTransaction() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryTransaction
        InventoryTransaction updatedInventoryTransaction = inventoryTransactionRepository
            .findById(inventoryTransaction.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedInventoryTransaction are not directly saved in db
        em.detach(updatedInventoryTransaction);
        updatedInventoryTransaction
            .transactionType(UPDATED_TRANSACTION_TYPE)
            .quantity(UPDATED_QUANTITY)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .referenceNumber(UPDATED_REFERENCE_NUMBER)
            .notes(UPDATED_NOTES);
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(updatedInventoryTransaction);

        restInventoryTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInventoryTransactionToMatchAllProperties(updatedInventoryTransaction);
    }

    @Test
    @Transactional
    void putNonExistingInventoryTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryTransaction.setId(longCount.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInventoryTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryTransaction.setId(longCount.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInventoryTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryTransaction.setId(longCount.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryTransactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInventoryTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryTransaction using partial update
        InventoryTransaction partialUpdatedInventoryTransaction = new InventoryTransaction();
        partialUpdatedInventoryTransaction.setId(inventoryTransaction.getId());

        partialUpdatedInventoryTransaction
            .transactionType(UPDATED_TRANSACTION_TYPE)
            .quantity(UPDATED_QUANTITY)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .notes(UPDATED_NOTES);

        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventoryTransaction))
            )
            .andExpect(status().isOk());

        // Validate the InventoryTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryTransactionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInventoryTransaction, inventoryTransaction),
            getPersistedInventoryTransaction(inventoryTransaction)
        );
    }

    @Test
    @Transactional
    void fullUpdateInventoryTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryTransaction using partial update
        InventoryTransaction partialUpdatedInventoryTransaction = new InventoryTransaction();
        partialUpdatedInventoryTransaction.setId(inventoryTransaction.getId());

        partialUpdatedInventoryTransaction
            .transactionType(UPDATED_TRANSACTION_TYPE)
            .quantity(UPDATED_QUANTITY)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .referenceNumber(UPDATED_REFERENCE_NUMBER)
            .notes(UPDATED_NOTES);

        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventoryTransaction))
            )
            .andExpect(status().isOk());

        // Validate the InventoryTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryTransactionUpdatableFieldsEquals(
            partialUpdatedInventoryTransaction,
            getPersistedInventoryTransaction(partialUpdatedInventoryTransaction)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInventoryTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryTransaction.setId(longCount.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inventoryTransactionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInventoryTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryTransaction.setId(longCount.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInventoryTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryTransaction.setId(longCount.incrementAndGet());

        // Create the InventoryTransaction
        InventoryTransactionDTO inventoryTransactionDTO = inventoryTransactionMapper.toDto(inventoryTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(inventoryTransactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInventoryTransaction() throws Exception {
        // Initialize the database
        insertedInventoryTransaction = inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inventoryTransaction
        restInventoryTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, inventoryTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inventoryTransactionRepository.count();
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

    protected InventoryTransaction getPersistedInventoryTransaction(InventoryTransaction inventoryTransaction) {
        return inventoryTransactionRepository.findById(inventoryTransaction.getId()).orElseThrow();
    }

    protected void assertPersistedInventoryTransactionToMatchAllProperties(InventoryTransaction expectedInventoryTransaction) {
        assertInventoryTransactionAllPropertiesEquals(
            expectedInventoryTransaction,
            getPersistedInventoryTransaction(expectedInventoryTransaction)
        );
    }

    protected void assertPersistedInventoryTransactionToMatchUpdatableProperties(InventoryTransaction expectedInventoryTransaction) {
        assertInventoryTransactionAllUpdatablePropertiesEquals(
            expectedInventoryTransaction,
            getPersistedInventoryTransaction(expectedInventoryTransaction)
        );
    }
}
