package com.adeem.stockflow.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.Quota} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuotaDTO implements Serializable {

    private Long id;

    private Integer users;

    private Integer products;

    private Integer productFamilies;

    private Integer showcasedProducts;

    private Integer saleOrders;

    private Integer purchaseOrders;

    private Integer customers;

    private Integer suppliers;

    private Integer shipments;

    @NotNull
    private ZonedDateTime resetDate;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Long clientAccountId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUsers() {
        return users;
    }

    public void setUsers(Integer users) {
        this.users = users;
    }

    public Integer getProducts() {
        return products;
    }

    public void setProducts(Integer products) {
        this.products = products;
    }

    public Integer getProductFamilies() {
        return productFamilies;
    }

    public void setProductFamilies(Integer productFamilies) {
        this.productFamilies = productFamilies;
    }

    public Integer getShowcasedProducts() {
        return showcasedProducts;
    }

    public void setShowcasedProducts(Integer showcasedProducts) {
        this.showcasedProducts = showcasedProducts;
    }

    public Integer getSaleOrders() {
        return saleOrders;
    }

    public void setSaleOrders(Integer saleOrders) {
        this.saleOrders = saleOrders;
    }

    public Integer getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(Integer purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public Integer getCustomers() {
        return customers;
    }

    public void setCustomers(Integer customers) {
        this.customers = customers;
    }

    public Integer getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Integer suppliers) {
        this.suppliers = suppliers;
    }

    public Integer getShipments() {
        return shipments;
    }

    public void setShipments(Integer shipments) {
        this.shipments = shipments;
    }

    public ZonedDateTime getResetDate() {
        return resetDate;
    }

    public void setResetDate(ZonedDateTime resetDate) {
        this.resetDate = resetDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Long getClientAccountId() {
        return clientAccountId;
    }

    public void setClientAccountId(Long clientAccountId) {
        this.clientAccountId = clientAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuotaDTO)) {
            return false;
        }

        QuotaDTO quotaDTO = (QuotaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, quotaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuotaDTO{" +
            "id=" + getId() +
            ", users=" + getUsers() +
            ", products=" + getProducts() +
            ", productFamilies=" + getProductFamilies() +
            ", showcasedProducts=" + getShowcasedProducts() +
            ", saleOrders=" + getSaleOrders() +
            ", purchaseOrders=" + getPurchaseOrders() +
            ", customers=" + getCustomers() +
            ", suppliers=" + getSuppliers() +
            ", shipments=" + getShipments() +
            ", resetDate='" + getResetDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
