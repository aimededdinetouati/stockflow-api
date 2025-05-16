package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.ItemCondition;
import com.adeem.stockflow.domain.enumeration.ReturnReason;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A ReturnOrderItem.
 */
@Entity
@Table(name = "return_order_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnOrderItem extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "quantity", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantity;

    @NotNull
    @Column(name = "unit_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @NotNull
    @Column(name = "subtotal", precision = 21, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "allocated_discount", precision = 21, scale = 2)
    private BigDecimal allocatedDiscount;

    @Column(name = "total", precision = 21, scale = 2)
    private BigDecimal total;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "return_reason", nullable = false)
    private ReturnReason returnReason;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false)
    private ItemCondition condition;

    @Column(name = "notes")
    private String notes;

    @NotNull
    @Column(name = "is_refundable", nullable = false)
    private Boolean isRefundable;

    @NotNull
    @Column(name = "is_restockable", nullable = false)
    private Boolean isRestockable;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "images", "inventories", "clientAccount", "productFamily" }, allowSetters = true)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "product", "saleOrder" }, allowSetters = true)
    private SaleOrderItem originalSaleOrderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "product", "purchaseOrder" }, allowSetters = true)
    private PurchaseOrderItem originalPurchaseOrderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "items", "clientAccount", "processedBy", "customer", "supplier", "originalSaleOrder", "originalPurchaseOrder" },
        allowSetters = true
    )
    private ReturnOrder returnOrder;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReturnOrderItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public ReturnOrderItem quantity(BigDecimal quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public ReturnOrderItem unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public ReturnOrderItem subtotal(BigDecimal subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getAllocatedDiscount() {
        return this.allocatedDiscount;
    }

    public ReturnOrderItem allocatedDiscount(BigDecimal allocatedDiscount) {
        this.setAllocatedDiscount(allocatedDiscount);
        return this;
    }

    public void setAllocatedDiscount(BigDecimal allocatedDiscount) {
        this.allocatedDiscount = allocatedDiscount;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public ReturnOrderItem total(BigDecimal total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public ReturnReason getReturnReason() {
        return this.returnReason;
    }

    public ReturnOrderItem returnReason(ReturnReason returnReason) {
        this.setReturnReason(returnReason);
        return this;
    }

    public void setReturnReason(ReturnReason returnReason) {
        this.returnReason = returnReason;
    }

    public ItemCondition getCondition() {
        return this.condition;
    }

    public ReturnOrderItem condition(ItemCondition condition) {
        this.setCondition(condition);
        return this;
    }

    public void setCondition(ItemCondition condition) {
        this.condition = condition;
    }

    public String getNotes() {
        return this.notes;
    }

    public ReturnOrderItem notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsRefundable() {
        return this.isRefundable;
    }

    public ReturnOrderItem isRefundable(Boolean isRefundable) {
        this.setIsRefundable(isRefundable);
        return this;
    }

    public void setIsRefundable(Boolean isRefundable) {
        this.isRefundable = isRefundable;
    }

    public Boolean getIsRestockable() {
        return this.isRestockable;
    }

    public ReturnOrderItem isRestockable(Boolean isRestockable) {
        this.setIsRestockable(isRestockable);
        return this;
    }

    public void setIsRestockable(Boolean isRestockable) {
        this.isRestockable = isRestockable;
    }

    // Inherited createdBy methods
    public ReturnOrderItem createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public ReturnOrderItem createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public ReturnOrderItem lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public ReturnOrderItem lastModifiedDate(Instant lastModifiedDate) {
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

    public ReturnOrderItem setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ReturnOrderItem product(Product product) {
        this.setProduct(product);
        return this;
    }

    public SaleOrderItem getOriginalSaleOrderItem() {
        return this.originalSaleOrderItem;
    }

    public void setOriginalSaleOrderItem(SaleOrderItem saleOrderItem) {
        this.originalSaleOrderItem = saleOrderItem;
    }

    public ReturnOrderItem originalSaleOrderItem(SaleOrderItem saleOrderItem) {
        this.setOriginalSaleOrderItem(saleOrderItem);
        return this;
    }

    public PurchaseOrderItem getOriginalPurchaseOrderItem() {
        return this.originalPurchaseOrderItem;
    }

    public void setOriginalPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
        this.originalPurchaseOrderItem = purchaseOrderItem;
    }

    public ReturnOrderItem originalPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
        this.setOriginalPurchaseOrderItem(purchaseOrderItem);
        return this;
    }

    public ReturnOrder getReturnOrder() {
        return this.returnOrder;
    }

    public void setReturnOrder(ReturnOrder returnOrder) {
        this.returnOrder = returnOrder;
    }

    public ReturnOrderItem returnOrder(ReturnOrder returnOrder) {
        this.setReturnOrder(returnOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnOrderItem)) {
            return false;
        }
        return getId() != null && getId().equals(((ReturnOrderItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnOrderItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", subtotal=" + getSubtotal() +
            ", allocatedDiscount=" + getAllocatedDiscount() +
            ", total=" + getTotal() +
            ", returnReason='" + getReturnReason() + "'" +
            ", condition='" + getCondition() + "'" +
            ", notes='" + getNotes() + "'" +
            ", isRefundable='" + getIsRefundable() + "'" +
            ", isRestockable='" + getIsRestockable() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
