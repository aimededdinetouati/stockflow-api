package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.PlanFormula;
import com.adeem.stockflow.repository.PlanFormulaRepository;
import com.adeem.stockflow.service.dto.PlanFormulaDTO;
import com.adeem.stockflow.service.mapper.PlanFormulaMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.PlanFormula}.
 */
@Service
@Transactional
public class PlanFormulaService {

    private static final Logger LOG = LoggerFactory.getLogger(PlanFormulaService.class);

    private final PlanFormulaRepository planFormulaRepository;

    private final PlanFormulaMapper planFormulaMapper;

    public PlanFormulaService(PlanFormulaRepository planFormulaRepository, PlanFormulaMapper planFormulaMapper) {
        this.planFormulaRepository = planFormulaRepository;
        this.planFormulaMapper = planFormulaMapper;
    }

    /**
     * Save a planFormula.
     *
     * @param planFormulaDTO the entity to save.
     * @return the persisted entity.
     */
    public PlanFormulaDTO save(PlanFormulaDTO planFormulaDTO) {
        LOG.debug("Request to save PlanFormula : {}", planFormulaDTO);
        PlanFormula planFormula = planFormulaMapper.toEntity(planFormulaDTO);
        planFormula = planFormulaRepository.save(planFormula);
        return planFormulaMapper.toDto(planFormula);
    }

    /**
     * Update a planFormula.
     *
     * @param planFormulaDTO the entity to save.
     * @return the persisted entity.
     */
    public PlanFormulaDTO update(PlanFormulaDTO planFormulaDTO) {
        LOG.debug("Request to update PlanFormula : {}", planFormulaDTO);
        PlanFormula planFormula = planFormulaMapper.toEntity(planFormulaDTO);
        planFormula.setIsPersisted();
        planFormula = planFormulaRepository.save(planFormula);
        return planFormulaMapper.toDto(planFormula);
    }

    /**
     * Partially update a planFormula.
     *
     * @param planFormulaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PlanFormulaDTO> partialUpdate(PlanFormulaDTO planFormulaDTO) {
        LOG.debug("Request to partially update PlanFormula : {}", planFormulaDTO);

        return planFormulaRepository
            .findById(planFormulaDTO.getId())
            .map(existingPlanFormula -> {
                planFormulaMapper.partialUpdate(existingPlanFormula, planFormulaDTO);

                return existingPlanFormula;
            })
            .map(planFormulaRepository::save)
            .map(planFormulaMapper::toDto);
    }

    /**
     * Get all the planFormulas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PlanFormulaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PlanFormulas");
        return planFormulaRepository.findAll(pageable).map(planFormulaMapper::toDto);
    }

    /**
     * Get one planFormula by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PlanFormulaDTO> findOne(Long id) {
        LOG.debug("Request to get PlanFormula : {}", id);
        return planFormulaRepository.findById(id).map(planFormulaMapper::toDto);
    }

    /**
     * Delete the planFormula by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PlanFormula : {}", id);
        planFormulaRepository.deleteById(id);
    }
}
