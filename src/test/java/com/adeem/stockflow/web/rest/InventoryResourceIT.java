package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.InventoryAsserts.*;
import static com.adeem.stockflow.security.TestSecurityContextHelper.setSecurityContextWithClientAccountId;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adeem.stockflow.web.rest.TestUtil.sameNumber;
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
import com.adeem.stockflow.security.WithMockClientAccount;
import com.adeem.stockflow.service.dto.InventoryDTO;
import com.adeem.stockflow.service.dto.InventoryStatsDTO;
import com.adeem.stockflow.service.dto.InventoryWithProductDTO;
import com.adeem.stockflow.service.mapper.InventoryMapper;
import com.adeem.stockflow.service.mapper.ProductMapper;
import com.adeem.stockflow.web.rest.InventoryResource.InventoryAdjustmentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
 * Integration tests for the {@link InventoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class InventoryResourceIT {

    private static final BigDecimal DEFAULT_QUANTITY = new BigDecimal("10.00");
    private static final BigDecimal UPDATED_QUANTITY = new BigDecimal("20.00");

    private static final BigDecimal DEFAULT_AVAILABLE_QUANTITY = new BigDecimal("8.00");
    private static final BigDecimal UPDATED_AVAILABLE_QUANTITY = new BigDecimal("18.00");

    private static final InventoryStatus DEFAULT_STATUS = InventoryStatus.AVAILABLE;
    private static final InventoryStatus UPDATED_STATUS = InventoryStatus.RESERVED;

    private static final String ENTITY_API_URL = "/api/inventories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventoryMockMvc;

    private Inventory inventory;
    private Product product;
    private ClientAccount clientAccount;

    private Inventory insertedInventory;

    /**
     * Create an entity for this test.
     */
    public static Inventory createEntity(EntityManager em) {
        Inventory inventory = new Inventory()
            .quantity(DEFAULT_QUANTITY)
            .availableQuantity(DEFAULT_AVAILABLE_QUANTITY)
            .status(DEFAULT_STATUS);
        return inventory;
    }

    /**
     * Create an updated entity for this test.
     */
    public static Inventory createUpdatedEntity(EntityManager em) {
        Inventory inventory = new Inventory()
            .quantity(UPDATED_QUANTITY)
            .availableQuantity(UPDATED_AVAILABLE_QUANTITY)
            .status(UPDATED_STATUS);
        return inventory;
    }

    @BeforeEach
    void initTest() {
        // Create client account
        clientAccount = ClientAccountResourceIT.createEntity();
        clientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        // Create product
        product = ProductResourceIT.createEntity();
        product.setClientAccount(clientAccount);
        product = productRepository.saveAndFlush(product);

        // Create inventory
        inventory = createEntity(em);
        inventory.setProduct(product);
    }

    @AfterEach
    void cleanup() {
        if (insertedInventory != null) {
            inventoryRepository.delete(insertedInventory);
            insertedInventory = null;
        }
        TestSecurityContextHelper.clearSecurityContext();
    }

    @Test
    @Transactional
    @WithMockClientAccount
    void createInventory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);
        inventoryDTO.setProduct(productMapper.toDto(product));

        restInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryDTO)))
            .andExpect(status().isCreated());

        // Validate the Inventory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInventory = inventoryRepository.findAll().get(inventoryRepository.findAll().size() - 1);
        assertInventoryUpdatableFieldsEquals(inventoryMapper.toEntity(inventoryDTO), returnedInventory);

        insertedInventory = returnedInventory;
    }

    @Test
    @Transactional
    @WithMockClientAccount
    void createInventoryWithExistingId() throws Exception {
        inventory.setId(1L);
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);
        inventoryDTO.setProduct(productMapper.toDto(product));

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInventory() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Initialize the database
        inventory.setClientAccount(clientAccount);
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        // Get all the inventory list
        restInventoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventory.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].availableQuantity").value(hasItem(sameNumber(DEFAULT_AVAILABLE_QUANTITY))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getInventory() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Initialize the database
        inventory.setClientAccount(clientAccount);
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        // Get the inventory
        restInventoryMockMvc
            .perform(get(ENTITY_API_URL_ID, inventory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventory.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(sameNumber(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.availableQuantity").value(sameNumber(DEFAULT_AVAILABLE_QUANTITY)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.product").exists())
            .andExpect(jsonPath("$.product.id").value(product.getId().intValue()));
    }

    @Test
    @Transactional
    @WithMockClientAccount
    void getNonExistingInventory() throws Exception {
        // Get the inventory
        restInventoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInventory() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        inventory.setClientAccount(clientAccount);
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventory
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInventory are not directly saved in db
        em.detach(updatedInventory);
        updatedInventory.quantity(UPDATED_QUANTITY).availableQuantity(UPDATED_AVAILABLE_QUANTITY).status(UPDATED_STATUS);
        InventoryDTO inventoryDTO = inventoryMapper.toDto(updatedInventory);
        inventoryDTO.setProduct(productMapper.toDto(product));

        restInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInventoryToMatchAllProperties(updatedInventory);
    }

    @Test
    @Transactional
    void deleteInventory() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        inventory.setClientAccount(clientAccount);
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inventory
        restInventoryMockMvc.perform(delete(ENTITY_API_URL_ID, inventory.getId())).andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void getInventoryStats() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Initialize the database with multiple inventory records
        inventory.setClientAccount(clientAccount);
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        // Create additional inventory for stats calculation
        Product product2 = ProductResourceIT.createEntity();
        product2.setCode("PRODUCT2");
        product2.setClientAccount(clientAccount);
        product2.setMinimumStockLevel(new BigDecimal("5"));
        product2 = productRepository.saveAndFlush(product2);

        Inventory inventory2 = createEntity(em);
        inventory2.setProduct(product2);
        inventory2.setQuantity(new BigDecimal("2")); // Below minimum stock
        inventory2.setAvailableQuantity(new BigDecimal("2"));
        inventory2.setClientAccount(clientAccount);
        inventoryRepository.saveAndFlush(inventory2);

        // Healthy stock item
        Product healthyProduct = ProductResourceIT.createEntity();
        healthyProduct.setCode("PRODUCT_HEALTHY");
        healthyProduct.setClientAccount(clientAccount);
        healthyProduct.setMinimumStockLevel(new BigDecimal("5"));
        healthyProduct = productRepository.saveAndFlush(healthyProduct);

        Inventory healthyInventory = createEntity(em);
        healthyInventory.setProduct(healthyProduct);
        healthyInventory.setQuantity(new BigDecimal("10"));
        healthyInventory.setAvailableQuantity(new BigDecimal("10"));
        healthyInventory.setClientAccount(clientAccount);
        inventoryRepository.saveAndFlush(healthyInventory);

        // Get the inventory stats
        restInventoryMockMvc
            .perform(get(ENTITY_API_URL + "/stats"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.totalProducts").value(3))
            .andExpect(jsonPath("$.totalUnits").value(sameNumber(new BigDecimal("22.00"))))
            .andExpect(jsonPath("$.lowStockItems").value(1))
            .andExpect(jsonPath("$.healthyStockItems").value(1));
    }

    @Test
    @Transactional
    void getLowStockInventory() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create low stock inventory
        product.setMinimumStockLevel(new BigDecimal("15")); // Higher than current quantity
        productRepository.saveAndFlush(product);

        inventory.setClientAccount(clientAccount);
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        // Get low stock items
        restInventoryMockMvc
            .perform(get(ENTITY_API_URL + "/low-stock"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].id").value(inventory.getId().intValue()))
            .andExpect(jsonPath("$.[0].isLowStock").value(true));
    }

    @Test
    @Transactional
    void getOutOfStockInventory() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Create out of stock inventory
        inventory.setClientAccount(clientAccount);
        inventory.setQuantity(BigDecimal.ZERO);
        inventory.setAvailableQuantity(BigDecimal.ZERO);
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        // Get out of stock items
        restInventoryMockMvc
            .perform(get(ENTITY_API_URL + "/out-of-stock"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].id").value(inventory.getId().intValue()))
            .andExpect(jsonPath("$.[0].isOutOfStock").value(true));
    }

    @Test
    @Transactional
    void adjustInventory() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());
        // Initialize the database
        inventory.setClientAccount(clientAccount);
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest();
        request.setType(AdjustmentType.INCREASE);
        request.setQuantity(new BigDecimal("5"));
        request.setReason("Physical count correction");
        request.setNotes("Found extra items during audit");

        restInventoryMockMvc
            .perform(
                post(ENTITY_API_URL + "/{id}/adjust", inventory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantity").value(sameNumber(new BigDecimal("15.00"))))
            .andExpect(jsonPath("$.availableQuantity").value(sameNumber(new BigDecimal("13.00"))));

        // Verify transaction was created
        List<InventoryTransaction> transactions = inventoryTransactionRepository.findByProductId(product.getId());
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getTransactionType()).isEqualTo(TransactionType.ADJUSTMENT);
        assertThat(transactions.get(0).getQuantity()).isEqualByComparingTo(new BigDecimal("5"));
    }

    @Test
    @Transactional
    void adjustInventoryWithDecrease() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        inventory.setClientAccount(clientAccount);
        inventory.setQuantity(new BigDecimal("15"));
        inventory.setAvailableQuantity(new BigDecimal("13"));
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest();
        request.setType(AdjustmentType.DECREASE);
        request.setQuantity(new BigDecimal("5"));
        request.setReason("Stock loss");
        request.setNotes("Damaged items removed");

        restInventoryMockMvc
            .perform(
                post(ENTITY_API_URL + "/{id}/adjust", inventory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantity").value(sameNumber(new BigDecimal("10.00"))))
            .andExpect(jsonPath("$.availableQuantity").value(sameNumber(new BigDecimal("8.00"))));

        List<InventoryTransaction> transactions = inventoryTransactionRepository.findByProductId(product.getId());
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getTransactionType()).isEqualTo(TransactionType.ADJUSTMENT);
        assertThat(transactions.get(0).getQuantity()).isEqualByComparingTo(new BigDecimal("-5"));
    }

    @Test
    @Transactional
    void adjustInventoryWithSetExact() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        inventory.setClientAccount(clientAccount);
        inventory.setQuantity(new BigDecimal("20"));
        inventory.setAvailableQuantity(new BigDecimal("15")); // 5 reserved
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest();
        request.setType(AdjustmentType.SET_EXACT);
        request.setQuantity(new BigDecimal("12")); // New total, 5 reserved => 7 available
        request.setReason("Stock reconciliation");
        request.setNotes("Rechecked count");

        restInventoryMockMvc
            .perform(
                post(ENTITY_API_URL + "/{id}/adjust", inventory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantity").value(sameNumber(new BigDecimal("12.00"))))
            .andExpect(jsonPath("$.availableQuantity").value(sameNumber(new BigDecimal("7.00"))));

        List<InventoryTransaction> transactions = inventoryTransactionRepository.findByProductId(product.getId());
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getTransactionType()).isEqualTo(TransactionType.ADJUSTMENT);
        assertThat(transactions.get(0).getQuantity()).isEqualByComparingTo(new BigDecimal("-8")); // 12 - 20
    }

    @Test
    @Transactional
    void adjustInventoryWithNegativeQuantityShouldFail() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        inventory.setClientAccount(clientAccount);
        inventory.setQuantity(new BigDecimal("3"));
        inventory.setAvailableQuantity(new BigDecimal("3"));
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest();
        request.setType(AdjustmentType.DECREASE);
        request.setQuantity(new BigDecimal("5"));
        request.setReason("Shrinkage");
        request.setNotes("Lost items");

        restInventoryMockMvc
            .perform(
                post(ENTITY_API_URL + "/{id}/adjust", inventory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.invalidquantity"));
    }

    @Test
    @Transactional
    void getInventoryByProduct() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Initialize the database
        inventory.setClientAccount(clientAccount);
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        // Get inventory by product ID
        restInventoryMockMvc
            .perform(get(ENTITY_API_URL + "/product/{productId}", product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventory.getId().intValue()))
            .andExpect(jsonPath("$.product.id").value(product.getId().intValue()));
    }

    @Test
    @Transactional
    void getInventoryHistory() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Initialize the database
        inventory.setClientAccount(clientAccount);
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        // Create some transaction history
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProduct(product);
        transaction.setTransactionType(TransactionType.PURCHASE);
        transaction.setQuantity(new BigDecimal("10"));
        transaction.setTransactionDate(ZonedDateTime.now());
        transaction.setReferenceNumber("PO-001");
        transaction.setNotes("Initial stock");
        inventoryTransactionRepository.saveAndFlush(transaction);

        // Get inventory history
        restInventoryMockMvc
            .perform(get(ENTITY_API_URL + "/{id}/history", inventory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].transactionType").value("PURCHASE"))
            .andExpect(jsonPath("$.[0].quantity").value(sameNumber(new BigDecimal("10"))))
            .andExpect(jsonPath("$.[0].referenceNumber").value("PO-001"));
    }

    @Test
    @Transactional
    void countInventory() throws Exception {
        setSecurityContextWithClientAccountId(clientAccount.getId());

        // Initialize the database
        inventory.setClientAccount(clientAccount);
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        // Get the count
        restInventoryMockMvc
            .perform(get(ENTITY_API_URL + "/count"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").value(1));
    }

    protected long getRepositoryCount() {
        return inventoryRepository.count();
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

    protected Inventory getPersistedInventory(Inventory inventory) {
        return inventoryRepository.findById(inventory.getId()).orElseThrow();
    }

    protected void assertPersistedInventoryToMatchAllProperties(Inventory expectedInventory) {
        assertInventoryAllPropertiesEquals(expectedInventory, getPersistedInventory(expectedInventory));
    }

    protected void assertPersistedInventoryToMatchUpdatableProperties(Inventory expectedInventory) {
        assertInventoryAllUpdatablePropertiesEquals(expectedInventory, getPersistedInventory(expectedInventory));
    }
}
