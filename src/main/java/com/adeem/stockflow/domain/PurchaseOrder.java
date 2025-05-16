package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A PurchaseOrder.
 */
@Entity
@Table(name = "purchase_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PurchaseOrder extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false)
    private String reference;

    @NotNull
    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @Column(name = "notes")
    private String notes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "shipping", precision = 21, scale = 2)
    private BigDecimal shipping;

    @Column(name = "mission_fee", precision = 21, scale = 2)
    private BigDecimal missionFee;

    @Column(name = "handling", precision = 21, scale = 2)
    private BigDecimal handling;

    @Column(name = "cost_total", precision = 21, scale = 2)
    private BigDecimal costTotal;

    @Column(name = "tva_rate", precision = 21, scale = 2)
    private BigDecimal tvaRate;

    @Column(name = "stamp_rate", precision = 21, scale = 2)
    private BigDecimal stampRate;

    @Column(name = "discount_rate", precision = 21, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "tva_amount", precision = 21, scale = 2)
    private BigDecimal tvaAmount;

    @Column(name = "stamp_amount", precision = 21, scale = 2)
    private BigDecimal stampAmount;

    @Column(name = "discount_amount", precision = 21, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "sub_total", precision = 21, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "total", precision = 21, scale = 2)
    private BigDecimal total;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchaseOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "purchaseOrder" }, allowSetters = true)
    private Set<PurchaseOrderItem> orderItems = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "address", "subscriptions", "quotas" }, allowSetters = true)
    private ClientAccount clientAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "userRoles", "clientAccount" }, allowSetters = true)
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "address", "clientAccount" }, allowSetters = true)
    private Supplier supplier;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PurchaseOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public PurchaseOrder reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public PurchaseOrder date(ZonedDateTime date) {
        this.setDate(date);
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getNotes() {
        return this.notes;
    }

    public PurchaseOrder notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public PurchaseOrder status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getShipping() {
        return this.shipping;
    }

    public PurchaseOrder shipping(BigDecimal shipping) {
        this.setShipping(shipping);
        return this;
    }

    public void setShipping(BigDecimal shipping) {
        this.shipping = shipping;
    }

    public BigDecimal getMissionFee() {
        return this.missionFee;
    }

    public PurchaseOrder missionFee(BigDecimal missionFee) {
        this.setMissionFee(missionFee);
        return this;
    }

    public void setMissionFee(BigDecimal missionFee) {
        this.missionFee = missionFee;
    }

    public BigDecimal getHandling() {
        return this.handling;
    }

    public PurchaseOrder handling(BigDecimal handling) {
        this.setHandling(handling);
        return this;
    }

    public void setHandling(BigDecimal handling) {
        this.handling = handling;
    }

    public BigDecimal getCostTotal() {
        return this.costTotal;
    }

    public PurchaseOrder costTotal(BigDecimal costTotal) {
        this.setCostTotal(costTotal);
        return this;
    }

    public void setCostTotal(BigDecimal costTotal) {
        this.costTotal = costTotal;
    }

    public BigDecimal getTvaRate() {
        return this.tvaRate;
    }

    public PurchaseOrder tvaRate(BigDecimal tvaRate) {
        this.setTvaRate(tvaRate);
        return this;
    }

    public void setTvaRate(BigDecimal tvaRate) {
        this.tvaRate = tvaRate;
    }

    public BigDecimal getStampRate() {
        return this.stampRate;
    }

    public PurchaseOrder stampRate(BigDecimal stampRate) {
        this.setStampRate(stampRate);
        return this;
    }

    public void setStampRate(BigDecimal stampRate) {
        this.stampRate = stampRate;
    }

    public BigDecimal getDiscountRate() {
        return this.discountRate;
    }

    public PurchaseOrder discountRate(BigDecimal discountRate) {
        this.setDiscountRate(discountRate);
        return this;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getTvaAmount() {
        return this.tvaAmount;
    }

    public PurchaseOrder tvaAmount(BigDecimal tvaAmount) {
        this.setTvaAmount(tvaAmount);
        return this;
    }

    public void setTvaAmount(BigDecimal tvaAmount) {
        this.tvaAmount = tvaAmount;
    }

    public BigDecimal getStampAmount() {
        return this.stampAmount;
    }

    public PurchaseOrder stampAmount(BigDecimal stampAmount) {
        this.setStampAmount(stampAmount);
        return this;
    }

    public void setStampAmount(BigDecimal stampAmount) {
        this.stampAmount = stampAmount;
    }

    public BigDecimal getDiscountAmount() {
        return this.discountAmount;
    }

    public PurchaseOrder discountAmount(BigDecimal discountAmount) {
        this.setDiscountAmount(discountAmount);
        return this;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getSubTotal() {
        return this.subTotal;
    }

    public PurchaseOrder subTotal(BigDecimal subTotal) {
        this.setSubTotal(subTotal);
        return this;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public PurchaseOrder total(BigDecimal total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    // Inherited createdBy methods
    public PurchaseOrder createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public PurchaseOrder createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public PurchaseOrder lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public PurchaseOrder lastModifiedDate(Instant lastModifiedDate) {
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

    public PurchaseOrder setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<PurchaseOrderItem> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set<PurchaseOrderItem> purchaseOrderItems) {
        if (this.orderItems != null) {
            this.orderItems.forEach(i -> i.setPurchaseOrder(null));
        }
        if (purchaseOrderItems != null) {
            purchaseOrderItems.forEach(i -> i.setPurchaseOrder(this));
        }
        this.orderItems = purchaseOrderItems;
    }

    public PurchaseOrder orderItems(Set<PurchaseOrderItem> purchaseOrderItems) {
        this.setOrderItems(purchaseOrderItems);
        return this;
    }

    public PurchaseOrder addOrderItems(PurchaseOrderItem purchaseOrderItem) {
        this.orderItems.add(purchaseOrderItem);
        purchaseOrderItem.setPurchaseOrder(this);
        return this;
    }

    public PurchaseOrder removeOrderItems(PurchaseOrderItem purchaseOrderItem) {
        this.orderItems.remove(purchaseOrderItem);
        purchaseOrderItem.setPurchaseOrder(null);
        return this;
    }

    public ClientAccount getClientAccount() {
        return this.clientAccount;
    }

    public void setClientAccount(ClientAccount clientAccount) {
        this.clientAccount = clientAccount;
    }

    public PurchaseOrder clientAccount(ClientAccount clientAccount) {
        this.setClientAccount(clientAccount);
        return this;
    }

    public Admin getAdmin() {
        return this.admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public PurchaseOrder admin(Admin admin) {
        this.setAdmin(admin);
        return this;
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public PurchaseOrder supplier(Supplier supplier) {
        this.setSupplier(supplier);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((PurchaseOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseOrder{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", date='" + getDate() + "'" +
            ", notes='" + getNotes() + "'" +
            ", status='" + getStatus() + "'" +
            ", shipping=" + getShipping() +
            ", missionFee=" + getMissionFee() +
            ", handling=" + getHandling() +
            ", costTotal=" + getCostTotal() +
            ", tvaRate=" + getTvaRate() +
            ", stampRate=" + getStampRate() +
            ", discountRate=" + getDiscountRate() +
            ", tvaAmount=" + getTvaAmount() +
            ", stampAmount=" + getStampAmount() +
            ", discountAmount=" + getDiscountAmount() +
            ", subTotal=" + getSubTotal() +
            ", total=" + getTotal() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
