package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.ResourceLimitRepository;
import com.adeem.stockflow.service.ResourceLimitService;
import com.adeem.stockflow.service.dto.ResourceLimitDTO;
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
 * REST controller for managing {@link com.adeem.stockflow.domain.ResourceLimit}.
 */
@RestController
@RequestMapping("/api/resource-limits")
public class ResourceLimitResource {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLimitResource.class);

    private static final String ENTITY_NAME = "resourceLimit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResourceLimitService resourceLimitService;

    private final ResourceLimitRepository resourceLimitRepository;

    public ResourceLimitResource(ResourceLimitService resourceLimitService, ResourceLimitRepository resourceLimitRepository) {
        this.resourceLimitService = resourceLimitService;
        this.resourceLimitRepository = resourceLimitRepository;
    }

    /**
     * {@code POST  /resource-limits} : Create a new resourceLimit.
     *
     * @param resourceLimitDTO the resourceLimitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new resourceLimitDTO, or with status {@code 400 (Bad Request)} if the resourceLimit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ResourceLimitDTO> createResourceLimit(@Valid @RequestBody ResourceLimitDTO resourceLimitDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ResourceLimit : {}", resourceLimitDTO);
        if (resourceLimitDTO.getId() != null) {
            throw new BadRequestAlertException("A new resourceLimit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        resourceLimitDTO = resourceLimitService.save(resourceLimitDTO);
        return ResponseEntity.created(new URI("/api/resource-limits/" + resourceLimitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, resourceLimitDTO.getId().toString()))
            .body(resourceLimitDTO);
    }

    /**
     * {@code PUT  /resource-limits/:id} : Updates an existing resourceLimit.
     *
     * @param id the id of the resourceLimitDTO to save.
     * @param resourceLimitDTO the resourceLimitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resourceLimitDTO,
     * or with status {@code 400 (Bad Request)} if the resourceLimitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the resourceLimitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResourceLimitDTO> updateResourceLimit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ResourceLimitDTO resourceLimitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ResourceLimit : {}, {}", id, resourceLimitDTO);
        if (resourceLimitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resourceLimitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!resourceLimitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        resourceLimitDTO = resourceLimitService.update(resourceLimitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, resourceLimitDTO.getId().toString()))
            .body(resourceLimitDTO);
    }

    /**
     * {@code PATCH  /resource-limits/:id} : Partial updates given fields of an existing resourceLimit, field will ignore if it is null
     *
     * @param id the id of the resourceLimitDTO to save.
     * @param resourceLimitDTO the resourceLimitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resourceLimitDTO,
     * or with status {@code 400 (Bad Request)} if the resourceLimitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the resourceLimitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the resourceLimitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ResourceLimitDTO> partialUpdateResourceLimit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ResourceLimitDTO resourceLimitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ResourceLimit partially : {}, {}", id, resourceLimitDTO);
        if (resourceLimitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resourceLimitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!resourceLimitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ResourceLimitDTO> result = resourceLimitService.partialUpdate(resourceLimitDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, resourceLimitDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /resource-limits} : get all the resourceLimits.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of resourceLimits in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ResourceLimitDTO>> getAllResourceLimits(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ResourceLimits");
        Page<ResourceLimitDTO> page = resourceLimitService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /resource-limits/:id} : get the "id" resourceLimit.
     *
     * @param id the id of the resourceLimitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the resourceLimitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceLimitDTO> getResourceLimit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ResourceLimit : {}", id);
        Optional<ResourceLimitDTO> resourceLimitDTO = resourceLimitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(resourceLimitDTO);
    }

    /**
     * {@code DELETE  /resource-limits/:id} : delete the "id" resourceLimit.
     *
     * @param id the id of the resourceLimitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResourceLimit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ResourceLimit : {}", id);
        resourceLimitService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
