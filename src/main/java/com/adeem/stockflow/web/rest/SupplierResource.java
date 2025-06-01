package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.config.Constants;
import com.adeem.stockflow.domain.Supplier;
import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.SupplierService;
import com.adeem.stockflow.service.criteria.SupplierSpecification;
import com.adeem.stockflow.service.criteria.filter.SupplierCriteria;
import com.adeem.stockflow.service.dto.*;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adeem.stockflow.domain.Supplier}.
 * Enhanced with multi-tenant security and comprehensive supplier management.
 */
@RestController
@RequestMapping("/api/suppliers")
public class SupplierResource {

    private static final Logger LOG = LoggerFactory.getLogger(SupplierResource.class);

    private static final String ENTITY_NAME = "supplier";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SupplierService supplierService;

    public SupplierResource(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    /**
     * {@code POST  /suppliers} : Create a new supplier.
     *
     * @param supplierDTO the supplier with address data to create
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new supplierDTO,
     *         or with status {@code 400 (Bad Request)} if the supplier has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) throws URISyntaxException {
        LOG.debug("REST supplierDTO to save Supplier : {}", supplierDTO);

        AddressDTO addressDTO = supplierDTO.getAddress();

        if (supplierDTO.getId() != null) {
            throw new BadRequestAlertException("A new supplier cannot already have an ID", ENTITY_NAME, ErrorConstants.ID_EXISTS);
        }

        // Set client account from security context
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
        supplierDTO.setClientAccountId(clientAccountId);

        SupplierDTO result = supplierService.create(supplierDTO, addressDTO);

        return ResponseEntity.created(new URI("/api/suppliers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /suppliers/:id} : Updates an existing supplier.
     *
     * @param id the id of the supplierDTO to save
     * @param supplierDTO the supplier with address data to update
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplierDTO,
     *         or with status {@code 400 (Bad Request)} if the supplierDTO is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the supplierDTO couldn't be updated.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SupplierDTO supplierDTO
    ) {
        LOG.debug("REST request to update Supplier : {}, {}", id, supplierDTO);

        AddressDTO addressDTO = supplierDTO.getAddress();

        if (supplierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, ErrorConstants.ID_NULL);
        }
        if (!Objects.equals(id, supplierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, ErrorConstants.ID_INVALID);
        }

        // Get current client account
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Set client account ID to ensure it doesn't change
        supplierDTO.setClientAccountId(clientAccountId);

        SupplierDTO result = supplierService.update(supplierDTO, addressDTO);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supplierDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /suppliers} : get all the suppliers for the current client account.
     *
     * @param pageable the pagination information
     * @param criteria the criteria which the requested entities should match
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of suppliers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers(
        SupplierCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Suppliers by criteria: {}", criteria);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Apply client account filter to criteria
        Specification<Supplier> specification = SupplierSpecification.createSpecification(criteria)
            .and(SupplierSpecification.withClientAccountId(clientAccountId))
            .and(SupplierSpecification.withActive(true)); // Only return active suppliers by default

        Page<SupplierDTO> page = supplierService.findAll(specification, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /suppliers/all} : get all suppliers including inactive ones.
     *
     * @param pageable the pagination information
     * @param criteria the criteria which the requested entities should match
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of suppliers in body.
     */
    @GetMapping("/all")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliersIncludingInactive(
        SupplierCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get all Suppliers including inactive by criteria: {}", criteria);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Apply client account filter to criteria (include inactive)
        Specification<Supplier> specification = SupplierSpecification.createSpecification(criteria).and(
            SupplierSpecification.withClientAccountId(clientAccountId)
        );

        Page<SupplierDTO> page = supplierService.findAll(specification, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /suppliers/count} : count all the suppliers.
     *
     * @param criteria the criteria which the requested entities should match
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSuppliers(SupplierCriteria criteria) {
        LOG.debug("REST request to count Suppliers by criteria: {}", criteria);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Apply client account filter to criteria
        Specification<Supplier> specification = SupplierSpecification.createSpecification(criteria)
            .and(SupplierSpecification.withClientAccountId(clientAccountId))
            .and(SupplierSpecification.withActive(true));

        long count = supplierService.countByCriteria(specification);
        return ResponseEntity.ok().body(count);
    }

    /**
     * {@code GET  /suppliers/:id} : get the "id" supplier.
     *
     * @param id the id of the supplierDTO to retrieve
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the supplierDTO,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplier(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Supplier : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Optional<SupplierDTO> supplierWithAddress = supplierService.findOneForClientAccount(id, clientAccountId);
        return ResponseUtil.wrapOrNotFound(supplierWithAddress);
    }

    /**
     * {@code DELETE  /suppliers/:id} : delete the "id" supplier (soft delete).
     *
     * @param id the id of the supplierDTO to delete
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Supplier : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Verify the supplier belongs to the current client account
        supplierService
            .findOneForClientAccount(id, clientAccountId)
            .orElseThrow(() -> new BadRequestAlertException("Supplier not found or access denied", ENTITY_NAME, ErrorConstants.NOT_FOUND));

        supplierService.softDelete(id, clientAccountId);

        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code POST  /suppliers/:id/reactivate} : reactivate a soft-deleted supplier.
     *
     * @param id the id of the supplier to reactivate
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @PostMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivateSupplier(@PathVariable("id") Long id) {
        LOG.debug("REST request to reactivate Supplier : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        supplierService.reactivate(id, clientAccountId);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "Supplier reactivated successfully", id.toString()))
            .build();
    }

    /**
     * {@code GET  /suppliers/search} : search suppliers.
     *
     * @param query the search query
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of matching suppliers in body.
     */
    @GetMapping("/search")
    public ResponseEntity<List<SupplierDTO>> searchSuppliers(
        @RequestParam("q") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search Suppliers with query: {}", query);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Page<SupplierDTO> page = supplierService.searchSuppliers(query, clientAccountId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /suppliers/statistics} : get supplier statistics.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the statistics in body.
     */
    @GetMapping("/statistics")
    public ResponseEntity<SupplierStatsDTO> getSupplierStatistics() {
        LOG.debug("REST request to get Supplier statistics");

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        SupplierStatsDTO stats = supplierService.getSupplierStatistics(clientAccountId);
        return ResponseEntity.ok().body(stats);
    }

    /**
     * {@code GET  /suppliers/:id/purchase-orders} : get supplier's purchase orders.
     *
     * @param id the supplier ID
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of purchase orders in body.
     */
    @GetMapping("/{id}/purchase-orders")
    public ResponseEntity<List<PurchaseOrderDTO>> getSupplierPurchaseOrders(
        @PathVariable("id") Long id,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Purchase Orders for Supplier : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Verify the supplier belongs to the current client account
        supplierService
            .findOneForClientAccount(id, clientAccountId)
            .orElseThrow(() -> new BadRequestAlertException("Supplier not found or access denied", ENTITY_NAME, ErrorConstants.NOT_FOUND));

        // TODO: Implement purchase order service integration
        // This will be implemented when PurchaseOrderService is available
        // For now, return empty list
        List<PurchaseOrderDTO> purchaseOrders = List.of();

        return ResponseEntity.ok().body(purchaseOrders);
    }

    /**
     * {@code GET  /suppliers/check-email} : check if email exists.
     *
     * @param email the email to check
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and boolean result.
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam("email") String email) {
        LOG.debug("REST request to check if email exists: {}", email);

        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
        boolean exists = supplierService.existsByEmail(email, clientAccountId);

        return ResponseEntity.ok().body(exists);
    }

    /**
     * {@code GET  /suppliers/check-phone} : check if phone exists.
     *
     * @param phone the phone to check
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and boolean result.
     */
    @GetMapping("/check-phone")
    public ResponseEntity<Boolean> checkPhoneExists(@RequestParam("phone") String phone) {
        LOG.debug("REST request to check if phone exists: {}", phone);

        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
        boolean exists = supplierService.existsByPhone(phone, clientAccountId);

        return ResponseEntity.ok().body(exists);
    }

    /**
     * {@code GET  /suppliers/check-tax-id} : check if tax ID exists.
     *
     * @param taxId the tax ID to check
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and boolean result.
     */
    @GetMapping("/check-tax-id")
    public ResponseEntity<Boolean> checkTaxIdExists(@RequestParam("taxId") String taxId) {
        LOG.debug("REST request to check if tax ID exists: {}", taxId);

        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
        boolean exists = supplierService.existsByTaxId(taxId, clientAccountId);

        return ResponseEntity.ok().body(exists);
    }
}
