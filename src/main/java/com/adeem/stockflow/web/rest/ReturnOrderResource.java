package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.ReturnOrderRepository;
import com.adeem.stockflow.service.ReturnOrderService;
import com.adeem.stockflow.service.dto.ReturnOrderDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adeem.stockflow.domain.ReturnOrder}.
 */
@RestController
@RequestMapping("/api/return-orders")
public class ReturnOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnOrderResource.class);

    private static final String ENTITY_NAME = "returnOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReturnOrderService returnOrderService;

    private final ReturnOrderRepository returnOrderRepository;

    public ReturnOrderResource(ReturnOrderService returnOrderService, ReturnOrderRepository returnOrderRepository) {
        this.returnOrderService = returnOrderService;
        this.returnOrderRepository = returnOrderRepository;
    }

    /**
     * {@code POST  /return-orders} : Create a new returnOrder.
     *
     * @param returnOrderDTO the returnOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new returnOrderDTO, or with status {@code 400 (Bad Request)} if the returnOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReturnOrderDTO> createReturnOrder(@Valid @RequestBody ReturnOrderDTO returnOrderDTO) throws URISyntaxException {
        LOG.debug("REST request to save ReturnOrder : {}", returnOrderDTO);
        if (returnOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new returnOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        returnOrderDTO = returnOrderService.save(returnOrderDTO);
        return ResponseEntity.created(new URI("/api/return-orders/" + returnOrderDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, returnOrderDTO.getId().toString()))
            .body(returnOrderDTO);
    }

    /**
     * {@code PUT  /return-orders/:id} : Updates an existing returnOrder.
     *
     * @param id the id of the returnOrderDTO to save.
     * @param returnOrderDTO the returnOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnOrderDTO,
     * or with status {@code 400 (Bad Request)} if the returnOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the returnOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReturnOrderDTO> updateReturnOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReturnOrderDTO returnOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReturnOrder : {}, {}", id, returnOrderDTO);
        if (returnOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        returnOrderDTO = returnOrderService.update(returnOrderDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnOrderDTO.getId().toString()))
            .body(returnOrderDTO);
    }

    /**
     * {@code PATCH  /return-orders/:id} : Partial updates given fields of an existing returnOrder, field will ignore if it is null
     *
     * @param id the id of the returnOrderDTO to save.
     * @param returnOrderDTO the returnOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnOrderDTO,
     * or with status {@code 400 (Bad Request)} if the returnOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the returnOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the returnOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReturnOrderDTO> partialUpdateReturnOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReturnOrderDTO returnOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReturnOrder partially : {}, {}", id, returnOrderDTO);
        if (returnOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReturnOrderDTO> result = returnOrderService.partialUpdate(returnOrderDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnOrderDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /return-orders} : get all the returnOrders.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of returnOrders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReturnOrderDTO>> getAllReturnOrders(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ReturnOrders");
        Page<ReturnOrderDTO> page = returnOrderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /return-orders/:id} : get the "id" returnOrder.
     *
     * @param id the id of the returnOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the returnOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReturnOrderDTO> getReturnOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReturnOrder : {}", id);
        Optional<ReturnOrderDTO> returnOrderDTO = returnOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(returnOrderDTO);
    }

    /**
     * {@code DELETE  /return-orders/:id} : delete the "id" returnOrder.
     *
     * @param id the id of the returnOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturnOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReturnOrder : {}", id);
        returnOrderService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
