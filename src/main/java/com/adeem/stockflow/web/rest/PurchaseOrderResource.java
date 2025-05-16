package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.PurchaseOrderRepository;
import com.adeem.stockflow.service.PurchaseOrderService;
import com.adeem.stockflow.service.dto.PurchaseOrderDTO;
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
 * REST controller for managing {@link com.adeem.stockflow.domain.PurchaseOrder}.
 */
@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderResource.class);

    private static final String ENTITY_NAME = "purchaseOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PurchaseOrderService purchaseOrderService;

    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderResource(PurchaseOrderService purchaseOrderService, PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderService = purchaseOrderService;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    /**
     * {@code POST  /purchase-orders} : Create a new purchaseOrder.
     *
     * @param purchaseOrderDTO the purchaseOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new purchaseOrderDTO, or with status {@code 400 (Bad Request)} if the purchaseOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@Valid @RequestBody PurchaseOrderDTO purchaseOrderDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PurchaseOrder : {}", purchaseOrderDTO);
        if (purchaseOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new purchaseOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        purchaseOrderDTO = purchaseOrderService.save(purchaseOrderDTO);
        return ResponseEntity.created(new URI("/api/purchase-orders/" + purchaseOrderDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, purchaseOrderDTO.getId().toString()))
            .body(purchaseOrderDTO);
    }

    /**
     * {@code PUT  /purchase-orders/:id} : Updates an existing purchaseOrder.
     *
     * @param id the id of the purchaseOrderDTO to save.
     * @param purchaseOrderDTO the purchaseOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseOrderDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the purchaseOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> updatePurchaseOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PurchaseOrderDTO purchaseOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PurchaseOrder : {}, {}", id, purchaseOrderDTO);
        if (purchaseOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        purchaseOrderDTO = purchaseOrderService.update(purchaseOrderDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchaseOrderDTO.getId().toString()))
            .body(purchaseOrderDTO);
    }

    /**
     * {@code PATCH  /purchase-orders/:id} : Partial updates given fields of an existing purchaseOrder, field will ignore if it is null
     *
     * @param id the id of the purchaseOrderDTO to save.
     * @param purchaseOrderDTO the purchaseOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseOrderDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the purchaseOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the purchaseOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PurchaseOrderDTO> partialUpdatePurchaseOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PurchaseOrderDTO purchaseOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PurchaseOrder partially : {}, {}", id, purchaseOrderDTO);
        if (purchaseOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PurchaseOrderDTO> result = purchaseOrderService.partialUpdate(purchaseOrderDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchaseOrderDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /purchase-orders} : get all the purchaseOrders.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of purchaseOrders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PurchaseOrderDTO>> getAllPurchaseOrders(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of PurchaseOrders");
        Page<PurchaseOrderDTO> page = purchaseOrderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /purchase-orders/:id} : get the "id" purchaseOrder.
     *
     * @param id the id of the purchaseOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the purchaseOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> getPurchaseOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PurchaseOrder : {}", id);
        Optional<PurchaseOrderDTO> purchaseOrderDTO = purchaseOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(purchaseOrderDTO);
    }

    /**
     * {@code DELETE  /purchase-orders/:id} : delete the "id" purchaseOrder.
     *
     * @param id the id of the purchaseOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PurchaseOrder : {}", id);
        purchaseOrderService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
