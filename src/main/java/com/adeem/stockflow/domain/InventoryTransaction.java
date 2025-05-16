package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A InventoryTransaction.
 */
@Entity
@Table(name = "inventory_transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryTransaction extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @NotNull
    @Column(name = "quantity", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantity;

    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private ZonedDateTime transactionDate;

    @NotNull
    @Column(name = "reference_number", nullable = false)
    private String referenceNumber;

    @Column(name = "notes")
    private String notes;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "product", "originalSaleOrderItem", "originalPurchaseOrderItem", "returnOrder" }, allowSetters = true)
    private ReturnOrderItem returnOrderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "payment", "orderItems", "clientAccount", "customer", "shipment" }, allowSetters = true)
    private SaleOrder saleOrderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "orderItems", "clientAccount", "admin", "supplier" }, allowSetters = true)
    private PurchaseOrder purchaseOrderItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InventoryTransaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public InventoryTransaction transactionType(TransactionType transactionType) {
        this.setTransactionType(transactionType);
        return this;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public InventoryTransaction quantity(BigDecimal quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public ZonedDateTime getTransactionDate() {
        return this.transactionDate;
    }

    public InventoryTransaction transactionDate(ZonedDateTime transactionDate) {
        this.setTransactionDate(transactionDate);
        return this;
    }

    public void setTransactionDate(ZonedDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getReferenceNumber() {
        return this.referenceNumber;
    }

    public InventoryTransaction referenceNumber(String referenceNumber) {
        this.setReferenceNumber(referenceNumber);
        return this;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getNotes() {
        return this.notes;
    }

    public InventoryTransaction notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Inherited createdBy methods
    public InventoryTransaction createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public InventoryTransaction createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public InventoryTransaction lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public InventoryTransaction lastModifiedDate(Instant lastModifiedDate) {
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

    public InventoryTransaction setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public ReturnOrderItem getReturnOrderItem() {
        return this.returnOrderItem;
    }

    public void setReturnOrderItem(ReturnOrderItem returnOrderItem) {
        this.returnOrderItem = returnOrderItem;
    }

    public InventoryTransaction returnOrderItem(ReturnOrderItem returnOrderItem) {
        this.setReturnOrderItem(returnOrderItem);
        return this;
    }

    public SaleOrder getSaleOrderItem() {
        return this.saleOrderItem;
    }

    public void setSaleOrderItem(SaleOrder saleOrder) {
        this.saleOrderItem = saleOrder;
    }

    public InventoryTransaction saleOrderItem(SaleOrder saleOrder) {
        this.setSaleOrderItem(saleOrder);
        return this;
    }

    public PurchaseOrder getPurchaseOrderItem() {
        return this.purchaseOrderItem;
    }

    public void setPurchaseOrderItem(PurchaseOrder purchaseOrder) {
        this.purchaseOrderItem = purchaseOrder;
    }

    public InventoryTransaction purchaseOrderItem(PurchaseOrder purchaseOrder) {
        this.setPurchaseOrderItem(purchaseOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventoryTransaction)) {
            return false;
        }
        return getId() != null && getId().equals(((InventoryTransaction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryTransaction{" +
            "id=" + getId() +
            ", transactionType='" + getTransactionType() + "'" +
            ", quantity=" + getQuantity() +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", referenceNumber='" + getReferenceNumber() + "'" +
            ", notes='" + getNotes() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
