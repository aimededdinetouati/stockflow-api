package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.SaleOrderItem;
import com.adeem.stockflow.repository.SaleOrderItemRepository;
import com.adeem.stockflow.service.dto.SaleOrderItemDTO;
import com.adeem.stockflow.service.mapper.SaleOrderItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.SaleOrderItem}.
 */
@Service
@Transactional
public class SaleOrderItemService {

    private static final Logger LOG = LoggerFactory.getLogger(SaleOrderItemService.class);

    private final SaleOrderItemRepository saleOrderItemRepository;

    private final SaleOrderItemMapper saleOrderItemMapper;

    public SaleOrderItemService(SaleOrderItemRepository saleOrderItemRepository, SaleOrderItemMapper saleOrderItemMapper) {
        this.saleOrderItemRepository = saleOrderItemRepository;
        this.saleOrderItemMapper = saleOrderItemMapper;
    }

    /**
     * Save a saleOrderItem.
     *
     * @param saleOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public SaleOrderItemDTO save(SaleOrderItemDTO saleOrderItemDTO) {
        LOG.debug("Request to save SaleOrderItem : {}", saleOrderItemDTO);
        SaleOrderItem saleOrderItem = saleOrderItemMapper.toEntity(saleOrderItemDTO);
        saleOrderItem = saleOrderItemRepository.save(saleOrderItem);
        return saleOrderItemMapper.toDto(saleOrderItem);
    }

    /**
     * Update a saleOrderItem.
     *
     * @param saleOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public SaleOrderItemDTO update(SaleOrderItemDTO saleOrderItemDTO) {
        LOG.debug("Request to update SaleOrderItem : {}", saleOrderItemDTO);
        SaleOrderItem saleOrderItem = saleOrderItemMapper.toEntity(saleOrderItemDTO);
        saleOrderItem.setIsPersisted();
        saleOrderItem = saleOrderItemRepository.save(saleOrderItem);
        return saleOrderItemMapper.toDto(saleOrderItem);
    }

    /**
     * Partially update a saleOrderItem.
     *
     * @param saleOrderItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SaleOrderItemDTO> partialUpdate(SaleOrderItemDTO saleOrderItemDTO) {
        LOG.debug("Request to partially update SaleOrderItem : {}", saleOrderItemDTO);

        return saleOrderItemRepository
            .findById(saleOrderItemDTO.getId())
            .map(existingSaleOrderItem -> {
                saleOrderItemMapper.partialUpdate(existingSaleOrderItem, saleOrderItemDTO);

                return existingSaleOrderItem;
            })
            .map(saleOrderItemRepository::save)
            .map(saleOrderItemMapper::toDto);
    }

    /**
     * Get all the saleOrderItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SaleOrderItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SaleOrderItems");
        return saleOrderItemRepository.findAll(pageable).map(saleOrderItemMapper::toDto);
    }

    /**
     * Get one saleOrderItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SaleOrderItemDTO> findOne(Long id) {
        LOG.debug("Request to get SaleOrderItem : {}", id);
        return saleOrderItemRepository.findById(id).map(saleOrderItemMapper::toDto);
    }

    /**
     * Delete the saleOrderItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SaleOrderItem : {}", id);
        saleOrderItemRepository.deleteById(id);
    }
}
