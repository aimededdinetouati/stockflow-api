// File: src/main/java/com/adeem/stockflow/service/MarketplaceProductService.java
package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.enumeration.ProductCategory;
import com.adeem.stockflow.repository.InventoryRepository;
import com.adeem.stockflow.repository.ProductRepository;
import com.adeem.stockflow.service.criteria.ProductSpecification;
import com.adeem.stockflow.service.dto.CategoryStatsDTO;
import com.adeem.stockflow.service.dto.MarketplaceProductDTO;
import com.adeem.stockflow.service.dto.MarketplaceProductDetailDTO;
import com.adeem.stockflow.service.mapper.MarketplaceProductMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing marketplace product operations.
 * Handles public-facing product catalog functionality with marketplace-specific business logic.
 */
@Service
@Transactional(readOnly = true)
public class MarketplaceProductService {

    private static final Logger LOG = LoggerFactory.getLogger(MarketplaceProductService.class);

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final MarketplaceProductMapper marketplaceProductMapper;

    public MarketplaceProductService(
        ProductRepository productRepository,
        InventoryRepository inventoryRepository,
        MarketplaceProductMapper marketplaceProductMapper
    ) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.marketplaceProductMapper = marketplaceProductMapper;
    }

    /**
     * Find marketplace products with filtering and pagination.
     * Only returns products that are visible to customers.
     *
     * @param search optional search term for name/description
     * @param category optional category filter
     * @param minPrice optional minimum price filter
     * @param maxPrice optional maximum price filter
     * @param companyName optional company name filter
     * @param availableOnly if true, only return products with available inventory
     * @param pageable pagination information
     * @return page of marketplace products
     */
    public Page<MarketplaceProductDTO> findMarketplaceProducts(
        String search,
        ProductCategory category,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String companyName,
        Boolean availableOnly,
        Pageable pageable
    ) {
        LOG.debug(
            "Request to find marketplace products - search: {}, category: {}, minPrice: {}, maxPrice: {}, company: {}, availableOnly: {}",
            search,
            category,
            minPrice,
            maxPrice,
            companyName,
            availableOnly
        );

        Specification<Product> spec = buildMarketplaceProductSpecification(
            search,
            category,
            minPrice,
            maxPrice,
            companyName,
            availableOnly
        );

        return productRepository.findAll(spec, pageable).map(marketplaceProductMapper::toDto);
    }

    /**
     * Get detailed product information for marketplace product detail page.
     * Only returns products that are visible to customers.
     *
     * @param productId the product ID
     * @return marketplace product detail DTO if found and visible
     */
    public Optional<MarketplaceProductDetailDTO> findMarketplaceProductDetail(Long productId) {
        LOG.debug("Request to get marketplace product detail: {}", productId);

        return productRepository
            .findById(productId)
            .filter(product -> Boolean.TRUE.equals(product.getIsVisibleToCustomers()))
            .map(this::convertToDetailDTO);
    }

    /**
     * Get product categories with product counts for marketplace navigation.
     * Only counts products that are visible to customers.
     *
     * @return list of category statistics
     */
    public List<CategoryStatsDTO> getProductCategoriesWithCounts() {
        LOG.debug("Request to get product categories with counts");

        return Arrays.stream(ProductCategory.values())
            .map(this::getCategoryStats)
            .filter(stats -> stats.getProductCount() > 0)
            .collect(Collectors.toList());
    }

    /**
     * Search products by text in name and description.
     * Only searches products that are visible to customers.
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching products
     */
    public Page<MarketplaceProductDTO> searchProducts(String searchTerm, Pageable pageable) {
        LOG.debug("Request to search products: {}", searchTerm);

        Specification<Product> spec = Specification.where(ProductSpecification.withVisibleToCustomers(true)).and(
            ProductSpecification.withNameOrDescriptionContaining(searchTerm)
        );

        return productRepository.findAll(spec, pageable).map(marketplaceProductMapper::toDto);
    }

    /**
     * Get products from the same company.
     * Used for "More from this seller" functionality.
     *
     * @param companyId the company ID
     * @param excludeProductId optional product ID to exclude from results
     * @param pageable pagination information
     * @return page of products from the same company
     */
    public Page<MarketplaceProductDTO> findProductsByCompany(Long companyId, Long excludeProductId, Pageable pageable) {
        LOG.debug("Request to find products by company: {}, excluding: {}", companyId, excludeProductId);

        Specification<Product> spec = Specification.where(ProductSpecification.withVisibleToCustomers(true)).and(
            ProductSpecification.withClientAccountId(companyId)
        );

        if (excludeProductId != null) {
            spec = spec.and(ProductSpecification.withIdNot(excludeProductId));
        }

        return productRepository.findAll(spec, pageable).map(marketplaceProductMapper::toDto);
    }

    /**
     * Get related products based on category.
     * Used for "Similar products" functionality.
     *
     * @param category the product category
     * @param excludeProductId optional product ID to exclude from results
     * @param limit maximum number of products to return
     * @return list of related products
     */
    public List<MarketplaceProductDTO> findRelatedProductsByCategory(ProductCategory category, Long excludeProductId, int limit) {
        LOG.debug("Request to find related products by category: {}, excluding: {}, limit: {}", category, excludeProductId, limit);

        Specification<Product> spec = Specification.where(ProductSpecification.withVisibleToCustomers(true)).and(
            ProductSpecification.withCategory(category.name())
        );

        if (excludeProductId != null) {
            spec = spec.and(ProductSpecification.withIdNot(excludeProductId));
        }

        Pageable pageable = PageRequest.of(0, limit);
        return productRepository
            .findAll(spec, pageable)
            .getContent()
            .stream()
            .map(marketplaceProductMapper::toDto)
            .collect(Collectors.toList());
    }

    // Private helper methods

    private Specification<Product> buildMarketplaceProductSpecification(
        String search,
        ProductCategory category,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String companyName,
        Boolean availableOnly
    ) {
        Specification<Product> spec = Specification.where(ProductSpecification.withVisibleToCustomers(true));

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and(ProductSpecification.withNameOrDescriptionContaining(search.trim()));
        }

        if (category != null) {
            spec = spec.and(ProductSpecification.withCategory(category.name()));
        }

        if (minPrice != null) {
            spec = spec.and(ProductSpecification.withSellingPriceGreaterThanOrEqual(minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and(ProductSpecification.withSellingPriceLessThanOrEqual(maxPrice));
        }

        if (companyName != null && !companyName.trim().isEmpty()) {
            spec = spec.and(ProductSpecification.withCompanyNameContaining(companyName.trim()));
        }

        if (Boolean.TRUE.equals(availableOnly)) {
            spec = spec.and(ProductSpecification.withAvailableInventory());
        }

        return spec;
    }

    private CategoryStatsDTO getCategoryStats(ProductCategory category) {
        Long totalCount = productRepository.count(
            Specification.where(ProductSpecification.withVisibleToCustomers(true)).and(ProductSpecification.withCategory(category.name()))
        );

        Long availableCount = productRepository.count(
            Specification.where(ProductSpecification.withVisibleToCustomers(true))
                .and(ProductSpecification.withCategory(category.name()))
                .and(ProductSpecification.withAvailableInventory())
        );

        return new CategoryStatsDTO(category, totalCount, availableCount);
    }

    private MarketplaceProductDetailDTO convertToDetailDTO(Product product) {
        MarketplaceProductDetailDTO detailDTO = new MarketplaceProductDetailDTO();

        // Basic product information
        detailDTO.setId(product.getId());
        detailDTO.setName(product.getName());
        detailDTO.setDescription(product.getDescription());
        detailDTO.setCode(product.getCode());
        detailDTO.setSellingPrice(product.getSellingPrice());
        detailDTO.setCategory(product.getCategory());
        detailDTO.setIsVisibleToCustomers(product.getIsVisibleToCustomers());
        detailDTO.setMinimumStockLevel(product.getMinimumStockLevel());

        // Calculate availability from inventories
        BigDecimal availableQty = calculateAvailableQuantity(product);
        detailDTO.setAvailableQuantity(availableQty);
        detailDTO.setIsAvailable(availableQty.compareTo(BigDecimal.ZERO) > 0);
        detailDTO.setIsLowStock(product.getMinimumStockLevel() != null && availableQty.compareTo(product.getMinimumStockLevel()) <= 0);

        // Company information (exclude sensitive data)
        if (product.getClientAccount() != null) {
            MarketplaceProductDetailDTO.CompanyProfileDTO companyProfile = new MarketplaceProductDetailDTO.CompanyProfileDTO();
            companyProfile.setId(product.getClientAccount().getId());
            companyProfile.setCompanyName(product.getClientAccount().getCompanyName());
            companyProfile.setPhone(product.getClientAccount().getPhone());

            if (product.getClientAccount().getAddress() != null) {
                companyProfile.setCity(product.getClientAccount().getAddress().getCity());
                companyProfile.setCountry(product.getClientAccount().getAddress().getCountry());
            }

            companyProfile.setContactEmail(product.getClientAccount().getEmail());
            detailDTO.setCompany(companyProfile);
        }

        // Image URLs
        if (product.getImages() != null) {
            List<String> imageUrls = product
                .getImages()
                .stream()
                .map(attachment -> "/api/attachments/" + attachment.getId() + "/download")
                .collect(Collectors.toList());
            detailDTO.setImageUrls(imageUrls);
        }

        // Related products
        if (product.getCategory() != null) {
            List<MarketplaceProductDTO> relatedProducts = findRelatedProductsByCategory(product.getCategory(), product.getId(), 6);
            detailDTO.setRelatedProducts(relatedProducts);
        }

        return detailDTO;
    }

    private BigDecimal calculateAvailableQuantity(Product product) {
        Set<Inventory> inventories = inventoryRepository.findByProductId(product.getId());
        if (inventories == null || inventories.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return inventories
            .stream()
            .map(inventory -> inventory.getAvailableQuantity() != null ? inventory.getAvailableQuantity() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
