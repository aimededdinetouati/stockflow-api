package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.DiscountAllocationMethod;
import com.adeem.stockflow.domain.enumeration.ReturnStatus;
import com.adeem.stockflow.domain.enumeration.ReturnType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.ReturnOrder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnOrderDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private ZonedDateTime returnDate;

    private ZonedDateTime processedDate;

    @NotNull
    private ReturnStatus status;

    @NotNull
    private ReturnType returnType;

    private String notes;

    private BigDecimal refundAmount;

    @NotNull
    private String originalOrderReference;

    @NotNull
    private Boolean isPartialReturn;

    private DiscountAllocationMethod discountAllocationMethod;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Long clientAccountId;

    private AdminDTO processedBy;

    private CustomerDTO customer;

    private SupplierDTO supplier;

    private SaleOrderDTO originalSaleOrder;

    private PurchaseOrderDTO originalPurchaseOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ZonedDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(ZonedDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public ZonedDateTime getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(ZonedDateTime processedDate) {
        this.processedDate = processedDate;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getOriginalOrderReference() {
        return originalOrderReference;
    }

    public void setOriginalOrderReference(String originalOrderReference) {
        this.originalOrderReference = originalOrderReference;
    }

    public Boolean getIsPartialReturn() {
        return isPartialReturn;
    }

    public void setIsPartialReturn(Boolean isPartialReturn) {
        this.isPartialReturn = isPartialReturn;
    }

    public DiscountAllocationMethod getDiscountAllocationMethod() {
        return discountAllocationMethod;
    }

    public void setDiscountAllocationMethod(DiscountAllocationMethod discountAllocationMethod) {
        this.discountAllocationMethod = discountAllocationMethod;
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

    public AdminDTO getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(AdminDTO processedBy) {
        this.processedBy = processedBy;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public SupplierDTO getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierDTO supplier) {
        this.supplier = supplier;
    }

    public SaleOrderDTO getOriginalSaleOrder() {
        return originalSaleOrder;
    }

    public void setOriginalSaleOrder(SaleOrderDTO originalSaleOrder) {
        this.originalSaleOrder = originalSaleOrder;
    }

    public PurchaseOrderDTO getOriginalPurchaseOrder() {
        return originalPurchaseOrder;
    }

    public void setOriginalPurchaseOrder(PurchaseOrderDTO originalPurchaseOrder) {
        this.originalPurchaseOrder = originalPurchaseOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnOrderDTO)) {
            return false;
        }

        ReturnOrderDTO returnOrderDTO = (ReturnOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, returnOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnOrderDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", returnDate='" + getReturnDate() + "'" +
            ", processedDate='" + getProcessedDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", returnType='" + getReturnType() + "'" +
            ", notes='" + getNotes() + "'" +
            ", refundAmount=" + getRefundAmount() +
            ", originalOrderReference='" + getOriginalOrderReference() + "'" +
            ", isPartialReturn='" + getIsPartialReturn() + "'" +
            ", discountAllocationMethod='" + getDiscountAllocationMethod() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", clientAccountId=" + getClientAccountId() +
            ", processedBy=" + getProcessedBy() +
            ", customer=" + getCustomer() +
            ", supplier=" + getSupplier() +
            ", originalSaleOrder=" + getOriginalSaleOrder() +
            ", originalPurchaseOrder=" + getOriginalPurchaseOrder() +
            "}";
    }
}
