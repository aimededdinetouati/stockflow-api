package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.ShippingStatus;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.Shipment} entity.
 * Enhanced to support Yalidine integration and flexible carrier management.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentDTO implements Serializable {

    private Long id;

    private String reference;

    private String trackingNumber;

    @NotNull
    private String carrier;

    private LocalDateTime shippingDate;

    private LocalDateTime estimatedDeliveryDate;

    private LocalDateTime actualDeliveryDate;

    @NotNull
    private ShippingStatus status;

    private Double weight;

    private String notes;

    // NEW FIELDS for Yalidine integration
    private String yalidineShipmentId;

    private String yalidineTrackingUrl;

    private JsonNode yalidineResponseData;

    // Relationships
    private SaleOrderDTO saleOrder;

    private AddressDTO address;

    private ClientAccountDTO clientAccount;

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

    public LocalDateTime getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(LocalDateTime shippingDate) {
        this.shippingDate = shippingDate;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public LocalDateTime getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public ShippingStatus getStatus() {
        return status;
    }

    public void setStatus(ShippingStatus status) {
        this.status = status;
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

    // NEW FIELD GETTERS/SETTERS for Yalidine integration
    public String getYalidineShipmentId() {
        return yalidineShipmentId;
    }

    public void setYalidineShipmentId(String yalidineShipmentId) {
        this.yalidineShipmentId = yalidineShipmentId;
    }

    public String getYalidineTrackingUrl() {
        return yalidineTrackingUrl;
    }

    public void setYalidineTrackingUrl(String yalidineTrackingUrl) {
        this.yalidineTrackingUrl = yalidineTrackingUrl;
    }

    public JsonNode getYalidineResponseData() {
        return yalidineResponseData;
    }

    public void setYalidineResponseData(JsonNode yalidineResponseData) {
        this.yalidineResponseData = yalidineResponseData;
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

    public ClientAccountDTO getClientAccount() {
        return clientAccount;
    }

    public void setClientAccount(ClientAccountDTO clientAccount) {
        this.clientAccount = clientAccount;
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
            ", weight=" + getWeight() +
            ", notes='" + getNotes() + "'" +
            ", yalidineShipmentId='" + getYalidineShipmentId() + "'" +
            ", yalidineTrackingUrl='" + getYalidineTrackingUrl() + "'" +
            ", saleOrder=" + getSaleOrder() +
            ", address=" + getAddress() +
            ", clientAccount=" + getClientAccount() +
            "}";
    }
}
