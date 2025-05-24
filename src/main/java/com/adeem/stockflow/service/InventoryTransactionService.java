package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.InventoryTransaction;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.enumeration.TransactionType;
import com.adeem.stockflow.repository.InventoryTransactionRepository;
import com.adeem.stockflow.service.dto.InventoryTransactionDTO;
import com.adeem.stockflow.service.mapper.InventoryTransactionMapper;
import com.adeem.stockflow.service.util.DateTimeUtils;
import com.adeem.stockflow.service.util.GlobalUtils;
import java.math.BigDecimal;
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
     */
    public void save(Product product, BigDecimal quantity, TransactionType transactionType) {
        LOG.debug("Request to save InventoryTransaction");

        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setQuantity(quantity);
        inventoryTransaction.setTransactionDate(DateTimeUtils.nowAlgeria());
        inventoryTransaction.setTransactionType(transactionType);
        inventoryTransaction.setProduct(product);
        inventoryTransaction.setReferenceNumber(generateReference());

        inventoryTransactionRepository.save(inventoryTransaction);
    }

    /**
     * Update a inventoryTransaction.
     *
     * @param inventoryTransaction the entity to save.
     * @return the persisted entity.
     */
    public void update(InventoryTransaction inventoryTransaction) {
        LOG.debug("Request to update InventoryTransaction : {}", inventoryTransaction);
        inventoryTransaction.setIsPersisted();
        inventoryTransactionRepository.save(inventoryTransaction);
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

    private String generateReference() {
        String reference = inventoryTransactionRepository.getLastReference().orElse(null);
        return GlobalUtils.generateReference(reference);
    }
}
