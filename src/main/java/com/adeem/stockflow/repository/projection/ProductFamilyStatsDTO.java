package com.adeem.stockflow.repository.projection;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for product family statistics and analytics.
 */
public class ProductFamilyStatsDTO implements Serializable {

    private Long totalFamilies;
    private Long totalProducts;
    private BigDecimal totalInventoryValue;
    private BigDecimal averageProductsPerFamily;

    private Long familiesWithProducts;
    private Long emptyFamilies;
    private Long familiesWithLowStock;
    private Long familiesWithOutOfStock;

    private List<FamilyDetailStatsDTO> topFamiliesByProducts;
    private List<FamilyDetailStatsDTO> topFamiliesByValue;
    private List<CategoryStatsDTO> categoryStats;

    // Recent activity
    private Long familiesCreatedThisMonth;
    private Long familiesCreatedThisWeek;
    private Instant lastFamilyCreated;
    private Instant lastFamilyModified;

    // Distribution stats
    private BigDecimal highestFamilyValue;
    private BigDecimal lowestFamilyValue;
    private Long largestFamilySize;
    private Long smallestFamilySize;

    public ProductFamilyStatsDTO() {}

    // Getters and setters

    public Long getTotalFamilies() {
        return totalFamilies;
    }

    public void setTotalFamilies(Long totalFamilies) {
        this.totalFamilies = totalFamilies;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public BigDecimal getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(BigDecimal totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }

    public BigDecimal getAverageProductsPerFamily() {
        return averageProductsPerFamily;
    }

    public void setAverageProductsPerFamily(BigDecimal averageProductsPerFamily) {
        this.averageProductsPerFamily = averageProductsPerFamily;
    }

    public Long getFamiliesWithProducts() {
        return familiesWithProducts;
    }

    public void setFamiliesWithProducts(Long familiesWithProducts) {
        this.familiesWithProducts = familiesWithProducts;
    }

    public Long getEmptyFamilies() {
        return emptyFamilies;
    }

    public void setEmptyFamilies(Long emptyFamilies) {
        this.emptyFamilies = emptyFamilies;
    }

    public Long getFamiliesWithLowStock() {
        return familiesWithLowStock;
    }

    public void setFamiliesWithLowStock(Long familiesWithLowStock) {
        this.familiesWithLowStock = familiesWithLowStock;
    }

    public Long getFamiliesWithOutOfStock() {
        return familiesWithOutOfStock;
    }

    public void setFamiliesWithOutOfStock(Long familiesWithOutOfStock) {
        this.familiesWithOutOfStock = familiesWithOutOfStock;
    }

    public List<FamilyDetailStatsDTO> getTopFamiliesByProducts() {
        return topFamiliesByProducts;
    }

    public void setTopFamiliesByProducts(List<FamilyDetailStatsDTO> topFamiliesByProducts) {
        this.topFamiliesByProducts = topFamiliesByProducts;
    }

    public List<FamilyDetailStatsDTO> getTopFamiliesByValue() {
        return topFamiliesByValue;
    }

    public void setTopFamiliesByValue(List<FamilyDetailStatsDTO> topFamiliesByValue) {
        this.topFamiliesByValue = topFamiliesByValue;
    }

    public List<CategoryStatsDTO> getCategoryStats() {
        return categoryStats;
    }

    public void setCategoryStats(List<CategoryStatsDTO> categoryStats) {
        this.categoryStats = categoryStats;
    }

    public Long getFamiliesCreatedThisMonth() {
        return familiesCreatedThisMonth;
    }

    public void setFamiliesCreatedThisMonth(Long familiesCreatedThisMonth) {
        this.familiesCreatedThisMonth = familiesCreatedThisMonth;
    }

    public Long getFamiliesCreatedThisWeek() {
        return familiesCreatedThisWeek;
    }

    public void setFamiliesCreatedThisWeek(Long familiesCreatedThisWeek) {
        this.familiesCreatedThisWeek = familiesCreatedThisWeek;
    }

    public Instant getLastFamilyCreated() {
        return lastFamilyCreated;
    }

    public void setLastFamilyCreated(Instant lastFamilyCreated) {
        this.lastFamilyCreated = lastFamilyCreated;
    }

    public Instant getLastFamilyModified() {
        return lastFamilyModified;
    }

    public void setLastFamilyModified(Instant lastFamilyModified) {
        this.lastFamilyModified = lastFamilyModified;
    }

    public BigDecimal getHighestFamilyValue() {
        return highestFamilyValue;
    }

    public void setHighestFamilyValue(BigDecimal highestFamilyValue) {
        this.highestFamilyValue = highestFamilyValue;
    }

    public BigDecimal getLowestFamilyValue() {
        return lowestFamilyValue;
    }

    public void setLowestFamilyValue(BigDecimal lowestFamilyValue) {
        this.lowestFamilyValue = lowestFamilyValue;
    }

    public Long getLargestFamilySize() {
        return largestFamilySize;
    }

    public void setLargestFamilySize(Long largestFamilySize) {
        this.largestFamilySize = largestFamilySize;
    }

    public Long getSmallestFamilySize() {
        return smallestFamilySize;
    }

    public void setSmallestFamilySize(Long smallestFamilySize) {
        this.smallestFamilySize = smallestFamilySize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductFamilyStatsDTO)) return false;
        ProductFamilyStatsDTO that = (ProductFamilyStatsDTO) o;
        return (
            Objects.equals(totalFamilies, that.totalFamilies) &&
            Objects.equals(totalProducts, that.totalProducts) &&
            Objects.equals(totalInventoryValue, that.totalInventoryValue)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalFamilies, totalProducts, totalInventoryValue);
    }

    @Override
    public String toString() {
        return (
            "ProductFamilyStatsDTO{" +
            "totalFamilies=" +
            totalFamilies +
            ", totalProducts=" +
            totalProducts +
            ", totalInventoryValue=" +
            totalInventoryValue +
            ", familiesWithProducts=" +
            familiesWithProducts +
            ", emptyFamilies=" +
            emptyFamilies +
            ", familiesWithLowStock=" +
            familiesWithLowStock +
            "}"
        );
    }

    // Inner classes for nested statistics

    public static class FamilyDetailStatsDTO implements Serializable {

        private Long familyId;
        private String familyName;
        private Long productCount;
        private BigDecimal totalValue;
        private BigDecimal totalQuantity;
        private Long lowStockProducts;
        private Long outOfStockProducts;
        private String healthStatus; // HEALTHY, LOW_STOCK, OUT_OF_STOCK
        private Instant lastModified;

        // Constructors
        public FamilyDetailStatsDTO() {}

        public FamilyDetailStatsDTO(Long familyId, String familyName, Long productCount, BigDecimal totalValue) {
            this.familyId = familyId;
            this.familyName = familyName;
            this.productCount = productCount;
            this.totalValue = totalValue;
        }

        // Getters and setters
        public Long getFamilyId() {
            return familyId;
        }

        public void setFamilyId(Long familyId) {
            this.familyId = familyId;
        }

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public Long getProductCount() {
            return productCount;
        }

        public void setProductCount(Long productCount) {
            this.productCount = productCount;
        }

        public BigDecimal getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(BigDecimal totalValue) {
            this.totalValue = totalValue;
        }

        public BigDecimal getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(BigDecimal totalQuantity) {
            this.totalQuantity = totalQuantity;
        }

        public Long getLowStockProducts() {
            return lowStockProducts;
        }

        public void setLowStockProducts(Long lowStockProducts) {
            this.lowStockProducts = lowStockProducts;
        }

        public Long getOutOfStockProducts() {
            return outOfStockProducts;
        }

        public void setOutOfStockProducts(Long outOfStockProducts) {
            this.outOfStockProducts = outOfStockProducts;
        }

        public String getHealthStatus() {
            return healthStatus;
        }

        public void setHealthStatus(String healthStatus) {
            this.healthStatus = healthStatus;
        }

        public Instant getLastModified() {
            return lastModified;
        }

        public void setLastModified(Instant lastModified) {
            this.lastModified = lastModified;
        }
    }

    public static class CategoryStatsDTO implements Serializable {

        private String category;
        private Long familyCount;
        private Long productCount;
        private BigDecimal totalValue;
        private BigDecimal totalQuantity;

        // Constructors
        public CategoryStatsDTO() {}

        public CategoryStatsDTO(String category, Long familyCount, Long productCount, BigDecimal totalValue) {
            this.category = category;
            this.familyCount = familyCount;
            this.productCount = productCount;
            this.totalValue = totalValue;
        }

        // Getters and setters
        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Long getFamilyCount() {
            return familyCount;
        }

        public void setFamilyCount(Long familyCount) {
            this.familyCount = familyCount;
        }

        public Long getProductCount() {
            return productCount;
        }

        public void setProductCount(Long productCount) {
            this.productCount = productCount;
        }

        public BigDecimal getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(BigDecimal totalValue) {
            this.totalValue = totalValue;
        }

        public BigDecimal getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(BigDecimal totalQuantity) {
            this.totalQuantity = totalQuantity;
        }
    }
}
