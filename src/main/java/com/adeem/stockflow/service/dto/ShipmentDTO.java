package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.ShippingStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.Shipment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentDTO implements Serializable {

    private Long id;

    private String reference;

    private String trackingNumber;

    @NotNull
    private String carrier;

    private Instant shippingDate;

    private Instant estimatedDeliveryDate;

    private Instant actualDeliveryDate;

    @NotNull
    private ShippingStatus status;

    @NotNull
    private BigDecimal shippingCost;

    private Double weight;

    private String notes;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private SaleOrderDTO saleOrder;

    private AddressDTO address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Instant getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(Instant shippingDate) {
        this.shippingDate = shippingDate;
    }

    public Instant getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(Instant estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public Instant getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(Instant actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public ShippingStatus getStatus() {
        return status;
    }

    public void setStatus(ShippingStatus status) {
        this.status = status;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public SaleOrderDTO getSaleOrder() {
        return saleOrder;
    }

    public void setSaleOrder(SaleOrderDTO saleOrder) {
        this.saleOrder = saleOrder;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShipmentDTO)) {
            return false;
        }

        ShipmentDTO shipmentDTO = (ShipmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shipmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentDTO{" +
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
            ", saleOrder=" + getSaleOrder() +
            ", address=" + getAddress() +
            "}";
    }
}
