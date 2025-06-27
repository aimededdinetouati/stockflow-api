package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.domain.enumeration.ProductCategory;
import com.adeem.stockflow.service.MarketplaceProductService;
import com.adeem.stockflow.service.dto.CategoryStatsDTO;
import com.adeem.stockflow.service.dto.MarketplaceProductDTO;
import com.adeem.stockflow.service.dto.MarketplaceProductDetailDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing public marketplace product catalog.
 * Provides anonymous access to product browsing functionality.
 */
@RestController
@RequestMapping("/api/public/products")
public class PublicProductResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicProductResource.class);

    private final MarketplaceProductService marketplaceProductService;

    public PublicProductResource(MarketplaceProductService marketplaceProductService) {
        this.marketplaceProductService = marketplaceProductService;
    }

    /**
     * {@code GET  /api/public/products} : Browse marketplace products with optional filters.
     *
     * @param search optional search term for product name/description
     * @param category optional category filter
     * @param minPrice optional minimum price filter
     * @param maxPrice optional maximum price filter
     * @param companyName optional company name filter
     * @param availableOnly if true, only return products with available inventory
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body
     */
    @GetMapping
    public ResponseEntity<List<MarketplaceProductDTO>> findMarketplaceProducts(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) ProductCategory category,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) String companyName,
        @RequestParam(required = false, defaultValue = "false") Boolean availableOnly,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug(
            "REST request to get marketplace products - search: {}, category: {}, minPrice: {}, maxPrice: {}, company: {}, availableOnly: {}",
            search,
            category,
            minPrice,
            maxPrice,
            companyName,
            availableOnly
        );

        Page<MarketplaceProductDTO> page = marketplaceProductService.findMarketplaceProducts(
            search,
            category,
            minPrice,
            maxPrice,
            companyName,
            availableOnly,
            pageable
        );

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /api/public/products/search} : Search products by text.
     * Alternative endpoint specifically for search functionality.
     *
     * @param q the search query
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body
     */
    @GetMapping("/search")
    public ResponseEntity<List<MarketplaceProductDTO>> searchProducts(
        @RequestParam String q,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search products: {}", q);

        Page<MarketplaceProductDTO> page = marketplaceProductService.searchProducts(q, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /api/public/products/{id}} : Get product detail for marketplace.
     *
     * @param id the id of the product to retrieve
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productDetailDTO,
     *         or with status {@code 404 (Not Found)}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MarketplaceProductDetailDTO> getProductDetail(@PathVariable Long id) {
        LOG.debug("REST request to get marketplace product detail : {}", id);

        Optional<MarketplaceProductDetailDTO> productDetailDTO = marketplaceProductService.findMarketplaceProductDetail(id);
        return ResponseUtil.wrapOrNotFound(productDetailDTO);
    }

    /**
     * {@code GET  /api/public/products/categories} : Get product categories with counts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of category statistics
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryStatsDTO>> getProductCategories() {
        LOG.debug("REST request to get product categories with counts");

        List<CategoryStatsDTO> categories = marketplaceProductService.getProductCategoriesWithCounts();
        return ResponseEntity.ok().body(categories);
    }

    /**
     * {@code GET  /api/public/products/company/{companyId}} : Get products from a specific company.
     *
     * @param companyId the company ID
     * @param excludeProductId optional product ID to exclude from results
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<MarketplaceProductDTO>> getProductsByCompany(
        @PathVariable Long companyId,
        @RequestParam(required = false) Long excludeProductId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get products by company: {}, excluding: {}", companyId, excludeProductId);

        Page<MarketplaceProductDTO> page = marketplaceProductService.findProductsByCompany(companyId, excludeProductId, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /api/public/products/related} : Get related products by category.
     *
     * @param category the product category
     * @param excludeProductId optional product ID to exclude from results
     * @param limit maximum number of products to return (default 6)
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of related products in body
     */
    @GetMapping("/related")
    public ResponseEntity<List<MarketplaceProductDTO>> getRelatedProducts(
        @RequestParam ProductCategory category,
        @RequestParam(required = false) Long excludeProductId,
        @RequestParam(defaultValue = "6") int limit
    ) {
        LOG.debug("REST request to get related products by category: {}, excluding: {}, limit: {}", category, excludeProductId, limit);

        List<MarketplaceProductDTO> relatedProducts = marketplaceProductService.findRelatedProductsByCategory(
            category,
            excludeProductId,
            limit
        );

        return ResponseEntity.ok().body(relatedProducts);
    }

    /**
     * {@code GET  /api/public/products/categories/{category}} : Get products by specific category.
     *
     * @param category the product category
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body
     */
    @GetMapping("/categories/{category}")
    public ResponseEntity<List<MarketplaceProductDTO>> getProductsByCategory(
        @PathVariable ProductCategory category,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get products by category: {}", category);

        Page<MarketplaceProductDTO> page = marketplaceProductService.findMarketplaceProducts(
            null, // search
            category, // category
            null, // minPrice
            null, // maxPrice
            null, // companyName
            true, // availableOnly
            pageable
        );

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
