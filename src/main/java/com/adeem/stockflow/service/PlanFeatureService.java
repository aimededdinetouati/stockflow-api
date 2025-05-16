package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.PlanFeature;
import com.adeem.stockflow.repository.PlanFeatureRepository;
import com.adeem.stockflow.service.dto.PlanFeatureDTO;
import com.adeem.stockflow.service.mapper.PlanFeatureMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.PlanFeature}.
 */
@Service
@Transactional
public class PlanFeatureService {

    private static final Logger LOG = LoggerFactory.getLogger(PlanFeatureService.class);

    private final PlanFeatureRepository planFeatureRepository;

    private final PlanFeatureMapper planFeatureMapper;

    public PlanFeatureService(PlanFeatureRepository planFeatureRepository, PlanFeatureMapper planFeatureMapper) {
        this.planFeatureRepository = planFeatureRepository;
        this.planFeatureMapper = planFeatureMapper;
    }

    /**
     * Save a planFeature.
     *
     * @param planFeatureDTO the entity to save.
     * @return the persisted entity.
     */
    public PlanFeatureDTO save(PlanFeatureDTO planFeatureDTO) {
        LOG.debug("Request to save PlanFeature : {}", planFeatureDTO);
        PlanFeature planFeature = planFeatureMapper.toEntity(planFeatureDTO);
        planFeature = planFeatureRepository.save(planFeature);
        return planFeatureMapper.toDto(planFeature);
    }

    /**
     * Update a planFeature.
     *
     * @param planFeatureDTO the entity to save.
     * @return the persisted entity.
     */
    public PlanFeatureDTO update(PlanFeatureDTO planFeatureDTO) {
        LOG.debug("Request to update PlanFeature : {}", planFeatureDTO);
        PlanFeature planFeature = planFeatureMapper.toEntity(planFeatureDTO);
        planFeature.setIsPersisted();
        planFeature = planFeatureRepository.save(planFeature);
        return planFeatureMapper.toDto(planFeature);
    }

    /**
     * Partially update a planFeature.
     *
     * @param planFeatureDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PlanFeatureDTO> partialUpdate(PlanFeatureDTO planFeatureDTO) {
        LOG.debug("Request to partially update PlanFeature : {}", planFeatureDTO);

        return planFeatureRepository
            .findById(planFeatureDTO.getId())
            .map(existingPlanFeature -> {
                planFeatureMapper.partialUpdate(existingPlanFeature, planFeatureDTO);

                return existingPlanFeature;
            })
            .map(planFeatureRepository::save)
            .map(planFeatureMapper::toDto);
    }

    /**
     * Get all the planFeatures.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PlanFeatureDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PlanFeatures");
        return planFeatureRepository.findAll(pageable).map(planFeatureMapper::toDto);
    }

    /**
     * Get one planFeature by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PlanFeatureDTO> findOne(Long id) {
        LOG.debug("Request to get PlanFeature : {}", id);
        return planFeatureRepository.findById(id).map(planFeatureMapper::toDto);
    }

    /**
     * Delete the planFeature by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PlanFeature : {}", id);
        planFeatureRepository.deleteById(id);
    }
}
