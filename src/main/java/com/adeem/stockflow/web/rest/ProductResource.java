package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.config.Constants;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.repository.ProductRepository;
import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.InventoryService;
import com.adeem.stockflow.service.ProductService;
import com.adeem.stockflow.service.criteria.InventorySpecification;
import com.adeem.stockflow.service.criteria.ProductSpecification;
import com.adeem.stockflow.service.criteria.filter.ProductCriteria;
import com.adeem.stockflow.service.dto.BulkOperationResult;
import com.adeem.stockflow.service.dto.InventoryDTO;
import com.adeem.stockflow.service.dto.ProductDTO;
import com.adeem.stockflow.service.dto.ProductWithInventoryDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adeem.stockflow.domain.Product}.
 */
@RestController
@RequestMapping("/api/products")
public class ProductResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductResource.class);

    private static final String ENTITY_NAME = "product";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductService productService;

    private final InventoryService inventoryService;

    public ProductResource(ProductService productService, InventoryService inventoryService) {
        this.productService = productService;
        this.inventoryService = inventoryService;
    }

    /**
     * {@code POST  /products} : Create a new product.
     *
     * @param productWithInventoryDTO the productWithInventoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productDTO, or with status {@code 400 (Bad Request)} if the product has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductDTO> createProduct(
        @Valid @RequestPart ProductWithInventoryDTO productWithInventoryDTO,
        @RequestPart(value = "productImage", required = false) List<MultipartFile> images
    ) throws URISyntaxException, IOException {
        LOG.debug("REST request to save Product : {}", productWithInventoryDTO);
        ProductDTO productDTO = productWithInventoryDTO.getProduct();
        InventoryDTO inventoryDTO = productWithInventoryDTO.getInventory();
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
        productDTO.setClientAccountId(clientAccountId);
        productDTO = productService.create(productDTO, inventoryDTO, images);
        return ResponseEntity.created(new URI("/api/products/" + productDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, productDTO.getId().toString()))
            .body(productDTO);
    }

    /**
     * {@code PUT  /products/:id} : Updates an existing product.
     *
     * @param id the id of the productDTO to save.
     * @param productWithInventoryDTO the productWithInventoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productDTO,
     * or with status {@code 400 (Bad Request)} if the productDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestPart ProductWithInventoryDTO productWithInventoryDTO,
        @RequestPart(value = "productImage", required = false) List<MultipartFile> images
    ) throws URISyntaxException, IOException {
        LOG.debug("REST request to update Product : {}, {}", id, productWithInventoryDTO);
        ProductDTO productDTO = productWithInventoryDTO.getProduct();
        InventoryDTO inventoryDTO = productWithInventoryDTO.getInventory();
        if (productDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        // Set the current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
        productDTO.setClientAccountId(clientAccountId);

        productDTO = productService.update(productDTO, inventoryDTO, images);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productDTO.getId().toString()))
            .body(productDTO);
    }

    /**
     * {@code GET  /products} : get all the products.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProductDTO>> getAllProducts(
        ProductCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Products by criteria: {}", criteria);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Apply client account filter to criteria
        Specification<Product> specification = ProductSpecification.createSpecification(criteria).and(
            ProductSpecification.withClientAccountId(clientAccountId)
        );

        Page<ProductDTO> page = productService.findAll(specification, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /products/count} : count all the products.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countProducts(ProductCriteria criteria) {
        LOG.debug("REST request to count Products by criteria: {}", criteria);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Apply client account filter to criteria
        Specification<com.adeem.stockflow.domain.Product> specification = ProductSpecification.createSpecification(criteria).and(
            ProductSpecification.withClientAccountId(clientAccountId)
        );

        long count = productService.countByCriteria(specification);
        return ResponseEntity.ok().body(count);
    }

    /**
     * {@code GET  /products/:id} : get the "id" product with inventory information.
     *
     * @param id the id of the productDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductWithInventoryDTO> getProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Product : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Get product and verify it belongs to the current client account
        ProductDTO productDTO = productService
            .findOneForClientAccount(id, clientAccountId)
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        // Get inventory information for the product
        Optional<InventoryDTO> inventoryDTO = inventoryService.findOne(InventorySpecification.withProductId(id));

        // Combine product and inventory information
        ProductWithInventoryDTO result = new ProductWithInventoryDTO();
        result.setProduct(productDTO);
        inventoryDTO.ifPresent(result::setInventory);

        return ResponseEntity.ok(result);
    }

    /**
     * {@code DELETE  /products/:id} : delete the "id" product.
     *
     * @param id the id of the productDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Product : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Verify product exists and belongs to the current client account
        Optional<ProductDTO> productDTO = productService.findOneForClientAccount(id, clientAccountId);

        if (productDTO.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Delete the product (service will handle related inventory records)
        productService.delete(id);

        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    // Add these new endpoints to your ProductResource.java class

    /**
     * {@code DELETE  /products/bulk} : delete multiple products by list of IDs.
     *
     * @param productIds the list of product IDs to delete.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body containing deletion summary.
     */
    @DeleteMapping("/bulk")
    public ResponseEntity<Map<String, Object>> deleteProductsBulk(@RequestBody List<Long> productIds) {
        LOG.debug("REST request to delete Products with IDs: {}", productIds);

        if (productIds == null || productIds.isEmpty()) {
            throw new BadRequestAlertException("Product IDs list cannot be empty", ENTITY_NAME, "emptylist");
        }

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Perform bulk deletion
        BulkOperationResult result = productService.deleteBulk(productIds, clientAccountId);

        Map<String, Object> response = Map.of(
            "deletedCount",
            result.getSuccessCount(),
            "failedCount",
            result.getFailedCount(),
            "totalRequested",
            productIds.size(),
            "failedIds",
            result.getFailedIds()
        );

        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createAlert(
                    applicationName,
                    String.format("Bulk deletion completed: %d deleted, %d failed", result.getSuccessCount(), result.getFailedCount()),
                    "bulk-delete"
                )
            )
            .body(response);
    }

    /**
     * {@code PATCH  /products/bulk/toggle-visibility} : toggle isVisibleToCustomers for multiple products.
     *
     * @param productIds the list of product IDs to toggle visibility.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body containing toggle summary.
     */
    @PatchMapping("/bulk/toggle-visibility")
    public ResponseEntity<Map<String, Object>> toggleProductsVisibilityBulk(@RequestBody List<Long> productIds) {
        LOG.debug("REST request to toggle visibility for Products with IDs: {}", productIds);

        if (productIds == null || productIds.isEmpty()) {
            throw new BadRequestAlertException("Product IDs list cannot be empty", ENTITY_NAME, "emptylist");
        }

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Perform bulk visibility toggle
        BulkOperationResult result = productService.toggleVisibilityBulk(productIds, clientAccountId);

        Map<String, Object> response = Map.of(
            "updatedCount",
            result.getSuccessCount(),
            "failedCount",
            result.getFailedCount(),
            "totalRequested",
            productIds.size(),
            "failedIds",
            result.getFailedIds(),
            "updatedProducts",
            result.getUpdatedEntities()
        );

        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createAlert(
                    applicationName,
                    String.format(
                        "Bulk visibility toggle completed: %d updated, %d failed",
                        result.getSuccessCount(),
                        result.getFailedCount()
                    ),
                    "bulk-toggle-visibility"
                )
            )
            .body(response);
    }

    /**
     * {@code GET  /products/low-stock} : get all products with inventory below minimum stock level.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body.
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductWithInventoryDTO>> getLowStockProducts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get low stock Products");

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Page<ProductWithInventoryDTO> page = productService.findLowStockProducts(clientAccountId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
