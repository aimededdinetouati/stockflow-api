package com.adeem.stockflow.web.rest;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.enumeration.AccountStatus;
import com.adeem.stockflow.domain.enumeration.InventoryStatus;
import com.adeem.stockflow.domain.enumeration.ProductCategory;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.repository.InventoryRepository;
import com.adeem.stockflow.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link PublicProductResource}.
 */
@IntegrationTest
@AutoConfigureMockMvc
class PublicProductResourceIT {

    private static final String DEFAULT_NAME = "Test Product";
    private static final String DEFAULT_DESCRIPTION = "Test product description";
    private static final String DEFAULT_CODE = "TEST001";
    private static final BigDecimal DEFAULT_SELLING_PRICE = new BigDecimal("100.00");
    private static final ProductCategory DEFAULT_CATEGORY = ProductCategory.ELECTRONICS;
    private static final Boolean DEFAULT_IS_VISIBLE_TO_CUSTOMERS = true;
    private static final Boolean DEFAULT_FEATURED_IN_MARKETPLACE = false;

    private static final String ENTITY_API_URL = "/api/public/products";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private MockMvc restPublicProductMockMvc;

    private Product product;
    private ClientAccount clientAccount;
    private Inventory inventory;

    /**
     * Create test entities for this test.
     */
    public static Product createEntity() {
        return new Product()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .code(DEFAULT_CODE)
            .sellingPrice(DEFAULT_SELLING_PRICE)
            .category(DEFAULT_CATEGORY)
            .applyTva(false)
            .isVisibleToCustomers(DEFAULT_IS_VISIBLE_TO_CUSTOMERS);
    }

    public static ClientAccount createClientAccountEntity() {
        return new ClientAccount().companyName("Test Company").phone("0676841436").email("test@company.com").status(AccountStatus.ENABLED);
    }

    public static Inventory createInventoryEntity() {
        return new Inventory().availableQuantity(new BigDecimal("50"));
    }

    @BeforeEach
    void initTest() {
        clientAccount = createClientAccountEntity();
        product = createEntity();
        inventory = createInventoryEntity();
    }

    @Test
    @Transactional
    void getAllMarketplaceProducts() throws Exception {
        // Initialize the database with a client account
        clientAccountRepository.saveAndFlush(clientAccount);
        product.setClientAccount(clientAccount);

        // Save product
        productRepository.saveAndFlush(product);

        // Add inventory
        inventory.setProduct(product);
        inventory.setQuantity(BigDecimal.TEN);
        inventory.setAvailableQuantity(BigDecimal.TEN);
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventoryRepository.saveAndFlush(inventory);

        // Get all marketplace products
        ResultActions resultActions = restPublicProductMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc")).andExpect(status().isOk());
        resultActions
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].sellingPrice").value(hasItem(DEFAULT_SELLING_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].isVisibleToCustomers").value(hasItem(DEFAULT_IS_VISIBLE_TO_CUSTOMERS)))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem("Test Company")));
    }

    @Test
    @Transactional
    void getMarketplaceProductDetail() throws Exception {
        // Initialize the database
        clientAccountRepository.saveAndFlush(clientAccount);
        product.setClientAccount(clientAccount);
        productRepository.saveAndFlush(product);

        inventory.setProduct(product);
        inventory.setQuantity(BigDecimal.TEN);
        inventory.setAvailableQuantity(BigDecimal.TEN);
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventoryRepository.saveAndFlush(inventory);

        var x = productRepository.getOne(product.getId());

        // Get the product detail
        restPublicProductMockMvc
            .perform(get(ENTITY_API_URL + "/{id}", product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.sellingPrice").value(DEFAULT_SELLING_PRICE.intValue()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.isAvailable").value(true))
            .andExpect(jsonPath("$.availableQuantity").value(10))
            .andExpect(jsonPath("$.company.companyName").value("Test Company"));
    }

    @Test
    @Transactional
    void searchMarketplaceProducts() throws Exception {
        // Initialize the database
        clientAccountRepository.saveAndFlush(clientAccount);
        product.setClientAccount(clientAccount);
        productRepository.saveAndFlush(product);

        // Search for products
        restPublicProductMockMvc
            .perform(get(ENTITY_API_URL + "/search?q=Test"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getProductCategories() throws Exception {
        // Initialize the database
        clientAccountRepository.saveAndFlush(clientAccount);
        product.setClientAccount(clientAccount);
        productRepository.saveAndFlush(product);

        // Get categories
        restPublicProductMockMvc
            .perform(get(ENTITY_API_URL + "/categories"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].category").exists())
            .andExpect(jsonPath("$.[*].productCount").exists());
    }

    @Test
    @Transactional
    void getProductsByCategory() throws Exception {
        // Initialize the database
        clientAccountRepository.saveAndFlush(clientAccount);
        product.setClientAccount(clientAccount);
        productRepository.saveAndFlush(product);

        inventory.setProduct(product);
        inventory.setQuantity(BigDecimal.TEN);
        inventory.setAvailableQuantity(BigDecimal.TEN);
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventoryRepository.saveAndFlush(inventory);
        // Get products by category
        ResultActions resultActions = restPublicProductMockMvc
            .perform(get(ENTITY_API_URL + "/categories/{category}", DEFAULT_CATEGORY))
            .andExpect(status().isOk());
        resultActions
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())));
    }

    @Test
    @Transactional
    void filterProductsByPriceRange() throws Exception {
        // Initialize the database
        clientAccountRepository.saveAndFlush(clientAccount);
        product.setClientAccount(clientAccount);
        productRepository.saveAndFlush(product);

        // Filter by price range
        restPublicProductMockMvc
            .perform(get(ENTITY_API_URL + "?minPrice=50&maxPrice=150"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].sellingPrice").value(hasItem(DEFAULT_SELLING_PRICE.doubleValue())));
    }

    @Test
    @Transactional
    void filterProductsByCompany() throws Exception {
        // Initialize the database
        clientAccountRepository.saveAndFlush(clientAccount);
        product.setClientAccount(clientAccount);
        productRepository.saveAndFlush(product);

        // Filter by company name
        restPublicProductMockMvc
            .perform(get(ENTITY_API_URL + "?companyName=Test"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem("Test Company")));
    }

    @Test
    @Transactional
    void getNonVisibleProductShouldReturn404() throws Exception {
        // Initialize the database
        clientAccountRepository.saveAndFlush(clientAccount);
        product.setClientAccount(clientAccount);
        product.setIsVisibleToCustomers(false); // Make product not visible
        productRepository.saveAndFlush(product);

        // Try to get non-visible product - should return 404
        restPublicProductMockMvc.perform(get(ENTITY_API_URL + "/{id}", product.getId())).andExpect(status().isNotFound());
    }
}
