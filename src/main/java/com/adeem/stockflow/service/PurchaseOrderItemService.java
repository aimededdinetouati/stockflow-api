package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.PurchaseOrderItem;
import com.adeem.stockflow.repository.PurchaseOrderItemRepository;
import com.adeem.stockflow.service.dto.PurchaseOrderItemDTO;
import com.adeem.stockflow.service.mapper.PurchaseOrderItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.PurchaseOrderItem}.
 */
@Service
@Transactional
public class PurchaseOrderItemService {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderItemService.class);

    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    private final PurchaseOrderItemMapper purchaseOrderItemMapper;

    public PurchaseOrderItemService(
        PurchaseOrderItemRepository purchaseOrderItemRepository,
        PurchaseOrderItemMapper purchaseOrderItemMapper
    ) {
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
        this.purchaseOrderItemMapper = purchaseOrderItemMapper;
    }

    /**
     * Save a purchaseOrderItem.
     *
     * @param purchaseOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseOrderItemDTO save(PurchaseOrderItemDTO purchaseOrderItemDTO) {
        LOG.debug("Request to save PurchaseOrderItem : {}", purchaseOrderItemDTO);
        PurchaseOrderItem purchaseOrderItem = purchaseOrderItemMapper.toEntity(purchaseOrderItemDTO);
        purchaseOrderItem = purchaseOrderItemRepository.save(purchaseOrderItem);
        return purchaseOrderItemMapper.toDto(purchaseOrderItem);
    }

    /**
     * Update a purchaseOrderItem.
     *
     * @param purchaseOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseOrderItemDTO update(PurchaseOrderItemDTO purchaseOrderItemDTO) {
        LOG.debug("Request to update PurchaseOrderItem : {}", purchaseOrderItemDTO);
        PurchaseOrderItem purchaseOrderItem = purchaseOrderItemMapper.toEntity(purchaseOrderItemDTO);
        purchaseOrderItem.setIsPersisted();
        purchaseOrderItem = purchaseOrderItemRepository.save(purchaseOrderItem);
        return purchaseOrderItemMapper.toDto(purchaseOrderItem);
    }

    /**
     * Partially update a purchaseOrderItem.
     *
     * @param purchaseOrderItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PurchaseOrderItemDTO> partialUpdate(PurchaseOrderItemDTO purchaseOrderItemDTO) {
        LOG.debug("Request to partially update PurchaseOrderItem : {}", purchaseOrderItemDTO);

        return purchaseOrderItemRepository
            .findById(purchaseOrderItemDTO.getId())
            .map(existingPurchaseOrderItem -> {
                purchaseOrderItemMapper.partialUpdate(existingPurchaseOrderItem, purchaseOrderItemDTO);

                return existingPurchaseOrderItem;
            })
            .map(purchaseOrderItemRepository::save)
            .map(purchaseOrderItemMapper::toDto);
    }

    /**
     * Get all the purchaseOrderItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PurchaseOrderItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PurchaseOrderItems");
        return purchaseOrderItemRepository.findAll(pageable).map(purchaseOrderItemMapper::toDto);
    }

    /**
     * Get one purchaseOrderItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseOrderItemDTO> findOne(Long id) {
        LOG.debug("Request to get PurchaseOrderItem : {}", id);
        return purchaseOrderItemRepository.findById(id).map(purchaseOrderItemMapper::toDto);
    }

    /**
     * Delete the purchaseOrderItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PurchaseOrderItem : {}", id);
        purchaseOrderItemRepository.deleteById(id);
    }
}
