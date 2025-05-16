package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.AccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
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
    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    @NotNull
    @Column(name = "phone", nullable = false)
    private String phone;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientAccount")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "quotas", "planFormula", "clientAccount" }, allowSetters = true)
    private Set<Subscription> subscriptions = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientAccount")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "clientAccount", "subscription" }, allowSetters = true)
    private Set<Quota> quotas = new HashSet<>();

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

    public String getContactPerson() {
        return this.contactPerson;
    }

    public ClientAccount contactPerson(String contactPerson) {
        this.setContactPerson(contactPerson);
        return this;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
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

    public Set<Subscription> getSubscriptions() {
        return this.subscriptions;
    }

    public void setSubscriptions(Set<Subscription> subscriptiones) {
        if (this.subscriptions != null) {
            this.subscriptions.forEach(i -> i.setClientAccount(null));
        }
        if (subscriptiones != null) {
            subscriptiones.forEach(i -> i.setClientAccount(this));
        }
        this.subscriptions = subscriptiones;
    }

    public ClientAccount subscriptions(Set<Subscription> subscriptiones) {
        this.setSubscriptions(subscriptiones);
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

    public Set<Quota> getQuotas() {
        return this.quotas;
    }

    public void setQuotas(Set<Quota> quotas) {
        if (this.quotas != null) {
            this.quotas.forEach(i -> i.setClientAccount(null));
        }
        if (quotas != null) {
            quotas.forEach(i -> i.setClientAccount(this));
        }
        this.quotas = quotas;
    }

    public ClientAccount quotas(Set<Quota> quotas) {
        this.setQuotas(quotas);
        return this;
    }

    public ClientAccount addQuotas(Quota quota) {
        this.quotas.add(quota);
        quota.setClientAccount(this);
        return this;
    }

    public ClientAccount removeQuotas(Quota quota) {
        this.quotas.remove(quota);
        quota.setClientAccount(null);
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
            ", contactPerson='" + getContactPerson() + "'" +
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
