package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Quota;
import com.adeem.stockflow.repository.QuotaRepository;
import com.adeem.stockflow.service.dto.QuotaDTO;
import com.adeem.stockflow.service.mapper.QuotaMapper;
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
 * Service Implementation for managing {@link com.adeem.stockflow.domain.Quota}.
 */
@Service
@Transactional
public class QuotaService {

    private static final Logger LOG = LoggerFactory.getLogger(QuotaService.class);

    private final QuotaRepository quotaRepository;

    private final QuotaMapper quotaMapper;

    public QuotaService(QuotaRepository quotaRepository, QuotaMapper quotaMapper) {
        this.quotaRepository = quotaRepository;
        this.quotaMapper = quotaMapper;
    }

    /**
     * Save a quota.
     *
     * @param quotaDTO the entity to save.
     * @return the persisted entity.
     */
    public QuotaDTO save(QuotaDTO quotaDTO) {
        LOG.debug("Request to save Quota : {}", quotaDTO);
        Quota quota = quotaMapper.toEntity(quotaDTO);
        quota = quotaRepository.save(quota);
        return quotaMapper.toDto(quota);
    }

    /**
     * Update a quota.
     *
     * @param quotaDTO the entity to save.
     * @return the persisted entity.
     */
    public QuotaDTO update(QuotaDTO quotaDTO) {
        LOG.debug("Request to update Quota : {}", quotaDTO);
        Quota quota = quotaMapper.toEntity(quotaDTO);
        quota.setIsPersisted();
        quota = quotaRepository.save(quota);
        return quotaMapper.toDto(quota);
    }

    /**
     * Partially update a quota.
     *
     * @param quotaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<QuotaDTO> partialUpdate(QuotaDTO quotaDTO) {
        LOG.debug("Request to partially update Quota : {}", quotaDTO);

        return quotaRepository
            .findById(quotaDTO.getId())
            .map(existingQuota -> {
                quotaMapper.partialUpdate(existingQuota, quotaDTO);

                return existingQuota;
            })
            .map(quotaRepository::save)
            .map(quotaMapper::toDto);
    }

    /**
     * Get all the quotas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<QuotaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Quotas");
        return quotaRepository.findAll(pageable).map(quotaMapper::toDto);
    }

    /**
     *  Get all the quotas where ClientAccount is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<QuotaDTO> findAllWhereClientAccountIsNull() {
        LOG.debug("Request to get all quotas where ClientAccount is null");
        return StreamSupport.stream(quotaRepository.findAll().spliterator(), false)
            .filter(quota -> quota.getClientAccount() == null)
            .map(quotaMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one quota by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<QuotaDTO> findOne(Long id) {
        LOG.debug("Request to get Quota : {}", id);
        return quotaRepository.findById(id).map(quotaMapper::toDto);
    }

    /**
     * Delete the quota by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Quota : {}", id);
        quotaRepository.deleteById(id);
    }
}
