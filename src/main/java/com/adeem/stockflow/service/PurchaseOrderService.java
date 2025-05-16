package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.PurchaseOrder;
import com.adeem.stockflow.repository.PurchaseOrderRepository;
import com.adeem.stockflow.service.dto.PurchaseOrderDTO;
import com.adeem.stockflow.service.mapper.PurchaseOrderMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.PurchaseOrder}.
 */
@Service
@Transactional
public class PurchaseOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderService.class);

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final PurchaseOrderMapper purchaseOrderMapper;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository, PurchaseOrderMapper purchaseOrderMapper) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderMapper = purchaseOrderMapper;
    }

    /**
     * Save a purchaseOrder.
     *
     * @param purchaseOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseOrderDTO save(PurchaseOrderDTO purchaseOrderDTO) {
        LOG.debug("Request to save PurchaseOrder : {}", purchaseOrderDTO);
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    /**
     * Update a purchaseOrder.
     *
     * @param purchaseOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseOrderDTO update(PurchaseOrderDTO purchaseOrderDTO) {
        LOG.debug("Request to update PurchaseOrder : {}", purchaseOrderDTO);
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
        purchaseOrder.setIsPersisted();
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    /**
     * Partially update a purchaseOrder.
     *
     * @param purchaseOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PurchaseOrderDTO> partialUpdate(PurchaseOrderDTO purchaseOrderDTO) {
        LOG.debug("Request to partially update PurchaseOrder : {}", purchaseOrderDTO);

        return purchaseOrderRepository
            .findById(purchaseOrderDTO.getId())
            .map(existingPurchaseOrder -> {
                purchaseOrderMapper.partialUpdate(existingPurchaseOrder, purchaseOrderDTO);

                return existingPurchaseOrder;
            })
            .map(purchaseOrderRepository::save)
            .map(purchaseOrderMapper::toDto);
    }

    /**
     * Get all the purchaseOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PurchaseOrderDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PurchaseOrders");
        return purchaseOrderRepository.findAll(pageable).map(purchaseOrderMapper::toDto);
    }

    /**
     * Get one purchaseOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseOrderDTO> findOne(Long id) {
        LOG.debug("Request to get PurchaseOrder : {}", id);
        return purchaseOrderRepository.findById(id).map(purchaseOrderMapper::toDto);
    }

    /**
     * Delete the purchaseOrder by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PurchaseOrder : {}", id);
        purchaseOrderRepository.deleteById(id);
    }
}
