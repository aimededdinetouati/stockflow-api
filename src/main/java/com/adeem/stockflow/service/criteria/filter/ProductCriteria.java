package com.adeem.stockflow.service.criteria.filter;

import com.adeem.stockflow.domain.enumeration.ProductCategory;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adeem.stockflow.domain.Product} entity.
 * This class represents the filtering criteria used to retrieve products.
 */
public class ProductCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter name;
    private StringFilter code;
    private StringFilter manufacturerCode;
    private StringFilter upc;
    private BigDecimalFilter sellingPrice;
    private BigDecimalFilter costPrice;
    private BigDecimalFilter profitMargin;
    private BigDecimalFilter minimumStockLevel;
    private Filter<ProductCategory> category;
    private BooleanFilter applyTva;
    private BooleanFilter isVisibleToCustomers;
    private ZonedDateTimeFilter expirationDate;
    private LongFilter productFamilyId;
    private BigDecimalFilter inventoryQuantity;
    private BooleanFilter lowStock;

    public ProductCriteria() {}

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getCode() {
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public StringFilter getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(StringFilter manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public StringFilter getUpc() {
        return upc;
    }

    public void setUpc(StringFilter upc) {
        this.upc = upc;
    }

    public BigDecimalFilter getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimalFilter sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public BigDecimalFilter getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimalFilter costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimalFilter getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(BigDecimalFilter profitMargin) {
        this.profitMargin = profitMargin;
    }

    public BigDecimalFilter getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(BigDecimalFilter minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public Filter<ProductCategory> getCategory() {
        return category;
    }

    public void setCategory(Filter<ProductCategory> category) {
        this.category = category;
    }

    public BooleanFilter getApplyTva() {
        return applyTva;
    }

    public void setApplyTva(BooleanFilter applyTva) {
        this.applyTva = applyTva;
    }

    public BooleanFilter getIsVisibleToCustomers() {
        return isVisibleToCustomers;
    }

    public void setIsVisibleToCustomers(BooleanFilter isVisibleToCustomers) {
        this.isVisibleToCustomers = isVisibleToCustomers;
    }

    public ZonedDateTimeFilter getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(ZonedDateTimeFilter expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LongFilter getProductFamilyId() {
        return productFamilyId;
    }

    public void setProductFamilyId(LongFilter productFamilyId) {
        this.productFamilyId = productFamilyId;
    }

    public BigDecimalFilter getInventoryQuantity() {
        return inventoryQuantity;
    }

    public void setInventoryQuantity(BigDecimalFilter inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
    }

    public BooleanFilter getLowStock() {
        return lowStock;
    }

    public void setLowStock(BooleanFilter lowStock) {
        this.lowStock = lowStock;
    }

    @Override
    public String toString() {
        return (
            "ProductCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (manufacturerCode != null ? "manufacturerCode=" + manufacturerCode + ", " : "") +
            (upc != null ? "upc=" + upc + ", " : "") +
            (sellingPrice != null ? "sellingPrice=" + sellingPrice + ", " : "") +
            (costPrice != null ? "costPrice=" + costPrice + ", " : "") +
            (profitMargin != null ? "profitMargin=" + profitMargin + ", " : "") +
            (minimumStockLevel != null ? "minimumStockLevel=" + minimumStockLevel + ", " : "") +
            (category != null ? "category=" + category + ", " : "") +
            (applyTva != null ? "applyTva=" + applyTva + ", " : "") +
            (isVisibleToCustomers != null ? "isVisibleToCustomers=" + isVisibleToCustomers + ", " : "") +
            (expirationDate != null ? "expirationDate=" + expirationDate + ", " : "") +
            (productFamilyId != null ? "productFamilyId=" + productFamilyId + ", " : "") +
            (inventoryQuantity != null ? "inventoryQuantity=" + inventoryQuantity + ", " : "") +
            (lowStock != null ? "lowStock=" + lowStock + ", " : "") +
            "}"
        );
    }
}
