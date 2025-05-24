package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.domain.InventoryTransaction;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.enumeration.InventoryStatus;
import com.adeem.stockflow.domain.enumeration.TransactionType;
import com.adeem.stockflow.repository.InventoryRepository;
import com.adeem.stockflow.repository.InventoryTransactionRepository;
import com.adeem.stockflow.service.criteria.InventorySpecification;
import com.adeem.stockflow.service.criteria.InventoryTransactionSpecification;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.mapper.InventoryMapper;
import com.adeem.stockflow.service.mapper.InventoryTransactionMapper;
import com.adeem.stockflow.service.mapper.ProductMapper;
import com.adeem.stockflow.service.util.DateTimeUtils;
import com.adeem.stockflow.web.rest.InventoryResource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public InventoryDTO create(InventoryDTO inventoryDTO) {
        LOG.debug("Request to create Inventory : {}", inventoryDTO);

        checkFields(inventoryDTO);
        InventoryDTO savedInventory = save(inventoryDTO);

        // Record the initial inventory transaction
        inventoryTransactionService.save(savedInventory.getProductId(), savedInventory.getQuantity(), TransactionType.INITIAL);

        return savedInventory;
    }

    /**
     * Update a inventory.
     *
     * @param inventoryDTO the entity to save.
     * @return the persisted entity.
     */
    public InventoryDTO update(InventoryDTO inventoryDTO) {
        LOG.debug("Request to update Inventory : {}", inventoryDTO);

        if (!inventoryRepository.existsById(inventoryDTO.getId())) {
            throw new BadRequestAlertException("Entity not found", "", "idnotfound");
        }
        checkFields(inventoryDTO);

        Inventory inventory = inventoryMapper.toEntity(inventoryDTO);
        inventory.setIsPersisted();
        inventory = inventoryRepository.save(inventory);

        // Record inventory transaction
        inventoryTransactionService.save(inventoryDTO.getProductId(), inventoryDTO.getQuantity(), TransactionType.ADJUSTMENT);

        return inventoryMapper.toDto(inventory);
    }

    public void checkFields(InventoryDTO inventoryDTO) {
        BigDecimal quantity = inventoryDTO.getQuantity();
        BigDecimal availableQuantity = inventoryDTO.getAvailableQuantity();

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestAlertException("Quantity cannot be null or negative", "inventory", "quantityinvalid");
        }

        if (availableQuantity != null && availableQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestAlertException("Quantity cannot be null or negative", "inventory", "quantityinvalid");
        }
    }

    /**
     * Partially update a inventory.
     *
     * @param inventoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InventoryDTO> partialUpdate(InventoryDTO inventoryDTO) {
        LOG.debug("Request to partially update Inventory : {}", inventoryDTO);

        return inventoryRepository
            .findById(inventoryDTO.getId())
            .map(existingInventory -> {
                inventoryMapper.partialUpdate(existingInventory, inventoryDTO);

                return existingInventory;
            })
            .map(inventoryRepository::save)
            .map(inventoryMapper::toDto);
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
    @Transactional(readOnly = true)
    public InventoryStatsDTO getInventoryStats(Long clientAccountId) {
        LOG.debug("Request to get Inventory stats for client account: {}", clientAccountId);

        InventoryStatsDTO stats = new InventoryStatsDTO();

        // Basic counts
        Specification<Inventory> baseSpec = InventorySpecification.withClientAccountId(clientAccountId);

        long totalProducts = inventoryRepository.count(baseSpec);
        stats.setTotalProducts(totalProducts);

        // Get all inventories for calculations
        List<Inventory> allInventories = inventoryRepository.findAll(baseSpec);

        BigDecimal totalUnits = BigDecimal.ZERO;
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalAvailable = BigDecimal.ZERO;
        BigDecimal totalReserved = BigDecimal.ZERO;

        long lowStockCount = 0;
        long outOfStockCount = 0;
        long healthyStockCount = 0;
        long overstockCount = 0;

        for (Inventory inventory : allInventories) {
            totalUnits = totalUnits.add(inventory.getQuantity());
            totalAvailable = totalAvailable.add(inventory.getAvailableQuantity());
            totalReserved = totalReserved.add(inventory.getQuantity().subtract(inventory.getAvailableQuantity()));

            // Calculate value (cost price * available quantity)
            if (inventory.getProduct().getCostPrice() != null) {
                BigDecimal itemValue = inventory.getAvailableQuantity().multiply(inventory.getProduct().getCostPrice());
                totalValue = totalValue.add(itemValue);
            }

            // Stock level analysis
            BigDecimal availableQty = inventory.getAvailableQuantity();
            BigDecimal minLevel = inventory.getProduct().getMinimumStockLevel();

            if (availableQty.compareTo(BigDecimal.ZERO) == 0) {
                outOfStockCount++;
            } else if (minLevel != null && availableQty.compareTo(minLevel) <= 0) {
                lowStockCount++;
            } else if (minLevel != null && availableQty.compareTo(minLevel.multiply(BigDecimal.valueOf(3))) > 0) {
                overstockCount++;
            } else {
                healthyStockCount++;
            }
        }

        stats.setTotalUnits(totalUnits);
        stats.setTotalValue(totalValue);
        stats.setTotalAvailableQuantity(totalAvailable);
        stats.setTotalReservedQuantity(totalReserved);
        stats.setLowStockItems(lowStockCount);
        stats.setOutOfStockItems(outOfStockCount);
        stats.setHealthyStockItems(healthyStockCount);
        stats.setOverstockItems(overstockCount);

        // Calculate average stock level
        if (totalProducts > 0) {
            stats.setAverageStockLevel(totalUnits.divide(BigDecimal.valueOf(totalProducts), 2, java.math.RoundingMode.HALF_UP));
        }

        // Set inventory accuracy (placeholder - would need transaction data for real calculation)
        stats.setInventoryAccuracy(BigDecimal.valueOf(98.5));

        // Alert counts
        stats.setCriticalAlerts(outOfStockCount);
        stats.setWarningAlerts(lowStockCount);

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
    public InventoryDTO adjustInventory(Long id, InventoryResource.InventoryAdjustmentRequest adjustmentRequest) {
        LOG.debug("Request to adjust Inventory : {} with request: {}", id, adjustmentRequest);

        Inventory inventory = inventoryRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Inventory not found", "inventory", "idnotfound"));

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
                    throw new BadRequestAlertException("Quantity cannot be negative", "inventory", "invalidquantity");
                }
                if (newAvailable.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestAlertException("Available quantity cannot be negative", "inventory", "invalidavailable");
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
                throw new BadRequestAlertException("Invalid adjustment type", "inventory", "invalidtype");
        }

        inventory.setQuantity(newQuantity);
        inventory.setAvailableQuantity(newAvailable);
        inventory.setIsPersisted();

        Inventory savedInventory = inventoryRepository.save(inventory);

        BigDecimal quantityChange = newQuantity.subtract(oldQuantity);
        inventoryTransactionService.save(inventory.getProduct().getId(), quantityChange, TransactionType.ADJUSTMENT);

        return inventoryMapper.toDto(savedInventory);
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
            .orElseThrow(() -> new BadRequestAlertException("Inventory not found", "inventory", "idnotfound"));

        var specification = InventoryTransactionSpecification.withProductId(inventory.getProduct().getId());
        // Get transactions for this product
        return inventoryTransactionRepository.findAll(specification, pageable).map(inventoryTransactionMapper::toDto);
    }
}
