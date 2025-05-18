package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.SaleOrderItemRepository;
import com.adeem.stockflow.service.SaleOrderItemService;
import com.adeem.stockflow.service.dto.SaleOrderItemDTO;
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
 * REST controller for managing {@link com.adeem.stockflow.domain.SaleOrderItem}.
 */
@RestController
@RequestMapping("/api/sale-order-items")
public class SaleOrderItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(SaleOrderItemResource.class);

    private static final String ENTITY_NAME = "saleOrderItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SaleOrderItemService saleOrderItemService;

    private final SaleOrderItemRepository saleOrderItemRepository;

    public SaleOrderItemResource(SaleOrderItemService saleOrderItemService, SaleOrderItemRepository saleOrderItemRepository) {
        this.saleOrderItemService = saleOrderItemService;
        this.saleOrderItemRepository = saleOrderItemRepository;
    }

    /**
     * {@code POST  /sale-order-items} : Create a new saleOrderItem.
     *
     * @param saleOrderItemDTO the saleOrderItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new saleOrderItemDTO, or with status {@code 400 (Bad Request)} if the saleOrderItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SaleOrderItemDTO> createSaleOrderItem(@Valid @RequestBody SaleOrderItemDTO saleOrderItemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SaleOrderItem : {}", saleOrderItemDTO);
        if (saleOrderItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new saleOrderItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        saleOrderItemDTO = saleOrderItemService.save(saleOrderItemDTO);
        return ResponseEntity.created(new URI("/api/sale-order-items/" + saleOrderItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, saleOrderItemDTO.getId().toString()))
            .body(saleOrderItemDTO);
    }

    /**
     * {@code PUT  /sale-order-items/:id} : Updates an existing saleOrderItem.
     *
     * @param id the id of the saleOrderItemDTO to save.
     * @param saleOrderItemDTO the saleOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the saleOrderItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the saleOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SaleOrderItemDTO> updateSaleOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SaleOrderItemDTO saleOrderItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SaleOrderItem : {}, {}", id, saleOrderItemDTO);
        if (saleOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleOrderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        saleOrderItemDTO = saleOrderItemService.update(saleOrderItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, saleOrderItemDTO.getId().toString()))
            .body(saleOrderItemDTO);
    }

    /**
     * {@code PATCH  /sale-order-items/:id} : Partial updates given fields of an existing saleOrderItem, field will ignore if it is null
     *
     * @param id the id of the saleOrderItemDTO to save.
     * @param saleOrderItemDTO the saleOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the saleOrderItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the saleOrderItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the saleOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SaleOrderItemDTO> partialUpdateSaleOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SaleOrderItemDTO saleOrderItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SaleOrderItem partially : {}, {}", id, saleOrderItemDTO);
        if (saleOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleOrderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SaleOrderItemDTO> result = saleOrderItemService.partialUpdate(saleOrderItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, saleOrderItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sale-order-items} : get all the saleOrderItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of saleOrderItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SaleOrderItemDTO>> getAllSaleOrderItems(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of SaleOrderItems");
        Page<SaleOrderItemDTO> page = saleOrderItemService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sale-order-items/:id} : get the "id" saleOrderItem.
     *
     * @param id the id of the saleOrderItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the saleOrderItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SaleOrderItemDTO> getSaleOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SaleOrderItem : {}", id);
        Optional<SaleOrderItemDTO> saleOrderItemDTO = saleOrderItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(saleOrderItemDTO);
    }

    /**
     * {@code DELETE  /sale-order-items/:id} : delete the "id" saleOrderItem.
     *
     * @param id the id of the saleOrderItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaleOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SaleOrderItem : {}", id);
        saleOrderItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
