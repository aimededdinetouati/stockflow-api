package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.domain.InventoryTransaction;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.enumeration.InventoryStatus;
import com.adeem.stockflow.domain.enumeration.TransactionType;
import com.adeem.stockflow.repository.InventoryRepository;
import com.adeem.stockflow.service.dto.InventoryDTO;
import com.adeem.stockflow.service.dto.InventoryTransactionDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.mapper.InventoryMapper;
import com.adeem.stockflow.service.util.DateTimeUtils;
import java.math.BigDecimal;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final InventoryMapper inventoryMapper;

    public InventoryService(
        InventoryRepository inventoryRepository,
        InventoryTransactionService inventoryTransactionService,
        InventoryMapper inventoryMapper
    ) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryTransactionService = inventoryTransactionService;
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
     * Delete the inventory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Inventory : {}", id);
        inventoryRepository.deleteById(id);
    }
}
