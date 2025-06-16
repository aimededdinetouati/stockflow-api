package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.security.TestSecurityContextHelper.setSecurityContextWithClientAccountId;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.domain.User;
import com.adeem.stockflow.domain.enumeration.AccountStatus;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.repository.CustomerRepository;
import com.adeem.stockflow.repository.UserRepository;
import com.adeem.stockflow.security.AuthoritiesConstants;
import com.adeem.stockflow.security.TestSecurityContextHelper;
import com.adeem.stockflow.service.dto.CreateAccountRequestDTO;
import com.adeem.stockflow.service.dto.CustomerDTO;
import com.adeem.stockflow.service.mapper.CustomerMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CustomerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class CustomerResourceIT {

    private static final String DEFAULT_FIRST_NAME = "John";
    private static final String UPDATED_FIRST_NAME = "Jane";

    private static final String DEFAULT_LAST_NAME = "Doe";
    private static final String UPDATED_LAST_NAME = "Smith";

    private static final String DEFAULT_PHONE = "1234567890";
    private static final String UPDATED_PHONE = "0987654321";

    private static final String DEFAULT_EMAIL = "john.doe@example.com";
    private static final String UPDATED_EMAIL = "jane.smith@example.com";

    private static final String DEFAULT_TAX_ID = "TAX123456";
    private static final String UPDATED_TAX_ID = "TAX654321";

    private static final Boolean DEFAULT_ENABLED = true;
    private static final Boolean UPDATED_ENABLED = false;

    private static final String ENTITY_API_URL = "/api/customers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomerMockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer;
    private ClientAccount clientAccount;

    /**
     * Create an entity for this test.
     */
    public static Customer createEntity(EntityManager em, ClientAccount clientAccount) {
        Customer customer = new Customer()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .phone(DEFAULT_PHONE)
            .taxId(DEFAULT_TAX_ID)
            .enabled(DEFAULT_ENABLED)
            .createdByClientAccount(clientAccount);
        return customer;
    }

    /**
     * Create an updated entity for this test.
     */
    public static Customer createUpdatedEntity(EntityManager em, ClientAccount clientAccount) {
        Customer customer = new Customer()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phone(UPDATED_PHONE)
            .taxId(UPDATED_TAX_ID)
            .enabled(UPDATED_ENABLED)
            .createdByClientAccount(clientAccount);
        return customer;
    }

    @BeforeEach
    public void initTest() {
        // Create a client account for testing
        clientAccount = new ClientAccount();
        clientAccount.setCompanyName("Test Company");
        clientAccount.setPhone("1234567890");
        clientAccount.setEmail("test@company.com");
        clientAccount.setStatus(AccountStatus.ENABLED);
        clientAccountRepository.saveAndFlush(clientAccount);

        customer = createEntity(em, clientAccount);
    }

    @AfterEach
    void cleanup() {
        TestSecurityContextHelper.clearSecurityContext();
    }

    @Test
    @Transactional
    void createCustomer() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        int databaseSizeBeforeCreate = customerRepository.findAll().size();

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);
        customerDTO.setCreatedByClientAccountId(clientAccount.getId());

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerDTO)))
            .andExpect(status().isCreated());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeCreate + 1);
        Customer testCustomer = customerList.get(customerList.size() - 1);
        assertThat(testCustomer.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testCustomer.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCustomer.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCustomer.getTaxId()).isEqualTo(DEFAULT_TAX_ID);
        assertThat(testCustomer.getEnabled()).isEqualTo(DEFAULT_ENABLED);
        assertThat(testCustomer.getCreatedByClientAccount().getId()).isEqualTo(clientAccount.getId());
    }

    @Test
    @Transactional
    void createCustomerWithExistingId() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Create the Customer with an existing ID
        customer.setId(1L);
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        int databaseSizeBeforeCreate = customerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void createCustomerWithDuplicatePhone() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Create customer with phone number
        customerRepository.saveAndFlush(customer);

        // Try to create another customer with same phone number
        Customer duplicateCustomer = createEntity(em, clientAccount);
        duplicateCustomer.setPhone(DEFAULT_PHONE); // Same phone
        CustomerDTO customerDTO = customerMapper.toDto(duplicateCustomer);
        customerDTO.setCreatedByClientAccountId(clientAccount.getId());

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("phoneexists"));
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        int databaseSizeBeforeTest = customerRepository.findAll().size();
        // set the field null
        customer.setFirstName(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerDTO)))
            .andExpect(status().isBadRequest());

        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        int databaseSizeBeforeTest = customerRepository.findAll().size();
        // set the field null
        customer.setLastName(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerDTO)))
            .andExpect(status().isBadRequest());

        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        int databaseSizeBeforeTest = customerRepository.findAll().size();
        // set the field null
        customer.setPhone(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerDTO)))
            .andExpect(status().isBadRequest());

        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCustomers() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].taxId").value(hasItem(DEFAULT_TAX_ID)))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED)));
    }

    @Test
    @Transactional
    void getCustomer() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get the customer
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL_ID, customer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customer.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.taxId").value(DEFAULT_TAX_ID))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED));
    }

    @Test
    @Transactional
    void getCustomersByIdFiltering() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        Long id = customer.getId();

        defaultCustomerShouldBeFound("id.equals=" + id);
        defaultCustomerShouldNotBeFound("id.notEquals=" + id);

        defaultCustomerShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCustomerShouldNotBeFound("id.greaterThan=" + id);

        defaultCustomerShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCustomerShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCustomersByFirstNameIsEqualToSomething() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where firstName equals to DEFAULT_FIRST_NAME
        defaultCustomerShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the customerList where firstName equals to UPDATED_FIRST_NAME
        defaultCustomerShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneIsEqualToSomething() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where phone equals to DEFAULT_PHONE
        defaultCustomerShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the customerList where phone equals to UPDATED_PHONE
        defaultCustomerShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void updateCustomer() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        int databaseSizeBeforeUpdate = customerRepository.findAll().size();

        // Update the customer
        Customer updatedCustomer = customerRepository.findById(customer.getId()).get();
        // Disconnect from session so that the updates on updatedCustomer are not directly saved in db
        em.detach(updatedCustomer);
        updatedCustomer
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phone(UPDATED_PHONE)
            .taxId(UPDATED_TAX_ID)
            .enabled(UPDATED_ENABLED);
        CustomerDTO customerDTO = customerMapper.toDto(updatedCustomer);

        restCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(customerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
        Customer testCustomer = customerList.get(customerList.size() - 1);
        assertThat(testCustomer.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCustomer.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCustomer.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testCustomer.getTaxId()).isEqualTo(UPDATED_TAX_ID);
        assertThat(testCustomer.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    @Transactional
    void putNonExistingCustomer() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        int databaseSizeBeforeUpdate = customerRepository.findAll().size();
        customer.setId(count.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(customerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCustomer() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        int databaseSizeBeforeDelete = customerRepository.findAll().size();

        // Delete the customer (soft delete)
        restCustomerMockMvc
            .perform(delete(ENTITY_API_URL_ID, customer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains same number of items (soft delete)
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeDelete);

        // Validate the customer is disabled
        Customer deletedCustomer = customerRepository.findById(customer.getId()).get();
        assertThat(deletedCustomer.getEnabled()).isFalse();
    }

    @Test
    @Transactional
    void reactivateCustomer() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database with disabled customer
        customer.setEnabled(false);
        customerRepository.saveAndFlush(customer);

        // Reactivate the customer
        restCustomerMockMvc
            .perform(post(ENTITY_API_URL_ID + "/reactivate", customer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // Validate the customer is enabled
        Customer reactivatedCustomer = customerRepository.findById(customer.getId()).get();
        assertThat(reactivatedCustomer.getEnabled()).isTrue();
    }

    @Test
    @Transactional
    void searchCustomers() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Search customers
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "/search?q=" + DEFAULT_FIRST_NAME))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)));
    }

    @Test
    @Transactional
    void getCustomerStatistics() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get statistics
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.totalCustomers").value(greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.enabledCustomers").value(greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.managedCustomers").value(greaterThanOrEqualTo(0)));
    }

    @Test
    @Transactional
    void checkPhoneExistance() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Check existing phone (should return true)
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "/check-phone?phone=" + DEFAULT_PHONE + "&clientAccountId=" + clientAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        // Check new phone (should return true)
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "/check-phone?phone=9999999999&clientAccountId=" + clientAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    @Transactional
    void createMarketplaceAccount() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Initialize the database
        customerRepository.saveAndFlush(customer);

        CreateAccountRequestDTO request = new CreateAccountRequestDTO();
        request.setEmail(DEFAULT_EMAIL);
        request.setLangKey("en");

        // Create marketplace account
        restCustomerMockMvc
            .perform(
                post(ENTITY_API_URL_ID + "/create-account", customer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk());

        // Validate user was created and linked
        Customer updatedCustomer = customerRepository.findById(customer.getId()).get();
        assertThat(updatedCustomer.getUser()).isNotNull();
        assertThat(updatedCustomer.getUser().getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCustomerShouldBeFound(String filter) throws Exception {
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "?" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId().intValue())));
    }

    /**
     * Executes the search, and checks that the default entity is NOT returned.
     */
    private void defaultCustomerShouldNotBeFound(String filter) throws Exception {
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "?" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }
}
