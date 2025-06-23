package com.adeem.stockflow.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.*;
import com.adeem.stockflow.repository.*;
import com.adeem.stockflow.security.TestSecurityContextHelper;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import com.adeem.stockflow.service.mapper.SupplierMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SupplierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class SupplierResourceIT {

    private static final String DEFAULT_FIRST_NAME = "John";
    private static final String UPDATED_FIRST_NAME = "Jane";

    private static final String DEFAULT_LAST_NAME = "Doe";
    private static final String UPDATED_LAST_NAME = "Smith";

    private static final String DEFAULT_COMPANY_NAME = "ACME Corp";
    private static final String UPDATED_COMPANY_NAME = "ACME Industries";

    private static final String DEFAULT_PHONE = "+1234567890";
    private static final String UPDATED_PHONE = "+0987654321";

    private static final String DEFAULT_EMAIL = "john.doe@acme.com";
    private static final String UPDATED_EMAIL = "jane.smith@acme.com";

    private static final String DEFAULT_FAX = "+1234567891";
    private static final String UPDATED_FAX = "+0987654322";

    private static final String DEFAULT_TAX_ID = "TAX123456";
    private static final String UPDATED_TAX_ID = "TAX789012";

    private static final String DEFAULT_REGISTRATION_ARTICLE = "REG123";
    private static final String UPDATED_REGISTRATION_ARTICLE = "REG456";

    private static final String DEFAULT_STATISTICAL_ID = "STAT123";
    private static final String UPDATED_STATISTICAL_ID = "STAT456";

    private static final String DEFAULT_RC = "RC123456";
    private static final String UPDATED_RC = "RC789012";

    private static final String DEFAULT_NOTES = "Default notes";
    private static final String UPDATED_NOTES = "Updated notes";

    // Address constants
    private static final String DEFAULT_STREET_ADDRESS = "123 Main St";
    private static final String DEFAULT_CITY = "New York";
    private static final String DEFAULT_STATE = "NY";
    private static final String DEFAULT_POSTAL_CODE = "10001";
    private static final String DEFAULT_COUNTRY = "USA";

    private static final String ENTITY_API_URL = "/api/suppliers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSupplierMockMvc;

    private Supplier supplier;
    private ClientAccount clientAccount;
    private ClientAccount otherClientAccount;

    private Supplier insertedSupplier;

    @BeforeEach
    void initTest() {
        // Create client accounts
        clientAccount = ClientAccountResourceIT.createEntity();
        clientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        otherClientAccount = ClientAccountResourceIT.createEntity();
        otherClientAccount.setCompanyName("Other Company");
        otherClientAccount.setEmail("other@example.com");
        otherClientAccount = clientAccountRepository.saveAndFlush(otherClientAccount);

        // Create supplier
        supplier = createEntity();
        supplier.setClientAccount(clientAccount);
    }

    @AfterEach
    void cleanup() {
        if (insertedSupplier != null) {
            supplierRepository.delete(insertedSupplier);
            insertedSupplier = null;
        }
        TestSecurityContextHelper.clearSecurityContext();
    }

    public static Supplier createEntity() {
        return new Supplier()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .companyName(DEFAULT_COMPANY_NAME)
            .phone(DEFAULT_PHONE)
            .email(DEFAULT_EMAIL)
            .fax(DEFAULT_FAX)
            .taxId(DEFAULT_TAX_ID)
            .registrationArticle(DEFAULT_REGISTRATION_ARTICLE)
            .statisticalId(DEFAULT_STATISTICAL_ID)
            .rc(DEFAULT_RC)
            .notes(DEFAULT_NOTES)
            .active(true);
    }

    public static Supplier createUpdatedEntity() {
        return new Supplier()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .companyName(UPDATED_COMPANY_NAME)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .fax(UPDATED_FAX)
            .taxId(UPDATED_TAX_ID)
            .registrationArticle(UPDATED_REGISTRATION_ARTICLE)
            .statisticalId(UPDATED_STATISTICAL_ID)
            .rc(UPDATED_RC)
            .notes(UPDATED_NOTES)
            .active(true);
    }

    private AddressDTO createAddressDTO() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreetAddress(DEFAULT_STREET_ADDRESS);
        addressDTO.setCity(DEFAULT_CITY);
        addressDTO.setState(DEFAULT_STATE);
        addressDTO.setPostalCode(DEFAULT_POSTAL_CODE);
        addressDTO.setCountry(DEFAULT_COUNTRY);
        addressDTO.setAddressType(AddressType.BUSINESS);
        addressDTO.setIsDefault(true);
        return addressDTO;
    }

    @Test
    @Transactional
    void createSupplier() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        long databaseSizeBeforeCreate = getRepositoryCount();

        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);
        supplierDTO.setAddress(createAddressDTO());

        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.companyName").value(DEFAULT_COMPANY_NAME))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.clientAccountId").value(clientAccount.getId()))
            .andExpect(jsonPath("$.address.streetAddress").value(DEFAULT_STREET_ADDRESS))
            .andExpect(jsonPath("$.address.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.active").value(true));

        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);

        List<Supplier> supplierList = supplierRepository.findAll();
        Supplier testSupplier = supplierList.get(supplierList.size() - 1);
        assertThat(testSupplier.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testSupplier.getClientAccount().getId()).isEqualTo(clientAccount.getId());
        assertThat(testSupplier.getAddress()).isNotNull();
        assertThat(testSupplier.getAddress().getCity()).isEqualTo(DEFAULT_CITY);

        insertedSupplier = testSupplier;
    }

    @Test
    @Transactional
    void createSupplierWithoutAddress() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        long databaseSizeBeforeCreate = getRepositoryCount();

        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);
        // Don't set address

        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.address").doesNotExist());

        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);

        List<Supplier> supplierList = supplierRepository.findAll();
        insertedSupplier = supplierList.get(supplierList.size() - 1);
        assertThat(insertedSupplier.getAddress()).isNull();
    }

    @Test
    @Transactional
    void createSupplierWithExistingId() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        supplier.setId(1L);
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        long databaseSizeBeforeCreate = getRepositoryCount();

        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorKey").value(ErrorConstants.ID_EXISTS));

        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void createSupplierWithDuplicateEmail() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create first supplier
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Try to create another supplier with same email
        Supplier duplicateSupplier = createEntity();
        duplicateSupplier.setEmail(DEFAULT_EMAIL);
        duplicateSupplier.setPhone("1111111111"); // Different phone
        duplicateSupplier.setClientAccount(clientAccount);
        SupplierDTO duplicateSupplierDTO = supplierMapper.toDto(duplicateSupplier);

        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(duplicateSupplierDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorKey").value(ErrorConstants.EMAIL_ALREADY_EXISTS));
    }

    @Test
    @Transactional
    void createSupplierWithMissingRequiredFields() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // Test missing first name
        supplierDTO.setFirstName(null);
        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isBadRequest());

        // Test missing last name
        supplierDTO.setFirstName(DEFAULT_FIRST_NAME);
        supplierDTO.setLastName(null);
        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isBadRequest());

        // Test missing phone
        supplierDTO.setLastName(DEFAULT_LAST_NAME);
        supplierDTO.setPhone(null);
        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void getAllSuppliers() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create suppliers for current client
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        Supplier anotherSupplier = createEntity();
        anotherSupplier.setEmail("another@example.com");
        anotherSupplier.setPhone("9999999999");
        anotherSupplier.setClientAccount(clientAccount);
        Supplier secondSupplier = supplierRepository.saveAndFlush(anotherSupplier);

        // Create supplier for different client (should not appear)
        Supplier otherClientSupplier = createEntity();
        otherClientSupplier.setEmail("other@example.com");
        otherClientSupplier.setPhone("8888888888");
        otherClientSupplier.setClientAccount(otherClientAccount);
        supplierRepository.saveAndFlush(otherClientSupplier);

        // Get suppliers - should only return current client's active suppliers
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].clientAccountId").value(hasItem(clientAccount.getId().intValue())));

        // Cleanup
        supplierRepository.delete(secondSupplier);
        supplierRepository.delete(otherClientSupplier);
    }

    @Test
    @Transactional
    void getAllSuppliersIncludingInactive() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create active supplier
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Create inactive supplier
        Supplier inactiveSupplier = createEntity();
        inactiveSupplier.setEmail("inactive@example.com");
        inactiveSupplier.setPhone("7777777777");
        inactiveSupplier.setActive(false);
        inactiveSupplier.setClientAccount(clientAccount);
        Supplier savedInactiveSupplier = supplierRepository.saveAndFlush(inactiveSupplier);

        // Get all suppliers including inactive
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/all"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(true)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(false)));

        // Cleanup
        supplierRepository.delete(savedInactiveSupplier);
    }

    @Test
    @Transactional
    void getSupplier() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        restSupplierMockMvc
            .perform(get(ENTITY_API_URL_ID, supplier.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(supplier.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.clientAccountId").value(clientAccount.getId()));
    }

    @Test
    @Transactional
    void getSupplierFromDifferentClient() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create supplier for different client
        Supplier otherClientSupplier = createEntity();
        otherClientSupplier.setEmail("other@example.com");
        otherClientSupplier.setPhone("6666666666");
        otherClientSupplier.setClientAccount(otherClientAccount);
        Supplier savedSupplier = supplierRepository.saveAndFlush(otherClientSupplier);

        // Try to access it - should return 404
        restSupplierMockMvc.perform(get(ENTITY_API_URL_ID, savedSupplier.getId())).andExpect(status().isNotFound());

        // Cleanup
        supplierRepository.delete(savedSupplier);
    }

    @Test
    @Transactional
    void updateSupplier() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        Supplier updatedSupplier = supplierRepository.findById(supplier.getId()).orElseThrow();
        em.detach(updatedSupplier);
        updatedSupplier
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .companyName(UPDATED_COMPANY_NAME)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL);

        SupplierDTO supplierDTO = supplierMapper.toDto(updatedSupplier);

        restSupplierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplierDTO))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value(UPDATED_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(UPDATED_LAST_NAME))
            .andExpect(jsonPath("$.email").value(UPDATED_EMAIL));

        assertSameRepositoryCount(databaseSizeBeforeUpdate);

        Supplier testSupplier = supplierRepository.findById(supplier.getId()).orElseThrow();
        assertThat(testSupplier.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testSupplier.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void softDeleteSupplier() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedSupplier = supplierRepository.saveAndFlush(supplier);
        Long supplierId = insertedSupplier.getId();

        long databaseSizeBeforeDelete = getRepositoryCount();

        restSupplierMockMvc.perform(delete(ENTITY_API_URL_ID, supplierId)).andExpect(status().isNoContent());

        // Supplier should still exist but be inactive
        assertSameRepositoryCount(databaseSizeBeforeDelete);

        Supplier softDeletedSupplier = supplierRepository.findById(supplierId).orElseThrow();
        assertThat(softDeletedSupplier.getActive()).isFalse();
    }

    @Test
    @Transactional
    void reactivateSupplier() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create inactive supplier
        supplier.setActive(false);
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        restSupplierMockMvc.perform(post(ENTITY_API_URL_ID + "/reactivate", supplier.getId())).andExpect(status().isOk());

        // Verify supplier is now active
        Supplier reactivatedSupplier = supplierRepository.findById(supplier.getId()).orElseThrow();
        assertThat(reactivatedSupplier.getActive()).isTrue();
    }

    @Test
    @Transactional
    void searchSuppliers() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create suppliers with different data
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        Supplier supplierByName = createEntity();
        supplierByName.setFirstName("Alice");
        supplierByName.setLastName("Johnson");
        supplierByName.setEmail("alice@example.com");
        supplierByName.setPhone("5555555555");
        supplierByName.setClientAccount(clientAccount);
        Supplier savedSupplierByName = supplierRepository.saveAndFlush(supplierByName);

        Supplier supplierByCompany = createEntity();
        supplierByCompany.setCompanyName("TechCorp Solutions");
        supplierByCompany.setEmail("tech@example.com");
        supplierByCompany.setPhone("4444444444");
        supplierByCompany.setClientAccount(clientAccount);
        Supplier savedSupplierByCompany = supplierRepository.saveAndFlush(supplierByCompany);

        // Search by first name
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/search?q=Alice"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].firstName").value("Alice"));

        // Search by company name
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/search?q=TechCorp"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].companyName").value("TechCorp Solutions"));

        // Search by email
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/search?q=acme.com"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].email").value(DEFAULT_EMAIL));

        // Cleanup
        supplierRepository.delete(savedSupplierByName);
        supplierRepository.delete(savedSupplierByCompany);
    }

    @Test
    @Transactional
    void getSupplierStatistics() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create suppliers for statistics
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        Supplier anotherSupplier = createEntity();
        anotherSupplier.setEmail("another@example.com");
        anotherSupplier.setPhone("3333333333");
        anotherSupplier.setClientAccount(clientAccount);
        Supplier secondSupplier = supplierRepository.saveAndFlush(anotherSupplier);

        // Create inactive supplier
        Supplier inactiveSupplier = createEntity();
        inactiveSupplier.setEmail("inactive@example.com");
        inactiveSupplier.setPhone("2222222222");
        inactiveSupplier.setActive(false);
        inactiveSupplier.setClientAccount(clientAccount);
        Supplier thirdSupplier = supplierRepository.saveAndFlush(inactiveSupplier);

        // Get statistics
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.totalSuppliers").value(3))
            .andExpect(jsonPath("$.activeSuppliers").value(2))
            .andExpect(jsonPath("$.inactiveSuppliers").value(1));

        // Cleanup
        supplierRepository.delete(secondSupplier);
        supplierRepository.delete(thirdSupplier);
    }

    @Test
    @Transactional
    void checkEmailExists() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Check existing email
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/check-email?email=" + DEFAULT_EMAIL))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        // Check non-existing email
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/check-email?email=nonexistent@example.com"))
            .andExpect(status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    @Transactional
    void checkPhoneExists() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Check existing phone
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/check-phone?phone=" + DEFAULT_PHONE))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        // Check non-existing phone
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/check-phone?phone=1111111111"))
            .andExpect(status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    @Transactional
    void checkTaxIdExists() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Check existing tax ID
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/check-tax-id?taxId=" + DEFAULT_TAX_ID))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        // Check non-existing tax ID
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/check-tax-id?taxId=NONEXISTENT"))
            .andExpect(status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    @Transactional
    void countSuppliers() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get the count
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/count"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").value(1));
    }

    @Test
    @Transactional
    void testSupplierCriteriaFiltering() throws Exception {
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create suppliers with different attributes
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        Supplier supplier2 = createEntity();
        supplier2.setFirstName("Alice");
        supplier2.setCompanyName("TechCorp");
        supplier2.setEmail("alice@techcorp.com");
        supplier2.setPhone("1111111111");
        supplier2.setClientAccount(clientAccount);
        Supplier savedSupplier2 = supplierRepository.saveAndFlush(supplier2);

        // Test filtering by first name
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "?firstName.contains=Alice"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].firstName").value("Alice"));

        // Test filtering by company name
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "?companyName.contains=ACME"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].companyName").value(DEFAULT_COMPANY_NAME));

        // Cleanup
        supplierRepository.delete(savedSupplier2);
    }

    protected long getRepositoryCount() {
        return supplierRepository.count();
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
}
