package com.adeem.stockflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * Supports two customer types:
 * - Managed Customer: Created by company, no user account, can be managed by creator
 * - Independent Customer: Has a user account, self-managed, creator cannot modify
 */
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Customer extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "fax")
    private String fax;

    @Column(name = "tax_id")
    private String taxId;

    @Column(name = "registration_article")
    private String registrationArticle;

    @Column(name = "statistical_id")
    private String statisticalId;

    @Column(name = "rc")
    private String rc;

    /**
     * Creator company (nullable - independent customers have no creator)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_client_account_id")
    @JsonIgnoreProperties(value = { "address", "admin", "customers", "suppliers" }, allowSetters = true)
    private ClientAccount createdByClientAccount;

    /**
     * Marketplace account (nullable - managed customers may not have accounts)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private User user;

    /**
     * Soft delete flag
     */
    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /**
     * Customer-Company associations
     */
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "customer", "clientAccount" }, allowSetters = true)
    private Set<CustomerClientAssociation> associations = new HashSet<>();

    /**
     * Customer addresses
     */
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "customer", "clientAccount", "supplier" }, allowSetters = true)
    private Set<Address> addresses = new HashSet<>();

    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "cartItems", "customer" }, allowSetters = true)
    private Cart cart;

    @Transient
    private boolean isPersisted;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Customer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Customer firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Customer lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return this.phone;
    }

    public Customer phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return this.fax;
    }

    public Customer fax(String fax) {
        this.setFax(fax);
        return this;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getTaxId() {
        return this.taxId;
    }

    public Customer taxId(String taxId) {
        this.setTaxId(taxId);
        return this;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getRegistrationArticle() {
        return this.registrationArticle;
    }

    public Customer registrationArticle(String registrationArticle) {
        this.setRegistrationArticle(registrationArticle);
        return this;
    }

    public void setRegistrationArticle(String registrationArticle) {
        this.registrationArticle = registrationArticle;
    }

    public String getStatisticalId() {
        return this.statisticalId;
    }

    public Customer statisticalId(String statisticalId) {
        this.setStatisticalId(statisticalId);
        return this;
    }

    public void setStatisticalId(String statisticalId) {
        this.statisticalId = statisticalId;
    }

    public String getRc() {
        return this.rc;
    }

    public Customer rc(String rc) {
        this.setRc(rc);
        return this;
    }

    public void setRc(String rc) {
        this.rc = rc;
    }

    public ClientAccount getCreatedByClientAccount() {
        return this.createdByClientAccount;
    }

    public void setCreatedByClientAccount(ClientAccount clientAccount) {
        this.createdByClientAccount = clientAccount;
    }

    public Customer createdByClientAccount(ClientAccount clientAccount) {
        this.setCreatedByClientAccount(clientAccount);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Customer user(User user) {
        this.setUser(user);
        return this;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public Customer enabled(Boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<CustomerClientAssociation> getAssociations() {
        return this.associations;
    }

    public void setAssociations(Set<CustomerClientAssociation> customerClientAssociations) {
        if (this.associations != null) {
            this.associations.forEach(i -> i.setCustomer(null));
        }
        if (customerClientAssociations != null) {
            customerClientAssociations.forEach(i -> i.setCustomer(this));
        }
        this.associations = customerClientAssociations;
    }

    public Customer associations(Set<CustomerClientAssociation> customerClientAssociations) {
        this.setAssociations(customerClientAssociations);
        return this;
    }

    public Customer addAssociation(CustomerClientAssociation customerClientAssociation) {
        this.associations.add(customerClientAssociation);
        customerClientAssociation.setCustomer(this);
        return this;
    }

    public Customer removeAssociation(CustomerClientAssociation customerClientAssociation) {
        this.associations.remove(customerClientAssociation);
        customerClientAssociation.setCustomer(null);
        return this;
    }

    public Set<Address> getAddresses() {
        return this.addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        if (this.addresses != null) {
            this.addresses.forEach(i -> i.setCustomer(null));
        }
        if (addresses != null) {
            addresses.forEach(i -> i.setCustomer(this));
        }
        this.addresses = addresses;
    }

    public Customer addresses(Set<Address> addresses) {
        this.setAddresses(addresses);
        return this;
    }

    public Customer addAddress(Address address) {
        this.addresses.add(address);
        address.setCustomer(this);
        return this;
    }

    public Customer removeAddress(Address address) {
        this.addresses.remove(address);
        address.setCustomer(null);
        return this;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Customer setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return getId() != null && getId().equals(((Customer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
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
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
