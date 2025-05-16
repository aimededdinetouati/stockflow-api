package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.SaleOrder;
import com.adeem.stockflow.repository.SaleOrderRepository;
import com.adeem.stockflow.service.dto.SaleOrderDTO;
import com.adeem.stockflow.service.mapper.SaleOrderMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.SaleOrder}.
 */
@Service
@Transactional
public class SaleOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(SaleOrderService.class);

    private final SaleOrderRepository saleOrderRepository;

    private final SaleOrderMapper saleOrderMapper;

    public SaleOrderService(SaleOrderRepository saleOrderRepository, SaleOrderMapper saleOrderMapper) {
        this.saleOrderRepository = saleOrderRepository;
        this.saleOrderMapper = saleOrderMapper;
    }

    /**
     * Save a saleOrder.
     *
     * @param saleOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public SaleOrderDTO save(SaleOrderDTO saleOrderDTO) {
        LOG.debug("Request to save SaleOrder : {}", saleOrderDTO);
        SaleOrder saleOrder = saleOrderMapper.toEntity(saleOrderDTO);
        saleOrder = saleOrderRepository.save(saleOrder);
        return saleOrderMapper.toDto(saleOrder);
    }

    /**
     * Update a saleOrder.
     *
     * @param saleOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public SaleOrderDTO update(SaleOrderDTO saleOrderDTO) {
        LOG.debug("Request to update SaleOrder : {}", saleOrderDTO);
        SaleOrder saleOrder = saleOrderMapper.toEntity(saleOrderDTO);
        saleOrder.setIsPersisted();
        saleOrder = saleOrderRepository.save(saleOrder);
        return saleOrderMapper.toDto(saleOrder);
    }

    /**
     * Partially update a saleOrder.
     *
     * @param saleOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SaleOrderDTO> partialUpdate(SaleOrderDTO saleOrderDTO) {
        LOG.debug("Request to partially update SaleOrder : {}", saleOrderDTO);

        return saleOrderRepository
            .findById(saleOrderDTO.getId())
            .map(existingSaleOrder -> {
                saleOrderMapper.partialUpdate(existingSaleOrder, saleOrderDTO);

                return existingSaleOrder;
            })
            .map(saleOrderRepository::save)
            .map(saleOrderMapper::toDto);
    }

    /**
     * Get all the saleOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SaleOrderDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SaleOrders");
        return saleOrderRepository.findAll(pageable).map(saleOrderMapper::toDto);
    }

    /**
     *  Get all the saleOrders where Shipment is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SaleOrderDTO> findAllWhereShipmentIsNull() {
        LOG.debug("Request to get all saleOrders where Shipment is null");
        return StreamSupport.stream(saleOrderRepository.findAll().spliterator(), false)
            .filter(saleOrder -> saleOrder.getShipment() == null)
            .map(saleOrderMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one saleOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SaleOrderDTO> findOne(Long id) {
        LOG.debug("Request to get SaleOrder : {}", id);
        return saleOrderRepository.findById(id).map(saleOrderMapper::toDto);
    }

    /**
     * Delete the saleOrder by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SaleOrder : {}", id);
        saleOrderRepository.deleteById(id);
    }
}
