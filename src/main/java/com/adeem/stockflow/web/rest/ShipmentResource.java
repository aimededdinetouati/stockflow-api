package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.domain.Shipment;
import com.adeem.stockflow.domain.enumeration.ShippingStatus;
import com.adeem.stockflow.repository.ShipmentRepository;
import com.adeem.stockflow.security.AuthoritiesConstants;
import com.adeem.stockflow.service.ShipmentService;
import com.adeem.stockflow.service.criteria.ShipmentSpecification;
import com.adeem.stockflow.service.criteria.filter.ShipmentCriteria;
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
 * REST controller for managing {@link Shipment}.
 * Enhanced with Yalidine integration and flexible carrier management.
 */
@RestController
@RequestMapping("/api/shipments")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.USER_ADMIN + "')")
public class ShipmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentResource.class);

    private static final String ENTITY_NAME = "shipment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShipmentService shipmentService;
    private final ShipmentRepository shipmentRepository;

    public ShipmentResource(ShipmentService shipmentService, ShipmentRepository shipmentRepository) {
        this.shipmentService = shipmentService;
        this.shipmentRepository = shipmentRepository;
    }

    /**
     * {@code POST  /shipments/create-for-order/:orderId} : Create a shipment for a specific order.
     *
     * @param orderId the order ID.
     * @param createShipmentDTO the shipment creation details.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shipmentDTO.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/create-for-order/{orderId}")
    public ResponseEntity<ShipmentDTO> createShipmentForOrder(
        @PathVariable("orderId") Long orderId,
        @Valid @RequestBody ShipmentRequestDTO createShipmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to create Shipment for Order : {} with carrier: {}", orderId, createShipmentDTO.getCarrier());

        ShipmentDTO shipmentDTO = shipmentService.createShipmentForOrder(orderId, createShipmentDTO);
        return ResponseEntity.created(new URI("/api/shipments/" + shipmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, shipmentDTO.getId().toString()))
            .body(shipmentDTO);
    }

    /**
     * {@code PUT  /shipments/:id} : Updates an existing shipment.
     *
     * @param id the id of the shipmentDTO to save.
     * @param shipmentDTO the shipmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shipmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShipmentDTO> updateShipment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShipmentRequestDTO shipmentRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Shipment : {}, {}", id, shipmentRequestDTO);
        if (shipmentRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ShipmentDTO shipmentDTO = shipmentService.update(shipmentRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shipmentDTO.getId().toString()))
            .body(shipmentDTO);
    }

    /**
     * {@code GET  /shipments} : get all the shipments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shipments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ShipmentDTO>> getAllShipments(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ShipmentCriteria criteria
    ) {
        LOG.debug("REST request to get Shipments by criteria: {}", criteria);

        //Specification<Shipment> specification = new createSpecification(criteria);
        Page<ShipmentDTO> page = shipmentService.findAllWithCriteria(pageable, null);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shipments/:id} : get the "id" shipment.
     *
     * @param id the id of the shipmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shipmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentDTO> getShipment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Shipment : {}", id);
        Optional<ShipmentDTO> shipmentDTO = shipmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shipmentDTO);
    }

    /**
     * {@code DELETE  /shipments/:id} : delete the "id" shipment.
     *
     * @param id the id of the shipmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Shipment : {}", id);
        shipmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code PUT  /shipments/:id/status} : Update shipment status.
     *
     * @param id the id of the shipment to update.
     * @param request the status update request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentDTO.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ShipmentDTO> updateShipmentStatus(
        @PathVariable("id") Long id,
        @Valid @RequestBody UpdateShipmentStatusDTO request
    ) {
        LOG.debug("REST request to update Shipment status : {} to {}", id, request.getStatus());
        ShipmentDTO result = shipmentService.updateShipmentStatus(id, request);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "Shipment status updated successfully", id.toString()))
            .body(result);
    }

    //    /**
    //     * {@code GET  /shipments/:id/tracking} : Get shipment tracking information.
    //     *
    //     * @param id the id of the shipment.
    //     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tracking information.
    //     */
    //    @GetMapping("/{id}/tracking")
    //    public ResponseEntity<ShipmentTrackingDTO> getShipmentTracking(@PathVariable("id") Long id) {
    //        LOG.debug("REST request to get Shipment tracking : {}", id);
    //        ShipmentTrackingDTO tracking = shipmentService.getShipmentTracking(id);
    //        return ResponseEntity.ok(tracking);
    //    }
    //
    //    /**
    //     * {@code POST  /shipments/:id/sync-with-yalidine} : Sync shipment with Yalidine API.
    //     *
    //     * @param id the id of the shipment.
    //     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentDTO.
    //     */
    //    @PostMapping("/{id}/sync-with-yalidine")
    //    public ResponseEntity<ShipmentDTO> syncWithYalidine(@PathVariable("id") Long id) {
    //        LOG.debug("REST request to sync Shipment with Yalidine : {}", id);
    //        ShipmentDTO result = shipmentService.syncWithYalidine(id);
    //        return ResponseEntity.ok()
    //            .headers(HeaderUtil.createAlert(applicationName, "Shipment synced with Yalidine successfully", id.toString()))
    //            .body(result);
    //    }

    /**
     * {@code POST  /shipments/:id/cancel} : Cancel a shipment.
     *
     * @param id the id of the shipment to cancel.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentDTO.
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ShipmentDTO> cancelShipment(@PathVariable("id") Long id) {
        LOG.debug("REST request to cancel Shipment : {}", id);
        ShipmentDTO result = shipmentService.cancelShipment(id);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "Shipment cancelled successfully", id.toString()))
            .body(result);
    }

    /**
     * {@code GET  /shipments/search} : Search shipments with advanced criteria.
     *
     * @param pageable the pagination information.
     * @param query the search query.
     * @param status the shipment status.
     * @param carrier the carrier name.
     * @param trackingNumber the tracking number.
     * @param fromDate the from date.
     * @param toDate the to date.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shipments in body.
     */
    @GetMapping("/search")
    public ResponseEntity<List<ShipmentDTO>> searchShipments(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String carrier,
        @RequestParam(required = false) String trackingNumber,
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate
    ) {
        LOG.debug("REST request to search Shipments with query: {}", query);

        // Build specification based on parameters
        Specification<Shipment> spec = Specification.where(null);

        if (query != null && !query.trim().isEmpty()) {
            spec = spec.and(
                ShipmentSpecification.withReferenceContaining(query)
                    .or(ShipmentSpecification.withTrackingNumberContaining(query))
                    .or(ShipmentSpecification.withNotesContaining(query))
            );
        }

        if (status != null && !status.trim().isEmpty()) {
            try {
                spec = spec.and(ShipmentSpecification.withStatus(ShippingStatus.valueOf(status.toUpperCase())));
            } catch (IllegalArgumentException e) {
                throw new BadRequestAlertException("Invalid status value", ENTITY_NAME, "invalidStatus");
            }
        }

        if (carrier != null && !carrier.trim().isEmpty()) {
            spec = spec.and(ShipmentSpecification.withCarrierContaining(carrier));
        }

        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            spec = spec.and(ShipmentSpecification.withTrackingNumberContaining(trackingNumber));
        }

        if (fromDate != null && toDate != null) {
            try {
                java.time.Instant from = java.time.Instant.parse(fromDate);
                java.time.Instant to = java.time.Instant.parse(toDate);
                spec = spec.and(ShipmentSpecification.withShippingDateRange(from, to));
            } catch (java.time.format.DateTimeParseException e) {
                throw new BadRequestAlertException("Invalid date format", ENTITY_NAME, "invalidDateFormat");
            }
        }

        Page<ShipmentDTO> page = shipmentService.findAllWithCriteria(pageable, spec);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shipments/pending} : Get all pending shipments that need attention.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pending shipments in body.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ShipmentDTO>> getPendingShipments(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get pending Shipments");

        Specification<Shipment> spec = ShipmentSpecification.withStatus(ShippingStatus.PENDING);

        Page<ShipmentDTO> page = shipmentService.findAllWithCriteria(pageable, spec);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    //    /**
    //     * {@code GET  /shipments/yalidine} : Get all Yalidine shipments.
    //     *
    //     * @param pageable the pagination information.
    //     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Yalidine shipments in body.
    //     */
    //    @GetMapping("/yalidine")
    //    public ResponseEntity<List<ShipmentDTO>> getYalidineShipments(
    //        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    //    ) {
    //        LOG.debug("REST request to get Yalidine Shipments");
    //
    //        Specification<Shipment> spec = ShipmentSpecification.withYalidineShipments();
    //
    //        Page<ShipmentDTO> page = shipmentService.findAllWithCriteria(pageable, spec);
    //        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    //        return ResponseEntity.ok().headers(headers).body(page.getContent());
    //    }
}
