package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.ShippingStatus;
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
 * A Shipment.
 */
@Entity
@Table(name = "shipment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Shipment extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "reference", unique = true)
    private String reference;

    @Column(name = "tracking_number", unique = true)
    private String trackingNumber;

    @NotNull
    @Column(name = "carrier", nullable = false)
    private String carrier;

    @Column(name = "shipping_date")
    private Instant shippingDate;

    @Column(name = "estimated_delivery_date")
    private Instant estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private Instant actualDeliveryDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShippingStatus status;

    @NotNull
    @Column(name = "shipping_cost", precision = 21, scale = 2, nullable = false)
    private BigDecimal shippingCost;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "notes")
    private String notes;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @JsonIgnoreProperties(value = { "payment", "orderItems", "clientAccount", "customer", "shipment" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private SaleOrder saleOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "customer", "clientAccount", "supplier" }, allowSetters = true)
    private Address address;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Shipment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public Shipment reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTrackingNumber() {
        return this.trackingNumber;
    }

    public Shipment trackingNumber(String trackingNumber) {
        this.setTrackingNumber(trackingNumber);
        return this;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getCarrier() {
        return this.carrier;
    }

    public Shipment carrier(String carrier) {
        this.setCarrier(carrier);
        return this;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Instant getShippingDate() {
        return this.shippingDate;
    }

    public Shipment shippingDate(Instant shippingDate) {
        this.setShippingDate(shippingDate);
        return this;
    }

    public void setShippingDate(Instant shippingDate) {
        this.shippingDate = shippingDate;
    }

    public Instant getEstimatedDeliveryDate() {
        return this.estimatedDeliveryDate;
    }

    public Shipment estimatedDeliveryDate(Instant estimatedDeliveryDate) {
        this.setEstimatedDeliveryDate(estimatedDeliveryDate);
        return this;
    }

    public void setEstimatedDeliveryDate(Instant estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public Instant getActualDeliveryDate() {
        return this.actualDeliveryDate;
    }

    public Shipment actualDeliveryDate(Instant actualDeliveryDate) {
        this.setActualDeliveryDate(actualDeliveryDate);
        return this;
    }

    public void setActualDeliveryDate(Instant actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public ShippingStatus getStatus() {
        return this.status;
    }

    public Shipment status(ShippingStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ShippingStatus status) {
        this.status = status;
    }

    public BigDecimal getShippingCost() {
        return this.shippingCost;
    }

    public Shipment shippingCost(BigDecimal shippingCost) {
        this.setShippingCost(shippingCost);
        return this;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Double getWeight() {
        return this.weight;
    }

    public Shipment weight(Double weight) {
        this.setWeight(weight);
        return this;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getNotes() {
        return this.notes;
    }

    public Shipment notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Inherited createdBy methods
    public Shipment createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public Shipment createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public Shipment lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public Shipment lastModifiedDate(Instant lastModifiedDate) {
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

    public Shipment setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public SaleOrder getSaleOrder() {
        return this.saleOrder;
    }

    public void setSaleOrder(SaleOrder saleOrder) {
        this.saleOrder = saleOrder;
    }

    public Shipment saleOrder(SaleOrder saleOrder) {
        this.setSaleOrder(saleOrder);
        return this;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Shipment address(Address address) {
        this.setAddress(address);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shipment)) {
            return false;
        }
        return getId() != null && getId().equals(((Shipment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Shipment{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", trackingNumber='" + getTrackingNumber() + "'" +
            ", carrier='" + getCarrier() + "'" +
            ", shippingDate='" + getShippingDate() + "'" +
            ", estimatedDeliveryDate='" + getEstimatedDeliveryDate() + "'" +
            ", actualDeliveryDate='" + getActualDeliveryDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", shippingCost=" + getShippingCost() +
            ", weight=" + getWeight() +
            ", notes='" + getNotes() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
