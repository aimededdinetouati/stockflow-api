package com.adeem.stockflow.service;

import static com.adeem.stockflow.service.util.GlobalUtils.generateReference;

import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.domain.InventoryTransaction;
import com.adeem.stockflow.domain.enumeration.TransactionType;
import com.adeem.stockflow.repository.InventoryRepository;
import com.adeem.stockflow.repository.InventoryTransactionRepository;
import com.adeem.stockflow.repository.projection.InventoryFinancialStatsDTO;
import com.adeem.stockflow.repository.projection.InventoryStockLevelStatsDTO;
import com.adeem.stockflow.service.criteria.InventorySpecification;
import com.adeem.stockflow.service.criteria.InventoryTransactionSpecification;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import com.adeem.stockflow.service.exceptions.InsufficientInventoryException;
import com.adeem.stockflow.service.mapper.InventoryMapper;
import com.adeem.stockflow.service.mapper.InventoryTransactionMapper;
import com.adeem.stockflow.service.mapper.ProductMapper;
import com.adeem.stockflow.service.util.DateTimeUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.Inventory}.
 */
@Service
@Transactional
public class InventoryService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionService inventoryTransactionService;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryTransactionMapper inventoryTransactionMapper;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;

    public InventoryService(
        InventoryRepository inventoryRepository,
        InventoryTransactionService inventoryTransactionService,
        InventoryTransactionRepository inventoryTransactionRepository,
        InventoryTransactionMapper inventoryTransactionMapper,
        ProductMapper productMapper,
        InventoryMapper inventoryMapper
    ) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryTransactionService = inventoryTransactionService;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.inventoryTransactionMapper = inventoryTransactionMapper;
        this.productMapper = productMapper;
        this.inventoryMapper = inventoryMapper;
    }

    /**
     * Save a inventory.
     *
     * @param inventoryDTO the entity to save.
     * @return the persisted entity.
     */
    public InventoryDTO save(InventoryDTO inventoryDTO) {
        LOG.debug("Request to save Inventory : {}", inventoryDTO);
        Inventory inventory = inventoryMapper.toEntity(inventoryDTO);
        inventory = inventoryRepository.save(inventory);
        return inventoryMapper.toDto(inventory);
    }

    public void saveAll(List<Inventory> inventoriesToSave) {
        inventoryRepository.saveAll(inventoriesToSave);
    }

    //@CacheEvict(value = "inventoryStats", key = "#inventoryDTO.clientAccountId")
    public InventoryDTO create(InventoryDTO inventoryDTO) {
        LOG.debug("Request to create Inventory : {}", inventoryDTO);

        checkFields(inventoryDTO);
        InventoryDTO savedInventory = save(inventoryDTO);

        // Record the initial inventory transaction
        inventoryTransactionService.save(savedInventory.getProduct().getId(), savedInventory.getQuantity(), TransactionType.INITIAL);

        return savedInventory;
    }

    /**
     * Update a inventory.
     *
     * @param inventoryDTO the entity to save.
     * @return the persisted entity.
     */
    //@CacheEvict(value = "inventoryStats", key = "#inventoryDTO.clientAccountId")
    public InventoryDTO update(InventoryDTO inventoryDTO) {
        LOG.debug("Request to update Inventory : {}", inventoryDTO);

        if (!inventoryRepository.existsById(inventoryDTO.getId())) {
            throw new BadRequestAlertException("Entity not found", "", ErrorConstants.ID_NOT_FOUND);
        }
        checkFields(inventoryDTO);

        Inventory inventory = inventoryMapper.toEntity(inventoryDTO);
        inventory.setIsPersisted();
        inventory = inventoryRepository.save(inventory);

        // Record inventory transaction
        inventoryTransactionService.save(inventoryDTO.getProduct().getId(), inventoryDTO.getQuantity(), TransactionType.ADJUSTMENT);

        return inventoryMapper.toDto(inventory);
    }

    public void checkFields(InventoryDTO inventoryDTO) {
        BigDecimal quantity = inventoryDTO.getQuantity();
        BigDecimal availableQuantity = inventoryDTO.getAvailableQuantity();

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestAlertException("Quantity cannot be null or negative", "inventory", ErrorConstants.QUANTITY_INVALID);
        }

        if (availableQuantity != null && availableQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestAlertException("Quantity cannot be null or negative", "inventory", ErrorConstants.QUANTITY_INVALID);
        }
    }

    /**
     * Count inventories matching the given specification.
     *
     * @param specification the specification to match
     * @return the count of matching inventories
     */
    @Transactional(readOnly = true)
    public long countByCriteria(Specification<Inventory> specification) {
        LOG.debug("Request to count Inventories by criteria");
        return inventoryRepository.count(specification);
    }

    /**
     * Get all the inventories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InventoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Inventories");
        return inventoryRepository.findAll(pageable).map(inventoryMapper::toDto);
    }

    /**
     * Get one inventory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InventoryDTO> findOne(Long id) {
        LOG.debug("Request to get Inventory : {}", id);
        return inventoryRepository.findById(id).map(inventoryMapper::toDto);
    }

    /**
     * Get one inventory by id.
     *
     * @param spec the spec of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InventoryDTO> findOne(Specification<Inventory> spec) {
        LOG.debug("Request to get Inventory : {}", spec);
        return inventoryRepository.findOne(spec).map(inventoryMapper::toDto);
    }

    /**
     * Find inventory with product information by ID for a specific client account.
     *
     * @param id the inventory ID
     * @param clientAccountId the client account ID
     * @return the inventory with product DTO if found and belongs to client account
     */
    @Transactional(readOnly = true)
    public Optional<InventoryWithProductDTO> findOneWithProductForClientAccount(Long id, Long clientAccountId) {
        LOG.debug("Request to get Inventory with Product : {} for client account: {}", id, clientAccountId);

        Specification<Inventory> spec = InventorySpecification.withId(id).and(InventorySpecification.withClientAccountId(clientAccountId));

        return inventoryRepository
            .findOne(spec)
            .map(inventory -> {
                InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);
                ProductDTO productDTO = productMapper.toDto(inventory.getProduct());
                return new InventoryWithProductDTO(inventoryDTO, productDTO);
            });
    }

    /**
     * Find inventory by product ID for a specific client account.
     *
     * @param productId the product ID
     * @param clientAccountId the client account ID
     * @return the inventory DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<InventoryDTO> findByProductForClientAccount(Long productId, Long clientAccountId) {
        LOG.debug("Request to get Inventory for Product : {} and client account: {}", productId, clientAccountId);

        Specification<Inventory> spec = InventorySpecification.withProductId(productId).and(
            InventorySpecification.withClientAccountId(clientAccountId)
        );

        return inventoryRepository.findOne(spec).map(inventoryMapper::toDto);
    }

    /**
     * Find inventory by ID for a specific client account.
     *
     * @param id the inventory ID
     * @param clientAccountId the client account ID
     * @return the inventory DTO if found and belongs to client account
     */
    @Transactional(readOnly = true)
    public Optional<InventoryDTO> findOneForClientAccount(Long id, Long clientAccountId) {
        LOG.debug("Request to get Inventory : {} for client account: {}", id, clientAccountId);

        var x = inventoryRepository.findAll();

        Specification<Inventory> spec = InventorySpecification.withId(id).and(InventorySpecification.withClientAccountId(clientAccountId));

        return inventoryRepository.findOne(spec).map(inventoryMapper::toDto);
    }

    /**
     * Get all inventories with product information.
     *
     * @param specification the specification for filtering
     * @param pageable the pagination information
     * @return the page of inventory with product DTOs
     */
    @Transactional(readOnly = true)
    public Page<InventoryWithProductDTO> findAllWithProduct(Specification<Inventory> specification, Pageable pageable) {
        LOG.debug("Request to get all Inventories with Product info");

        Page<Inventory> inventories = inventoryRepository.findAll(specification, pageable);

        return inventories.map(inventory -> {
            InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);
            ProductDTO productDTO = productMapper.toDto(inventory.getProduct());
            return new InventoryWithProductDTO(inventoryDTO, productDTO);
        });
    }

    /**
     * Find inventory items with low stock levels.
     *
     * @param clientAccountId the client account ID
     * @param pageable the pagination information
     * @return page of low stock items
     */
    @Transactional(readOnly = true)
    public Page<InventoryWithProductDTO> findLowStockItems(Long clientAccountId, Pageable pageable) {
        LOG.debug("Request to get low stock items for client account: {}", clientAccountId);

        Specification<Inventory> spec = InventorySpecification.withLowStock()
            .and(InventorySpecification.withClientAccountId(clientAccountId));

        return findAllWithProduct(spec, pageable);
    }

    /**
     * Find inventory items that are out of stock.
     *
     * @param clientAccountId the client account ID
     * @param pageable the pagination information
     * @return page of out of stock items
     */
    @Transactional(readOnly = true)
    public Page<InventoryWithProductDTO> findOutOfStockItems(Long clientAccountId, Pageable pageable) {
        LOG.debug("Request to get out of stock items for client account: {}", clientAccountId);

        Specification<Inventory> spec = InventorySpecification.withOutOfStock()
            .and(InventorySpecification.withClientAccountId(clientAccountId));

        return findAllWithProduct(spec, pageable);
    }

    /**
     * Get inventory statistics for a client account.
     *
     * @param clientAccountId the client account ID
     * @return inventory statistics
     */
    //@Cacheable(value = "inventoryStats", key = "#clientAccountId")
    @Transactional(readOnly = true)
    public InventoryStatsDTO getInventoryStats(Long clientAccountId) {
        LOG.debug("Request to get Inventory stats for client account: {}", clientAccountId);

        // Single query for financial data
        InventoryFinancialStatsDTO financialStats = inventoryRepository.getFinancialStats(clientAccountId);

        // Single query for stock level analysis
        InventoryStockLevelStatsDTO stockLevelStats = inventoryRepository.getStockLevelStats(clientAccountId);

        // Combine results
        InventoryStatsDTO stats = new InventoryStatsDTO();
        stats.setTotalProducts(stockLevelStats.totalProducts());
        stats.setTotalUnits(financialStats.totalUnits());
        stats.setTotalValue(financialStats.totalValue());
        stats.setTotalAvailableQuantity(financialStats.totalAvailable());
        stats.setTotalReservedQuantity(financialStats.totalReserved());

        stats.setOutOfStockItems(stockLevelStats.outOfStockCount());
        stats.setLowStockItems(stockLevelStats.lowStockCount());
        stats.setHealthyStockItems(stockLevelStats.healthyStockCount());
        stats.setOverstockItems(stockLevelStats.overstockCount());

        // Calculate derived values
        if (stockLevelStats.totalProducts() > 0) {
            stats.setAverageStockLevel(
                financialStats.totalUnits().divide(BigDecimal.valueOf(stockLevelStats.totalProducts()), 2, RoundingMode.HALF_UP)
            );
        }

        // Set fixed accuracy (could be calculated separately if needed)
        stats.setInventoryAccuracy(BigDecimal.valueOf(98.5));

        // Alert counts
        stats.setCriticalAlerts(stockLevelStats.outOfStockCount());
        stats.setWarningAlerts(stockLevelStats.lowStockCount());

        return stats;
    }

    /**
     * Delete the inventory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Inventory : {}", id);
        inventoryRepository.deleteById(id);
    }

    /**
     * Adjust inventory quantity.
     *
     * @param id the inventory ID
     * @param adjustmentRequest the adjustment details
     * @return the updated inventory DTO
     */
    public InventoryDTO adjustInventory(Long id, InventoryAdjustmentRequest adjustmentRequest) {
        LOG.debug("Request to adjust Inventory : {} with request: {}", id, adjustmentRequest);

        Inventory inventory = inventoryRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Inventory not found", "inventory", ErrorConstants.ID_NOT_FOUND));

        BigDecimal oldQuantity = inventory.getQuantity();
        BigDecimal oldAvailable = inventory.getAvailableQuantity();
        BigDecimal newQuantity;
        BigDecimal newAvailable;

        switch (adjustmentRequest.getType()) {
            case INCREASE:
                newQuantity = oldQuantity.add(adjustmentRequest.getQuantity());
                newAvailable = oldAvailable.add(adjustmentRequest.getQuantity());
                break;
            case DECREASE:
                newQuantity = oldQuantity.subtract(adjustmentRequest.getQuantity());
                newAvailable = oldAvailable.subtract(adjustmentRequest.getQuantity());
                if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestAlertException("Quantity cannot be negative", "inventory", ErrorConstants.QUANTITY_INVALID);
                }
                if (newAvailable.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestAlertException(
                        "Available quantity cannot be negative",
                        "inventory",
                        ErrorConstants.INVALID_AVAILABLE_QUANTITY
                    );
                }
                break;
            case SET_EXACT:
                newQuantity = adjustmentRequest.getQuantity();
                BigDecimal reservedQuantity = oldQuantity.subtract(oldAvailable);
                newAvailable = newQuantity.subtract(reservedQuantity);
                if (newAvailable.compareTo(BigDecimal.ZERO) < 0) {
                    newAvailable = BigDecimal.ZERO;
                }
                break;
            default:
                throw new BadRequestAlertException("Invalid adjustment type", "inventory", ErrorConstants.INVALID_TYPE);
        }

        inventory.setQuantity(newQuantity);
        inventory.setAvailableQuantity(newAvailable);
        inventory.setIsPersisted();

        Inventory savedInventory = inventoryRepository.save(inventory);

        BigDecimal quantityChange = newQuantity.subtract(oldQuantity);
        inventoryTransactionService.save(inventory.getProduct().getId(), quantityChange, TransactionType.ADJUSTMENT);

        return inventoryMapper.toDto(savedInventory);
    }

    public void updateInventoryQuantities(
        Inventory inventory,
        BigDecimal quantity,
        String transactionReference,
        TransactionType transactionType,
        List<Inventory> inventoriesToSave,
        List<InventoryTransaction> transactionsToSave
    ) {
        BigDecimal currentQuantity = inventory.getQuantity();
        BigDecimal currentAvailable = inventory.getAvailableQuantity();

        switch (transactionType) {
            case RESERVATION:
                BigDecimal newAvailable = currentAvailable.subtract(quantity);
                if (newAvailable.compareTo(BigDecimal.ZERO) < 0) {
                    throw new InsufficientInventoryException(
                        String.format(
                            "Cannot reserve %s units. Only %s available for product %s",
                            quantity,
                            currentAvailable,
                            inventory.getProduct().getName()
                        )
                    );
                }
                inventory.setAvailableQuantity(newAvailable);
                break;
            case RESERVATION_RELEASE:
                inventory.setAvailableQuantity(currentAvailable.add(quantity));
                break;
            case SALE:
                BigDecimal newQuantity = currentQuantity.subtract(quantity);
                if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                    throw new InsufficientInventoryException(
                        String.format(
                            "Cannot complete sale of %s units. Only %s total for product %s",
                            quantity,
                            currentQuantity,
                            inventory.getProduct().getName()
                        )
                    );
                }
                inventory.setQuantity(newQuantity);
                break;
            case ADJUSTMENT:
                BigDecimal newAdjQuantity = currentQuantity.add(quantity);
                BigDecimal newAdjAvailable = currentAvailable.add(quantity);

                if (newAdjQuantity.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestAlertException(
                        "Adjustment would result in negative inventory",
                        "inventory",
                        ErrorConstants.QUANTITY_INVALID
                    );
                }
                if (newAdjAvailable.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestAlertException(
                        "Adjustment would result in negative available inventory",
                        "inventory",
                        ErrorConstants.INVALID_AVAILABLE_QUANTITY
                    );
                }

                inventory.setQuantity(newAdjQuantity);
                inventory.setAvailableQuantity(newAdjAvailable);
                break;
            case INITIAL:
                inventory.setQuantity(quantity);
                inventory.setAvailableQuantity(quantity);
                break;
        }

        InventoryTransaction transaction = inventoryTransactionService.createEntity(
            quantity,
            transactionType,
            inventory.getProduct(),
            inventory.getClientAccount(),
            transactionReference
        );

        inventoriesToSave.add(inventory);
        transactionsToSave.add(transaction);
    }

    /**
     * Get inventory transaction history.
     *
     * @param inventoryId the inventory ID
     * @param pageable the pagination information
     * @return page of inventory transactions
     */
    @Transactional(readOnly = true)
    public Page<InventoryTransactionDTO> getInventoryHistory(Long inventoryId, Pageable pageable) {
        LOG.debug("Request to get Inventory history : {}", inventoryId);

        // Get the product ID from inventory
        Inventory inventory = inventoryRepository
            .findById(inventoryId)
            .orElseThrow(() -> new BadRequestAlertException("Inventory not found", "inventory", ErrorConstants.ID_NOT_FOUND));

        var specification = InventoryTransactionSpecification.withProductId(inventory.getProduct().getId());
        // Get transactions for this product
        return inventoryTransactionRepository.findAll(specification, pageable).map(inventoryTransactionMapper::toDto);
    }

    /**
     * Bulk delete all inventory records for multiple products.
     * This method is used for efficient bulk product deletion.
     *
     * @param productIds the list of product IDs whose inventory records should be deleted
     * @return the number of deleted inventory records
     */
    @Transactional
    //@CacheEvict(value = "inventories", allEntries = true)
    public int deleteByProductIdsBulk(Long clientAccountId, List<Long> productIds) {
        LOG.debug("Request to delete all inventory records for product IDs: {}", productIds);

        try {
            // Find all inventory records for these products
            List<Inventory> inventoryRecords = inventoryRepository.findByProductIdIn(productIds);

            if (!inventoryRecords.isEmpty()) {
                // Create bulk transaction records for audit trail
                List<InventoryTransaction> transactions = inventoryRecords
                    .stream()
                    .map(inventory -> {
                        InventoryTransaction transaction = new InventoryTransaction();
                        transaction.setInventory(inventory);
                        transaction.setTransactionType(TransactionType.DELETION);
                        transaction.setQuantity(inventory.getQuantity().negate());
                        transaction.setReferenceNumber(inventoryTransactionService.generateReference(clientAccountId));
                        transaction.setNotes("Bulk product deletion - inventory cleanup");
                        return transaction;
                    })
                    .toList();

                // Save all transaction records
                inventoryTransactionRepository.saveAll(transactions);

                // Perform bulk deletion of inventory records
                int deletedCount = inventoryRepository.deleteByProductIdIn(productIds);

                LOG.debug("Successfully deleted {} inventory records for {} products", deletedCount, productIds.size());
                return deletedCount;
            }

            return 0;
        } catch (Exception e) {
            LOG.error("Error deleting inventory records for product IDs {}: {}", productIds, e.getMessage());
            throw new BadRequestAlertException("Failed to delete inventory records for products", "inventory", "bulkdeletionfailed");
        }
    }

    public Optional<Inventory> findByProductIdAndClientAccountId(Long productId, Long currentClientAccountId) {
        return inventoryRepository.findByProductIdAndClientAccountId(productId, currentClientAccountId);
    }
}
