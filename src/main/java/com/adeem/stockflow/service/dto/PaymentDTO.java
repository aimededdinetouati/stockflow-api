package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.PaymentGateway;
import com.adeem.stockflow.domain.enumeration.PaymentMethod;
import com.adeem.stockflow.domain.enumeration.PaymentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.Payment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private ZonedDateTime date;

    @NotNull
    private PaymentMethod method;

    @NotNull
    private PaymentStatus status;

    @NotNull
    private PaymentGateway gateway;

    private String chargilyCheckoutUrl;

    private String chargilyTransactionId;

    private String bankName;

    private String accountNumber;

    private String transferReferenceNumber;

    @NotNull
    private Boolean reconciled;

    private ZonedDateTime reconciledDate;

    private String reconciledBy;

    private String notes;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Long clientAccountId;

    private CustomerDTO customer;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentGateway getGateway() {
        return gateway;
    }

    public void setGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public String getChargilyCheckoutUrl() {
        return chargilyCheckoutUrl;
    }

    public void setChargilyCheckoutUrl(String chargilyCheckoutUrl) {
        this.chargilyCheckoutUrl = chargilyCheckoutUrl;
    }

    public String getChargilyTransactionId() {
        return chargilyTransactionId;
    }

    public void setChargilyTransactionId(String chargilyTransactionId) {
        this.chargilyTransactionId = chargilyTransactionId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTransferReferenceNumber() {
        return transferReferenceNumber;
    }

    public void setTransferReferenceNumber(String transferReferenceNumber) {
        this.transferReferenceNumber = transferReferenceNumber;
    }

    public Boolean getReconciled() {
        return reconciled;
    }

    public void setReconciled(Boolean reconciled) {
        this.reconciled = reconciled;
    }

    public ZonedDateTime getReconciledDate() {
        return reconciledDate;
    }

    public void setReconciledDate(ZonedDateTime reconciledDate) {
        this.reconciledDate = reconciledDate;
    }

    public String getReconciledBy() {
        return reconciledBy;
    }

    public void setReconciledBy(String reconciledBy) {
        this.reconciledBy = reconciledBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentDTO)) {
            return false;
        }

        PaymentDTO paymentDTO = (PaymentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", amount=" + getAmount() +
            ", date='" + getDate() + "'" +
            ", method='" + getMethod() + "'" +
            ", status='" + getStatus() + "'" +
            ", gateway='" + getGateway() + "'" +
            ", chargilyCheckoutUrl='" + getChargilyCheckoutUrl() + "'" +
            ", chargilyTransactionId='" + getChargilyTransactionId() + "'" +
            ", bankName='" + getBankName() + "'" +
            ", accountNumber='" + getAccountNumber() + "'" +
            ", transferReferenceNumber='" + getTransferReferenceNumber() + "'" +
            ", reconciled='" + getReconciled() + "'" +
            ", reconciledDate='" + getReconciledDate() + "'" +
            ", reconciledBy='" + getReconciledBy() + "'" +
            ", notes='" + getNotes() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", clientAccountId=" + getClientAccountId() +
            ", customer=" + getCustomer() +
            "}";
    }
}
