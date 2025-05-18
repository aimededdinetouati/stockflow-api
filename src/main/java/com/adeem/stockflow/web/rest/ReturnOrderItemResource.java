package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.ReturnOrderItemRepository;
import com.adeem.stockflow.service.ReturnOrderItemService;
import com.adeem.stockflow.service.dto.ReturnOrderItemDTO;
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
 * REST controller for managing {@link com.adeem.stockflow.domain.ReturnOrderItem}.
 */
@RestController
@RequestMapping("/api/return-order-items")
public class ReturnOrderItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnOrderItemResource.class);

    private static final String ENTITY_NAME = "returnOrderItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReturnOrderItemService returnOrderItemService;

    private final ReturnOrderItemRepository returnOrderItemRepository;

    public ReturnOrderItemResource(ReturnOrderItemService returnOrderItemService, ReturnOrderItemRepository returnOrderItemRepository) {
        this.returnOrderItemService = returnOrderItemService;
        this.returnOrderItemRepository = returnOrderItemRepository;
    }

    /**
     * {@code POST  /return-order-items} : Create a new returnOrderItem.
     *
     * @param returnOrderItemDTO the returnOrderItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new returnOrderItemDTO, or with status {@code 400 (Bad Request)} if the returnOrderItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReturnOrderItemDTO> createReturnOrderItem(@Valid @RequestBody ReturnOrderItemDTO returnOrderItemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ReturnOrderItem : {}", returnOrderItemDTO);
        if (returnOrderItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new returnOrderItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        returnOrderItemDTO = returnOrderItemService.save(returnOrderItemDTO);
        return ResponseEntity.created(new URI("/api/return-order-items/" + returnOrderItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, returnOrderItemDTO.getId().toString()))
            .body(returnOrderItemDTO);
    }

    /**
     * {@code PUT  /return-order-items/:id} : Updates an existing returnOrderItem.
     *
     * @param id the id of the returnOrderItemDTO to save.
     * @param returnOrderItemDTO the returnOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the returnOrderItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the returnOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReturnOrderItemDTO> updateReturnOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReturnOrderItemDTO returnOrderItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReturnOrderItem : {}, {}", id, returnOrderItemDTO);
        if (returnOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnOrderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        returnOrderItemDTO = returnOrderItemService.update(returnOrderItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnOrderItemDTO.getId().toString()))
            .body(returnOrderItemDTO);
    }

    /**
     * {@code PATCH  /return-order-items/:id} : Partial updates given fields of an existing returnOrderItem, field will ignore if it is null
     *
     * @param id the id of the returnOrderItemDTO to save.
     * @param returnOrderItemDTO the returnOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the returnOrderItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the returnOrderItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the returnOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReturnOrderItemDTO> partialUpdateReturnOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReturnOrderItemDTO returnOrderItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReturnOrderItem partially : {}, {}", id, returnOrderItemDTO);
        if (returnOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnOrderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReturnOrderItemDTO> result = returnOrderItemService.partialUpdate(returnOrderItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnOrderItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /return-order-items} : get all the returnOrderItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of returnOrderItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReturnOrderItemDTO>> getAllReturnOrderItems(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ReturnOrderItems");
        Page<ReturnOrderItemDTO> page = returnOrderItemService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /return-order-items/:id} : get the "id" returnOrderItem.
     *
     * @param id the id of the returnOrderItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the returnOrderItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReturnOrderItemDTO> getReturnOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReturnOrderItem : {}", id);
        Optional<ReturnOrderItemDTO> returnOrderItemDTO = returnOrderItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(returnOrderItemDTO);
    }

    /**
     * {@code DELETE  /return-order-items/:id} : delete the "id" returnOrderItem.
     *
     * @param id the id of the returnOrderItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturnOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReturnOrderItem : {}", id);
        returnOrderItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
