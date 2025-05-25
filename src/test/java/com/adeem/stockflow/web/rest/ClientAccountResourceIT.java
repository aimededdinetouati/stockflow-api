package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.ClientAccountAsserts.*;
import static com.adeem.stockflow.security.TestSecurityContextHelper.setSecurityContextWithClientAccountId;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.Address;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.enumeration.AccountStatus;
import com.adeem.stockflow.domain.enumeration.AddressType;
import com.adeem.stockflow.repository.AddressRepository;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.service.dto.AddressDTO;
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
class ClientAccountResourceIT {

    private static final String DEFAULT_COMPANY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA@mail.com";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB@mail.com";

    private static final AccountStatus DEFAULT_STATUS = AccountStatus.ENABLED;
    private static final AccountStatus UPDATED_STATUS = AccountStatus.ENABLED;

    private static final String ENTITY_API_URL = "/api/client-accounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_API_URL_MINE = ENTITY_API_URL + "/mine";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private AddressRepository addressRepository;

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
        return new ClientAccount().companyName(DEFAULT_COMPANY_NAME).phone(DEFAULT_PHONE).email(DEFAULT_EMAIL).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientAccount createUpdatedEntity() {
        return new ClientAccount().companyName(UPDATED_COMPANY_NAME).phone(UPDATED_PHONE).email(UPDATED_EMAIL).status(UPDATED_STATUS);
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
    @WithMockUser(authorities = "ROLE_ADMIN")
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
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
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
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getNonExistingClientAccount() throws Exception {
        // Get the clientAccount
        restClientAccountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
    void putExistingClientAccount() throws Exception {
        // Initialize the database
        insertedClientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientAccount
        ClientAccount updatedClientAccount = clientAccountRepository.findById(clientAccount.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClientAccount are not directly saved in db
        em.detach(updatedClientAccount);
        updatedClientAccount.companyName(UPDATED_COMPANY_NAME).phone(UPDATED_PHONE).email(UPDATED_EMAIL).status(UPDATED_STATUS);
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
    @WithMockUser(authorities = "ROLE_ADMIN")
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
    @WithMockUser(authorities = "ROLE_ADMIN")
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
    @WithMockUser(authorities = "ROLE_ADMIN")
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
    void updateMyClientAccountShouldWork() throws Exception {
        // Initialize the database
        insertedClientAccount = clientAccountRepository.saveAndFlush(clientAccount);
        setSecurityContextWithClientAccountId(insertedClientAccount.getId());

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientAccount
        ClientAccount updatedClientAccount = clientAccountRepository.findById(clientAccount.getId()).orElseThrow();
        em.detach(updatedClientAccount);
        updatedClientAccount.companyName(UPDATED_COMPANY_NAME).phone(UPDATED_PHONE).email(UPDATED_EMAIL);
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(updatedClientAccount);

        restClientAccountMockMvc
            .perform(put(ENTITY_API_URL_MINE).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientAccountDTO)))
            .andExpect(status().isOk());

        // Validate the ClientAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClientAccountToMatchUpdatableProperties(updatedClientAccount);
    }

    @Test
    @Transactional
    void updateMyClientAccountWithNonExistingAddressAndNewAddress() throws Exception {
        // Initialize the database
        insertedClientAccount = clientAccountRepository.saveAndFlush(clientAccount);
        setSecurityContextWithClientAccountId(insertedClientAccount.getId());

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientAccount
        ClientAccount updatedClientAccount = clientAccountRepository.findById(clientAccount.getId()).orElseThrow();
        em.detach(updatedClientAccount);
        updatedClientAccount.companyName(UPDATED_COMPANY_NAME).phone(UPDATED_PHONE).email(UPDATED_EMAIL);
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(updatedClientAccount);

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCountry("US");
        addressDTO.setCity("New York");
        addressDTO.setState("NY");
        addressDTO.setStreetAddress("New York");
        addressDTO.setPostalCode("12345");
        addressDTO.setAddressType(AddressType.PRIMARY);
        addressDTO.setIsDefault(true);
        clientAccountDTO.setAddress(addressDTO);

        restClientAccountMockMvc
            .perform(put(ENTITY_API_URL_MINE).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientAccountDTO)))
            .andExpect(status().isOk());

        ClientAccount clientAccount1 = clientAccountRepository.findById(clientAccount.getId()).orElseThrow();
        assertThat(addressRepository.findAll()).hasSize(1);
        assertThat(clientAccount1.getAddress().getStreetAddress()).isEqualTo(addressDTO.getStreetAddress());

        // Validate the ClientAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        //assertPersistedClientAccountToMatchUpdatableProperties(updatedClientAccount);
    }

