package com.adeem.stockflow.web.rest.uncostomized;

import com.adeem.stockflow.repository.PermissionRepository;
import com.adeem.stockflow.service.PermissionService;
import com.adeem.stockflow.service.dto.PermissionDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
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
 * REST controller for managing {@link com.adeem.stockflow.domain.Permission}.
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionResource {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionResource.class);

    private static final String ENTITY_NAME = "permission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PermissionService permissionService;

    private final PermissionRepository permissionRepository;

    public PermissionResource(PermissionService permissionService, PermissionRepository permissionRepository) {
        this.permissionService = permissionService;
        this.permissionRepository = permissionRepository;
    }

    /**
     * {@code POST  /permissions} : Create a new permission.
     *
     * @param permissionDTO the permissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new permissionDTO, or with status {@code 400 (Bad Request)} if the permission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PermissionDTO> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Permission : {}", permissionDTO);
        if (permissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new permission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        permissionDTO = permissionService.save(permissionDTO);
        return ResponseEntity.created(new URI("/api/permissions/" + permissionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, permissionDTO.getId().toString()))
            .body(permissionDTO);
    }

    /**
     * {@code PUT  /permissions/:id} : Updates an existing permission.
     *
     * @param id the id of the permissionDTO to save.
     * @param permissionDTO the permissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated permissionDTO,
     * or with status {@code 400 (Bad Request)} if the permissionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the permissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PermissionDTO> updatePermission(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PermissionDTO permissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Permission : {}, {}", id, permissionDTO);
        if (permissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, ErrorConstants.ID_NULL);
        }
        if (!Objects.equals(id, permissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, ErrorConstants.ID_INVALID);
        }

        if (!permissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, ErrorConstants.ID_NOT_FOUND);
        }

        permissionDTO = permissionService.update(permissionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, permissionDTO.getId().toString()))
            .body(permissionDTO);
    }

    /**
     * {@code PATCH  /permissions/:id} : Partial updates given fields of an existing permission, field will ignore if it is null
     *
     * @param id the id of the permissionDTO to save.
     * @param permissionDTO the permissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated permissionDTO,
     * or with status {@code 400 (Bad Request)} if the permissionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the permissionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the permissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PermissionDTO> partialUpdatePermission(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PermissionDTO permissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Permission partially : {}, {}", id, permissionDTO);
        if (permissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, permissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!permissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PermissionDTO> result = permissionService.partialUpdate(permissionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, permissionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /permissions} : get all the permissions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of permissions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Permissions");
        Page<PermissionDTO> page = permissionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /permissions/:id} : get the "id" permission.
     *
     * @param id the id of the permissionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the permissionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PermissionDTO> getPermission(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Permission : {}", id);
        Optional<PermissionDTO> permissionDTO = permissionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(permissionDTO);
    }

    /**
     * {@code DELETE  /permissions/:id} : delete the "id" permission.
     *
     * @param id the id of the permissionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Permission : {}", id);
        permissionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
