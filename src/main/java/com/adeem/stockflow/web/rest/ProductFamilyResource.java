package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.config.Constants;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.ProductFamily;
import com.adeem.stockflow.repository.ProductFamilyRepository;
import com.adeem.stockflow.repository.projection.ProductFamilyStatsDTO;
import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.ProductFamilyService;
import com.adeem.stockflow.service.ProductService;
import com.adeem.stockflow.service.criteria.ProductFamilySpecification;
import com.adeem.stockflow.service.criteria.ProductSpecification;
import com.adeem.stockflow.service.criteria.filter.ProductFamilyCriteria;
import com.adeem.stockflow.service.dto.ProductDTO;
import com.adeem.stockflow.service.dto.ProductFamilyDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adeem.stockflow.domain.ProductFamily}.
 * Enhanced with multi-tenant security and comprehensive family management.
 */
@RestController
@RequestMapping("/api/product-families")
public class ProductFamilyResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductFamilyResource.class);

    private static final String ENTITY_NAME = "productFamily";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductFamilyService productFamilyService;
    private final ProductService productService;
    private final ProductFamilyRepository productFamilyRepository;

    public ProductFamilyResource(
        ProductFamilyService productFamilyService,
        ProductService productService,
        ProductFamilyRepository productFamilyRepository
    ) {
        this.productFamilyService = productFamilyService;
        this.productService = productService;
        this.productFamilyRepository = productFamilyRepository;
    }

    /**
     * {@code POST  /product-families} : Create a new productFamily.
     *
     * @param productFamilyDTO the productFamilyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productFamilyDTO,
     *         or with status {@code 400 (Bad Request)} if the productFamily has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductFamilyDTO> createProductFamily(@Valid @RequestBody ProductFamilyDTO productFamilyDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProductFamily : {}", productFamilyDTO);

        if (productFamilyDTO.getId() != null) {
            throw new BadRequestAlertException("A new productFamily cannot already have an ID", ENTITY_NAME, ErrorConstants.ID_EXISTS);
        }

        // Set client account from security context
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
        productFamilyDTO.setClientAccountId(clientAccountId);

        // Validate family name uniqueness for this client account
        if (productFamilyService.existsByNameAndClientAccount(productFamilyDTO.getName(), clientAccountId)) {
            throw new BadRequestAlertException(
                "A product family with this name already exists",
                ENTITY_NAME,
                ErrorConstants.PRODUCT_FAMILY_NAME_EXISTS
            );
        }

        ProductFamilyDTO result = productFamilyService.save(productFamilyDTO);

        return ResponseEntity.created(new URI("/api/product-families/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /product-families/:id} : Updates an existing productFamily.
     *
     * @param id the id of the productFamilyDTO to save.
     * @param productFamilyDTO the productFamilyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productFamilyDTO,
     *         or with status {@code 400 (Bad Request)} if the productFamilyDTO is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the productFamilyDTO couldn't be updated.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductFamilyDTO> updateProductFamily(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductFamilyDTO productFamilyDTO
    ) {
        LOG.debug("REST request to update ProductFamily : {}, {}", id, productFamilyDTO);

        if (productFamilyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, ErrorConstants.ID_NULL);
        }
        if (!Objects.equals(id, productFamilyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, ErrorConstants.ID_INVALID);
        }

        // Get current client account
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Verify the family belongs to the current client account
        productFamilyService
            .findOneForClientAccount(id, clientAccountId)
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        // Set client account ID to ensure it doesn't change
        productFamilyDTO.setClientAccountId(clientAccountId);

        // Validate family name uniqueness (excluding current family)
        if (productFamilyService.existsByNameAndClientAccountExcluding(productFamilyDTO.getName(), clientAccountId, id)) {
            throw new BadRequestAlertException(
                "A product family with this name already exists",
                ENTITY_NAME,
                ErrorConstants.PRODUCT_FAMILY_NAME_EXISTS
            );
        }

        ProductFamilyDTO result = productFamilyService.update(productFamilyDTO);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productFamilyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /product-families} : get all the productFamilies for the current client account.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productFamilies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProductFamilyDTO>> getAllProductFamilies(
        ProductFamilyCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ProductFamilies by criteria: {}", criteria);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Apply client account filter to criteria
        Specification<ProductFamily> specification = ProductFamilySpecification.createSpecification(criteria).and(
            ProductFamilySpecification.withClientAccountId(clientAccountId)
        );

        Page<ProductFamilyDTO> page = productFamilyService.findAll(specification, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-families/statistics} : get overall product family statistics.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the statistics in body.
     */
    @GetMapping("/statistics")
    public ResponseEntity<ProductFamilyStatsDTO> getProductFamilyStatistics() {
        LOG.debug("REST request to get ProductFamily statistics");

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        ProductFamilyStatsDTO stats = productFamilyService.getStatistics(clientAccountId);
        return ResponseEntity.ok().body(stats);
    }

    /**
     * {@code GET  /product-families/:id} : get the "id" productFamily.
     *
     * @param id the id of the productFamilyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productFamilyDTO,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductFamilyDTO> getProductFamily(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductFamily : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Optional<ProductFamilyDTO> productFamilyDTO = productFamilyService.findOneForClientAccount(id, clientAccountId);
        return ResponseUtil.wrapOrNotFound(productFamilyDTO);
    }

    /**
     * {@code GET  /product-families/:id/statistics} : get statistics for a specific family.
     *
     * @param id the id of the productFamily to get statistics for.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statistics,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}/statistics")
    public ResponseEntity<ProductFamilyStatsDTO.FamilyDetailStatsDTO> getProductFamilyDetailStats(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductFamily statistics : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Verify the family belongs to the current client account
        ProductFamily family = productFamilyService
            .findEntityForClientAccount(id, clientAccountId)
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        ProductFamilyStatsDTO.FamilyDetailStatsDTO stats = productFamilyService.getFamilyDetailStats(family, clientAccountId);
        return ResponseEntity.ok().body(stats);
    }

    /**
     * {@code GET  /product-families/:id/products} : get all products in the specified family.
     *
     * @param id the id of the productFamily to get products for.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body.
     */
    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductDTO>> getProductFamilyProducts(
        @PathVariable("id") Long id,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Products for ProductFamily : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Verify the family belongs to the current client account
        Optional<ProductFamilyDTO> family = productFamilyService.findOneForClientAccount(id, clientAccountId);
        if (family.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Page<ProductDTO> page = productService.findAll(
            ProductSpecification.withProductFamilyId(id).and(ProductSpecification.withClientAccountId(clientAccountId)),
            pageable
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code POST  /product-families/:id/products/:productId} : assign a product to a family.
     *
     * @param id the id of the productFamily.
     * @param productIds the id of the product to assign.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productDTO.
     */
    @PostMapping("/{id}/assign")
    public ResponseEntity<Map<String, Integer>> assignProductsToFamily(
        @PathVariable("id") Long id,
        @RequestParam("productIds") List<Long> productIds
    ) {
        LOG.debug("REST request to assign Products {} to ProductFamily {}", productIds, id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Verify both family and product belong to the current client account
        ProductFamily family = productFamilyService
            .findEntityForClientAccount(id, clientAccountId)
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        Integer rowsAffected = productFamilyService.assignProductToFamily(clientAccountId, family, productIds);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "Products assigned to family successfully", id.toString()))
            .body(Map.of("assignedCount", rowsAffected));
    }

    /**
     * {@code DELETE  /product-families/:id/products/:productId} : remove a product from a family.
     *
     * @param id the id of the productFamily.
     * @param productIds the id of the product to remove.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productDTO.
     */
    @DeleteMapping("/{id}/unassign")
    public ResponseEntity<Map<String, Integer>> removeProductFromFamily(
        @PathVariable("id") Long id,
        @RequestParam("productIds") List<Long> productIds
    ) {
        LOG.debug("REST request to remove Product {} from ProductFamily {}", productIds, id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Verify both family and product belong to the current client account
        ProductFamily family = productFamilyService
            .findEntityForClientAccount(id, clientAccountId)
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        Integer rowsAffected = productFamilyService.removeProductFromFamily(clientAccountId, family, productIds);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "Products assigned to family successfully", id.toString()))
            .body(Map.of("unassignedCount", rowsAffected));
    }

    /**
     * {@code DELETE  /product-families/:id} : delete the "id" productFamily.
     *
     * @param id the id of the productFamilyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductFamily(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductFamily : {}", id);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Verify the family belongs to the current client account
        Optional<ProductFamilyDTO> family = productFamilyService.findOneForClientAccount(id, clientAccountId);
        if (family.isEmpty()) {
            throw new BadRequestAlertException("ProductFamily not found or access denied", ENTITY_NAME, ErrorConstants.NOT_FOUND);
        }

        // Check if family has products
        if (productFamilyService.hasProducts(id)) {
            throw new BadRequestAlertException(
                "Cannot delete product family that contains products. Please reassign or remove all products first.",
                ENTITY_NAME,
                ErrorConstants.PRODUCT_FAMILY_HAS_PRODUCTS
            );
        }

        productFamilyService.delete(id);

        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /product-families/search} : search product families.
     *
     * @param query the search query.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of matching families in body.
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductFamilyDTO>> searchProductFamilies(
        @RequestParam("q") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search ProductFamilies with query: {}", query);

        // Get current client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Page<ProductFamilyDTO> page = productFamilyService.searchByNameAndClientAccount(query, clientAccountId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
