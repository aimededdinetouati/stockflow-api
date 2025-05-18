package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.PurchaseOrderItemRepository;
import com.adeem.stockflow.service.PurchaseOrderItemService;
import com.adeem.stockflow.service.dto.PurchaseOrderItemDTO;
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
 * REST controller for managing {@link com.adeem.stockflow.domain.PurchaseOrderItem}.
 */
@RestController
@RequestMapping("/api/purchase-order-items")
public class PurchaseOrderItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderItemResource.class);

    private static final String ENTITY_NAME = "purchaseOrderItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PurchaseOrderItemService purchaseOrderItemService;

    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    public PurchaseOrderItemResource(
        PurchaseOrderItemService purchaseOrderItemService,
        PurchaseOrderItemRepository purchaseOrderItemRepository
    ) {
        this.purchaseOrderItemService = purchaseOrderItemService;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
    }

    /**
     * {@code POST  /purchase-order-items} : Create a new purchaseOrderItem.
     *
     * @param purchaseOrderItemDTO the purchaseOrderItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new purchaseOrderItemDTO, or with status {@code 400 (Bad Request)} if the purchaseOrderItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PurchaseOrderItemDTO> createPurchaseOrderItem(@Valid @RequestBody PurchaseOrderItemDTO purchaseOrderItemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PurchaseOrderItem : {}", purchaseOrderItemDTO);
        if (purchaseOrderItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new purchaseOrderItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        purchaseOrderItemDTO = purchaseOrderItemService.save(purchaseOrderItemDTO);
        return ResponseEntity.created(new URI("/api/purchase-order-items/" + purchaseOrderItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, purchaseOrderItemDTO.getId().toString()))
            .body(purchaseOrderItemDTO);
    }

    /**
     * {@code PUT  /purchase-order-items/:id} : Updates an existing purchaseOrderItem.
     *
     * @param id the id of the purchaseOrderItemDTO to save.
     * @param purchaseOrderItemDTO the purchaseOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseOrderItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the purchaseOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrderItemDTO> updatePurchaseOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PurchaseOrderItemDTO purchaseOrderItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PurchaseOrderItem : {}, {}", id, purchaseOrderItemDTO);
        if (purchaseOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseOrderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        purchaseOrderItemDTO = purchaseOrderItemService.update(purchaseOrderItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchaseOrderItemDTO.getId().toString()))
            .body(purchaseOrderItemDTO);
    }

    /**
     * {@code PATCH  /purchase-order-items/:id} : Partial updates given fields of an existing purchaseOrderItem, field will ignore if it is null
     *
     * @param id the id of the purchaseOrderItemDTO to save.
     * @param purchaseOrderItemDTO the purchaseOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseOrderItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the purchaseOrderItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the purchaseOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PurchaseOrderItemDTO> partialUpdatePurchaseOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PurchaseOrderItemDTO purchaseOrderItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PurchaseOrderItem partially : {}, {}", id, purchaseOrderItemDTO);
        if (purchaseOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseOrderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PurchaseOrderItemDTO> result = purchaseOrderItemService.partialUpdate(purchaseOrderItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchaseOrderItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /purchase-order-items} : get all the purchaseOrderItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of purchaseOrderItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PurchaseOrderItemDTO>> getAllPurchaseOrderItems(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of PurchaseOrderItems");
        Page<PurchaseOrderItemDTO> page = purchaseOrderItemService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /purchase-order-items/:id} : get the "id" purchaseOrderItem.
     *
     * @param id the id of the purchaseOrderItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the purchaseOrderItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderItemDTO> getPurchaseOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PurchaseOrderItem : {}", id);
        Optional<PurchaseOrderItemDTO> purchaseOrderItemDTO = purchaseOrderItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(purchaseOrderItemDTO);
    }

    /**
     * {@code DELETE  /purchase-order-items/:id} : delete the "id" purchaseOrderItem.
     *
     * @param id the id of the purchaseOrderItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PurchaseOrderItem : {}", id);
        purchaseOrderItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
