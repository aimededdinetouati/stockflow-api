package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.SaleOrderRepository;
import com.adeem.stockflow.service.SaleOrderService;
import com.adeem.stockflow.service.dto.SaleOrderDTO;
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
 * REST controller for managing {@link com.adeem.stockflow.domain.SaleOrder}.
 */
@RestController
@RequestMapping("/api/sale-orders")
public class SaleOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(SaleOrderResource.class);

    private static final String ENTITY_NAME = "saleOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SaleOrderService saleOrderService;

    private final SaleOrderRepository saleOrderRepository;

    public SaleOrderResource(SaleOrderService saleOrderService, SaleOrderRepository saleOrderRepository) {
        this.saleOrderService = saleOrderService;
        this.saleOrderRepository = saleOrderRepository;
    }

    /**
     * {@code POST  /sale-orders} : Create a new saleOrder.
     *
     * @param saleOrderDTO the saleOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new saleOrderDTO, or with status {@code 400 (Bad Request)} if the saleOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SaleOrderDTO> createSaleOrder(@Valid @RequestBody SaleOrderDTO saleOrderDTO) throws URISyntaxException {
        LOG.debug("REST request to save SaleOrder : {}", saleOrderDTO);
        if (saleOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new saleOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        saleOrderDTO = saleOrderService.save(saleOrderDTO);
        return ResponseEntity.created(new URI("/api/sale-orders/" + saleOrderDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, saleOrderDTO.getId().toString()))
            .body(saleOrderDTO);
    }

    /**
     * {@code PUT  /sale-orders/:id} : Updates an existing saleOrder.
     *
     * @param id the id of the saleOrderDTO to save.
     * @param saleOrderDTO the saleOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleOrderDTO,
     * or with status {@code 400 (Bad Request)} if the saleOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the saleOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SaleOrderDTO> updateSaleOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SaleOrderDTO saleOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SaleOrder : {}, {}", id, saleOrderDTO);
        if (saleOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        saleOrderDTO = saleOrderService.update(saleOrderDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, saleOrderDTO.getId().toString()))
            .body(saleOrderDTO);
    }

    /**
     * {@code PATCH  /sale-orders/:id} : Partial updates given fields of an existing saleOrder, field will ignore if it is null
     *
     * @param id the id of the saleOrderDTO to save.
     * @param saleOrderDTO the saleOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleOrderDTO,
     * or with status {@code 400 (Bad Request)} if the saleOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the saleOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the saleOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SaleOrderDTO> partialUpdateSaleOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SaleOrderDTO saleOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SaleOrder partially : {}, {}", id, saleOrderDTO);
        if (saleOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SaleOrderDTO> result = saleOrderService.partialUpdate(saleOrderDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, saleOrderDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sale-orders} : get all the saleOrders.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of saleOrders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SaleOrderDTO>> getAllSaleOrders(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        if ("shipment-is-null".equals(filter)) {
            LOG.debug("REST request to get all SaleOrders where shipment is null");
            return new ResponseEntity<>(saleOrderService.findAllWhereShipmentIsNull(), HttpStatus.OK);
        }
        LOG.debug("REST request to get a page of SaleOrders");
        Page<SaleOrderDTO> page = saleOrderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sale-orders/:id} : get the "id" saleOrder.
     *
     * @param id the id of the saleOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the saleOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SaleOrderDTO> getSaleOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SaleOrder : {}", id);
        Optional<SaleOrderDTO> saleOrderDTO = saleOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(saleOrderDTO);
    }

    /**
     * {@code DELETE  /sale-orders/:id} : delete the "id" saleOrder.
     *
     * @param id the id of the saleOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaleOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SaleOrder : {}", id);
        saleOrderService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
