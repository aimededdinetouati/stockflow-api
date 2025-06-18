package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.domain.SaleOrder;
import com.adeem.stockflow.repository.SaleOrderRepository;
import com.adeem.stockflow.security.AuthoritiesConstants;
import com.adeem.stockflow.service.SaleOrderService;
import com.adeem.stockflow.service.criteria.SaleOrderSpecification;
import com.adeem.stockflow.service.criteria.filter.SaleOrderCriteria;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import jakarta.validation.Valid;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adeem.stockflow.domain.SaleOrder}.
 * Enhanced with multi-tenant security, inventory reservation, and delivery management.
 */
@RestController
@RequestMapping("/api/sale-orders")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.USER_ADMIN + "')")
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

        saleOrderDTO = saleOrderService.create(saleOrderDTO);
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
     * {@code GET  /sale-orders} : get all the saleOrders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of saleOrders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SaleOrderDTO>> getAllSaleOrders(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        SaleOrderCriteria criteria
    ) {
        LOG.debug("REST request to get SaleOrders by criteria: {}", criteria);

        Specification<SaleOrder> specification = new SaleOrderSpecification().createSpecification(criteria);
        Page<SaleOrderDTO> page = saleOrderService.findAllWithCriteria(pageable, specification);
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

    /**
     * {@code POST  /sale-orders/:id/confirm} : Confirm a sale order.
     *
     * @param id the id of the saleOrder to confirm.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleOrderDTO.
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<SaleOrderDTO> confirmOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to confirm SaleOrder : {}", id);
        SaleOrderDTO result = saleOrderService.confirmOrder(id);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "Order confirmed successfully", id.toString()))
            .body(result);
    }

    /**
     * {@code POST  /sale-orders/:id/cancel} : Cancel a sale order.
     *
     * @param id the id of the saleOrder to cancel.
     * @param cancelRequest the cancellation details.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleOrderDTO.
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<SaleOrderDTO> cancelOrder(@PathVariable("id") Long id, @Valid @RequestBody CancelOrderDTO cancelRequest) {
        LOG.debug("REST request to cancel SaleOrder : {} with reason: {}", id, cancelRequest.getReason());
        SaleOrderDTO result = saleOrderService.cancelOrder(id, cancelRequest);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "Order cancelled successfully", id.toString()))
            .body(result);
    }

    /**
     * {@code POST  /sale-orders/validate-availability} : Validate inventory availability for order items.
     *
     * @param items the order items to validate.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the validation result.
     */
    @PostMapping("/validate-availability")
    public ResponseEntity<InventoryValidationDTO> validateOrderAvailability(@Valid @RequestBody List<OrderItemDTO> items) {
        LOG.debug("REST request to validate inventory availability for {} items", items.size());
        InventoryValidationDTO result = saleOrderService.validateOrderAvailability(items);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code GET  /sale-orders/stats} : Get order statistics.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statistics.
     */
    @GetMapping("/stats")
    public ResponseEntity<SaleOrderStatsDTO> getOrderStatistics() {
        LOG.debug("REST request to get order statistics");
        SaleOrderStatsDTO stats = saleOrderService.getOrderStatistics();
        return ResponseEntity.ok(stats);
    }
}
