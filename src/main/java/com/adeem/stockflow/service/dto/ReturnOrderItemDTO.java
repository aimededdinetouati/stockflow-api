package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.ItemCondition;
import com.adeem.stockflow.domain.enumeration.ReturnReason;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.ReturnOrderItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnOrderItemDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal quantity;

    @NotNull
    private BigDecimal unitPrice;

    @NotNull
    private BigDecimal subtotal;

    private BigDecimal allocatedDiscount;

    private BigDecimal total;

    @NotNull
    private ReturnReason returnReason;

    @NotNull
    private ItemCondition condition;

    private String notes;

    @NotNull
    private Boolean isRefundable;

    @NotNull
    private Boolean isRestockable;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private ProductDTO product;

    private SaleOrderItemDTO originalSaleOrderItem;

    private PurchaseOrderItemDTO originalPurchaseOrderItem;

    private Long returnOrderId;

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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getAllocatedDiscount() {
        return allocatedDiscount;
    }

    public void setAllocatedDiscount(BigDecimal allocatedDiscount) {
        this.allocatedDiscount = allocatedDiscount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public ReturnReason getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(ReturnReason returnReason) {
        this.returnReason = returnReason;
    }

    public ItemCondition getCondition() {
        return condition;
    }

    public void setCondition(ItemCondition condition) {
        this.condition = condition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsRefundable() {
        return isRefundable;
    }

    public void setIsRefundable(Boolean isRefundable) {
        this.isRefundable = isRefundable;
    }

    public Boolean getIsRestockable() {
        return isRestockable;
    }

    public void setIsRestockable(Boolean isRestockable) {
        this.isRestockable = isRestockable;
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

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public SaleOrderItemDTO getOriginalSaleOrderItem() {
        return originalSaleOrderItem;
    }

    public void setOriginalSaleOrderItem(SaleOrderItemDTO originalSaleOrderItem) {
        this.originalSaleOrderItem = originalSaleOrderItem;
    }

    public PurchaseOrderItemDTO getOriginalPurchaseOrderItem() {
        return originalPurchaseOrderItem;
    }

    public void setOriginalPurchaseOrderItem(PurchaseOrderItemDTO originalPurchaseOrderItem) {
        this.originalPurchaseOrderItem = originalPurchaseOrderItem;
    }

    public Long getReturnOrderId() {
        return returnOrderId;
    }

    public void setReturnOrderId(Long returnOrderId) {
        this.returnOrderId = returnOrderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnOrderItemDTO)) {
            return false;
        }

        ReturnOrderItemDTO returnOrderItemDTO = (ReturnOrderItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, returnOrderItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnOrderItemDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", subtotal=" + getSubtotal() +
            ", allocatedDiscount=" + getAllocatedDiscount() +
            ", total=" + getTotal() +
            ", returnReason='" + getReturnReason() + "'" +
            ", condition='" + getCondition() + "'" +
            ", notes='" + getNotes() + "'" +
            ", isRefundable='" + getIsRefundable() + "'" +
            ", isRestockable='" + getIsRestockable() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", product=" + getProduct() +
            ", originalSaleOrderItem=" + getOriginalSaleOrderItem() +
            ", originalPurchaseOrderItem=" + getOriginalPurchaseOrderItem() +
            ", returnOrder=" + getReturnOrderId() +
            "}";
    }
}
