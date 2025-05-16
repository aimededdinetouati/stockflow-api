package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.OrderStatus;
import com.adeem.stockflow.domain.enumeration.SaleType;
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
 * A SaleOrder.
 */
@Entity
@Table(name = "sale_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleOrder extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

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

    @Column(name = "due_date")
    private ZonedDateTime dueDate;

    @Column(name = "notes")
    private String notes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_type")
    private SaleType saleType;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @JsonIgnoreProperties(value = { "attachments", "clientAccount", "customer", "saleOrder" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Payment payment;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "saleOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "saleOrder" }, allowSetters = true)
    private Set<SaleOrderItem> orderItems = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "address", "subscriptions", "quotas" }, allowSetters = true)
    private ClientAccount clientAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "addressLists", "carts", "clientAccount" }, allowSetters = true)
    private Customer customer;

    @JsonIgnoreProperties(value = { "saleOrder", "address" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "saleOrder")
    private Shipment shipment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SaleOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public SaleOrder reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public SaleOrder date(ZonedDateTime date) {
        this.setDate(date);
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public ZonedDateTime getDueDate() {
        return this.dueDate;
    }

    public SaleOrder dueDate(ZonedDateTime dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public SaleOrder notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public SaleOrder status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTvaRate() {
        return this.tvaRate;
    }

    public SaleOrder tvaRate(BigDecimal tvaRate) {
        this.setTvaRate(tvaRate);
        return this;
    }

    public void setTvaRate(BigDecimal tvaRate) {
        this.tvaRate = tvaRate;
    }

    public BigDecimal getStampRate() {
        return this.stampRate;
    }

    public SaleOrder stampRate(BigDecimal stampRate) {
        this.setStampRate(stampRate);
        return this;
    }

    public void setStampRate(BigDecimal stampRate) {
        this.stampRate = stampRate;
    }

    public BigDecimal getDiscountRate() {
        return this.discountRate;
    }

    public SaleOrder discountRate(BigDecimal discountRate) {
        this.setDiscountRate(discountRate);
        return this;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getTvaAmount() {
        return this.tvaAmount;
    }

    public SaleOrder tvaAmount(BigDecimal tvaAmount) {
        this.setTvaAmount(tvaAmount);
        return this;
    }

    public void setTvaAmount(BigDecimal tvaAmount) {
        this.tvaAmount = tvaAmount;
    }

    public BigDecimal getStampAmount() {
        return this.stampAmount;
    }

    public SaleOrder stampAmount(BigDecimal stampAmount) {
        this.setStampAmount(stampAmount);
        return this;
    }

    public void setStampAmount(BigDecimal stampAmount) {
        this.stampAmount = stampAmount;
    }

    public BigDecimal getDiscountAmount() {
        return this.discountAmount;
    }

    public SaleOrder discountAmount(BigDecimal discountAmount) {
        this.setDiscountAmount(discountAmount);
        return this;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getSubTotal() {
        return this.subTotal;
    }

    public SaleOrder subTotal(BigDecimal subTotal) {
        this.setSubTotal(subTotal);
        return this;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public SaleOrder total(BigDecimal total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public SaleType getSaleType() {
        return this.saleType;
    }

    public SaleOrder saleType(SaleType saleType) {
        this.setSaleType(saleType);
        return this;
    }

    public void setSaleType(SaleType saleType) {
        this.saleType = saleType;
    }

    // Inherited createdBy methods
    public SaleOrder createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public SaleOrder createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public SaleOrder lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public SaleOrder lastModifiedDate(Instant lastModifiedDate) {
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

    public SaleOrder setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Payment getPayment() {
        return this.payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public SaleOrder payment(Payment payment) {
        this.setPayment(payment);
        return this;
    }

    public Set<SaleOrderItem> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set<SaleOrderItem> saleOrderItems) {
        if (this.orderItems != null) {
            this.orderItems.forEach(i -> i.setSaleOrder(null));
        }
        if (saleOrderItems != null) {
            saleOrderItems.forEach(i -> i.setSaleOrder(this));
        }
        this.orderItems = saleOrderItems;
    }

    public SaleOrder orderItems(Set<SaleOrderItem> saleOrderItems) {
        this.setOrderItems(saleOrderItems);
        return this;
    }

    public SaleOrder addOrderItems(SaleOrderItem saleOrderItem) {
        this.orderItems.add(saleOrderItem);
        saleOrderItem.setSaleOrder(this);
        return this;
    }

    public SaleOrder removeOrderItems(SaleOrderItem saleOrderItem) {
        this.orderItems.remove(saleOrderItem);
        saleOrderItem.setSaleOrder(null);
        return this;
    }

    public ClientAccount getClientAccount() {
        return this.clientAccount;
    }

    public void setClientAccount(ClientAccount clientAccount) {
        this.clientAccount = clientAccount;
    }

    public SaleOrder clientAccount(ClientAccount clientAccount) {
        this.setClientAccount(clientAccount);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public SaleOrder customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Shipment getShipment() {
        return this.shipment;
    }

    public void setShipment(Shipment shipment) {
        if (this.shipment != null) {
            this.shipment.setSaleOrder(null);
        }
        if (shipment != null) {
            shipment.setSaleOrder(this);
        }
        this.shipment = shipment;
    }

    public SaleOrder shipment(Shipment shipment) {
        this.setShipment(shipment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SaleOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((SaleOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleOrder{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", date='" + getDate() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", notes='" + getNotes() + "'" +
            ", status='" + getStatus() + "'" +
            ", tvaRate=" + getTvaRate() +
            ", stampRate=" + getStampRate() +
            ", discountRate=" + getDiscountRate() +
            ", tvaAmount=" + getTvaAmount() +
            ", stampAmount=" + getStampAmount() +
            ", discountAmount=" + getDiscountAmount() +
            ", subTotal=" + getSubTotal() +
            ", total=" + getTotal() +
            ", saleType='" + getSaleType() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
