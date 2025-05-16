package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.OrderStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.PurchaseOrder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PurchaseOrderDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private ZonedDateTime date;

    private String notes;

    @NotNull
    private OrderStatus status;

    private BigDecimal shipping;

    private BigDecimal missionFee;

    private BigDecimal handling;

    private BigDecimal costTotal;

    private BigDecimal tvaRate;

    private BigDecimal stampRate;

    private BigDecimal discountRate;

    private BigDecimal tvaAmount;

    private BigDecimal stampAmount;

    private BigDecimal discountAmount;

    private BigDecimal subTotal;

    private BigDecimal total;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private ClientAccountDTO clientAccount;

    private AdminDTO admin;

    private SupplierDTO supplier;

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

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getShipping() {
        return shipping;
    }

    public void setShipping(BigDecimal shipping) {
        this.shipping = shipping;
    }

    public BigDecimal getMissionFee() {
        return missionFee;
    }

    public void setMissionFee(BigDecimal missionFee) {
        this.missionFee = missionFee;
    }

    public BigDecimal getHandling() {
        return handling;
    }

    public void setHandling(BigDecimal handling) {
        this.handling = handling;
    }

    public BigDecimal getCostTotal() {
        return costTotal;
    }

    public void setCostTotal(BigDecimal costTotal) {
        this.costTotal = costTotal;
    }

    public BigDecimal getTvaRate() {
        return tvaRate;
    }

    public void setTvaRate(BigDecimal tvaRate) {
        this.tvaRate = tvaRate;
    }

    public BigDecimal getStampRate() {
        return stampRate;
    }

    public void setStampRate(BigDecimal stampRate) {
        this.stampRate = stampRate;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getTvaAmount() {
        return tvaAmount;
    }

    public void setTvaAmount(BigDecimal tvaAmount) {
        this.tvaAmount = tvaAmount;
    }

    public BigDecimal getStampAmount() {
        return stampAmount;
    }

    public void setStampAmount(BigDecimal stampAmount) {
        this.stampAmount = stampAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
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

    public ClientAccountDTO getClientAccount() {
        return clientAccount;
    }

    public void setClientAccount(ClientAccountDTO clientAccount) {
        this.clientAccount = clientAccount;
    }

    public AdminDTO getAdmin() {
        return admin;
    }

    public void setAdmin(AdminDTO admin) {
        this.admin = admin;
    }

    public SupplierDTO getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierDTO supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseOrderDTO)) {
            return false;
        }

        PurchaseOrderDTO purchaseOrderDTO = (PurchaseOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, purchaseOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseOrderDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", date='" + getDate() + "'" +
            ", notes='" + getNotes() + "'" +
            ", status='" + getStatus() + "'" +
            ", shipping=" + getShipping() +
            ", missionFee=" + getMissionFee() +
            ", handling=" + getHandling() +
            ", costTotal=" + getCostTotal() +
            ", tvaRate=" + getTvaRate() +
            ", stampRate=" + getStampRate() +
            ", discountRate=" + getDiscountRate() +
            ", tvaAmount=" + getTvaAmount() +
            ", stampAmount=" + getStampAmount() +
            ", discountAmount=" + getDiscountAmount() +
            ", subTotal=" + getSubTotal() +
            ", total=" + getTotal() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", clientAccount=" + getClientAccount() +
            ", admin=" + getAdmin() +
            ", supplier=" + getSupplier() +
            "}";
    }
}
