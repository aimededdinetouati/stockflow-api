package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.domain.CustomerClientAssociation;
import com.adeem.stockflow.repository.CustomerClientAssociationRepository;
import com.adeem.stockflow.security.AuthoritiesConstants;
import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.CustomerAssociationService;
import com.adeem.stockflow.service.dto.CustomerAssociationDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adeem.stockflow.domain.CustomerClientAssociation}.
 * Handles customer-company association relationships.
 */
@RestController
@RequestMapping("/api/customer-associations")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.USER_ADMIN + "')")
public class CustomerAssociationResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerAssociationResource.class);

    private static final String ENTITY_NAME = "customerAssociation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomerAssociationService associationService;
    private final CustomerClientAssociationRepository associationRepository;

    public CustomerAssociationResource(
        CustomerAssociationService associationService,
        CustomerClientAssociationRepository associationRepository
    ) {
        this.associationService = associationService;
        this.associationRepository = associationRepository;
    }

    /**
     * {@code POST  /customer-associations} : Create a new customer-company association.
     *
     * @param associationDTO the associationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new associationDTO, or with status {@code 400 (Bad Request)} if the association has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CustomerAssociationDTO> createAssociation(@Valid @RequestBody CustomerAssociationDTO associationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CustomerAssociation : {}", associationDTO);

        if (associationDTO.getId() != null) {
            throw new BadRequestAlertException("A new association cannot already have an ID", ENTITY_NAME, "idexists");
        }

        CustomerAssociationDTO result = associationService.create(associationDTO);

        return ResponseEntity.created(new URI("/api/customer-associations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /customer-associations/:id} : Updates an existing customer-company association.
     *
     * @param id the id of the associationDTO to save.
     * @param associationDTO the associationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated associationDTO,
     * or with status {@code 400 (Bad Request)} if the associationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the associationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerAssociationDTO> updateAssociation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CustomerAssociationDTO associationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CustomerAssociation : {}, {}", id, associationDTO);

        if (associationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, associationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!associationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CustomerAssociationDTO result = associationService.update(associationDTO);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, associationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /customer-associations} : get all associations for current context (customer or company).
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of associations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CustomerAssociationDTO>> getAllAssociations(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CustomerAssociations");

        Page<CustomerAssociationDTO> page = associationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /customer-associations/:id} : get the "id" association.
     *
     * @param id the id of the associationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the associationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerAssociationDTO> getAssociation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CustomerAssociation : {}", id);
        Optional<CustomerAssociationDTO> associationDTO = associationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(associationDTO);
    }

    /**
     * {@code DELETE  /customer-associations/:id} : delete the "id" association.
     *
     * @param id the id of the associationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssociation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CustomerAssociation : {}", id);
        associationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /customer-associations/companies/:companyId} : get association with specific company.
     *
     * @param companyId the company ID.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the associationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/companies/{companyId}")
    public ResponseEntity<CustomerAssociationDTO> getAssociationWithCompany(@PathVariable("companyId") Long companyId) {
        LOG.debug("REST request to get CustomerAssociation with company : {}", companyId);

        // For customer users: get current customer's association with the company
        Optional<Long> currentUserId = SecurityUtils.getCurrentOptUserId();
        if (currentUserId.isPresent()) {
            // This would need to be implemented - get customer ID from user ID
            // For now, return empty
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * {@code POST  /customer-associations/:id/toggle-status} : toggle association status.
     *
     * @param id the association ID.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated associationDTO.
     */
    @PostMapping("/{id}/toggle-status")
    public ResponseEntity<CustomerAssociationDTO> toggleAssociationStatus(@PathVariable("id") Long id) {
        LOG.debug("REST request to toggle status of CustomerAssociation : {}", id);

        CustomerAssociationDTO result = associationService.toggleStatus(id);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "Association status toggled", id.toString()))
            .body(result);
    }

    /**
     * {@code GET  /customer-associations/search} : search associations.
     *
     * @param query the search query.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the search results.
     */
    @GetMapping("/search")
    public ResponseEntity<List<CustomerAssociationDTO>> searchAssociations(
        @RequestParam("q") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search CustomerAssociations : {}", query);

        Page<CustomerAssociationDTO> page;

        // Determine if current user is a customer or company admin
        Optional<Long> currentUserId = SecurityUtils.getCurrentOptUserId();
        if (currentUserId.isPresent()) {
            // Customer searching by company name
            page = associationService.searchByCompanyName(query, pageable);
        } else {
            // Company admin searching by customer name
            page = associationService.searchByCustomerName(query, pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /customer-associations/recent} : get recent associations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recent associations.
     */
    @GetMapping("/recent")
    public ResponseEntity<List<CustomerAssociationDTO>> getRecentAssociations(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get recent CustomerAssociations");

        Page<CustomerAssociationDTO> page = associationService.getRecentAssociations(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

/**
 * REST controller for company-side association management.
 */
@RestController
@RequestMapping("/api/companies/{companyId}/customer-associations")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
class CompanyAssociationResource {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyAssociationResource.class);

    private final CustomerAssociationService associationService;

    public CompanyAssociationResource(CustomerAssociationService associationService) {
        this.associationService = associationService;
    }

    /**
     * {@code GET  /companies/:companyId/customer-associations} : get all associations for the company.
     *
     * @param companyId the company ID.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of associations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CustomerAssociationDTO>> getCompanyAssociations(
        @PathVariable("companyId") Long companyId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CustomerAssociations for company : {}", companyId);

        // Security check: can only access current company's associations
        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        if (!currentClientAccountId.equals(companyId)) {
            throw new BadRequestAlertException("Can only access current company's associations", "customerAssociation", "invalidcompany");
        }

        Page<CustomerAssociationDTO> page = associationService.findAllByClientAccountId(companyId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /companies/:companyId/customer-associations/statistics} : get association statistics for the company.
     *
     * @param companyId the company ID.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statistics.
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCompanyAssociationStatistics(@PathVariable("companyId") Long companyId) {
        LOG.debug("REST request to get association statistics for company : {}", companyId);

        // Security check: can only access current company's statistics
        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        if (!currentClientAccountId.equals(companyId)) {
            throw new BadRequestAlertException("Can only access current company's statistics", "customerAssociation", "invalidcompany");
        }

        Map<String, Object> stats = associationService.getAssociationStatistics();
        return ResponseEntity.ok().body(stats);
    }
}
