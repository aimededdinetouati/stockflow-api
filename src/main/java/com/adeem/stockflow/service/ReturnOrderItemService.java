package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.ReturnOrderItem;
import com.adeem.stockflow.repository.ReturnOrderItemRepository;
import com.adeem.stockflow.service.dto.ReturnOrderItemDTO;
import com.adeem.stockflow.service.mapper.ReturnOrderItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.ReturnOrderItem}.
 */
@Service
@Transactional
public class ReturnOrderItemService {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnOrderItemService.class);

    private final ReturnOrderItemRepository returnOrderItemRepository;

    private final ReturnOrderItemMapper returnOrderItemMapper;

    public ReturnOrderItemService(ReturnOrderItemRepository returnOrderItemRepository, ReturnOrderItemMapper returnOrderItemMapper) {
        this.returnOrderItemRepository = returnOrderItemRepository;
        this.returnOrderItemMapper = returnOrderItemMapper;
    }

    /**
     * Save a returnOrderItem.
     *
     * @param returnOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public ReturnOrderItemDTO save(ReturnOrderItemDTO returnOrderItemDTO) {
        LOG.debug("Request to save ReturnOrderItem : {}", returnOrderItemDTO);
        ReturnOrderItem returnOrderItem = returnOrderItemMapper.toEntity(returnOrderItemDTO);
        returnOrderItem = returnOrderItemRepository.save(returnOrderItem);
        return returnOrderItemMapper.toDto(returnOrderItem);
    }

    /**
     * Update a returnOrderItem.
     *
     * @param returnOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public ReturnOrderItemDTO update(ReturnOrderItemDTO returnOrderItemDTO) {
        LOG.debug("Request to update ReturnOrderItem : {}", returnOrderItemDTO);
        ReturnOrderItem returnOrderItem = returnOrderItemMapper.toEntity(returnOrderItemDTO);
        returnOrderItem.setIsPersisted();
        returnOrderItem = returnOrderItemRepository.save(returnOrderItem);
        return returnOrderItemMapper.toDto(returnOrderItem);
    }

    /**
     * Partially update a returnOrderItem.
     *
     * @param returnOrderItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ReturnOrderItemDTO> partialUpdate(ReturnOrderItemDTO returnOrderItemDTO) {
        LOG.debug("Request to partially update ReturnOrderItem : {}", returnOrderItemDTO);

        return returnOrderItemRepository
            .findById(returnOrderItemDTO.getId())
            .map(existingReturnOrderItem -> {
                returnOrderItemMapper.partialUpdate(existingReturnOrderItem, returnOrderItemDTO);

                return existingReturnOrderItem;
            })
            .map(returnOrderItemRepository::save)
            .map(returnOrderItemMapper::toDto);
    }

    /**
     * Get all the returnOrderItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ReturnOrderItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ReturnOrderItems");
        return returnOrderItemRepository.findAll(pageable).map(returnOrderItemMapper::toDto);
    }

    /**
     * Get one returnOrderItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReturnOrderItemDTO> findOne(Long id) {
        LOG.debug("Request to get ReturnOrderItem : {}", id);
        return returnOrderItemRepository.findById(id).map(returnOrderItemMapper::toDto);
    }

    /**
     * Delete the returnOrderItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ReturnOrderItem : {}", id);
        returnOrderItemRepository.deleteById(id);
    }
}
