package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.ProductFamilyAsserts.*;
import static com.adeem.stockflow.security.TestSecurityContextHelper.setSecurityContextWithClientAccountId;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.InventoryStatus;
import com.adeem.stockflow.domain.enumeration.ProductCategory;
import com.adeem.stockflow.repository.*;
import com.adeem.stockflow.security.TestSecurityContextHelper;
import com.adeem.stockflow.security.WithMockClientAccount;
import com.adeem.stockflow.service.dto.ProductFamilyDTO;
import com.adeem.stockflow.service.mapper.ProductFamilyMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the enhanced {@link ProductFamilyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class ProductFamilyResourceIT {

    private static final String DEFAULT_NAME = "Electronics";
    private static final String UPDATED_NAME = "Updated Electronics";
    private static final String DUPLICATE_NAME = "Duplicate Family";

    private static final String ENTITY_API_URL = "/api/product-families";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_API_URL_STATISTICS = ENTITY_API_URL + "/statistics";
    private static final String ENTITY_API_URL_SEARCH = ENTITY_API_URL + "/search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductFamilyRepository productFamilyRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductFamilyMapper productFamilyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductFamilyMockMvc;

    private ProductFamily productFamily;
    private ClientAccount clientAccount;
    private ClientAccount otherClientAccount;

    private ProductFamily insertedProductFamily;

    @BeforeEach
    void initTest() {
        // Create client accounts
        clientAccount = ClientAccountResourceIT.createEntity();
        clientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        otherClientAccount = ClientAccountResourceIT.createEntity();
        otherClientAccount.setCompanyName("Other Company");
        otherClientAccount.setEmail("other@example.com");
        otherClientAccount = clientAccountRepository.saveAndFlush(otherClientAccount);

        // Create product family
        productFamily = createEntity();
        productFamily.setClientAccount(clientAccount);
    }

    @AfterEach
    void cleanup() {
        if (insertedProductFamily != null) {
            productFamilyRepository.delete(insertedProductFamily);
            insertedProductFamily = null;
        }
        TestSecurityContextHelper.clearSecurityContext();
    }

    public static ProductFamily createEntity() {
        return new ProductFamily().name(DEFAULT_NAME);
    }

    public static ProductFamily createUpdatedEntity() {
        return new ProductFamily().name(UPDATED_NAME);
    }

    @Test
    @Transactional
    void createProductFamily() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        long databaseSizeBeforeCreate = getRepositoryCount();

        ProductFamilyDTO productFamilyDTO = productFamilyMapper.toDto(productFamily);

        restProductFamilyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productFamilyDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.clientAccountId").value(clientAccount.getId()));

        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);

        List<ProductFamily> familyList = productFamilyRepository.findAll();
        ProductFamily testFamily = familyList.get(familyList.size() - 1);
        assertThat(testFamily.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFamily.getClientAccount().getId()).isEqualTo(clientAccount.getId());

        insertedProductFamily = testFamily;
    }

    @Test
    @Transactional
    void createProductFamilyWithDuplicateName() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create first family
        productFamily.setName(DUPLICATE_NAME);
        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        // Try to create another family with same name for same client
        ProductFamily duplicateFamily = new ProductFamily();
        duplicateFamily.setName(DUPLICATE_NAME);
        duplicateFamily.setClientAccount(clientAccount);
        ProductFamilyDTO duplicateFamilyDTO = productFamilyMapper.toDto(duplicateFamily);

        ResultActions resultActions = restProductFamilyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(duplicateFamilyDTO)))
            .andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.message").value("E012"));
    }

    @Test
    @Transactional
    void createProductFamilyWithSameNameDifferentClient() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create family for first client
        productFamily.setName(DUPLICATE_NAME);
        ProductFamily firstFamily = productFamilyRepository.saveAndFlush(productFamily);

        // Create family with same name for different client (should succeed)
        setSecurityContextWithClientAccountId(otherClientAccount.getId());

        ProductFamily secondFamily = new ProductFamily();
        secondFamily.setName(DUPLICATE_NAME);
        secondFamily.setClientAccount(otherClientAccount);
        ProductFamilyDTO secondFamilyDTO = productFamilyMapper.toDto(secondFamily);

        restProductFamilyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(secondFamilyDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(DUPLICATE_NAME))
            .andExpect(jsonPath("$.clientAccountId").value(otherClientAccount.getId()));

        // Cleanup
        productFamilyRepository.delete(firstFamily);
        insertedProductFamily = productFamilyRepository
            .findAll()
            .stream()
            .filter(f -> f.getClientAccount().getId().equals(otherClientAccount.getId()))
            .findFirst()
            .orElse(null);
    }

    @Test
    @Transactional
    void getAllProductFamilies() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create families for current client
        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        ProductFamily anotherFamily = new ProductFamily();
        anotherFamily.setName("Another Family");
        anotherFamily.setClientAccount(clientAccount);
        ProductFamily secondFamily = productFamilyRepository.saveAndFlush(anotherFamily);

        // Create family for different client (should not appear)
        ProductFamily otherClientFamily = new ProductFamily();
        otherClientFamily.setName("Other Client Family");
        otherClientFamily.setClientAccount(otherClientAccount);
        productFamilyRepository.saveAndFlush(otherClientFamily);

        // Get families - should only return current client's families
        restProductFamilyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].name").value(hasItem("Another Family")))
            .andExpect(jsonPath("$.[*].clientAccountId").value(hasItem(clientAccount.getId().intValue())));

        // Cleanup
        productFamilyRepository.delete(secondFamily);
        productFamilyRepository.delete(otherClientFamily);
    }

    @Test
    @Transactional
    void getProductFamily() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        restProductFamilyMockMvc
            .perform(get(ENTITY_API_URL_ID, productFamily.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productFamily.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.clientAccountId").value(clientAccount.getId()));
    }

    @Test
    @Transactional
    void getProductFamilyFromDifferentClient() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create family for different client
        ProductFamily otherClientFamily = new ProductFamily();
        otherClientFamily.setName("Other Client Family");
        otherClientFamily.setClientAccount(otherClientAccount);
        ProductFamily savedFamily = productFamilyRepository.saveAndFlush(otherClientFamily);

        // Try to access it - should return 404
        restProductFamilyMockMvc.perform(get(ENTITY_API_URL_ID, savedFamily.getId())).andExpect(status().isNotFound());

        // Cleanup
        productFamilyRepository.delete(savedFamily);
    }

    @Test
    @Transactional
    void updateProductFamily() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        ProductFamily updatedProductFamily = productFamilyRepository.findById(productFamily.getId()).orElseThrow();
        em.detach(updatedProductFamily);
        updatedProductFamily.name(UPDATED_NAME);
        ProductFamilyDTO productFamilyDTO = productFamilyMapper.toDto(updatedProductFamily);

        restProductFamilyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productFamilyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productFamilyDTO))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(UPDATED_NAME))
            .andExpect(jsonPath("$.clientAccountId").value(clientAccount.getId()));

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductFamilyToMatchAllProperties(updatedProductFamily);
    }

    @Test
    @Transactional
    void deleteProductFamilyWithProducts() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        // Create a product in this family
        Product product = ProductResourceIT.createEntity();
        product.setClientAccount(clientAccount);
        product.setProductFamily(insertedProductFamily);
        Product savedProduct = productRepository.saveAndFlush(product);

        // Try to delete family with products - should fail
        restProductFamilyMockMvc
            .perform(delete(ENTITY_API_URL_ID, productFamily.getId()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("E013"));

        // Cleanup
        productRepository.delete(savedProduct);
    }

    @Test
    @Transactional
    void deleteEmptyProductFamily() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);
        Long familyId = insertedProductFamily.getId();

        long databaseSizeBeforeDelete = getRepositoryCount();

        restProductFamilyMockMvc.perform(delete(ENTITY_API_URL_ID, familyId)).andExpect(status().isNoContent());

        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        insertedProductFamily = null; // Already deleted
    }

    @Test
    @Transactional
    void assignProductToFamily() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        // Create a product
        Product product = ProductResourceIT.createEntity();
        product.setClientAccount(clientAccount);
        Product savedProduct = productRepository.saveAndFlush(product);

        // Assign products to family
        ResultActions resultActions = restProductFamilyMockMvc.perform(
            post(ENTITY_API_URL_ID + "/assign", productFamily.getId()).param("productIds", savedProduct.getId().toString())
        );
        resultActions.andExpect(status().isOk()).andExpect(jsonPath("$.assignedCount").value(1));

        // Verify assignment
        Product updatedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getProductFamily().getId()).isEqualTo(productFamily.getId());

        // Cleanup
        productRepository.delete(updatedProduct);
    }

    @Test
    @Transactional
    void removeProductFromFamily() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        // Create a product assigned to family
        Product product = ProductResourceIT.createEntity();
        product.setClientAccount(clientAccount);
        product.setProductFamily(insertedProductFamily);
        Product savedProduct = productRepository.saveAndFlush(product);

        // Remove product from family
        restProductFamilyMockMvc
            .perform(delete(ENTITY_API_URL_ID + "/unassign", productFamily.getId()).param("productIds", savedProduct.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productFamily").doesNotExist());

        // Verify removal
        Product updatedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getProductFamily()).isNull();

        // Cleanup
        productRepository.delete(updatedProduct);
    }

    @Test
    @Transactional
    void getProductFamilyProducts() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        // Create products in family
        Product product1 = ProductResourceIT.createEntity();
        product1.setClientAccount(clientAccount);
        product1.setProductFamily(insertedProductFamily);
        product1.setCode("PROD1");
        Product savedProduct1 = productRepository.saveAndFlush(product1);

        Product product2 = ProductResourceIT.createEntity();
        product2.setClientAccount(clientAccount);
        product2.setProductFamily(insertedProductFamily);
        product2.setCode("PROD2");
        Product savedProduct2 = productRepository.saveAndFlush(product2);

        // Get family products
        restProductFamilyMockMvc
            .perform(get(ENTITY_API_URL_ID + "/products", productFamily.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[*].code").value(hasItem("PROD1")))
            .andExpect(jsonPath("$.[*].code").value(hasItem("PROD2")))
            .andExpect(jsonPath("$.[*].productFamily.id").value(hasItem(productFamily.getId().intValue())));

        // Cleanup
        productRepository.delete(savedProduct1);
        productRepository.delete(savedProduct2);
    }

    @Test
    @Transactional
    void getProductFamilyStatistics() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create families with products
        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        ProductFamily family2 = new ProductFamily();
        family2.setName("Family 2");
        family2.setClientAccount(clientAccount);
        ProductFamily savedFamily2 = productFamilyRepository.saveAndFlush(family2);

        // Create products with inventory
        Product product1 = createProductWithInventory("PROD1", insertedProductFamily, new BigDecimal("100"), new BigDecimal("10"));
        Product product2 = createProductWithInventory("PROD2", savedFamily2, new BigDecimal("200"), new BigDecimal("5"));

        // Get statistics
        restProductFamilyMockMvc
            .perform(get(ENTITY_API_URL_STATISTICS))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.totalFamilies").value(2))
            .andExpect(jsonPath("$.totalProducts").value(2))
            .andExpect(jsonPath("$.familiesWithProducts").value(2))
            .andExpect(jsonPath("$.emptyFamilies").value(0));

        // Cleanup
        productRepository.delete(product1);
        productRepository.delete(product2);
        productFamilyRepository.delete(savedFamily2);
    }

    @Test
    @Transactional
    void searchProductFamilies() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create families
        insertedProductFamily = productFamilyRepository.saveAndFlush(productFamily);

        ProductFamily electronicsFamily = new ProductFamily();
        electronicsFamily.setName("Mobile Electronics");
        electronicsFamily.setClientAccount(clientAccount);
        ProductFamily savedElectronicsFamily = productFamilyRepository.saveAndFlush(electronicsFamily);

        ProductFamily furnitureFamily = new ProductFamily();
        furnitureFamily.setName("Office Furniture");
        furnitureFamily.setClientAccount(clientAccount);
        ProductFamily savedFurnitureFamily = productFamilyRepository.saveAndFlush(furnitureFamily);

        // Search for 'electronics'
        restProductFamilyMockMvc
            .perform(get(ENTITY_API_URL_SEARCH + "?q=electronics"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].name").value(hasItem("Mobile Electronics")));

        // Search for 'furniture'
        restProductFamilyMockMvc
            .perform(get(ENTITY_API_URL_SEARCH + "?q=furniture"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].name").value("Office Furniture"));

        // Cleanup
        productFamilyRepository.delete(savedElectronicsFamily);
        productFamilyRepository.delete(savedFurnitureFamily);
    }

    private Product createProductWithInventory(String code, ProductFamily family, BigDecimal price, BigDecimal quantity) {
        Product product = ProductResourceIT.createEntity();
        product.setCode(code);
        product.setSellingPrice(price);
        product.setMinimumStockLevel(new BigDecimal("5"));
        product.setClientAccount(clientAccount);
        product.setProductFamily(family);
        Product savedProduct = productRepository.saveAndFlush(product);

        Inventory inventory = new Inventory();
        inventory.setQuantity(quantity);
        inventory.setAvailableQuantity(quantity);
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventory.setProduct(savedProduct);
        inventory.setClientAccount(clientAccount);
        inventoryRepository.saveAndFlush(inventory);

        return savedProduct;
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
