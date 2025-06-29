package com.adeem.stockflow.web.rest.uncostomized;

import com.adeem.stockflow.repository.RolePermissionRepository;
import com.adeem.stockflow.service.RolePermissionService;
import com.adeem.stockflow.service.dto.RolePermissionDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
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
 * REST controller for managing {@link com.adeem.stockflow.domain.RolePermission}.
 */
@RestController
@RequestMapping("/api/role-permissions")
public class RolePermissionResource {

    private static final Logger LOG = LoggerFactory.getLogger(RolePermissionResource.class);

    private static final String ENTITY_NAME = "rolePermission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RolePermissionService rolePermissionService;

    private final RolePermissionRepository rolePermissionRepository;

    public RolePermissionResource(RolePermissionService rolePermissionService, RolePermissionRepository rolePermissionRepository) {
        this.rolePermissionService = rolePermissionService;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    /**
     * {@code POST  /role-permissions} : Create a new rolePermission.
     *
     * @param rolePermissionDTO the rolePermissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rolePermissionDTO, or with status {@code 400 (Bad Request)} if the rolePermission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RolePermissionDTO> createRolePermission(@RequestBody RolePermissionDTO rolePermissionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save RolePermission : {}", rolePermissionDTO);
        if (rolePermissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new rolePermission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        rolePermissionDTO = rolePermissionService.save(rolePermissionDTO);
        return ResponseEntity.created(new URI("/api/role-permissions/" + rolePermissionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, rolePermissionDTO.getId().toString()))
            .body(rolePermissionDTO);
    }

    /**
     * {@code PUT  /role-permissions/:id} : Updates an existing rolePermission.
     *
     * @param id the id of the rolePermissionDTO to save.
     * @param rolePermissionDTO the rolePermissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rolePermissionDTO,
     * or with status {@code 400 (Bad Request)} if the rolePermissionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rolePermissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RolePermissionDTO> updateRolePermission(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RolePermissionDTO rolePermissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RolePermission : {}, {}", id, rolePermissionDTO);
        if (rolePermissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rolePermissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rolePermissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        rolePermissionDTO = rolePermissionService.update(rolePermissionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rolePermissionDTO.getId().toString()))
            .body(rolePermissionDTO);
    }

    /**
     * {@code PATCH  /role-permissions/:id} : Partial updates given fields of an existing rolePermission, field will ignore if it is null
     *
     * @param id the id of the rolePermissionDTO to save.
     * @param rolePermissionDTO the rolePermissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rolePermissionDTO,
     * or with status {@code 400 (Bad Request)} if the rolePermissionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rolePermissionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rolePermissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RolePermissionDTO> partialUpdateRolePermission(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RolePermissionDTO rolePermissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RolePermission partially : {}, {}", id, rolePermissionDTO);
        if (rolePermissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rolePermissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rolePermissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RolePermissionDTO> result = rolePermissionService.partialUpdate(rolePermissionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rolePermissionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /role-permissions} : get all the rolePermissions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rolePermissions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RolePermissionDTO>> getAllRolePermissions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of RolePermissions");
        Page<RolePermissionDTO> page = rolePermissionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /role-permissions/:id} : get the "id" rolePermission.
     *
     * @param id the id of the rolePermissionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rolePermissionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RolePermissionDTO> getRolePermission(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RolePermission : {}", id);
        Optional<RolePermissionDTO> rolePermissionDTO = rolePermissionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rolePermissionDTO);
    }

    /**
     * {@code DELETE  /role-permissions/:id} : delete the "id" rolePermission.
     *
     * @param id the id of the rolePermissionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRolePermission(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RolePermission : {}", id);
        rolePermissionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
