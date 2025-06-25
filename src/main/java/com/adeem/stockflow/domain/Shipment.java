package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.ShippingStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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
    private LocalDateTime shippingDate;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShippingStatus status;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "notes")
    private String notes;

    // NEW FIELDS for Yalidine integration
    @Column(name = "yalidine_shipment_id")
    private String yalidineShipmentId;

    @Column(name = "yalidine_tracking_url")
    private String yalidineTrackingUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "yalidine_response_data", columnDefinition = "jsonb")
    private JsonNode yalidineResponseData;

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
    @JsonIgnoreProperties(value = { "customer", "clientAccount" }, allowSetters = true)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "user",
            "subscription",
            "quota",
            "address",
            "paymentConfiguration",
            "products",
            "inventories",
            "customers",
            "saleOrders",
            "purchaseOrders",
            "returnOrders",
            "payments",
            "shipments",
            "attachments",
        },
        allowSetters = true
    )
    private ClientAccount clientAccount;

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

    public LocalDateTime getShippingDate() {
        return this.shippingDate;
    }

    public Shipment shippingDate(LocalDateTime shippingDate) {
        this.setShippingDate(shippingDate);
        return this;
    }

    public void setShippingDate(LocalDateTime shippingDate) {
        this.shippingDate = shippingDate;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return this.estimatedDeliveryDate;
    }

    public Shipment estimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.setEstimatedDeliveryDate(estimatedDeliveryDate);
        return this;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public LocalDateTime getActualDeliveryDate() {
        return this.actualDeliveryDate;
    }

    public Shipment actualDeliveryDate(LocalDateTime actualDeliveryDate) {
        this.setActualDeliveryDate(actualDeliveryDate);
        return this;
    }

    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) {
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

    // NEW FIELD GETTERS/SETTERS for Yalidine integration
    public String getYalidineShipmentId() {
        return this.yalidineShipmentId;
    }

    public Shipment yalidineShipmentId(String yalidineShipmentId) {
        this.setYalidineShipmentId(yalidineShipmentId);
        return this;
    }

    public void setYalidineShipmentId(String yalidineShipmentId) {
        this.yalidineShipmentId = yalidineShipmentId;
    }

    public String getYalidineTrackingUrl() {
        return this.yalidineTrackingUrl;
    }

    public Shipment yalidineTrackingUrl(String yalidineTrackingUrl) {
        this.setYalidineTrackingUrl(yalidineTrackingUrl);
        return this;
    }

    public void setYalidineTrackingUrl(String yalidineTrackingUrl) {
        this.yalidineTrackingUrl = yalidineTrackingUrl;
    }

    public JsonNode getYalidineResponseData() {
        return this.yalidineResponseData;
    }

    public Shipment yalidineResponseData(JsonNode yalidineResponseData) {
        this.setYalidineResponseData(yalidineResponseData);
        return this;
    }

    public void setYalidineResponseData(JsonNode yalidineResponseData) {
        this.yalidineResponseData = yalidineResponseData;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    public Shipment setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    @Override
    public boolean isNew() {
        return !this.isPersisted;
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

    public ClientAccount getClientAccount() {
        return this.clientAccount;
    }

    public void setClientAccount(ClientAccount clientAccount) {
        this.clientAccount = clientAccount;
    }

    public Shipment clientAccount(ClientAccount clientAccount) {
        this.setClientAccount(clientAccount);
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
            ", weight=" + getWeight() +
            ", notes='" + getNotes() + "'" +
            ", yalidineShipmentId='" + getYalidineShipmentId() + "'" +
            ", yalidineTrackingUrl='" + getYalidineTrackingUrl() + "'" +
            "}";
    }
}
