package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.ReturnOrder;
import com.adeem.stockflow.repository.ReturnOrderRepository;
import com.adeem.stockflow.service.dto.ReturnOrderDTO;
import com.adeem.stockflow.service.mapper.ReturnOrderMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.ReturnOrder}.
 */
@Service
@Transactional
public class ReturnOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnOrderService.class);

    private final ReturnOrderRepository returnOrderRepository;

    private final ReturnOrderMapper returnOrderMapper;

    public ReturnOrderService(ReturnOrderRepository returnOrderRepository, ReturnOrderMapper returnOrderMapper) {
        this.returnOrderRepository = returnOrderRepository;
        this.returnOrderMapper = returnOrderMapper;
    }

    /**
     * Save a returnOrder.
     *
     * @param returnOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public ReturnOrderDTO save(ReturnOrderDTO returnOrderDTO) {
        LOG.debug("Request to save ReturnOrder : {}", returnOrderDTO);
        ReturnOrder returnOrder = returnOrderMapper.toEntity(returnOrderDTO);
        returnOrder = returnOrderRepository.save(returnOrder);
        return returnOrderMapper.toDto(returnOrder);
    }

    /**
     * Update a returnOrder.
     *
     * @param returnOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public ReturnOrderDTO update(ReturnOrderDTO returnOrderDTO) {
        LOG.debug("Request to update ReturnOrder : {}", returnOrderDTO);
        ReturnOrder returnOrder = returnOrderMapper.toEntity(returnOrderDTO);
        returnOrder.setIsPersisted();
        returnOrder = returnOrderRepository.save(returnOrder);
        return returnOrderMapper.toDto(returnOrder);
    }

    /**
     * Partially update a returnOrder.
     *
     * @param returnOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ReturnOrderDTO> partialUpdate(ReturnOrderDTO returnOrderDTO) {
        LOG.debug("Request to partially update ReturnOrder : {}", returnOrderDTO);

        return returnOrderRepository
            .findById(returnOrderDTO.getId())
            .map(existingReturnOrder -> {
                returnOrderMapper.partialUpdate(existingReturnOrder, returnOrderDTO);

                return existingReturnOrder;
            })
            .map(returnOrderRepository::save)
            .map(returnOrderMapper::toDto);
    }

    /**
     * Get all the returnOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ReturnOrderDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ReturnOrders");
        return returnOrderRepository.findAll(pageable).map(returnOrderMapper::toDto);
    }

    /**
     * Get one returnOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReturnOrderDTO> findOne(Long id) {
        LOG.debug("Request to get ReturnOrder : {}", id);
        return returnOrderRepository.findById(id).map(returnOrderMapper::toDto);
    }

    /**
     * Delete the returnOrder by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ReturnOrder : {}", id);
        returnOrderRepository.deleteById(id);
    }
}
