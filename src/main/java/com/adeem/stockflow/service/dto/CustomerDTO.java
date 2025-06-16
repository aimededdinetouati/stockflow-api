package com.adeem.stockflow.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.Customer} entity.
 * Enhanced for multi-tenant IMS with marketplace functionality.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    @NotNull
    @Pattern(regexp = "^[+]?[0-9]{8,15}$", message = "Phone number must be between 8 and 15 digits")
    private String phone;

    @Size(max = 20)
    private String fax;

    @Size(max = 50)
    private String taxId;

    @Size(max = 100)
    private String registrationArticle;

    @Size(max = 50)
    private String statisticalId;

    @Size(max = 50)
    private String rc;

    @NotNull
    private Boolean enabled = true;

    // Relationship fields
    private Long createdByClientAccountId;
    private String createdByClientAccountName;
    private Boolean hasUserAccount;

    @Email
    private String email; // From User entity if exists

    // Association info (optional, loaded when needed)
    private List<CustomerAssociationDTO> associations;

    // Computed fields
    private String fullName; // firstName + lastName
    private Boolean isManaged; // created by company and no user account
    private Boolean isIndependent; // has user account

    // Audit fields
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    // Constructors
    public CustomerDTO() {}

    public CustomerDTO(Long id, String firstName, String lastName, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getRegistrationArticle() {
        return registrationArticle;
    }

    public void setRegistrationArticle(String registrationArticle) {
        this.registrationArticle = registrationArticle;
    }

    public String getStatisticalId() {
        return statisticalId;
    }

    public void setStatisticalId(String statisticalId) {
        this.statisticalId = statisticalId;
    }

    public String getRc() {
        return rc;
    }

    public void setRc(String rc) {
        this.rc = rc;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getCreatedByClientAccountId() {
        return createdByClientAccountId;
    }

    public void setCreatedByClientAccountId(Long createdByClientAccountId) {
        this.createdByClientAccountId = createdByClientAccountId;
    }

    public String getCreatedByClientAccountName() {
        return createdByClientAccountName;
    }

    public void setCreatedByClientAccountName(String createdByClientAccountName) {
        this.createdByClientAccountName = createdByClientAccountName;
    }

    public Boolean getHasUserAccount() {
        return hasUserAccount;
    }

    public void setHasUserAccount(Boolean hasUserAccount) {
        this.hasUserAccount = hasUserAccount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<CustomerAssociationDTO> getAssociations() {
        return associations;
    }

    public void setAssociations(List<CustomerAssociationDTO> associations) {
        this.associations = associations;
    }

    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getIsManaged() {
        return createdByClientAccountId != null && !Boolean.TRUE.equals(hasUserAccount);
    }

    public void setIsManaged(Boolean isManaged) {
        this.isManaged = isManaged;
    }

    public Boolean getIsIndependent() {
        return Boolean.TRUE.equals(hasUserAccount);
    }

    public void setIsIndependent(Boolean isIndependent) {
        this.isIndependent = isIndependent;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerDTO)) {
            return false;
        }

        CustomerDTO customerDTO = (CustomerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerDTO{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", phone='" + getPhone() + "'" +
            ", fax='" + getFax() + "'" +
            ", taxId='" + getTaxId() + "'" +
            ", registrationArticle='" + getRegistrationArticle() + "'" +
            ", statisticalId='" + getStatisticalId() + "'" +
            ", rc='" + getRc() + "'" +
            ", enabled='" + getEnabled() + "'" +
            ", createdByClientAccountId=" + getCreatedByClientAccountId() +
            ", createdByClientAccountName='" + getCreatedByClientAccountName() + "'" +
            ", hasUserAccount='" + getHasUserAccount() + "'" +
            ", email='" + getEmail() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
