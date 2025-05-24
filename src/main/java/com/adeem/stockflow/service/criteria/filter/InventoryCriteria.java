package com.adeem.stockflow.service.criteria.filter;

import com.adeem.stockflow.domain.enumeration.InventoryStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adeem.stockflow.domain.Inventory} entity.
 * This class represents the filtering criteria used to retrieve inventory records.
 */
public class InventoryCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private BigDecimalFilter quantity;
    private BigDecimalFilter availableQuantity;
    private Filter<InventoryStatus> status;
    private StringFilter location;
    private InstantFilter lastUpdated;
    private LongFilter productId;
    private StringFilter productName;
    private StringFilter productCode;
    private StringFilter productCategory;
    private BigDecimalFilter minimumStockLevel;
    private BooleanFilter lowStock;
    private BooleanFilter outOfStock;
    private BigDecimalFilter stockLevel;

    public InventoryCriteria() {}

    public InventoryCriteria(InventoryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.availableQuantity = other.availableQuantity == null ? null : other.availableQuantity.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.location = other.location == null ? null : other.location.copy();
        this.lastUpdated = other.lastUpdated == null ? null : other.lastUpdated.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
        this.productName = other.productName == null ? null : other.productName.copy();
        this.productCode = other.productCode == null ? null : other.productCode.copy();
        this.productCategory = other.productCategory == null ? null : other.productCategory.copy();
        this.minimumStockLevel = other.minimumStockLevel == null ? null : other.minimumStockLevel.copy();
        this.lowStock = other.lowStock == null ? null : other.lowStock.copy();
        this.outOfStock = other.outOfStock == null ? null : other.outOfStock.copy();
        this.stockLevel = other.stockLevel == null ? null : other.stockLevel.copy();
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public BigDecimalFilter getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimalFilter quantity) {
        this.quantity = quantity;
    }

    public BigDecimalFilter getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimalFilter availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Filter<InventoryStatus> getStatus() {
        return status;
    }

    public void setStatus(Filter<InventoryStatus> status) {
        this.status = status;
    }

    public StringFilter getLocation() {
        return location;
    }

    public void setLocation(StringFilter location) {
        this.location = location;
    }

    public InstantFilter getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(InstantFilter lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    public StringFilter getProductName() {
        return productName;
    }

    public void setProductName(StringFilter productName) {
        this.productName = productName;
    }

    public StringFilter getProductCode() {
        return productCode;
    }

    public void setProductCode(StringFilter productCode) {
        this.productCode = productCode;
    }

    public StringFilter getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(StringFilter productCategory) {
        this.productCategory = productCategory;
    }

    public BigDecimalFilter getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(BigDecimalFilter minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public BooleanFilter getLowStock() {
        return lowStock;
    }

    public void setLowStock(BooleanFilter lowStock) {
        this.lowStock = lowStock;
    }

    public BooleanFilter getOutOfStock() {
        return outOfStock;
    }

    public void setOutOfStock(BooleanFilter outOfStock) {
        this.outOfStock = outOfStock;
    }

    public BigDecimalFilter getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(BigDecimalFilter stockLevel) {
        this.stockLevel = stockLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final InventoryCriteria that = (InventoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(availableQuantity, that.availableQuantity) &&
            Objects.equals(status, that.status) &&
            Objects.equals(location, that.location) &&
            Objects.equals(lastUpdated, that.lastUpdated) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(productName, that.productName) &&
            Objects.equals(productCode, that.productCode) &&
            Objects.equals(productCategory, that.productCategory) &&
            Objects.equals(minimumStockLevel, that.minimumStockLevel) &&
            Objects.equals(lowStock, that.lowStock) &&
            Objects.equals(outOfStock, that.outOfStock) &&
            Objects.equals(stockLevel, that.stockLevel)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            quantity,
            availableQuantity,
            status,
            location,
            lastUpdated,
            productId,
            productName,
            productCode,
            productCategory,
            minimumStockLevel,
            lowStock,
            outOfStock,
            stockLevel
        );
    }

    @Override
    public String toString() {
        return (
            "InventoryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (quantity != null ? "quantity=" + quantity + ", " : "") +
            (availableQuantity != null ? "availableQuantity=" + availableQuantity + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (location != null ? "location=" + location + ", " : "") +
            (lastUpdated != null ? "lastUpdated=" + lastUpdated + ", " : "") +
            (productId != null ? "productId=" + productId + ", " : "") +
            (productName != null ? "productName=" + productName + ", " : "") +
            (productCode != null ? "productCode=" + productCode + ", " : "") +
            (productCategory != null ? "productCategory=" + productCategory + ", " : "") +
            (minimumStockLevel != null ? "minimumStockLevel=" + minimumStockLevel + ", " : "") +
            (lowStock != null ? "lowStock=" + lowStock + ", " : "") +
            (outOfStock != null ? "outOfStock=" + outOfStock + ", " : "") +
            (stockLevel != null ? "stockLevel=" + stockLevel + ", " : "") +
            "}"
        );
    }
}
