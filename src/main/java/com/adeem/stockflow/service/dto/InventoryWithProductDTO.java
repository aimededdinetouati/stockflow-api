package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.InventoryStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for inventory information combined with product details.
 */
public class InventoryWithProductDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal quantity;

    @NotNull
    private BigDecimal availableQuantity;

    @NotNull
    private InventoryStatus status;

    private String location;

    private Instant lastUpdated;

    private ProductDTO product;

    private String stockHealthStatus;

    private BigDecimal stockHealthPercentage;

    private BigDecimal reservedQuantity;

    private Boolean isLowStock;

    private Boolean isOutOfStock;

    private Integer daysSinceLastUpdate;

    public InventoryWithProductDTO() {}

    public InventoryWithProductDTO(InventoryDTO inventory, ProductDTO product) {
        this.id = inventory.getId();
        this.quantity = inventory.getQuantity();
        this.availableQuantity = inventory.getAvailableQuantity();
        this.status = inventory.getStatus();
        this.lastUpdated = inventory.getLastModifiedDate();
        this.product = product;

        // Calculate derived fields
        calculateDerivedFields();
    }

    private void calculateDerivedFields() {
        if (quantity != null && availableQuantity != null) {
            this.reservedQuantity = quantity.subtract(availableQuantity);
        }

        if (product != null && product.getMinimumStockLevel() != null && availableQuantity != null) {
            this.isOutOfStock = availableQuantity.compareTo(BigDecimal.ZERO) == 0;
            this.isLowStock = !isOutOfStock && availableQuantity.compareTo(product.getMinimumStockLevel()) <= 0;

            // Calculate stock health percentage
            if (product.getMinimumStockLevel().compareTo(BigDecimal.ZERO) > 0) {
                this.stockHealthPercentage = availableQuantity
                    .divide(product.getMinimumStockLevel(), 2, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            }

            // Determine stock health status
            if (isOutOfStock) {
                this.stockHealthStatus = "OUT_OF_STOCK";
            } else if (isLowStock) {
                this.stockHealthStatus = "LOW_STOCK";
            } else if (stockHealthPercentage != null && stockHealthPercentage.compareTo(BigDecimal.valueOf(300)) > 0) {
                this.stockHealthStatus = "OVERSTOCK";
            } else {
                this.stockHealthStatus = "HEALTHY";
            }
        }

        // Calculate days since last update
        if (lastUpdated != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastUpdated, Instant.now());
            this.daysSinceLastUpdate = (int) daysBetween;
        }
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
        // Recalculate derived fields when product is set
        calculateDerivedFields();
    }

    public String getStockHealthStatus() {
        return stockHealthStatus;
    }

    public void setStockHealthStatus(String stockHealthStatus) {
        this.stockHealthStatus = stockHealthStatus;
    }

    public BigDecimal getStockHealthPercentage() {
        return stockHealthPercentage;
    }

    public void setStockHealthPercentage(BigDecimal stockHealthPercentage) {
        this.stockHealthPercentage = stockHealthPercentage;
    }

    public BigDecimal getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(BigDecimal reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Boolean getIsLowStock() {
        return isLowStock;
    }

    public void setIsLowStock(Boolean isLowStock) {
        this.isLowStock = isLowStock;
    }

    public Boolean getIsOutOfStock() {
        return isOutOfStock;
    }

    public void setIsOutOfStock(Boolean isOutOfStock) {
        this.isOutOfStock = isOutOfStock;
    }

    public Integer getDaysSinceLastUpdate() {
        return daysSinceLastUpdate;
    }

    public void setDaysSinceLastUpdate(Integer daysSinceLastUpdate) {
        this.daysSinceLastUpdate = daysSinceLastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventoryWithProductDTO)) {
            return false;
        }

        InventoryWithProductDTO that = (InventoryWithProductDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return (
            "InventoryWithProductDTO{" +
            "id=" +
            getId() +
            ", quantity=" +
            getQuantity() +
            ", availableQuantity=" +
            getAvailableQuantity() +
            ", status='" +
            getStatus() +
            "'" +
            ", location='" +
            getLocation() +
            "'" +
            ", lastUpdated='" +
            getLastUpdated() +
            "'" +
            ", stockHealthStatus='" +
            getStockHealthStatus() +
            "'" +
            ", stockHealthPercentage=" +
            getStockHealthPercentage() +
            ", reservedQuantity=" +
            getReservedQuantity() +
            ", isLowStock=" +
            getIsLowStock() +
            ", isOutOfStock=" +
            getIsOutOfStock() +
            ", daysSinceLastUpdate=" +
            getDaysSinceLastUpdate() +
            ", product=" +
            getProduct() +
            "}"
        );
    }
}
