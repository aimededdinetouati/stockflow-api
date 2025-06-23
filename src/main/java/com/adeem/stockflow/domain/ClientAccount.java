package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.AccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A ClientAccount.
 */
@Entity
@Table(name = "client_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClientAccount extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @NotNull
    @Column(name = "phone", nullable = false)
    private String phone;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "fax")
    private String fax;

    @Column(name = "website")
    private String website;

    @Column(name = "tax_identifier") // IF - Identifiant Fiscal
    private String taxIdentifier;

    @Column(name = "registration_article") // AI - Article d'Immatriculation
    private String registrationArticle;

    @Column(name = "statistical_id") // NIS - Numéro d'Identification Statistique
    private String statisticalId;

    @Column(name = "commercial_registry") // RC - Registre de Commerce
    private String commercialRegistry;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "social_capital")
    private Long socialCapital;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    @Column(name = "registration_date")
    private Instant registrationDate;

    @Column(name = "last_activity_date")
    private Instant lastActivityDate;

    @Column(name = "default_shipping_cost", precision = 21, scale = 2)
    private BigDecimal defaultShippingCost;

    @Column(name = "reservation_timeout_hours")
    private Integer reservationTimeoutHours;

    @Column(name = "yalidine_api_key")
    private String yalidineApiKey;

    @Column(name = "yalidine_api_secret")
    private String yalidineApiSecret;

    @Column(name = "yalidine_enabled")
    private Boolean yalidineEnabled;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @JsonIgnoreProperties(value = { "customer", "clientAccount", "supplier" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Address address;

    @JsonIgnoreProperties(value = { "clientAccount" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Quota quota;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientAccount")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "planFormula", "clientAccount" }, allowSetters = true)
    private Set<Subscription> subscriptions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClientAccount id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public ClientAccount companyName(String companyName) {
        this.setCompanyName(companyName);
        return this;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhone() {
        return this.phone;
    }

    public ClientAccount phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return this.email;
    }

    public ClientAccount email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountStatus getStatus() {
        return this.status;
    }

    public ClientAccount status(AccountStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
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

    // Inherited createdBy methods
    public ClientAccount createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public ClientAccount createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public ClientAccount lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public ClientAccount lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public Instant getRegistrationDate() {
        return this.registrationDate;
    }

    public ClientAccount registrationDate(Instant registrationDate) {
        this.setRegistrationDate(registrationDate);
        return this;
    }

    public void setRegistrationDate(Instant registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Instant getLastActivityDate() {
        return this.lastActivityDate;
    }

    public ClientAccount lastActivityDate(Instant lastActivityDate) {
        this.setLastActivityDate(lastActivityDate);
        return this;
    }

    public void setLastActivityDate(Instant lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    // NEW FIELD GETTERS/SETTERS
    public BigDecimal getDefaultShippingCost() {
        return this.defaultShippingCost;
    }

    public ClientAccount defaultShippingCost(BigDecimal defaultShippingCost) {
        this.setDefaultShippingCost(defaultShippingCost);
        return this;
    }

    public void setDefaultShippingCost(BigDecimal defaultShippingCost) {
        this.defaultShippingCost = defaultShippingCost;
    }

    public Integer getReservationTimeoutHours() {
        return this.reservationTimeoutHours;
    }

    public ClientAccount reservationTimeoutHours(Integer reservationTimeoutHours) {
        this.setReservationTimeoutHours(reservationTimeoutHours);
        return this;
    }

    public void setReservationTimeoutHours(Integer reservationTimeoutHours) {
        this.reservationTimeoutHours = reservationTimeoutHours;
    }

    public String getYalidineApiKey() {
        return this.yalidineApiKey;
    }

    public ClientAccount yalidineApiKey(String yalidineApiKey) {
        this.setYalidineApiKey(yalidineApiKey);
        return this;
    }

    public void setYalidineApiKey(String yalidineApiKey) {
        this.yalidineApiKey = yalidineApiKey;
    }

    public String getYalidineApiSecret() {
        return this.yalidineApiSecret;
    }

    public ClientAccount yalidineApiSecret(String yalidineApiSecret) {
        this.setYalidineApiSecret(yalidineApiSecret);
        return this;
    }

    public void setYalidineApiSecret(String yalidineApiSecret) {
        this.yalidineApiSecret = yalidineApiSecret;
    }

    public Boolean getYalidineEnabled() {
        return this.yalidineEnabled;
    }

    public ClientAccount yalidineEnabled(Boolean yalidineEnabled) {
        this.setYalidineEnabled(yalidineEnabled);
        return this;
    }

    public void setYalidineEnabled(Boolean yalidineEnabled) {
        this.yalidineEnabled = yalidineEnabled;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    @org.springframework.data.annotation.Transient
    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public ClientAccount setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ClientAccount address(Address address) {
        this.setAddress(address);
        return this;
    }

    public Quota getQuota() {
        return this.quota;
    }

    public void setQuota(Quota quota) {
        this.quota = quota;
    }

    public ClientAccount quota(Quota quota) {
        this.setQuota(quota);
        return this;
    }

    public Set<Subscription> getSubscriptions() {
        return this.subscriptions;
    }

    public void setSubscriptions(Set<Subscription> subscriptions) {
        if (this.subscriptions != null) {
            this.subscriptions.forEach(i -> i.setClientAccount(null));
        }
        if (subscriptions != null) {
            subscriptions.forEach(i -> i.setClientAccount(this));
        }
        this.subscriptions = subscriptions;
    }

    public ClientAccount subscriptions(Set<Subscription> subscriptions) {
        this.setSubscriptions(subscriptions);
        return this;
    }

    public ClientAccount addSubscriptions(Subscription subscription) {
        this.subscriptions.add(subscription);
        subscription.setClientAccount(this);
        return this;
    }

    public ClientAccount removeSubscriptions(Subscription subscription) {
        this.subscriptions.remove(subscription);
        subscription.setClientAccount(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientAccount)) {
            return false;
        }
        return getId() != null && getId().equals(((ClientAccount) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientAccount{" +
            "id=" + getId() +
            ", companyName='" + getCompanyName() + "'" +
            ", phone='" + getPhone() + "'" +
            ", email='" + getEmail() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
