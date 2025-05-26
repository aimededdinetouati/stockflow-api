package com.adeem.stockflow.service.criteria.filter;

import java.io.Serializable;
import java.time.Instant;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adeem.stockflow.domain.ProductFamily} entity.
 * This class represents the filtering criteria used to retrieve product families.
 */
public class ProductFamilyCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter name;
    private InstantFilter createdDate;
    private InstantFilter lastModifiedDate;
    private StringFilter createdBy;
    private BooleanFilter hasProducts;
    private LongFilter productCount;
    private StringFilter productCategory;
    private BooleanFilter hasLowStockProducts;
    private LongFilter clientAccountId;

    public ProductFamilyCriteria() {}

    public ProductFamilyCriteria(ProductFamilyCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.hasProducts = other.hasProducts == null ? null : other.hasProducts.copy();
        this.productCount = other.productCount == null ? null : other.productCount.copy();
        this.productCategory = other.productCategory == null ? null : other.productCategory.copy();
        this.hasLowStockProducts = other.hasLowStockProducts == null ? null : other.hasLowStockProducts.copy();
        this.clientAccountId = other.clientAccountId == null ? null : other.clientAccountId.copy();
    }

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

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public BooleanFilter getHasProducts() {
        return hasProducts;
    }

    public void setHasProducts(BooleanFilter hasProducts) {
        this.hasProducts = hasProducts;
    }

    public LongFilter getProductCount() {
        return productCount;
    }

    public void setProductCount(LongFilter productCount) {
        this.productCount = productCount;
    }

    public StringFilter getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(StringFilter productCategory) {
        this.productCategory = productCategory;
    }

    public BooleanFilter getHasLowStockProducts() {
        return hasLowStockProducts;
    }

    public void setHasLowStockProducts(BooleanFilter hasLowStockProducts) {
        this.hasLowStockProducts = hasLowStockProducts;
    }

    public LongFilter getClientAccountId() {
        return clientAccountId;
    }

    public void setClientAccountId(LongFilter clientAccountId) {
        this.clientAccountId = clientAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductFamilyCriteria that = (ProductFamilyCriteria) o;
        return (
            java.util.Objects.equals(id, that.id) &&
            java.util.Objects.equals(name, that.name) &&
            java.util.Objects.equals(createdDate, that.createdDate) &&
            java.util.Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            java.util.Objects.equals(createdBy, that.createdBy) &&
            java.util.Objects.equals(hasProducts, that.hasProducts) &&
            java.util.Objects.equals(productCount, that.productCount) &&
            java.util.Objects.equals(productCategory, that.productCategory) &&
            java.util.Objects.equals(hasLowStockProducts, that.hasLowStockProducts) &&
            java.util.Objects.equals(clientAccountId, that.clientAccountId)
        );
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(
            id,
            name,
            createdDate,
            lastModifiedDate,
            createdBy,
            hasProducts,
            productCount,
            productCategory,
            hasLowStockProducts,
            clientAccountId
        );
    }

    @Override
    public String toString() {
        return (
            "ProductFamilyCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (hasProducts != null ? "hasProducts=" + hasProducts + ", " : "") +
            (productCount != null ? "productCount=" + productCount + ", " : "") +
            (productCategory != null ? "productCategory=" + productCategory + ", " : "") +
            (hasLowStockProducts != null ? "hasLowStockProducts=" + hasLowStockProducts + ", " : "") +
            (clientAccountId != null ? "clientAccountId=" + clientAccountId + ", " : "") +
            "}"
        );
    }
}
