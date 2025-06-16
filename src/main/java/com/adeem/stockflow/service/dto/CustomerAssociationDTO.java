package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.AssociationStatus;
import com.adeem.stockflow.domain.enumeration.AssociationType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.CustomerClientAssociation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerAssociationDTO implements Serializable {

    private Long id;

    @NotNull
    private Long customerId;

    @NotNull
    private Long clientAccountId;

    private String clientAccountName;

    // Customer info (for company-side views)
    private String customerFirstName;
    private String customerLastName;
    private String customerPhone;
    private String customerEmail;

    @NotNull
    private AssociationType associationType;

    @NotNull
    private AssociationStatus status;

    @Size(max = 500)
    private String notes;

    // Audit fields
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    // Constructors
    public CustomerAssociationDTO() {}

    public CustomerAssociationDTO(Long customerId, Long clientAccountId, AssociationType associationType) {
        this.customerId = customerId;
        this.clientAccountId = clientAccountId;
        this.associationType = associationType;
        this.status = AssociationStatus.ACTIVE; // Default status
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getClientAccountId() {
        return clientAccountId;
    }

    public void setClientAccountId(Long clientAccountId) {
        this.clientAccountId = clientAccountId;
    }

    public String getClientAccountName() {
        return clientAccountName;
    }

    public void setClientAccountName(String clientAccountName) {
        this.clientAccountName = clientAccountName;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public AssociationType getAssociationType() {
        return associationType;
    }

    public void setAssociationType(AssociationType associationType) {
        this.associationType = associationType;
    }

    public AssociationStatus getStatus() {
        return status;
    }

    public void setStatus(AssociationStatus status) {
        this.status = status;
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

    // Computed properties
    public String getCustomerFullName() {
        if (customerFirstName != null && customerLastName != null) {
            return customerFirstName + " " + customerLastName;
        }
        return null;
    }

    public Boolean isActive() {
        return AssociationStatus.ACTIVE.equals(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerAssociationDTO)) {
            return false;
        }

        CustomerAssociationDTO associationDTO = (CustomerAssociationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, associationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerAssociationDTO{" +
            "id=" + getId() +
            ", customerId=" + getCustomerId() +
            ", clientAccountId=" + getClientAccountId() +
            ", clientAccountName='" + getClientAccountName() + "'" +
            ", customerFirstName='" + getCustomerFirstName() + "'" +
            ", customerLastName='" + getCustomerLastName() + "'" +
            ", associationType='" + getAssociationType() + "'" +
            ", status='" + getStatus() + "'" +
            ", notes='" + getNotes() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
