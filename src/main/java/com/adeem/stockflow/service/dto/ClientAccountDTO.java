package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.AccountStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.ClientAccount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClientAccountDTO implements Serializable {

    private Long id;

    @NotNull
    private String companyName;

    @NotNull
    private String phone;

    @NotNull
    @Email
    private String email;

    private String fax;

    private String website;

    private String taxIdentifier;

    private String registrationArticle;

    private String statisticalId;

    private String commercialRegistry;

    private String bankAccount;

    private String bankName;

    private Long socialCapital;

    private AccountStatus status;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private AddressDTO address;

    private QuotaDTO quota;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTaxIdentifier() {
        return taxIdentifier;
    }

    public void setTaxIdentifier(String taxIdentifier) {
        this.taxIdentifier = taxIdentifier;
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

    public String getCommercialRegistry() {
        return commercialRegistry;
    }

    public void setCommercialRegistry(String commercialRegistry) {
        this.commercialRegistry = commercialRegistry;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Long getSocialCapital() {
        return socialCapital;
    }

    public void setSocialCapital(Long socialCapital) {
        this.socialCapital = socialCapital;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
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

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public QuotaDTO getQuota() {
        return quota;
    }

    public void setQuota(QuotaDTO quota) {
        this.quota = quota;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientAccountDTO)) {
            return false;
        }

        ClientAccountDTO clientAccountDTO = (ClientAccountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, clientAccountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientAccountDTO{" +
            "id=" + getId() +
            ", companyName='" + getCompanyName() + "'" +
            ", phone='" + getPhone() + "'" +
            ", email='" + getEmail() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", address=" + getAddress() +
            ", quota=" + getQuota() +
            "}";
    }
}
