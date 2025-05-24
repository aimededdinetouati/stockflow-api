package com.adeem.stockflow.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for inventory statistics and analytics.
 */
public class InventoryStatsDTO implements Serializable {

    private Long totalProducts;
    private BigDecimal totalUnits;
    private BigDecimal totalValue;
    private BigDecimal averageStockLevel;

    private Long lowStockItems;
    private Long outOfStockItems;
    private Long overstockItems;
    private Long healthyStockItems;

    private BigDecimal inventoryAccuracy;
    private BigDecimal inventoryTurnoverRate;

    private BigDecimal totalReservedQuantity;
    private BigDecimal totalAvailableQuantity;

    private List<CategoryStatsDTO> categoryStats;
    private List<LocationStatsDTO> locationStats;
    private InventoryTrendDTO trend;

    // Recent activity stats
    private Long transactionsToday;
    private Long transactionsThisWeek;
    private Long transactionsThisMonth;

    // Value analytics
    private BigDecimal highestValueProduct;
    private BigDecimal lowestValueProduct;
    private BigDecimal averageProductValue;

    // Alerts and notifications
    private Long criticalAlerts;
    private Long warningAlerts;
    private Long expiringSoonItems;

    public InventoryStatsDTO() {}

    // Getters and setters

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public BigDecimal getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(BigDecimal totalUnits) {
        this.totalUnits = totalUnits;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getAverageStockLevel() {
        return averageStockLevel;
    }

    public void setAverageStockLevel(BigDecimal averageStockLevel) {
        this.averageStockLevel = averageStockLevel;
    }

    public Long getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(Long lowStockItems) {
        this.lowStockItems = lowStockItems;
    }

    public Long getOutOfStockItems() {
        return outOfStockItems;
    }

    public void setOutOfStockItems(Long outOfStockItems) {
        this.outOfStockItems = outOfStockItems;
    }

    public Long getOverstockItems() {
        return overstockItems;
    }

    public void setOverstockItems(Long overstockItems) {
        this.overstockItems = overstockItems;
    }

    public Long getHealthyStockItems() {
        return healthyStockItems;
    }

    public void setHealthyStockItems(Long healthyStockItems) {
        this.healthyStockItems = healthyStockItems;
    }

    public BigDecimal getInventoryAccuracy() {
        return inventoryAccuracy;
    }

    public void setInventoryAccuracy(BigDecimal inventoryAccuracy) {
        this.inventoryAccuracy = inventoryAccuracy;
    }

    public BigDecimal getInventoryTurnoverRate() {
        return inventoryTurnoverRate;
    }

    public void setInventoryTurnoverRate(BigDecimal inventoryTurnoverRate) {
        this.inventoryTurnoverRate = inventoryTurnoverRate;
    }

    public BigDecimal getTotalReservedQuantity() {
        return totalReservedQuantity;
    }

    public void setTotalReservedQuantity(BigDecimal totalReservedQuantity) {
        this.totalReservedQuantity = totalReservedQuantity;
    }

    public BigDecimal getTotalAvailableQuantity() {
        return totalAvailableQuantity;
    }

    public void setTotalAvailableQuantity(BigDecimal totalAvailableQuantity) {
        this.totalAvailableQuantity = totalAvailableQuantity;
    }

    public List<CategoryStatsDTO> getCategoryStats() {
        return categoryStats;
    }

    public void setCategoryStats(List<CategoryStatsDTO> categoryStats) {
        this.categoryStats = categoryStats;
    }

    public List<LocationStatsDTO> getLocationStats() {
        return locationStats;
    }

    public void setLocationStats(List<LocationStatsDTO> locationStats) {
        this.locationStats = locationStats;
    }

    public InventoryTrendDTO getTrend() {
        return trend;
    }

    public void setTrend(InventoryTrendDTO trend) {
        this.trend = trend;
    }

    public Long getTransactionsToday() {
        return transactionsToday;
    }

    public void setTransactionsToday(Long transactionsToday) {
        this.transactionsToday = transactionsToday;
    }

    public Long getTransactionsThisWeek() {
        return transactionsThisWeek;
    }

    public void setTransactionsThisWeek(Long transactionsThisWeek) {
        this.transactionsThisWeek = transactionsThisWeek;
    }

    public Long getTransactionsThisMonth() {
        return transactionsThisMonth;
    }

    public void setTransactionsThisMonth(Long transactionsThisMonth) {
        this.transactionsThisMonth = transactionsThisMonth;
    }

    public BigDecimal getHighestValueProduct() {
        return highestValueProduct;
    }

    public void setHighestValueProduct(BigDecimal highestValueProduct) {
        this.highestValueProduct = highestValueProduct;
    }

    public BigDecimal getLowestValueProduct() {
        return lowestValueProduct;
    }

    public void setLowestValueProduct(BigDecimal lowestValueProduct) {
        this.lowestValueProduct = lowestValueProduct;
    }

    public BigDecimal getAverageProductValue() {
        return averageProductValue;
    }

    public void setAverageProductValue(BigDecimal averageProductValue) {
        this.averageProductValue = averageProductValue;
    }

    public Long getCriticalAlerts() {
        return criticalAlerts;
    }

    public void setCriticalAlerts(Long criticalAlerts) {
        this.criticalAlerts = criticalAlerts;
    }

    public Long getWarningAlerts() {
        return warningAlerts;
    }

    public void setWarningAlerts(Long warningAlerts) {
        this.warningAlerts = warningAlerts;
    }

    public Long getExpiringSoonItems() {
        return expiringSoonItems;
    }

    public void setExpiringSoonItems(Long expiringSoonItems) {
        this.expiringSoonItems = expiringSoonItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryStatsDTO)) return false;
        InventoryStatsDTO that = (InventoryStatsDTO) o;
        return (
            Objects.equals(totalProducts, that.totalProducts) &&
            Objects.equals(totalUnits, that.totalUnits) &&
            Objects.equals(totalValue, that.totalValue)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalProducts, totalUnits, totalValue);
    }

    @Override
    public String toString() {
        return (
            "InventoryStatsDTO{" +
            "totalProducts=" +
            totalProducts +
            ", totalUnits=" +
            totalUnits +
            ", totalValue=" +
            totalValue +
            ", lowStockItems=" +
            lowStockItems +
            ", outOfStockItems=" +
            outOfStockItems +
            ", healthyStockItems=" +
            healthyStockItems +
            ", inventoryAccuracy=" +
            inventoryAccuracy +
            "}"
        );
    }

    // Inner classes for nested statistics

    public static class CategoryStatsDTO implements Serializable {

        private String category;
        private Long productCount;
        private BigDecimal totalValue;
        private BigDecimal totalQuantity;
        private Long lowStockCount;
        private Long outOfStockCount;

        // Constructors, getters, and setters
        public CategoryStatsDTO() {}

        public CategoryStatsDTO(String category, Long productCount, BigDecimal totalValue, BigDecimal totalQuantity) {
            this.category = category;
            this.productCount = productCount;
            this.totalValue = totalValue;
            this.totalQuantity = totalQuantity;
        }

        // Getters and setters
        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
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

        public Long getLowStockCount() {
            return lowStockCount;
        }

        public void setLowStockCount(Long lowStockCount) {
            this.lowStockCount = lowStockCount;
        }

        public Long getOutOfStockCount() {
            return outOfStockCount;
        }

        public void setOutOfStockCount(Long outOfStockCount) {
            this.outOfStockCount = outOfStockCount;
        }
    }

    public static class LocationStatsDTO implements Serializable {

        private String location;
        private Long productCount;
        private BigDecimal totalQuantity;
        private BigDecimal utilizationPercentage;

        // Constructors, getters, and setters
        public LocationStatsDTO() {}

        public LocationStatsDTO(String location, Long productCount, BigDecimal totalQuantity) {
            this.location = location;
            this.productCount = productCount;
            this.totalQuantity = totalQuantity;
        }

        // Getters and setters
        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Long getProductCount() {
            return productCount;
        }

        public void setProductCount(Long productCount) {
            this.productCount = productCount;
        }

        public BigDecimal getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(BigDecimal totalQuantity) {
            this.totalQuantity = totalQuantity;
        }

        public BigDecimal getUtilizationPercentage() {
            return utilizationPercentage;
        }

        public void setUtilizationPercentage(BigDecimal utilizationPercentage) {
            this.utilizationPercentage = utilizationPercentage;
        }
    }

    public static class InventoryTrendDTO implements Serializable {

        private String period;
        private BigDecimal valueChange;
        private BigDecimal valueChangePercentage;
        private BigDecimal quantityChange;
        private BigDecimal quantityChangePercentage;
        private String trend; // INCREASING, DECREASING, STABLE

        // Constructors, getters, and setters
        public InventoryTrendDTO() {}

        // Getters and setters
        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public BigDecimal getValueChange() {
            return valueChange;
        }

        public void setValueChange(BigDecimal valueChange) {
            this.valueChange = valueChange;
        }

        public BigDecimal getValueChangePercentage() {
            return valueChangePercentage;
        }

        public void setValueChangePercentage(BigDecimal valueChangePercentage) {
            this.valueChangePercentage = valueChangePercentage;
        }

        public BigDecimal getQuantityChange() {
            return quantityChange;
        }

        public void setQuantityChange(BigDecimal quantityChange) {
            this.quantityChange = quantityChange;
        }

        public BigDecimal getQuantityChangePercentage() {
            return quantityChangePercentage;
        }

        public void setQuantityChangePercentage(BigDecimal quantityChangePercentage) {
            this.quantityChangePercentage = quantityChangePercentage;
        }

        public String getTrend() {
            return trend;
        }

        public void setTrend(String trend) {
            this.trend = trend;
        }
    }
}
