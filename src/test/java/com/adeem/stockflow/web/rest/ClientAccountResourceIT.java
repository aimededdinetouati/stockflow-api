package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.ClientAccountAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.enumeration.AccountStatus;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.mapper.ClientAccountMapper;
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
 * Integration tests for the {@link ClientAccountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientAccountResourceIT {

    private static final String DEFAULT_COMPANY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_PERSON = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_PERSON = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final AccountStatus DEFAULT_STATUS = AccountStatus.ENABLED;
    private static final AccountStatus UPDATED_STATUS = AccountStatus.DISABLED;

    private static final String ENTITY_API_URL = "/api/client-accounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private ClientAccountMapper clientAccountMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientAccountMockMvc;

    private ClientAccount clientAccount;

    private ClientAccount insertedClientAccount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientAccount createEntity() {
        return new ClientAccount()
            .companyName(DEFAULT_COMPANY_NAME)
            .contactPerson(DEFAULT_CONTACT_PERSON)
            .phone(DEFAULT_PHONE)
            .email(DEFAULT_EMAIL)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientAccount createUpdatedEntity() {
        return new ClientAccount()
            .companyName(UPDATED_COMPANY_NAME)
            .contactPerson(UPDATED_CONTACT_PERSON)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        clientAccount = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClientAccount != null) {
            clientAccountRepository.delete(insertedClientAccount);
            insertedClientAccount = null;
        }
    }

    @Test
    @Transactional
    void createClientAccount() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClientAccount
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);
        var returnedClientAccountDTO = om.readValue(
            restClientAccountMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientAccountDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClientAccountDTO.class
        );

        // Validate the ClientAccount in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClientAccount = clientAccountMapper.toEntity(returnedClientAccountDTO);
        assertClientAccountUpdatableFieldsEquals(returnedClientAccount, getPersistedClientAccount(returnedClientAccount));

        insertedClientAccount = returnedClientAccount;
    }

    @Test
    @Transactional
    void createClientAccountWithExistingId() throws Exception {
        // Create the ClientAccount with an existing ID
        clientAccount.setId(1L);
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientAccountDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClientAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCompanyNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clientAccount.setCompanyName(null);

        // Create the ClientAccount, which fails.
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);

        restClientAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientAccountDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContactPersonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clientAccount.setContactPerson(null);

        // Create the ClientAccount, which fails.
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);

        restClientAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientAccountDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clientAccount.setPhone(null);

        // Create the ClientAccount, which fails.
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);

        restClientAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientAccountDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clientAccount.setEmail(null);

        // Create the ClientAccount, which fails.
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);

        restClientAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientAccountDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clientAccount.setStatus(null);

        // Create the ClientAccount, which fails.
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);

        restClientAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientAccountDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClientAccounts() throws Exception {
        // Initialize the database
        insertedClientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        // Get all the clientAccountList
        restClientAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].contactPerson").value(hasItem(DEFAULT_CONTACT_PERSON)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getClientAccount() throws Exception {
        // Initialize the database
        insertedClientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        // Get the clientAccount
        restClientAccountMockMvc
            .perform(get(ENTITY_API_URL_ID, clientAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientAccount.getId().intValue()))
            .andExpect(jsonPath("$.companyName").value(DEFAULT_COMPANY_NAME))
            .andExpect(jsonPath("$.contactPerson").value(DEFAULT_CONTACT_PERSON))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingClientAccount() throws Exception {
        // Get the clientAccount
        restClientAccountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClientAccount() throws Exception {
        // Initialize the database
        insertedClientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientAccount
        ClientAccount updatedClientAccount = clientAccountRepository.findById(clientAccount.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClientAccount are not directly saved in db
        em.detach(updatedClientAccount);
        updatedClientAccount
            .companyName(UPDATED_COMPANY_NAME)
            .contactPerson(UPDATED_CONTACT_PERSON)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .status(UPDATED_STATUS);
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(updatedClientAccount);

        restClientAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientAccountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientAccountDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClientAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClientAccountToMatchAllProperties(updatedClientAccount);
    }

    @Test
    @Transactional
    void putNonExistingClientAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientAccount.setId(longCount.incrementAndGet());

        // Create the ClientAccount
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientAccountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientAccount.setId(longCount.incrementAndGet());

        // Create the ClientAccount
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientAccount.setId(longCount.incrementAndGet());

        // Create the ClientAccount
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientAccountMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientAccountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientAccountWithPatch() throws Exception {
        // Initialize the database
        insertedClientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientAccount using partial update
        ClientAccount partialUpdatedClientAccount = new ClientAccount();
        partialUpdatedClientAccount.setId(clientAccount.getId());

        partialUpdatedClientAccount.phone(UPDATED_PHONE).email(UPDATED_EMAIL).status(UPDATED_STATUS);

        restClientAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClientAccount))
            )
            .andExpect(status().isOk());

        // Validate the ClientAccount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientAccountUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClientAccount, clientAccount),
            getPersistedClientAccount(clientAccount)
        );
    }

    @Test
    @Transactional
    void fullUpdateClientAccountWithPatch() throws Exception {
        // Initialize the database
        insertedClientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientAccount using partial update
        ClientAccount partialUpdatedClientAccount = new ClientAccount();
        partialUpdatedClientAccount.setId(clientAccount.getId());

        partialUpdatedClientAccount
            .companyName(UPDATED_COMPANY_NAME)
            .contactPerson(UPDATED_CONTACT_PERSON)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .status(UPDATED_STATUS);

        restClientAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClientAccount))
            )
            .andExpect(status().isOk());

        // Validate the ClientAccount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientAccountUpdatableFieldsEquals(partialUpdatedClientAccount, getPersistedClientAccount(partialUpdatedClientAccount));
    }

    @Test
    @Transactional
    void patchNonExistingClientAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientAccount.setId(longCount.incrementAndGet());

        // Create the ClientAccount
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientAccountDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientAccount.setId(longCount.incrementAndGet());

        // Create the ClientAccount
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientAccount.setId(longCount.incrementAndGet());

        // Create the ClientAccount
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(clientAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientAccountMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clientAccountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientAccount() throws Exception {
        // Initialize the database
        insertedClientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the clientAccount
        restClientAccountMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientAccount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clientAccountRepository.count();
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

    protected ClientAccount getPersistedClientAccount(ClientAccount clientAccount) {
        return clientAccountRepository.findById(clientAccount.getId()).orElseThrow();
    }

    protected void assertPersistedClientAccountToMatchAllProperties(ClientAccount expectedClientAccount) {
        assertClientAccountAllPropertiesEquals(expectedClientAccount, getPersistedClientAccount(expectedClientAccount));
    }

    protected void assertPersistedClientAccountToMatchUpdatableProperties(ClientAccount expectedClientAccount) {
        assertClientAccountAllUpdatablePropertiesEquals(expectedClientAccount, getPersistedClientAccount(expectedClientAccount));
    }
}
