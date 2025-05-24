package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.domain.enumeration.AdjustmentType;
import com.adeem.stockflow.repository.InventoryRepository;
import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.InventoryService;
import com.adeem.stockflow.service.criteria.InventorySpecification;
import com.adeem.stockflow.service.criteria.ProductSpecification;
import com.adeem.stockflow.service.criteria.filter.InventoryCriteria;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adeem.stockflow.domain.Inventory}.
 */
@RestController
@RequestMapping("/api/inventories")
public class InventoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryResource.class);

    private static final String ENTITY_NAME = "inventory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InventoryService inventoryService;

    private final InventoryRepository inventoryRepository;

    public InventoryResource(InventoryService inventoryService, InventoryRepository inventoryRepository) {
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * {@code POST  /inventories} : Create a new inventory.
     *
     * @param inventoryDTO the inventoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new inventoryDTO, or with status {@code 400 (Bad Request)} if the inventory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InventoryDTO> createInventory(@Valid @RequestBody InventoryDTO inventoryDTO) throws URISyntaxException {
        LOG.debug("REST request to save Inventory : {}", inventoryDTO);
        if (inventoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new inventory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        inventoryDTO = inventoryService.save(inventoryDTO);
        return ResponseEntity.created(new URI("/api/inventories/" + inventoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, inventoryDTO.getId().toString()))
            .body(inventoryDTO);
    }

    /**
     * {@code PUT  /inventory/:id} : Updates an existing inventory record.
     *
     * @param id the id of the inventoryDTO to save.
     * @param inventoryDTO the inventoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inventoryDTO,
     *         or with status {@code 400 (Bad Request)} if the inventoryDTO is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the inventoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InventoryDTO> updateInventory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InventoryDTO inventoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Inventory : {}, {}", id, inventoryDTO);

        if (inventoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inventoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        // Verify inventory belongs to current client account
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
        Optional<InventoryDTO> existingInventory = inventoryService.findOneForClientAccount(id, clientAccountId);
        if (existingInventory.isEmpty()) {
            throw new BadRequestAlertException("Inventory not found or access denied", ENTITY_NAME, "notfound");
        }

        inventoryDTO.setClientAccountId(clientAccountId);
        InventoryDTO result = inventoryService.update(inventoryDTO);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inventoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /inventory} : get all inventory records for the current client account.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of inventory records in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InventoryWithProductDTO>> getAllInventory(
        InventoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Inventory by criteria: {}", criteria);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Apply client account filter to criteria
        Specification<Inventory> specification = InventorySpecification.createSpecification(criteria).and(
            InventorySpecification.withClientAccountId(clientAccountId)
        );

        Page<InventoryWithProductDTO> page = inventoryService.findAllWithProduct(specification, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /inventory/count} : count all inventory records.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countInventory(InventoryCriteria criteria) {
        LOG.debug("REST request to count Inventory by criteria: {}", criteria);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Apply client account filter to criteria
        Specification<Inventory> specification = InventorySpecification.createSpecification(criteria).and(
            InventorySpecification.withClientAccountId(clientAccountId)
        );

        long count = inventoryService.countByCriteria(specification);
        return ResponseEntity.ok().body(count);
    }

    /**
     * {@code GET  /inventory/:id} : get the "id" inventory record.
     *
     * @param id the id of the inventoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inventoryDTO,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryWithProductDTO> getInventory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Inventory : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Optional<InventoryWithProductDTO> inventoryDTO = inventoryService.findOneWithProductForClientAccount(id, clientAccountId);
        return ResponseUtil.wrapOrNotFound(inventoryDTO);
    }

    /**
     * {@code GET  /inventory/product/:productId} : get inventory for a specific product.
     *
     * @param productId the id of the product to get inventory for.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inventoryDTO,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryByProduct(@PathVariable("productId") Long productId) {
        LOG.debug("REST request to get Inventory for Product : {}", productId);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // First verify the product belongs to the client account
        Specification<com.adeem.stockflow.domain.Product> productSpec = ProductSpecification.withId(productId).and(
            ProductSpecification.withClientAccountId(clientAccountId)
        );

        Optional<InventoryDTO> inventoryDTO = inventoryService.findByProductForClientAccount(productId, clientAccountId);
        return ResponseUtil.wrapOrNotFound(inventoryDTO);
    }

    /**
     * {@code GET  /inventory/stats} : get inventory statistics for the current client account.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the stats in body.
     */
    @GetMapping("/stats")
    public ResponseEntity<InventoryStatsDTO> getInventoryStats() {
        LOG.debug("REST request to get Inventory stats");

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        InventoryStatsDTO stats = inventoryService.getInventoryStats(clientAccountId);
        return ResponseEntity.ok().body(stats);
    }

    /**
     * {@code GET  /inventory/low-stock} : get all inventory records with low stock levels.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of low stock inventory in body.
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryWithProductDTO>> getLowStockInventory(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get low stock Inventory");

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Page<InventoryWithProductDTO> page = inventoryService.findLowStockItems(clientAccountId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /inventory/out-of-stock} : get all inventory records that are out of stock.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of out of stock inventory in body.
     */
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<InventoryWithProductDTO>> getOutOfStockInventory(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get out of stock Inventory");

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Page<InventoryWithProductDTO> page = inventoryService.findOutOfStockItems(clientAccountId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code DELETE  /inventory/:id} : delete the "id" inventory record.
     *
     * @param id the id of the inventoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Inventory : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Verify inventory belongs to current client account
        Optional<InventoryDTO> existingInventory = inventoryService.findOneForClientAccount(id, clientAccountId);
        if (existingInventory.isEmpty()) {
            throw new BadRequestAlertException("Inventory not found or access denied", ENTITY_NAME, "notfound");
        }

        inventoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code POST  /inventory/{id}/adjust} : adjust inventory quantity.
     *
     * @param id the id of the inventory to adjust.
     * @param adjustmentRequest the adjustment details.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inventoryDTO.
     */
    @PostMapping("/{id}/adjust")
    public ResponseEntity<InventoryDTO> adjustInventory(
        @PathVariable("id") Long id,
        @Valid @RequestBody InventoryAdjustmentRequest adjustmentRequest
    ) {
        LOG.debug("REST request to adjust Inventory : {} with adjustment: {}", id, adjustmentRequest);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Verify inventory belongs to current client account
        Optional<InventoryDTO> existingInventory = inventoryService.findOneForClientAccount(id, clientAccountId);
        if (existingInventory.isEmpty()) {
            throw new BadRequestAlertException("Inventory not found or access denied", ENTITY_NAME, "notfound");
        }

        InventoryDTO result = inventoryService.adjustInventory(id, adjustmentRequest);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    /**
     * {@code GET  /inventory/{id}/history} : get inventory transaction history for a specific inventory item.
     *
     * @param id the id of the inventory item.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the transaction history in body.
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<InventoryTransactionDTO>> getInventoryHistory(
        @PathVariable("id") Long id,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Inventory history : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Verify inventory belongs to current client account
        Optional<InventoryDTO> existingInventory = inventoryService.findOneForClientAccount(id, clientAccountId);
        if (existingInventory.isEmpty()) {
            throw new BadRequestAlertException("Inventory not found or access denied", ENTITY_NAME, "notfound");
        }

        Page<InventoryTransactionDTO> page = inventoryService.getInventoryHistory(id, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
