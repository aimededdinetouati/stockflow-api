package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.InventoryTransaction;
import com.adeem.stockflow.repository.InventoryTransactionRepository;
import com.adeem.stockflow.service.dto.InventoryTransactionDTO;
import com.adeem.stockflow.service.mapper.InventoryTransactionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.InventoryTransaction}.
 */
@Service
@Transactional
public class InventoryTransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryTransactionService.class);

    private final InventoryTransactionRepository inventoryTransactionRepository;

    private final InventoryTransactionMapper inventoryTransactionMapper;

    public InventoryTransactionService(
        InventoryTransactionRepository inventoryTransactionRepository,
        InventoryTransactionMapper inventoryTransactionMapper
    ) {
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.inventoryTransactionMapper = inventoryTransactionMapper;
    }

    /**
     * Save a inventoryTransaction.
     *
     * @param inventoryTransactionDTO the entity to save.
     * @return the persisted entity.
     */
    public InventoryTransactionDTO save(InventoryTransactionDTO inventoryTransactionDTO) {
        LOG.debug("Request to save InventoryTransaction : {}", inventoryTransactionDTO);
        InventoryTransaction inventoryTransaction = inventoryTransactionMapper.toEntity(inventoryTransactionDTO);
        inventoryTransaction = inventoryTransactionRepository.save(inventoryTransaction);
        return inventoryTransactionMapper.toDto(inventoryTransaction);
    }

    public void create(InventoryTransactionDTO inventoryTransactionDTO) {
        LOG.debug("Request to save Inventory Transaction : {}", inventoryTransactionDTO);

        InventoryTransactionDTO newInventoryTransaction = new InventoryTransactionDTO();
        newInventoryTransaction.setQuantity(inventoryTransactionDTO.getQuantity());
        newInventoryTransaction.setTransactionDate(inventoryTransactionDTO.getTransactionDate());
        newInventoryTransaction.setTransactionType(inventoryTransactionDTO.getTransactionType());
        newInventoryTransaction.setReferenceNumber(inventoryTransactionDTO.getReferenceNumber());
        newInventoryTransaction.setNotes(inventoryTransactionDTO.getNotes());
        newInventoryTransaction.setReturnItemId(inventoryTransactionDTO.getReturnItemId());
        newInventoryTransaction.setSaleItemId(inventoryTransactionDTO.getSaleItemId());
        newInventoryTransaction.setPurchaseItemId(inventoryTransactionDTO.getPurchaseItemId());
        save(newInventoryTransaction);
    }

    /**
     * Update a inventoryTransaction.
     *
     * @param inventoryTransactionDTO the entity to save.
     * @return the persisted entity.
     */
    public InventoryTransactionDTO update(InventoryTransactionDTO inventoryTransactionDTO) {
        LOG.debug("Request to update InventoryTransaction : {}", inventoryTransactionDTO);
        InventoryTransaction inventoryTransaction = inventoryTransactionMapper.toEntity(inventoryTransactionDTO);
        inventoryTransaction.setIsPersisted();
        inventoryTransaction = inventoryTransactionRepository.save(inventoryTransaction);
        return inventoryTransactionMapper.toDto(inventoryTransaction);
    }

    /**
     * Partially update a inventoryTransaction.
     *
     * @param inventoryTransactionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InventoryTransactionDTO> partialUpdate(InventoryTransactionDTO inventoryTransactionDTO) {
        LOG.debug("Request to partially update InventoryTransaction : {}", inventoryTransactionDTO);

        return inventoryTransactionRepository
            .findById(inventoryTransactionDTO.getId())
            .map(existingInventoryTransaction -> {
                inventoryTransactionMapper.partialUpdate(existingInventoryTransaction, inventoryTransactionDTO);

                return existingInventoryTransaction;
            })
            .map(inventoryTransactionRepository::save)
            .map(inventoryTransactionMapper::toDto);
    }

    /**
     * Get all the inventoryTransactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InventoryTransactionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all InventoryTransactions");
        return inventoryTransactionRepository.findAll(pageable).map(inventoryTransactionMapper::toDto);
    }

    /**
     * Get one inventoryTransaction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InventoryTransactionDTO> findOne(Long id) {
        LOG.debug("Request to get InventoryTransaction : {}", id);
        return inventoryTransactionRepository.findById(id).map(inventoryTransactionMapper::toDto);
    }

    /**
     * Delete the inventoryTransaction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete InventoryTransaction : {}", id);
        inventoryTransactionRepository.deleteById(id);
    }
}
