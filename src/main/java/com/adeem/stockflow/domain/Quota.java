package com.adeem.stockflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A Quota.
 */
@Entity
@Table(name = "quota")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Quota extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "users")
    private Integer users = 1;

    @Column(name = "products")
    private Integer products = 0;

    @Column(name = "product_families")
    private Integer productFamilies = 0;

    @Column(name = "showcased_products")
    private Integer showcasedProducts = 0;

    @Column(name = "sale_orders")
    private Integer saleOrders = 0;

    @Column(name = "purchase_orders")
    private Integer purchaseOrders = 0;

    @Column(name = "customers")
    private Integer customers = 0;

    @Column(name = "suppliers")
    private Integer suppliers = 0;

    @Column(name = "shipments")
    private Integer shipments = 0;

    @NotNull
    @Column(name = "reset_date", nullable = false)
    private ZonedDateTime resetDate;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @JsonIgnoreProperties(value = { "address", "quota", "subscriptions" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "quota")
    private ClientAccount clientAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Quota id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUsers() {
        return this.users;
    }

    public Quota users(Integer users) {
        this.setUsers(users);
        return this;
    }

    public void setUsers(Integer users) {
        this.users = users;
    }

    public Integer getProducts() {
        return this.products;
    }

    public Quota products(Integer products) {
        this.setProducts(products);
        return this;
    }

    public void setProducts(Integer products) {
        this.products = products;
    }

    public Integer getProductFamilies() {
        return this.productFamilies;
    }

    public Quota productFamilies(Integer productFamilies) {
        this.setProductFamilies(productFamilies);
        return this;
    }

    public void setProductFamilies(Integer productFamilies) {
        this.productFamilies = productFamilies;
    }

    public Integer getShowcasedProducts() {
        return this.showcasedProducts;
    }

    public Quota showcasedProducts(Integer showcasedProducts) {
        this.setShowcasedProducts(showcasedProducts);
        return this;
    }

    public void setShowcasedProducts(Integer showcasedProducts) {
        this.showcasedProducts = showcasedProducts;
    }

    public Integer getSaleOrders() {
        return this.saleOrders;
    }

    public Quota saleOrders(Integer saleOrders) {
        this.setSaleOrders(saleOrders);
        return this;
    }

    public void setSaleOrders(Integer saleOrders) {
        this.saleOrders = saleOrders;
    }

    public Integer getPurchaseOrders() {
        return this.purchaseOrders;
    }

    public Quota purchaseOrders(Integer purchaseOrders) {
        this.setPurchaseOrders(purchaseOrders);
        return this;
    }

    public void setPurchaseOrders(Integer purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public Integer getCustomers() {
        return this.customers;
    }

    public Quota customers(Integer customers) {
        this.setCustomers(customers);
        return this;
    }

    public void setCustomers(Integer customers) {
        this.customers = customers;
    }

    public Integer getSuppliers() {
        return this.suppliers;
    }

    public Quota suppliers(Integer suppliers) {
        this.setSuppliers(suppliers);
        return this;
    }

    public void setSuppliers(Integer suppliers) {
        this.suppliers = suppliers;
    }

    public Integer getShipments() {
        return this.shipments;
    }

    public Quota shipments(Integer shipments) {
        this.setShipments(shipments);
        return this;
    }

    public void setShipments(Integer shipments) {
        this.shipments = shipments;
    }

    public ZonedDateTime getResetDate() {
        return this.resetDate;
    }

    public Quota resetDate(ZonedDateTime resetDate) {
        this.setResetDate(resetDate);
        return this;
    }

    public void setResetDate(ZonedDateTime resetDate) {
        this.resetDate = resetDate;
    }

    // Inherited createdBy methods
    public Quota createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public Quota createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public Quota lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public Quota lastModifiedDate(Instant lastModifiedDate) {
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

    public Quota setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public ClientAccount getClientAccount() {
        return this.clientAccount;
    }

    public void setClientAccount(ClientAccount clientAccount) {
        if (this.clientAccount != null) {
            this.clientAccount.setQuota(null);
        }
        if (clientAccount != null) {
            clientAccount.setQuota(this);
        }
        this.clientAccount = clientAccount;
    }

    public Quota clientAccount(ClientAccount clientAccount) {
        this.setClientAccount(clientAccount);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quota)) {
            return false;
        }
        return getId() != null && getId().equals(((Quota) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Quota{" +
            "id=" + getId() +
            ", users=" + getUsers() +
            ", products=" + getProducts() +
            ", productFamilies=" + getProductFamilies() +
            ", showcasedProducts=" + getShowcasedProducts() +
            ", saleOrders=" + getSaleOrders() +
            ", purchaseOrders=" + getPurchaseOrders() +
            ", customers=" + getCustomers() +
            ", suppliers=" + getSuppliers() +
            ", shipments=" + getShipments() +
            ", resetDate='" + getResetDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
