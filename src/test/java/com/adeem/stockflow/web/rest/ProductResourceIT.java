package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.ProductAsserts.*;
import static com.adeem.stockflow.security.TestSecurityContextHelper.setSecurityContextWithClientAccountId;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameInstant;
import static com.adeem.stockflow.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.InventoryStatus;
import com.adeem.stockflow.domain.enumeration.ProductCategory;
import com.adeem.stockflow.domain.enumeration.TransactionType;
import com.adeem.stockflow.repository.*;
import com.adeem.stockflow.security.TestSecurityContextHelper;
import com.adeem.stockflow.security.WithMockClientAccount;
import com.adeem.stockflow.service.dto.InventoryDTO;
import com.adeem.stockflow.service.dto.ProductDTO;
import com.adeem.stockflow.service.dto.ProductWithInventoryDTO;
import com.adeem.stockflow.service.mapper.ProductMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class ProductResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_MANUFACTURER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_MANUFACTURER_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_UPC = "AAAAAAAAAA";
    private static final String UPDATED_UPC = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_SELLING_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_SELLING_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_COST_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_COST_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_PROFIT_MARGIN = new BigDecimal(1);
    private static final BigDecimal UPDATED_PROFIT_MARGIN = new BigDecimal(2);

    private static final BigDecimal DEFAULT_MINIMUM_STOCK_LEVEL = new BigDecimal(1);
    private static final BigDecimal UPDATED_MINIMUM_STOCK_LEVEL = new BigDecimal(2);

    private static final ProductCategory DEFAULT_CATEGORY = ProductCategory.ELECTRONICS;
    private static final ProductCategory UPDATED_CATEGORY = ProductCategory.COMPUTERS;

    private static final Boolean DEFAULT_APPLY_TVA = false;
    private static final Boolean UPDATED_APPLY_TVA = true;

    private static final Boolean DEFAULT_IS_VISIBLE_TO_CUSTOMERS = false;
    private static final Boolean UPDATED_IS_VISIBLE_TO_CUSTOMERS = true;

    private static final ZonedDateTime DEFAULT_EXPIRATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_EXPIRATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

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
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;
    private ClientAccount clientAccount;

    private Product insertedProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity() {
        return new Product()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .code(DEFAULT_CODE)
            .manufacturerCode(DEFAULT_MANUFACTURER_CODE)
            .upc(DEFAULT_UPC)
            .sellingPrice(DEFAULT_SELLING_PRICE)
            .costPrice(DEFAULT_COST_PRICE)
            .profitMargin(DEFAULT_PROFIT_MARGIN)
            .minimumStockLevel(DEFAULT_MINIMUM_STOCK_LEVEL)
            .category(DEFAULT_CATEGORY)
            .applyTva(DEFAULT_APPLY_TVA)
            .isVisibleToCustomers(DEFAULT_IS_VISIBLE_TO_CUSTOMERS)
            .expirationDate(DEFAULT_EXPIRATION_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity() {
        return new Product()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .code(UPDATED_CODE)
            .manufacturerCode(UPDATED_MANUFACTURER_CODE)
            .upc(UPDATED_UPC)
            .sellingPrice(UPDATED_SELLING_PRICE)
            .costPrice(UPDATED_COST_PRICE)
            .profitMargin(UPDATED_PROFIT_MARGIN)
            .minimumStockLevel(UPDATED_MINIMUM_STOCK_LEVEL)
            .category(UPDATED_CATEGORY)
            .applyTva(UPDATED_APPLY_TVA)
            .isVisibleToCustomers(UPDATED_IS_VISIBLE_TO_CUSTOMERS)
            .expirationDate(UPDATED_EXPIRATION_DATE);
    }

    /**
     * Create a ProductWithInventoryDTO for testing
     */
    private ProductWithInventoryDTO createProductWithInventoryDTO(ProductDTO productDTO, InventoryDTO inventoryDTO) {
        ProductWithInventoryDTO dto = new ProductWithInventoryDTO();
        dto.setProduct(productDTO);
        dto.setInventory(inventoryDTO);
        return dto;
    }

    /**
     * Create default inventory DTO for testing
     */
    private InventoryDTO createDefaultInventoryDTO() {
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setQuantity(new BigDecimal("10.0"));
        inventoryDTO.setAvailableQuantity(new BigDecimal("10.0"));
        inventoryDTO.setStatus(InventoryStatus.AVAILABLE);
        return inventoryDTO;
    }

    @BeforeEach
    void initTest() {
        // Create client account
        clientAccount = ClientAccountResourceIT.createEntity();
        clientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        product = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProduct != null) {
            productRepository.delete(insertedProduct);
            insertedProduct = null;
        }
        TestSecurityContextHelper.clearSecurityContext();
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();

        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Initialize the database
        product.setClientAccount(clientAccount);
        ProductDTO productDTO = productMapper.toDto(product);
        InventoryDTO inventoryDTO = createDefaultInventoryDTO();
        ProductWithInventoryDTO productWithInventoryDTO = createProductWithInventoryDTO(productDTO, inventoryDTO);

        // Create multipart files
        MockMultipartFile productWithInventoryPart = new MockMultipartFile(
            "productWithInventoryDTO",
            "",
            "application/json",
            om.writeValueAsBytes(productWithInventoryDTO)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
            "productImage",
            "test-image.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        // Perform the request
        MvcResult result = restProductMockMvc
            .perform(multipart(ENTITY_API_URL).file(productWithInventoryPart).file(imagePart))
            .andExpect(status().isCreated())
            .andReturn();

        // Extract the result
        var returnedProductDTO = om.readValue(result.getResponse().getContentAsString(), ProductDTO.class);

        // Validate the Product in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProduct = productMapper.toEntity(returnedProductDTO);
        assertProductUpdatableFieldsEquals(returnedProduct, getPersistedProduct(returnedProduct));

        // Verify that inventory was created
        var inventory = inventoryRepository.findByProductId(returnedProduct.getId());
        assertThat(inventory).isPresent();

        // Verify that image was attached
        List<Attachment> attachments = attachmentRepository.findByProductId(returnedProduct.getId());
        Assertions.assertFalse(attachments.isEmpty());

        insertedProduct = returnedProduct;
    }

    @Test
    @Transactional
    @WithMockClientAccount
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        product.setName(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);
        InventoryDTO inventoryDTO = createDefaultInventoryDTO();
        ProductWithInventoryDTO productWithInventoryDTO = createProductWithInventoryDTO(productDTO, inventoryDTO);

        MockMultipartFile productWithInventoryPart = new MockMultipartFile(
            "productWithInventoryDTO",
            "",
            "application/json",
            om.writeValueAsBytes(productWithInventoryDTO)
        );

        restProductMockMvc.perform(multipart(ENTITY_API_URL).file(productWithInventoryPart)).andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockClientAccount
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        product.setCode(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);
        InventoryDTO inventoryDTO = createDefaultInventoryDTO();
        ProductWithInventoryDTO productWithInventoryDTO = createProductWithInventoryDTO(productDTO, inventoryDTO);

        MockMultipartFile productWithInventoryPart = new MockMultipartFile(
            "productWithInventoryDTO",
            "",
            "application/json",
            om.writeValueAsBytes(productWithInventoryDTO)
        );

        restProductMockMvc.perform(multipart(ENTITY_API_URL).file(productWithInventoryPart)).andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockClientAccount
    void checkApplyTvaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        product.setApplyTva(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);
        InventoryDTO inventoryDTO = createDefaultInventoryDTO();
        ProductWithInventoryDTO productWithInventoryDTO = createProductWithInventoryDTO(productDTO, inventoryDTO);

        MockMultipartFile productWithInventoryPart = new MockMultipartFile(
            "productWithInventoryDTO",
            "",
            "application/json",
            om.writeValueAsBytes(productWithInventoryDTO)
        );

        restProductMockMvc.perform(multipart(ENTITY_API_URL).file(productWithInventoryPart)).andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].manufacturerCode").value(hasItem(DEFAULT_MANUFACTURER_CODE)))
            .andExpect(jsonPath("$.[*].upc").value(hasItem(DEFAULT_UPC)))
            .andExpect(jsonPath("$.[*].sellingPrice").value(hasItem(sameNumber(DEFAULT_SELLING_PRICE))))
            .andExpect(jsonPath("$.[*].costPrice").value(hasItem(sameNumber(DEFAULT_COST_PRICE))))
            .andExpect(jsonPath("$.[*].profitMargin").value(hasItem(sameNumber(DEFAULT_PROFIT_MARGIN))))
            .andExpect(jsonPath("$.[*].minimumStockLevel").value(hasItem(sameNumber(DEFAULT_MINIMUM_STOCK_LEVEL))))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].applyTva").value(hasItem(DEFAULT_APPLY_TVA)))
            .andExpect(jsonPath("$.[*].isVisibleToCustomers").value(hasItem(DEFAULT_IS_VISIBLE_TO_CUSTOMERS)))
            .andExpect(jsonPath("$.[*].expirationDate").value(hasItem(sameInstant(DEFAULT_EXPIRATION_DATE))));
    }

    @Test
    @Transactional
    void getAllProductsWithNameFilter() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Create second product with different name
        Product product2 = createEntity();
        product2.setName("Different Product Name");
        product2.setCode("DIFFERENT_CODE");
        product2.setClientAccount(clientAccount);

        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);
        Product insertedProduct2 = productRepository.saveAndFlush(product2);

        // Test filtering by name (contains)
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?name.contains=AAAAAAAAAA"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME));

        // Test filtering by name (equals)
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?name.equals=Different Product Name"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].name").value("Different Product Name"));

        // Cleanup
        productRepository.delete(insertedProduct2);
    }

    @Test
    @Transactional
    void getAllProductsWithCategoryFilter() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Create second product with different category
        Product product2 = createEntity();
        product2.setCategory(ProductCategory.COMPUTERS);
        product2.setCode("COMPUTER_CODE");
        product2.setClientAccount(clientAccount);

        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);
        Product insertedProduct2 = productRepository.saveAndFlush(product2);

        try {
            // Test filtering by category
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?category.equals=ELECTRONICS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].category").value("ELECTRONICS"));

            // Test filtering by different category
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?category.equals=COMPUTERS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].category").value("COMPUTERS"));
        } finally {
            // Cleanup
            productRepository.delete(insertedProduct2);
        }
    }

    @Test
    @Transactional
    void getAllProductsWithPriceRangeFilter() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Create products with different prices
        Product product2 = createEntity();
        product2.setSellingPrice(new BigDecimal("50"));
        product2.setCostPrice(new BigDecimal("30"));
        product2.setCode("EXPENSIVE_CODE");
        product2.setClientAccount(clientAccount);

        Product product3 = createEntity();
        product3.setSellingPrice(new BigDecimal("100"));
        product3.setCostPrice(new BigDecimal("80"));
        product3.setCode("VERY_EXPENSIVE_CODE");
        product3.setClientAccount(clientAccount);

        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);
        Product insertedProduct2 = productRepository.saveAndFlush(product2);
        Product insertedProduct3 = productRepository.saveAndFlush(product3);

        try {
            // Test filtering by selling price greater than
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?sellingPrice.greaterThan=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(2))); // product2 and product3

            // Test filtering by selling price less than
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?sellingPrice.lessThan=60"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(2))); // product1 and product2

            // Test filtering by cost price equals
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?costPrice.equals=30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].costPrice").value(30));
        } finally {
            // Cleanup
            productRepository.delete(insertedProduct2);
            productRepository.delete(insertedProduct3);
        }
    }

    @Test
    @Transactional
    void getAllProductsWithBooleanFilters() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Create products with different boolean values
        Product product2 = createEntity();
        product2.setApplyTva(true);
        product2.setIsVisibleToCustomers(true);
        product2.setCode("TVA_VISIBLE_CODE");
        product2.setClientAccount(clientAccount);

        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);
        Product insertedProduct2 = productRepository.saveAndFlush(product2);

        try {
            // Test filtering by applyTva = true
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?applyTva.equals=true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].applyTva").value(true));

            // Test filtering by applyTva = false
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?applyTva.equals=false"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].applyTva").value(false));

            // Test filtering by isVisibleToCustomers = true
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?isVisibleToCustomers.equals=true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].isVisibleToCustomers").value(true));
        } finally {
            // Cleanup
            productRepository.delete(insertedProduct2);
        }
    }

    @Test
    @Transactional
    void getAllProductsWithCodeFilter() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Create products with different codes
        Product product2 = createEntity();
        product2.setCode("SPECIAL_CODE_123");
        product2.setClientAccount(clientAccount);

        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);
        Product insertedProduct2 = productRepository.saveAndFlush(product2);

        try {
            // Test filtering by code (contains)
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?code.contains=SPECIAL"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].code").value("SPECIAL_CODE_123"));

            // Test filtering by code (equals)
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?code.equals=" + DEFAULT_CODE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].code").value(DEFAULT_CODE));
        } finally {
            // Cleanup
            productRepository.delete(insertedProduct2);
        }
    }

    @Test
    @Transactional
    void getAllProductsWithManufacturerCodeFilter() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Create products with different manufacturer codes
        Product product2 = createEntity();
        product2.setManufacturerCode("SONY_12345");
        product2.setCode("SONY_PRODUCT");
        product2.setClientAccount(clientAccount);

        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);
        Product insertedProduct2 = productRepository.saveAndFlush(product2);

        try {
            // Test filtering by manufacturer code (contains)
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?manufacturerCode.contains=SONY"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].manufacturerCode").value("SONY_12345"));

            // Test filtering by manufacturer code (equals)
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?manufacturerCode.equals=" + DEFAULT_MANUFACTURER_CODE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].manufacturerCode").value(DEFAULT_MANUFACTURER_CODE));
        } finally {
            // Cleanup
            productRepository.delete(insertedProduct2);
        }
    }

    @Test
    @Transactional
    void getAllProductsWithInventoryQuantityFilter() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Create products
        Product product2 = createEntity();
        product2.setCode("HIGH_STOCK_PRODUCT");
        product2.setClientAccount(clientAccount);

        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);
        Product insertedProduct2 = productRepository.saveAndFlush(product2);

        // Create inventories with different quantities
        Inventory inventory1 = new Inventory();
        inventory1.setQuantity(new BigDecimal("5"));
        inventory1.setAvailableQuantity(new BigDecimal("5"));
        inventory1.setStatus(InventoryStatus.AVAILABLE);
        inventory1.setProduct(insertedProduct);
        inventoryRepository.saveAndFlush(inventory1);

        Inventory inventory2 = new Inventory();
        inventory2.setQuantity(new BigDecimal("50"));
        inventory2.setAvailableQuantity(new BigDecimal("50"));
        inventory2.setStatus(InventoryStatus.AVAILABLE);
        inventory2.setProduct(insertedProduct2);
        inventoryRepository.saveAndFlush(inventory2);

        try {
            // Test filtering by inventory quantity greater than
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?inventoryQuantity.greaterThan=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].code").value("HIGH_STOCK_PRODUCT"));

            // Test filtering by inventory quantity less than
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?inventoryQuantity.lessThan=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].code").value(DEFAULT_CODE));

            // Test filtering by inventory quantity equals
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?inventoryQuantity.equals=50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].code").value("HIGH_STOCK_PRODUCT"));
        } finally {
            // Cleanup
            inventoryRepository.delete(inventory1);
            inventoryRepository.delete(inventory2);
            productRepository.delete(insertedProduct2);
        }
    }

    @Test
    @Transactional
    void getAllProductsWithMultipleCriteria() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Create products with different attributes
        Product product1 = createEntity();
        product1.setCode("ELECTRONICS_PRODUCT_1");
        product1.setCategory(ProductCategory.ELECTRONICS);
        product1.setApplyTva(true);
        product1.setSellingPrice(new BigDecimal("100"));
        product1.setClientAccount(clientAccount);

        Product product2 = createEntity();
        product2.setCode("COMPUTER_PRODUCT_1");
        product2.setCategory(ProductCategory.COMPUTERS);
        product2.setApplyTva(true);
        product2.setSellingPrice(new BigDecimal("150"));
        product2.setClientAccount(clientAccount);

        Product product3 = createEntity();
        product3.setCode("ELECTRONICS_PRODUCT_2");
        product3.setCategory(ProductCategory.ELECTRONICS);
        product3.setApplyTva(false);
        product3.setSellingPrice(new BigDecimal("80"));
        product3.setClientAccount(clientAccount);

        // Initialize the database
        Product insertedProduct1 = productRepository.saveAndFlush(product1);
        Product insertedProduct2 = productRepository.saveAndFlush(product2);
        Product insertedProduct3 = productRepository.saveAndFlush(product3);

        try {
            // Test multiple criteria: Electronics category AND applyTva = true
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?category.equals=ELECTRONICS&applyTva.equals=true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].code").value("ELECTRONICS_PRODUCT_1"));

            // Test multiple criteria: applyTva = true AND sellingPrice > 120
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?applyTva.equals=true&sellingPrice.greaterThan=120"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].code").value("COMPUTER_PRODUCT_1"));

            // Test multiple criteria: Electronics category AND sellingPrice < 90
            restProductMockMvc
                .perform(get(ENTITY_API_URL + "?category.equals=ELECTRONICS&sellingPrice.lessThan=90"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].code").value("ELECTRONICS_PRODUCT_2"));
        } finally {
            // Cleanup
            productRepository.delete(insertedProduct1);
            productRepository.delete(insertedProduct2);
            productRepository.delete(insertedProduct3);
        }
    }

    @Test
    @Transactional
    void getProduct() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get the product (now returns ProductWithInventoryDTO)
        restProductMockMvc
            .perform(get(ENTITY_API_URL_ID, product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.product.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.product.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.product.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.product.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.product.manufacturerCode").value(DEFAULT_MANUFACTURER_CODE))
            .andExpect(jsonPath("$.product.upc").value(DEFAULT_UPC))
            .andExpect(jsonPath("$.product.sellingPrice").value(sameNumber(DEFAULT_SELLING_PRICE)))
            .andExpect(jsonPath("$.product.costPrice").value(sameNumber(DEFAULT_COST_PRICE)))
            .andExpect(jsonPath("$.product.profitMargin").value(sameNumber(DEFAULT_PROFIT_MARGIN)))
            .andExpect(jsonPath("$.product.minimumStockLevel").value(sameNumber(DEFAULT_MINIMUM_STOCK_LEVEL)))
            .andExpect(jsonPath("$.product.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.product.applyTva").value(DEFAULT_APPLY_TVA))
            .andExpect(jsonPath("$.product.isVisibleToCustomers").value(DEFAULT_IS_VISIBLE_TO_CUSTOMERS))
            .andExpect(jsonPath("$.product.expirationDate").value(sameInstant(DEFAULT_EXPIRATION_DATE)));
    }

    @Test
    @Transactional
    @WithMockClientAccount
    void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void updateProductWithInventoryAndImages() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Initialize the database
        product.setClientAccount(clientAccount);
        insertedProduct = productRepository.saveAndFlush(product);

        Inventory initialInventory = new Inventory();
        initialInventory.setQuantity(new BigDecimal("10.0"));
        initialInventory.setAvailableQuantity(new BigDecimal("10.0"));
        initialInventory.setStatus(InventoryStatus.AVAILABLE);
        initialInventory.setProduct(insertedProduct);
        inventoryRepository.saveAndFlush(initialInventory);

        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setProduct(insertedProduct);
        inventoryTransaction.setQuantity(new BigDecimal("10.0"));
        inventoryTransaction.setTransactionDate(ZonedDateTime.now());
        inventoryTransaction.setTransactionType(TransactionType.INITIAL);
        inventoryTransaction.setReferenceNumber("0001");
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Find the inventory to update
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(insertedProduct.getId());
        assertThat(inventoryOpt).isPresent();
        Inventory inventoryToUpdate = inventoryOpt.get();

        // Count the initial images
        List<Attachment> initialAttachments = attachmentRepository.findByProductId(insertedProduct.getId());
        int initialImageCount = initialAttachments.size();

        // Update the product
        Product updatedProduct = createUpdatedEntity();
        updatedProduct.setId(insertedProduct.getId());
        updatedProduct.setClientAccount(clientAccount);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        // Update inventory
        InventoryDTO updatedInventory = new InventoryDTO();
        updatedInventory.setId(inventoryToUpdate.getId());
        updatedInventory.setQuantity(new BigDecimal("20.0"));
        updatedInventory.setAvailableQuantity(new BigDecimal("15.0"));
        updatedInventory.setStatus(InventoryStatus.AVAILABLE);
        updatedInventory.setProduct(productMapper.toDto(insertedProduct));

        ProductWithInventoryDTO productWithInventoryDTO = createProductWithInventoryDTO(productDTO, updatedInventory);

        // Create multipart request
        MockMultipartFile productWithInventoryPart = new MockMultipartFile(
            "productWithInventoryDTO",
            "",
            "application/json",
            om.writeValueAsBytes(productWithInventoryDTO)
        );

        MockMultipartFile imagePart = new MockMultipartFile("productImage", "new-image.jpg", "image/jpeg", "new test image".getBytes());

        // Perform the request
        restProductMockMvc
            .perform(multipart(ENTITY_API_URL_ID, insertedProduct.getId()).file(productWithInventoryPart).file(imagePart))
            .andExpect(status().isOk());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductToMatchAllProperties(updatedProduct);

        // Verify that inventory was updated
        Inventory updatedInventoryEntity = inventoryRepository.findById(inventoryToUpdate.getId()).orElseThrow();
        assertThat(updatedInventoryEntity.getQuantity()).isEqualByComparingTo(new BigDecimal("20.0"));
        assertThat(updatedInventoryEntity.getAvailableQuantity()).isEqualByComparingTo(new BigDecimal("15.0"));

        // Verify that image was added
        List<Attachment> attachments = attachmentRepository.findByProductId(insertedProduct.getId());
        assertThat(attachments).hasSize(initialImageCount + 1);
    }

    @Test
    @Transactional
    void updateProductWithInventory() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Initialize the database
        product.setClientAccount(clientAccount);
        insertedProduct = productRepository.saveAndFlush(product);

        // Create initial inventory
        Inventory initialInventory = new Inventory();
        initialInventory.setQuantity(new BigDecimal("10.0"));
        initialInventory.setAvailableQuantity(new BigDecimal("10.0"));
        initialInventory.setStatus(InventoryStatus.AVAILABLE);
        initialInventory.setProduct(insertedProduct);
        inventoryRepository.saveAndFlush(initialInventory);

        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setProduct(insertedProduct);
        inventoryTransaction.setQuantity(new BigDecimal("10.0"));
        inventoryTransaction.setTransactionDate(ZonedDateTime.now());
        inventoryTransaction.setTransactionType(TransactionType.INITIAL);
        inventoryTransaction.setReferenceNumber("0001");
        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        Product updatedProduct = productRepository.findById(insertedProduct.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .code(UPDATED_CODE)
            .manufacturerCode(UPDATED_MANUFACTURER_CODE)
            .upc(UPDATED_UPC)
            .sellingPrice(UPDATED_SELLING_PRICE)
            .costPrice(UPDATED_COST_PRICE)
            .profitMargin(UPDATED_PROFIT_MARGIN)
            .minimumStockLevel(UPDATED_MINIMUM_STOCK_LEVEL)
            .category(UPDATED_CATEGORY)
            .applyTva(UPDATED_APPLY_TVA)
            .isVisibleToCustomers(UPDATED_IS_VISIBLE_TO_CUSTOMERS)
            .expirationDate(UPDATED_EXPIRATION_DATE);

        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        // Find the inventory to update
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(insertedProduct.getId());
        assertThat(inventoryOpt).isPresent();
        Inventory inventoryToUpdate = inventoryOpt.get();

        // Update inventory
        InventoryDTO updatedInventory = new InventoryDTO();
        updatedInventory.setId(inventoryToUpdate.getId());
        updatedInventory.setQuantity(new BigDecimal("20.0"));
        updatedInventory.setAvailableQuantity(new BigDecimal("15.0"));
        updatedInventory.setStatus(InventoryStatus.AVAILABLE);
        updatedInventory.setProduct(productMapper.toDto(insertedProduct));

        ProductWithInventoryDTO productWithInventoryDTO = createProductWithInventoryDTO(productDTO, updatedInventory);

        // Create multipart request
        MockMultipartFile productWithInventoryPart = new MockMultipartFile(
            "productWithInventoryDTO",
            "",
            "application/json",
            om.writeValueAsBytes(productWithInventoryDTO)
        );

        // Perform the request
        restProductMockMvc
            .perform(multipart(ENTITY_API_URL_ID, insertedProduct.getId()).file(productWithInventoryPart))
            .andExpect(status().isOk());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductToMatchAllProperties(updatedProduct);

        // Verify that inventory was updated
        Inventory updatedInventoryEntity = inventoryRepository.findById(inventoryToUpdate.getId()).orElseThrow();
        assertThat(updatedInventoryEntity.getQuantity()).isEqualByComparingTo(new BigDecimal("20.0"));
        assertThat(updatedInventoryEntity.getAvailableQuantity()).isEqualByComparingTo(new BigDecimal("15.0"));
    }

    @Test
    @Transactional
    void updateProductWithImages() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Initialize the database
        product.setClientAccount(clientAccount);
        insertedProduct = productRepository.saveAndFlush(product);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Count the initial images
        List<Attachment> initialAttachments = attachmentRepository.findByProductId(insertedProduct.getId());
        int initialImageCount = initialAttachments.size();

        // Update the product
        Product updatedProduct = createUpdatedEntity();
        updatedProduct.setClientAccount(clientAccount);
        updatedProduct.setId(insertedProduct.getId());
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        ProductWithInventoryDTO productWithInventoryDTO = createProductWithInventoryDTO(productDTO, null);

        // Create multipart request
        MockMultipartFile productWithInventoryPart = new MockMultipartFile(
            "productWithInventoryDTO",
            "",
            "application/json",
            om.writeValueAsBytes(productWithInventoryDTO)
        );

        MockMultipartFile imagePart1 = new MockMultipartFile(
            "productImage",
            "new-image-1.jpg",
            "image/jpeg",
            "new test image 1".getBytes()
        );

        MockMultipartFile imagePart2 = new MockMultipartFile(
            "productImage",
            "new-image-2.jpg",
            "image/jpeg",
            "new test image 2".getBytes()
        );

        // Perform the request
        restProductMockMvc
            .perform(multipart(ENTITY_API_URL_ID, insertedProduct.getId()).file(productWithInventoryPart).file(imagePart1).file(imagePart2))
            .andExpect(status().isOk());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductToMatchAllProperties(updatedProduct);

        // Verify that images were added
        List<Attachment> attachments = attachmentRepository.findByProductId(insertedProduct.getId());
        assertThat(attachments).hasSize(initialImageCount + 2);
    }

    @Test
    @Transactional
    void updateWithIdMismatchProduct() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        product.setClientAccount(clientAccount);
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        ProductWithInventoryDTO productWithInventoryDTO = createProductWithInventoryDTO(productDTO, null);

        // Create multipart request
        MockMultipartFile productWithInventoryPart = new MockMultipartFile(
            "productWithInventoryDTO",
            "",
            "application/json",
            om.writeValueAsBytes(productWithInventoryDTO)
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(multipart(ENTITY_API_URL_ID, longCount.incrementAndGet()).file(productWithInventoryPart))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockClientAccount
    void updateWithMissingIdProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Create the Product without ID
        ProductDTO productDTO = productMapper.toDto(product);
        productDTO.setId(null);
        ProductWithInventoryDTO productWithInventoryDTO = createProductWithInventoryDTO(productDTO, null);

        // Create multipart request
        MockMultipartFile productWithInventoryPart = new MockMultipartFile(
            "productWithInventoryDTO",
            "",
            "application/json",
            om.writeValueAsBytes(productWithInventoryDTO)
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(multipart(ENTITY_API_URL_ID, longCount.incrementAndGet()).file(productWithInventoryPart))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the product
        restProductMockMvc.perform(delete(ENTITY_API_URL_ID, product.getId())).andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void getLowStockProducts() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Initialize the database with a product that has low stock
        insertedProduct = productRepository.saveAndFlush(product);

        // Create inventory with low stock (below minimum stock level)
        Inventory lowStockInventory = new Inventory();
        lowStockInventory.setQuantity(new BigDecimal("0.5")); // Below minimum stock level of 1
        lowStockInventory.setAvailableQuantity(new BigDecimal("0.5"));
        lowStockInventory.setStatus(InventoryStatus.AVAILABLE);
        lowStockInventory.setProduct(insertedProduct);
        inventoryRepository.saveAndFlush(lowStockInventory);

        // Get low stock products
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/low-stock"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].product.id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].product.name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].inventory.quantity").value(hasItem(0.5)));
    }

    @Test
    @Transactional
    void countProducts() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        product.setClientAccount(clientAccount);
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get the count
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").value(1));
    }

    // Example test methods for ProductResourceIT.java

    @Test
    @Transactional
    void testBulkDeleteProducts() throws Exception {
        // Setup - create test products
        setSecurityContextWithClientAccountId(clientAccount.getId());

        Product product1 = createEntity();
        product1.setCode("BULK_TEST_1");
        product1.setClientAccount(clientAccount);

        Product product2 = createEntity();
        product2.setCode("BULK_TEST_2");
        product2.setClientAccount(clientAccount);

        Product savedProduct1 = productRepository.saveAndFlush(product1);
        Product savedProduct2 = productRepository.saveAndFlush(product2);

        List<Long> productIds = List.of(savedProduct1.getId(), savedProduct2.getId());

        try {
            // Test bulk deletion
            restProductMockMvc
                .perform(delete(ENTITY_API_URL + "/bulk").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productIds)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.deletedCount").value(2))
                .andExpect(jsonPath("$.failedCount").value(0))
                .andExpect(jsonPath("$.totalRequested").value(2));

            // Verify products are actually deleted
            em.clear();
            em.flush();
            assertThat(productRepository.findById(savedProduct1.getId())).isEmpty();
            assertThat(productRepository.findById(savedProduct2.getId())).isEmpty();
        } finally {
            // Cleanup is not needed as products are deleted
        }
    }

    @Test
    @Transactional
    void testBulkToggleVisibility() throws Exception {
        // Setup - create test products
        setSecurityContextWithClientAccountId(clientAccount.getId());

        Product product1 = createEntity();
        product1.setCode("VISIBILITY_TEST_1");
        product1.setIsVisibleToCustomers(true);
        product1.setClientAccount(clientAccount);

        Product product2 = createEntity();
        product2.setCode("VISIBILITY_TEST_2");
        product2.setIsVisibleToCustomers(false);
        product2.setClientAccount(clientAccount);

        Product savedProduct1 = productRepository.saveAndFlush(product1);
        Product savedProduct2 = productRepository.saveAndFlush(product2);

        List<Long> productIds = List.of(savedProduct1.getId(), savedProduct2.getId());

        try {
            // Test bulk visibility toggle
            restProductMockMvc
                .perform(
                    patch(ENTITY_API_URL + "/bulk/toggle-visibility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(productIds))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.updatedCount").value(2))
                .andExpect(jsonPath("$.failedCount").value(0))
                .andExpect(jsonPath("$.totalRequested").value(2))
                .andExpect(jsonPath("$.updatedProducts", hasSize(2)));

            // Verify visibility was toggled
            em.clear();
            em.flush();
            Product updatedProduct1 = productRepository.findById(savedProduct1.getId()).orElseThrow();
            Product updatedProduct2 = productRepository.findById(savedProduct2.getId()).orElseThrow();

            assertThat(updatedProduct1.getIsVisibleToCustomers()).isFalse(); // was true, now false
            assertThat(updatedProduct2.getIsVisibleToCustomers()).isTrue(); // was false, now true
        } finally {
            // Cleanup
            productRepository.delete(savedProduct1);
            productRepository.delete(savedProduct2);
        }
    }

    @Test
    @Transactional
    void testBulkDeleteWithInvalidIds() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Test with non-existent IDs
        List<Long> invalidIds = List.of(99999L, 99998L);

        restProductMockMvc
            .perform(delete(ENTITY_API_URL + "/bulk").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invalidIds)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.deletedCount").value(0))
            .andExpect(jsonPath("$.failedCount").value(2))
            .andExpect(jsonPath("$.totalRequested").value(2))
            .andExpect(jsonPath("$.failedIds", hasSize(2)))
            .andExpect(jsonPath("$.failedIds[0]").value(99999))
            .andExpect(jsonPath("$.failedIds[1]").value(99998));
    }

    @Test
    @Transactional
    void testBulkOperationsWithEmptyList() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Test with empty list
        List<Long> emptyIds = List.of();

        // Should return 400 Bad Request for empty list
        restProductMockMvc
            .perform(delete(ENTITY_API_URL + "/bulk").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emptyIds)))
            .andExpect(status().isBadRequest());
    }

    protected long getRepositoryCount() {
        return productRepository.count();
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

    protected Product getPersistedProduct(Product product) {
        return productRepository.findById(product.getId()).orElseThrow();
    }

    protected void assertPersistedProductToMatchAllProperties(Product expectedProduct) {
        assertProductAllPropertiesEquals(expectedProduct, getPersistedProduct(expectedProduct));
    }

    protected void assertPersistedProductToMatchUpdatableProperties(Product expectedProduct) {
        assertProductAllUpdatablePropertiesEquals(expectedProduct, getPersistedProduct(expectedProduct));
    }
}