    @Test
    @Transactional
    void updateMyClientAccountWithExistingAddressAndNewAddress() throws Exception {
        // Initialize the database
        Address address = new Address();
        address.setCountry("US");
        address.setCity("New York");
        address.setState("NY");
        address.setStreetAddress("New York");
        address.setPostalCode("12345");
        address.setAddressType(AddressType.PRIMARY);
        address.setIsDefault(true);
        var savedAddress = addressRepository.save(address);

        clientAccount.setAddress(savedAddress);
        insertedClientAccount = clientAccountRepository.saveAndFlush(clientAccount);
        setSecurityContextWithClientAccountId(insertedClientAccount.getId());

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientAccount
        Address updatedAddress = addressRepository.findById(address.getId()).orElseThrow();
        ClientAccount updatedClientAccount = clientAccountRepository.findById(clientAccount.getId()).orElseThrow();
        em.detach(updatedClientAccount);
        em.detach(updatedAddress);
        updatedClientAccount.companyName(UPDATED_COMPANY_NAME).phone(UPDATED_PHONE).email(UPDATED_EMAIL);

        updatedAddress.setCountry("DZ");
        updatedAddress.setCity("Oran");
        updatedAddress.setState("Oran");
        updatedAddress.setStreetAddress("Oran");
        updatedAddress.setPostalCode("0001");
        updatedAddress.setAddressType(AddressType.SHIPPING);
        updatedAddress.setIsDefault(true);

        updatedClientAccount.setAddress(updatedAddress);
        ClientAccountDTO clientAccountDTO = clientAccountMapper.toDto(updatedClientAccount);

        restClientAccountMockMvc
            .perform(put(ENTITY_API_URL_MINE).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientAccountDTO)))
            .andExpect(status().isOk());

        ClientAccount clientAccount1 = clientAccountRepository.findById(clientAccount.getId()).orElseThrow();
        assertThat(addressRepository.findAll()).hasSize(1);
        assertThat(clientAccount1.getAddress().getStreetAddress()).isEqualTo(updatedAddress.getStreetAddress());
        assertThat(clientAccount1.getAddress().getAddressType()).isEqualTo(updatedAddress.getAddressType());
        assertThat(clientAccount1.getAddress().getCountry()).isEqualTo(updatedAddress.getCountry());
        assertThat(clientAccount1.getAddress().getCity()).isEqualTo(updatedAddress.getCity());
        assertThat(clientAccount1.getAddress().getState()).isEqualTo(updatedAddress.getState());
        assertThat(clientAccount1.getAddress().getPostalCode()).isEqualTo(updatedAddress.getPostalCode());

        // Validate the ClientAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClientAccountToMatchUpdatableProperties(updatedClientAccount);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_USER")
    void nonAdminShouldNotAccessAdminEndpoints() throws Exception {
        // Initialize the database
        insertedClientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        restClientAccountMockMvc.perform(get(ENTITY_API_URL)).andExpect(status().isForbidden());
        restClientAccountMockMvc.perform(get(ENTITY_API_URL_ID, clientAccount.getId())).andExpect(status().isForbidden());
        restClientAccountMockMvc.perform(delete(ENTITY_API_URL_ID, clientAccount.getId())).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
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
