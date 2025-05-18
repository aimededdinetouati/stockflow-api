package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.PlanFormulaRepository;
import com.adeem.stockflow.service.PlanFormulaService;
import com.adeem.stockflow.service.dto.PlanFormulaDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adeem.stockflow.domain.PlanFormula}.
 */
@RestController
@RequestMapping("/api/plan-formulas")
public class PlanFormulaResource {

    private static final Logger LOG = LoggerFactory.getLogger(PlanFormulaResource.class);

    private static final String ENTITY_NAME = "planFormula";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlanFormulaService planFormulaService;

    private final PlanFormulaRepository planFormulaRepository;

    public PlanFormulaResource(PlanFormulaService planFormulaService, PlanFormulaRepository planFormulaRepository) {
        this.planFormulaService = planFormulaService;
        this.planFormulaRepository = planFormulaRepository;
    }

    /**
     * {@code POST  /plan-formulas} : Create a new planFormula.
     *
     * @param planFormulaDTO the planFormulaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new planFormulaDTO, or with status {@code 400 (Bad Request)} if the planFormula has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PlanFormulaDTO> createPlanFormula(@Valid @RequestBody PlanFormulaDTO planFormulaDTO) throws URISyntaxException {
        LOG.debug("REST request to save PlanFormula : {}", planFormulaDTO);
        if (planFormulaDTO.getId() != null) {
            throw new BadRequestAlertException("A new planFormula cannot already have an ID", ENTITY_NAME, "idexists");
        }
        planFormulaDTO = planFormulaService.save(planFormulaDTO);
        return ResponseEntity.created(new URI("/api/plan-formulas/" + planFormulaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, planFormulaDTO.getId().toString()))
            .body(planFormulaDTO);
    }

    /**
     * {@code PUT  /plan-formulas/:id} : Updates an existing planFormula.
     *
     * @param id the id of the planFormulaDTO to save.
     * @param planFormulaDTO the planFormulaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated planFormulaDTO,
     * or with status {@code 400 (Bad Request)} if the planFormulaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the planFormulaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlanFormulaDTO> updatePlanFormula(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PlanFormulaDTO planFormulaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PlanFormula : {}, {}", id, planFormulaDTO);
        if (planFormulaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, planFormulaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!planFormulaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        planFormulaDTO = planFormulaService.update(planFormulaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, planFormulaDTO.getId().toString()))
            .body(planFormulaDTO);
    }

    /**
     * {@code PATCH  /plan-formulas/:id} : Partial updates given fields of an existing planFormula, field will ignore if it is null
     *
     * @param id the id of the planFormulaDTO to save.
     * @param planFormulaDTO the planFormulaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated planFormulaDTO,
     * or with status {@code 400 (Bad Request)} if the planFormulaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the planFormulaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the planFormulaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PlanFormulaDTO> partialUpdatePlanFormula(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PlanFormulaDTO planFormulaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PlanFormula partially : {}, {}", id, planFormulaDTO);
        if (planFormulaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, planFormulaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!planFormulaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PlanFormulaDTO> result = planFormulaService.partialUpdate(planFormulaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, planFormulaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /plan-formulas} : get all the planFormulas.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of planFormulas in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PlanFormulaDTO>> getAllPlanFormulas(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of PlanFormulas");
        Page<PlanFormulaDTO> page = planFormulaService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /plan-formulas/:id} : get the "id" planFormula.
     *
     * @param id the id of the planFormulaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the planFormulaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlanFormulaDTO> getPlanFormula(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PlanFormula : {}", id);
        Optional<PlanFormulaDTO> planFormulaDTO = planFormulaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(planFormulaDTO);
    }

    /**
     * {@code DELETE  /plan-formulas/:id} : delete the "id" planFormula.
     *
     * @param id the id of the planFormulaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanFormula(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PlanFormula : {}", id);
        planFormulaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
