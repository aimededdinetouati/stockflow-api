package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.InventoryTransaction;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.enumeration.TransactionType;
import com.adeem.stockflow.repository.InventoryTransactionRepository;
import com.adeem.stockflow.repository.ProductRepository;
import com.adeem.stockflow.service.dto.InventoryTransactionDTO;
import com.adeem.stockflow.service.mapper.InventoryTransactionMapper;
import com.adeem.stockflow.service.util.DateTimeUtils;
import com.adeem.stockflow.service.util.GlobalUtils;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
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

    private final ProductRepository productRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryTransactionMapper inventoryTransactionMapper;

    public InventoryTransactionService(
        ProductRepository productRepository,
        InventoryTransactionRepository inventoryTransactionRepository,
        InventoryTransactionMapper inventoryTransactionMapper
    ) {
        this.productRepository = productRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.inventoryTransactionMapper = inventoryTransactionMapper;
    }

    /**
     * Save a inventoryTransaction.
     *
     */
    public void save(Long productId, BigDecimal quantity, TransactionType transactionType) {
        LOG.debug("Request to save InventoryTransaction");
        Product product = productRepository.findById(productId).orElseThrow();
        ClientAccount clientAccount = product.getClientAccount();

        InventoryTransaction inventoryTransaction = createEntity(
            quantity,
            transactionType,
            product,
            clientAccount,
            generateReference(clientAccount.getId())
        );
        inventoryTransactionRepository.save(inventoryTransaction);
    }

    public void saveAll(List<InventoryTransaction> transactionsToSave) {
        inventoryTransactionRepository.saveAll(transactionsToSave);
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

    public String generateReference(Long clientAccountId) {
        String reference = inventoryTransactionRepository.getLastReference(clientAccountId).orElse(null);
        return GlobalUtils.generateReference(reference);
    }

    public InventoryTransaction createEntity(
        BigDecimal quantity,
        TransactionType type,
        Product product,
        ClientAccount clientAccount,
        String reference
    ) {
        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setQuantity(quantity);
        inventoryTransaction.setTransactionDate(DateTimeUtils.nowAlgeria());
        inventoryTransaction.setTransactionType(type);
        inventoryTransaction.setProduct(product);
        inventoryTransaction.setClientAccount(clientAccount);
        inventoryTransaction.setReferenceNumber(reference);
        return inventoryTransaction;
    }
}
