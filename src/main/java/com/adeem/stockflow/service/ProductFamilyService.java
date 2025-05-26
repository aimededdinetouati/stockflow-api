package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.ProductFamily;
import com.adeem.stockflow.repository.ProductFamilyRepository;
import com.adeem.stockflow.repository.ProductRepository;
import com.adeem.stockflow.repository.projection.CategoryStatsProjection;
import com.adeem.stockflow.repository.projection.FamilyDetailStatsProjection;
import com.adeem.stockflow.repository.projection.ProductFamilyStatsDTO;
import com.adeem.stockflow.repository.projection.ProductFamilyStatsProjection;
import com.adeem.stockflow.service.dto.ProductDTO;
import com.adeem.stockflow.service.dto.ProductFamilyDTO;
import com.adeem.stockflow.service.mapper.ProductFamilyMapper;
import com.adeem.stockflow.service.mapper.ProductMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProductFamily}.
 * Enhanced with multi-tenant security and comprehensive family management.
 */
@Service
@Transactional
public class ProductFamilyService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductFamilyService.class);

    private final ProductFamilyRepository productFamilyRepository;
    private final ProductRepository productRepository;
    private final ProductFamilyMapper productFamilyMapper;
    private final ProductMapper productMapper;
    private final ProductService productService;

    public ProductFamilyService(
        ProductFamilyRepository productFamilyRepository,
        ProductRepository productRepository,
        ProductFamilyMapper productFamilyMapper,
        ProductMapper productMapper,
        ProductService productService
    ) {
        this.productFamilyRepository = productFamilyRepository;
        this.productRepository = productRepository;
        this.productFamilyMapper = productFamilyMapper;
        this.productMapper = productMapper;
        this.productService = productService;
    }

    /**
     * Save a productFamily.
     *
     * @param productFamilyDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductFamilyDTO save(ProductFamilyDTO productFamilyDTO) {
        LOG.debug("Request to save ProductFamily : {}", productFamilyDTO);
        ProductFamily productFamily = productFamilyMapper.toEntity(productFamilyDTO);
        productFamily = productFamilyRepository.save(productFamily);
        return productFamilyMapper.toDto(productFamily);
    }

    /**
     * Update a productFamily.
     *
     * @param productFamilyDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductFamilyDTO update(ProductFamilyDTO productFamilyDTO) {
        LOG.debug("Request to update ProductFamily : {}", productFamilyDTO);
        ProductFamily productFamily = productFamilyMapper.toEntity(productFamilyDTO);
        productFamily.setIsPersisted();
        productFamily = productFamilyRepository.save(productFamily);
        return productFamilyMapper.toDto(productFamily);
    }

    /**
     * Partially update a productFamily.
     *
     * @param productFamilyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductFamilyDTO> partialUpdate(ProductFamilyDTO productFamilyDTO) {
        LOG.debug("Request to partially update ProductFamily : {}", productFamilyDTO);

        return productFamilyRepository
            .findById(productFamilyDTO.getId())
            .map(existingProductFamily -> {
                productFamilyMapper.partialUpdate(existingProductFamily, productFamilyDTO);
                return existingProductFamily;
            })
            .map(productFamilyRepository::save)
            .map(productFamilyMapper::toDto);
    }

    /**
     * Get all the productFamilies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductFamilyDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ProductFamilies");
        return productFamilyRepository.findAll(pageable).map(productFamilyMapper::toDto);
    }

    /**
     * Get all the productFamilies with specification.
     *
     * @param specification the specification to filter by.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductFamilyDTO> findAll(Specification<ProductFamily> specification, Pageable pageable) {
        LOG.debug("Request to get all ProductFamilies with specification");
        return productFamilyRepository.findAll(specification, pageable).map(productFamilyMapper::toDto);
    }

    /**
     * Count entities by specification.
     *
     * @param specification the specification to filter by.
     * @return the count of entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(Specification<ProductFamily> specification) {
        LOG.debug("Request to count ProductFamilies by specification");
        return productFamilyRepository.count(specification);
    }

    /**
     * Get one productFamily by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductFamilyDTO> findOne(Long id) {
        LOG.debug("Request to get ProductFamily : {}", id);
        return productFamilyRepository.findById(id).map(productFamilyMapper::toDto);
    }

    /**
     * Get one productFamily by id for a specific client account.
     *
     * @param id the id of the entity.
     * @param clientAccountId the client account id.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductFamilyDTO> findOneForClientAccount(Long id, Long clientAccountId) {
        LOG.debug("Request to get ProductFamily : {} for client account : {}", id, clientAccountId);
        return productFamilyRepository.findByIdAndClientAccountId(id, clientAccountId).map(productFamilyMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ProductFamily> findEntityForClientAccount(Long id, Long clientAccountId) {
        LOG.debug("Request to get ProductFamily : {} for client account : {}", id, clientAccountId);
        return productFamilyRepository.findByIdAndClientAccountId(id, clientAccountId);
    }

    /**
     * Delete the productFamily by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ProductFamily : {}", id);
        productFamilyRepository.deleteById(id);
    }

    /**
     * Check if a family name exists for a client account.
     *
     * @param name the family name.
     * @param clientAccountId the client account id.
     * @return true if exists.
     */
    @Transactional(readOnly = true)
    public boolean existsByNameAndClientAccount(String name, Long clientAccountId) {
        LOG.debug("Request to check if ProductFamily name exists : {} for client account : {}", name, clientAccountId);
        return productFamilyRepository.existsByNameAndClientAccountId(name, clientAccountId);
    }

    /**
     * Check if a family name exists for a client account excluding a specific family.
     *
     * @param name the family name.
     * @param clientAccountId the client account id.
     * @param excludeId the family id to exclude.
     * @return true if exists.
     */
    @Transactional(readOnly = true)
    public boolean existsByNameAndClientAccountExcluding(String name, Long clientAccountId, Long excludeId) {
        LOG.debug(
            "Request to check if ProductFamily name exists : {} for client account : {} excluding : {}",
            name,
            clientAccountId,
            excludeId
        );
        return productFamilyRepository.existsByNameAndClientAccountIdAndIdNot(name, clientAccountId, excludeId);
    }

    /**
     * Check if a family has products.
     *
     * @param familyId the family id.
     * @return true if has products.
     */
    @Transactional(readOnly = true)
    public boolean hasProducts(Long familyId) {
        LOG.debug("Request to check if ProductFamily has products : {}", familyId);
        return productRepository.existsByProductFamilyId(familyId);
    }

    /**
     * Search product families by name for a client account.
     *
     * @param query the search query.
     * @param clientAccountId the client account id.
     * @param pageable the pagination information.
     * @return the list of matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductFamilyDTO> searchByNameAndClientAccount(String query, Long clientAccountId, Pageable pageable) {
        LOG.debug("Request to search ProductFamilies with query : {} for client account : {}", query, clientAccountId);
        return productFamilyRepository
            .findByNameContainingIgnoreCaseAndClientAccountId(query, clientAccountId, pageable)
            .map(productFamilyMapper::toDto);
    }

    /**
     * Assign a product to a family.
     *
     * @param productIds the products ids.
     * @param family the product family.
     */
    public Integer assignProductToFamily(Long clientAccountId, ProductFamily family, List<Long> productIds) {
        LOG.debug("Request to assign Products : {} to ProductFamily : {}}", productIds.toString(), family.getId());
        return productFamilyRepository.assignProductToFamily(clientAccountId, family, productIds);
    }

    /**
     * Remove a product from its family.
     *
     * @param productIds the product ids.
     * @param clientAccountId the client account id.
     * @return the updated product.
     */
    public Integer removeProductFromFamily(Long clientAccountId, ProductFamily family, List<Long> productIds) {
        LOG.debug("Request to remove Products : {} from ProductFamily : {}}", productIds.toString(), family.getId());
        return productFamilyRepository.removeProductsFromFamily(clientAccountId, family, productIds);
    }

    /**
     * Get comprehensive statistics for all families of a client account.
     * OPTIMIZED VERSION - Uses only 3 efficient queries instead of 15+ separate calls.
     *
     * @param clientAccountId the client account id.
     * @return the statistics.
     */
    @Transactional(readOnly = true)
    public ProductFamilyStatsDTO getStatistics(Long clientAccountId) {
        LOG.debug("Request to get ProductFamily statistics for client account : {}", clientAccountId);

        ProductFamilyStatsDTO stats = new ProductFamilyStatsDTO();

        // Query 1: Get comprehensive family overview statistics
        Optional<ProductFamilyStatsProjection> projectionOpt = productFamilyRepository.getComprehensiveOverviewStats(clientAccountId);
        if (projectionOpt.isPresent()) {
            ProductFamilyStatsProjection projection = projectionOpt.get();

            stats.setTotalFamilies(projection.getTotalFamilies());
            stats.setTotalProducts(projection.getTotalProducts());
            stats.setFamiliesWithProducts(projection.getFamiliesWithProducts());
            stats.setFamiliesWithLowStock(projection.getFamiliesWithLowStock());
            stats.setFamiliesWithOutOfStock(projection.getFamiliesWithOutOfStock());
            stats.setTotalInventoryValue(projection.getTotalInventoryValue());
            stats.setFamiliesCreatedThisWeek(projection.getFamiliesCreatedThisWeek());
            stats.setFamiliesCreatedThisMonth(projection.getFamiliesCreatedThisMonth());
            stats.setLastFamilyCreated(projection.getLastFamilyCreated());
            stats.setLastFamilyModified(projection.getLastFamilyModified());
            stats.setHighestFamilyValue(projection.getHighestFamilyValue());
            stats.setLowestFamilyValue(projection.getLowestFamilyValue());
            stats.setLargestFamilySize(projection.getLargestFamilySize());
            stats.setSmallestFamilySize(projection.getSmallestFamilySize());

            // Calculate derived values
            Long totalFamilies = stats.getTotalFamilies();
            Long totalProducts = stats.getTotalProducts();
            stats.setEmptyFamilies(totalFamilies - stats.getFamiliesWithProducts());

            if (totalFamilies > 0) {
                BigDecimal avgProducts = BigDecimal.valueOf(totalProducts).divide(
                    BigDecimal.valueOf(totalFamilies),
                    2,
                    RoundingMode.HALF_UP
                );
                stats.setAverageProductsPerFamily(avgProducts);
            } else {
                stats.setAverageProductsPerFamily(BigDecimal.ZERO);
            }
        }

        // Query 2: Get top families by products and value - NO MORE MANUAL SORTING!
        List<FamilyDetailStatsProjection> topFamiliesProjections = productFamilyRepository.getTopFamiliesStats(clientAccountId, 5);

        // Convert to DTOs and sort by product count (descending)
        List<ProductFamilyStatsDTO.FamilyDetailStatsDTO> topFamiliesByProducts = topFamiliesProjections
            .stream()
            .sorted((a, b) -> b.getProductCount().compareTo(a.getProductCount()))
            .limit(5)
            .map(proj ->
                new ProductFamilyStatsDTO.FamilyDetailStatsDTO(
                    proj.getFamilyId(),
                    proj.getFamilyName(),
                    proj.getProductCount(),
                    proj.getTotalValue()
                )
            )
            .collect(Collectors.toList());
        stats.setTopFamiliesByProducts(topFamiliesByProducts);

        // Convert to DTOs and sort by value (descending)
        List<ProductFamilyStatsDTO.FamilyDetailStatsDTO> topFamiliesByValue = topFamiliesProjections
            .stream()
            .sorted((a, b) -> b.getTotalValue().compareTo(a.getTotalValue()))
            .limit(5)
            .map(proj ->
                new ProductFamilyStatsDTO.FamilyDetailStatsDTO(
                    proj.getFamilyId(),
                    proj.getFamilyName(),
                    proj.getProductCount(),
                    proj.getTotalValue()
                )
            )
            .collect(Collectors.toList());
        stats.setTopFamiliesByValue(topFamiliesByValue);

        // Query 3: Get category statistics - SUPER CLEAN!
        List<CategoryStatsProjection> categoryProjections = productRepository.getCategoryStatsOptimized(clientAccountId);

        List<ProductFamilyStatsDTO.CategoryStatsDTO> categoryStats = categoryProjections
            .stream()
            .map(proj ->
                new ProductFamilyStatsDTO.CategoryStatsDTO(
                    proj.getCategory(),
                    proj.getFamilyCount(),
                    proj.getProductCount(),
                    proj.getTotalValue()
                )
            )
            .collect(Collectors.toList());
        stats.setCategoryStats(categoryStats);

        return stats;
    }

    // Helper method to handle different timestamp types from database
    private Instant convertToInstant(Object timestampObject) {
        if (timestampObject instanceof Instant) {
            return (Instant) timestampObject;
        } else if (timestampObject instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) timestampObject).toInstant();
        } else if (timestampObject instanceof java.time.LocalDateTime) {
            return ((java.time.LocalDateTime) timestampObject).atZone(ZoneId.systemDefault()).toInstant();
        } else if (timestampObject instanceof java.util.Date) {
            return ((java.util.Date) timestampObject).toInstant();
        }
        throw new IllegalArgumentException("Unsupported timestamp type: " + timestampObject.getClass());
    }

    // Helper method to safely convert to BigDecimal for comparisons
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    /**
     * Get detailed statistics for a specific family.
     * OPTIMIZED VERSION - Uses only 1 efficient query instead of 5+ separate calls.
     *
     * @param family the product family entity.
     * @param clientAccountId the client account id.
     * @return the family detail statistics.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "family-detail-stats", key = "#family.id + '_' + #clientAccountId")
    public ProductFamilyStatsDTO.FamilyDetailStatsDTO getFamilyDetailStats(ProductFamily family, Long clientAccountId) {
        LOG.debug("Request to get detailed statistics for ProductFamily : {} for client account : {}", family.getId(), clientAccountId);

        Long familyId = family.getId();

        // SINGLE OPTIMIZED QUERY: Get all family statistics in one database call
        Object[] statsData = productFamilyRepository.getFamilyDetailStatsOptimized(familyId, clientAccountId);

        ProductFamilyStatsDTO.FamilyDetailStatsDTO stats = new ProductFamilyStatsDTO.FamilyDetailStatsDTO();

        // Set basic family info (no database call needed)
        stats.setFamilyId(family.getId());
        stats.setFamilyName(family.getName());
        stats.setLastModified(family.getLastModifiedDate());

        // Extract all statistics from single query result
        if (statsData != null && statsData.length >= 5) {
            Long productCount = (Long) statsData[0];
            BigDecimal totalValue = (BigDecimal) statsData[1];
            BigDecimal totalQuantity = (BigDecimal) statsData[2];
            Long lowStockProducts = (Long) statsData[3];
            Long outOfStockProducts = (Long) statsData[4];

            stats.setProductCount(productCount != null ? productCount : 0L);
            stats.setTotalValue(totalValue != null ? totalValue : BigDecimal.ZERO);
            stats.setTotalQuantity(totalQuantity != null ? totalQuantity : BigDecimal.ZERO);
            stats.setLowStockProducts(lowStockProducts != null ? lowStockProducts : 0L);
            stats.setOutOfStockProducts(outOfStockProducts != null ? outOfStockProducts : 0L);

            // Determine overall health status (computed in Java for clarity)
            if (outOfStockProducts != null && outOfStockProducts > 0) {
                stats.setHealthStatus("OUT_OF_STOCK");
            } else if (lowStockProducts != null && lowStockProducts > 0) {
                stats.setHealthStatus("LOW_STOCK");
            } else {
                stats.setHealthStatus("HEALTHY");
            }
        } else {
            // Handle case where family has no products
            stats.setProductCount(0L);
            stats.setTotalValue(BigDecimal.ZERO);
            stats.setTotalQuantity(BigDecimal.ZERO);
            stats.setLowStockProducts(0L);
            stats.setOutOfStockProducts(0L);
            stats.setHealthStatus("EMPTY");
        }

        return stats;
    }
}
