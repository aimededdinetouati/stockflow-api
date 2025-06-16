package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.AssociationStatus;
import com.adeem.stockflow.domain.enumeration.AssociationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * CustomerClientAssociation entity.
 *
 * Represents the many-to-many relationship between customers and companies
 * with additional metadata about the type and status of the association.
 */
@Entity
@Table(name = "customer_client_association")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerClientAssociation extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties(value = { "createdByClientAccount", "user", "associations", "addresses" }, allowSetters = true)
    private Customer customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_account_id", nullable = false)
    @JsonIgnoreProperties(value = { "address", "admin", "customers", "suppliers" }, allowSetters = true)
    private ClientAccount clientAccount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "association_type", nullable = false)
    private AssociationType associationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssociationStatus status;

    @Column(name = "notes")
    private String notes;

    @Transient
    private boolean isPersisted;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CustomerClientAssociation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CustomerClientAssociation customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public ClientAccount getClientAccount() {
        return this.clientAccount;
    }

    public void setClientAccount(ClientAccount clientAccount) {
        this.clientAccount = clientAccount;
    }

    public CustomerClientAssociation clientAccount(ClientAccount clientAccount) {
        this.setClientAccount(clientAccount);
        return this;
    }

    public AssociationType getAssociationType() {
        return this.associationType;
    }

    public CustomerClientAssociation associationType(AssociationType associationType) {
        this.setAssociationType(associationType);
        return this;
    }

    public void setAssociationType(AssociationType associationType) {
        this.associationType = associationType;
    }

    public AssociationStatus getStatus() {
        return this.status;
    }

    public CustomerClientAssociation status(AssociationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AssociationStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return this.notes;
    }

    public CustomerClientAssociation notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public CustomerClientAssociation setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerClientAssociation)) {
            return false;
        }
        return getId() != null && getId().equals(((CustomerClientAssociation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerClientAssociation{" +
            "id=" + getId() +
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
