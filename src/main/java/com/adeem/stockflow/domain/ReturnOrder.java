package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.DiscountAllocationMethod;
import com.adeem.stockflow.domain.enumeration.ReturnStatus;
import com.adeem.stockflow.domain.enumeration.ReturnType;
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
 * A ReturnOrder.
 */
@Entity
@Table(name = "return_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnOrder extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @NotNull
    @Column(name = "return_date", nullable = false)
    private ZonedDateTime returnDate;

    @Column(name = "processed_date")
    private ZonedDateTime processedDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReturnStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "return_type", nullable = false)
    private ReturnType returnType;

    @Column(name = "notes")
    private String notes;

    @Column(name = "refund_amount", precision = 21, scale = 2)
    private BigDecimal refundAmount;

    @NotNull
    @Column(name = "original_order_reference", nullable = false)
    private String originalOrderReference;

    @NotNull
    @Column(name = "is_partial_return", nullable = false)
    private Boolean isPartialReturn;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_allocation_method")
    private DiscountAllocationMethod discountAllocationMethod;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "returnOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "originalSaleOrderItem", "originalPurchaseOrderItem", "returnOrder" }, allowSetters = true)
    private Set<ReturnOrderItem> items = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "address", "subscriptions", "quotas" }, allowSetters = true)
    private ClientAccount clientAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "userRoles", "clientAccount" }, allowSetters = true)
    private Admin processedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "addressLists", "carts", "clientAccount" }, allowSetters = true)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "address", "clientAccount" }, allowSetters = true)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "payment", "orderItems", "clientAccount", "customer", "shipment" }, allowSetters = true)
    private SaleOrder originalSaleOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "orderItems", "clientAccount", "admin", "supplier" }, allowSetters = true)
    private PurchaseOrder originalPurchaseOrder;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReturnOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public ReturnOrder reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ZonedDateTime getReturnDate() {
        return this.returnDate;
    }

    public ReturnOrder returnDate(ZonedDateTime returnDate) {
        this.setReturnDate(returnDate);
        return this;
    }

    public void setReturnDate(ZonedDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public ZonedDateTime getProcessedDate() {
        return this.processedDate;
    }

    public ReturnOrder processedDate(ZonedDateTime processedDate) {
        this.setProcessedDate(processedDate);
        return this;
    }

    public void setProcessedDate(ZonedDateTime processedDate) {
        this.processedDate = processedDate;
    }

    public ReturnStatus getStatus() {
        return this.status;
    }

    public ReturnOrder status(ReturnStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public ReturnType getReturnType() {
        return this.returnType;
    }

    public ReturnOrder returnType(ReturnType returnType) {
        this.setReturnType(returnType);
        return this;
    }

    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

    public String getNotes() {
        return this.notes;
    }

    public ReturnOrder notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getRefundAmount() {
        return this.refundAmount;
    }

    public ReturnOrder refundAmount(BigDecimal refundAmount) {
        this.setRefundAmount(refundAmount);
        return this;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getOriginalOrderReference() {
        return this.originalOrderReference;
    }

    public ReturnOrder originalOrderReference(String originalOrderReference) {
        this.setOriginalOrderReference(originalOrderReference);
        return this;
    }

    public void setOriginalOrderReference(String originalOrderReference) {
        this.originalOrderReference = originalOrderReference;
    }

    public Boolean getIsPartialReturn() {
        return this.isPartialReturn;
    }

    public ReturnOrder isPartialReturn(Boolean isPartialReturn) {
        this.setIsPartialReturn(isPartialReturn);
        return this;
    }

    public void setIsPartialReturn(Boolean isPartialReturn) {
        this.isPartialReturn = isPartialReturn;
    }

    public DiscountAllocationMethod getDiscountAllocationMethod() {
        return this.discountAllocationMethod;
    }

    public ReturnOrder discountAllocationMethod(DiscountAllocationMethod discountAllocationMethod) {
        this.setDiscountAllocationMethod(discountAllocationMethod);
        return this;
    }

    public void setDiscountAllocationMethod(DiscountAllocationMethod discountAllocationMethod) {
        this.discountAllocationMethod = discountAllocationMethod;
    }

    // Inherited createdBy methods
    public ReturnOrder createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public ReturnOrder createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public ReturnOrder lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public ReturnOrder lastModifiedDate(Instant lastModifiedDate) {
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

    public ReturnOrder setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<ReturnOrderItem> getItems() {
        return this.items;
    }

    public void setItems(Set<ReturnOrderItem> returnOrderItems) {
        if (this.items != null) {
            this.items.forEach(i -> i.setReturnOrder(null));
        }
        if (returnOrderItems != null) {
            returnOrderItems.forEach(i -> i.setReturnOrder(this));
        }
        this.items = returnOrderItems;
    }

    public ReturnOrder items(Set<ReturnOrderItem> returnOrderItems) {
        this.setItems(returnOrderItems);
        return this;
    }

    public ReturnOrder addItems(ReturnOrderItem returnOrderItem) {
        this.items.add(returnOrderItem);
        returnOrderItem.setReturnOrder(this);
        return this;
    }

    public ReturnOrder removeItems(ReturnOrderItem returnOrderItem) {
        this.items.remove(returnOrderItem);
        returnOrderItem.setReturnOrder(null);
        return this;
    }

    public ClientAccount getClientAccount() {
        return this.clientAccount;
    }

    public void setClientAccount(ClientAccount clientAccount) {
        this.clientAccount = clientAccount;
    }

    public ReturnOrder clientAccount(ClientAccount clientAccount) {
        this.setClientAccount(clientAccount);
        return this;
    }

    public Admin getProcessedBy() {
        return this.processedBy;
    }

    public void setProcessedBy(Admin admin) {
        this.processedBy = admin;
    }

    public ReturnOrder processedBy(Admin admin) {
        this.setProcessedBy(admin);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ReturnOrder customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public ReturnOrder supplier(Supplier supplier) {
        this.setSupplier(supplier);
        return this;
    }

    public SaleOrder getOriginalSaleOrder() {
        return this.originalSaleOrder;
    }

    public void setOriginalSaleOrder(SaleOrder saleOrder) {
        this.originalSaleOrder = saleOrder;
    }

    public ReturnOrder originalSaleOrder(SaleOrder saleOrder) {
        this.setOriginalSaleOrder(saleOrder);
        return this;
    }

    public PurchaseOrder getOriginalPurchaseOrder() {
        return this.originalPurchaseOrder;
    }

    public void setOriginalPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.originalPurchaseOrder = purchaseOrder;
    }

    public ReturnOrder originalPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.setOriginalPurchaseOrder(purchaseOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((ReturnOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnOrder{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", returnDate='" + getReturnDate() + "'" +
            ", processedDate='" + getProcessedDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", returnType='" + getReturnType() + "'" +
            ", notes='" + getNotes() + "'" +
            ", refundAmount=" + getRefundAmount() +
            ", originalOrderReference='" + getOriginalOrderReference() + "'" +
            ", isPartialReturn='" + getIsPartialReturn() + "'" +
            ", discountAllocationMethod='" + getDiscountAllocationMethod() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
