package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.QuotaRepository;
import com.adeem.stockflow.service.QuotaService;
import com.adeem.stockflow.service.dto.QuotaDTO;
import com.adeem.stockflow.web.rest.errors.BadRequestAlertException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adeem.stockflow.domain.Quota}.
 */
@RestController
@RequestMapping("/api/quotas")
public class QuotaResource {

    private static final Logger LOG = LoggerFactory.getLogger(QuotaResource.class);

    private static final String ENTITY_NAME = "quota";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final QuotaService quotaService;

    private final QuotaRepository quotaRepository;

    public QuotaResource(QuotaService quotaService, QuotaRepository quotaRepository) {
        this.quotaService = quotaService;
        this.quotaRepository = quotaRepository;
    }

    /**
     * {@code POST  /quotas} : Create a new quota.
     *
     * @param quotaDTO the quotaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new quotaDTO, or with status {@code 400 (Bad Request)} if the quota has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<QuotaDTO> createQuota(@Valid @RequestBody QuotaDTO quotaDTO) throws URISyntaxException {
        LOG.debug("REST request to save Quota : {}", quotaDTO);
        if (quotaDTO.getId() != null) {
            throw new BadRequestAlertException("A new quota cannot already have an ID", ENTITY_NAME, "idexists");
        }
        quotaDTO = quotaService.save(quotaDTO);
        return ResponseEntity.created(new URI("/api/quotas/" + quotaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, quotaDTO.getId().toString()))
            .body(quotaDTO);
    }

    /**
     * {@code PUT  /quotas/:id} : Updates an existing quota.
     *
     * @param id the id of the quotaDTO to save.
     * @param quotaDTO the quotaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quotaDTO,
     * or with status {@code 400 (Bad Request)} if the quotaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the quotaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuotaDTO> updateQuota(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody QuotaDTO quotaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Quota : {}, {}", id, quotaDTO);
        if (quotaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quotaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quotaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        quotaDTO = quotaService.update(quotaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quotaDTO.getId().toString()))
            .body(quotaDTO);
    }

    /**
     * {@code PATCH  /quotas/:id} : Partial updates given fields of an existing quota, field will ignore if it is null
     *
     * @param id the id of the quotaDTO to save.
     * @param quotaDTO the quotaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quotaDTO,
     * or with status {@code 400 (Bad Request)} if the quotaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the quotaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the quotaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<QuotaDTO> partialUpdateQuota(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody QuotaDTO quotaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Quota partially : {}, {}", id, quotaDTO);
        if (quotaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quotaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quotaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<QuotaDTO> result = quotaService.partialUpdate(quotaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quotaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /quotas} : get all the quotas.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of quotas in body.
     */
    @GetMapping("")
    public ResponseEntity<List<QuotaDTO>> getAllQuotas(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        if ("clientaccount-is-null".equals(filter)) {
            LOG.debug("REST request to get all Quotas where clientAccount is null");
            return new ResponseEntity<>(quotaService.findAllWhereClientAccountIsNull(), HttpStatus.OK);
        }
        LOG.debug("REST request to get a page of Quotas");
        Page<QuotaDTO> page = quotaService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /quotas/:id} : get the "id" quota.
     *
     * @param id the id of the quotaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the quotaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuotaDTO> getQuota(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Quota : {}", id);
        Optional<QuotaDTO> quotaDTO = quotaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(quotaDTO);
    }

    /**
     * {@code DELETE  /quotas/:id} : delete the "id" quota.
     *
     * @param id the id of the quotaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuota(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Quota : {}", id);
        quotaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
