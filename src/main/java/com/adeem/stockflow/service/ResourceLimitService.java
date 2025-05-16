package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.ResourceLimit;
import com.adeem.stockflow.repository.ResourceLimitRepository;
import com.adeem.stockflow.service.dto.ResourceLimitDTO;
import com.adeem.stockflow.service.mapper.ResourceLimitMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.ResourceLimit}.
 */
@Service
@Transactional
public class ResourceLimitService {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLimitService.class);

    private final ResourceLimitRepository resourceLimitRepository;

    private final ResourceLimitMapper resourceLimitMapper;

    public ResourceLimitService(ResourceLimitRepository resourceLimitRepository, ResourceLimitMapper resourceLimitMapper) {
        this.resourceLimitRepository = resourceLimitRepository;
        this.resourceLimitMapper = resourceLimitMapper;
    }

    /**
     * Save a resourceLimit.
     *
     * @param resourceLimitDTO the entity to save.
     * @return the persisted entity.
     */
    public ResourceLimitDTO save(ResourceLimitDTO resourceLimitDTO) {
        LOG.debug("Request to save ResourceLimit : {}", resourceLimitDTO);
        ResourceLimit resourceLimit = resourceLimitMapper.toEntity(resourceLimitDTO);
        resourceLimit = resourceLimitRepository.save(resourceLimit);
        return resourceLimitMapper.toDto(resourceLimit);
    }

    /**
     * Update a resourceLimit.
     *
     * @param resourceLimitDTO the entity to save.
     * @return the persisted entity.
     */
    public ResourceLimitDTO update(ResourceLimitDTO resourceLimitDTO) {
        LOG.debug("Request to update ResourceLimit : {}", resourceLimitDTO);
        ResourceLimit resourceLimit = resourceLimitMapper.toEntity(resourceLimitDTO);
        resourceLimit.setIsPersisted();
        resourceLimit = resourceLimitRepository.save(resourceLimit);
        return resourceLimitMapper.toDto(resourceLimit);
    }

    /**
     * Partially update a resourceLimit.
     *
     * @param resourceLimitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ResourceLimitDTO> partialUpdate(ResourceLimitDTO resourceLimitDTO) {
        LOG.debug("Request to partially update ResourceLimit : {}", resourceLimitDTO);

        return resourceLimitRepository
            .findById(resourceLimitDTO.getId())
            .map(existingResourceLimit -> {
                resourceLimitMapper.partialUpdate(existingResourceLimit, resourceLimitDTO);

                return existingResourceLimit;
            })
            .map(resourceLimitRepository::save)
            .map(resourceLimitMapper::toDto);
    }

    /**
     * Get all the resourceLimits.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ResourceLimitDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ResourceLimits");
        return resourceLimitRepository.findAll(pageable).map(resourceLimitMapper::toDto);
    }

    /**
     * Get one resourceLimit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ResourceLimitDTO> findOne(Long id) {
        LOG.debug("Request to get ResourceLimit : {}", id);
        return resourceLimitRepository.findById(id).map(resourceLimitMapper::toDto);
    }

    /**
     * Delete the resourceLimit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ResourceLimit : {}", id);
        resourceLimitRepository.deleteById(id);
    }
}
