package com.adeem.stockflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A PaymentConfiguration.
 */
@Entity
@Table(name = "payment_configuration")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentConfiguration extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "online_payment_enabled")
    private Boolean onlinePaymentEnabled;

    @Column(name = "ccp")
    private String ccp;

    @Column(name = "rip")
    private String rip;

    @Column(name = "rib")
    private String rib;

    @Column(name = "iban")
    private String iban;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "address", "quota", "subscriptions" }, allowSetters = true)
    private ClientAccount clientAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PaymentConfiguration id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getOnlinePaymentEnabled() {
        return this.onlinePaymentEnabled;
    }

    public PaymentConfiguration onlinePaymentEnabled(Boolean onlinePaymentEnabled) {
        this.setOnlinePaymentEnabled(onlinePaymentEnabled);
        return this;
    }

    public void setOnlinePaymentEnabled(Boolean onlinePaymentEnabled) {
        this.onlinePaymentEnabled = onlinePaymentEnabled;
    }

    public String getCcp() {
        return this.ccp;
    }

    public PaymentConfiguration ccp(String ccp) {
        this.setCcp(ccp);
        return this;
    }

    public void setCcp(String ccp) {
        this.ccp = ccp;
    }

    public String getRip() {
        return this.rip;
    }

    public PaymentConfiguration rip(String rip) {
        this.setRip(rip);
        return this;
    }

    public void setRip(String rip) {
        this.rip = rip;
    }

    public String getRib() {
        return this.rib;
    }

    public PaymentConfiguration rib(String rib) {
        this.setRib(rib);
        return this;
    }

    public void setRib(String rib) {
        this.rib = rib;
    }

    public String getIban() {
        return this.iban;
    }

    public PaymentConfiguration iban(String iban) {
        this.setIban(iban);
        return this;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    // Inherited createdBy methods
    public PaymentConfiguration createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public PaymentConfiguration createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public PaymentConfiguration lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public PaymentConfiguration lastModifiedDate(Instant lastModifiedDate) {
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

    public PaymentConfiguration setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public ClientAccount getClientAccount() {
        return this.clientAccount;
    }

    public void setClientAccount(ClientAccount clientAccount) {
        this.clientAccount = clientAccount;
    }

    public PaymentConfiguration clientAccount(ClientAccount clientAccount) {
        this.setClientAccount(clientAccount);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentConfiguration)) {
            return false;
        }
        return getId() != null && getId().equals(((PaymentConfiguration) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentConfiguration{" +
            "id=" + getId() +
            ", onlinePaymentEnabled='" + getOnlinePaymentEnabled() + "'" +
            ", ccp='" + getCcp() + "'" +
            ", rip='" + getRip() + "'" +
            ", rib='" + getRib() + "'" +
            ", iban='" + getIban() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
