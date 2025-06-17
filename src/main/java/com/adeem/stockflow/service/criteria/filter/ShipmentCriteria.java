package com.adeem.stockflow.service.criteria.filter;

import com.adeem.stockflow.domain.enumeration.OrderStatus;
import com.adeem.stockflow.domain.enumeration.OrderType;
import com.adeem.stockflow.domain.enumeration.PaymentStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for filtering Shipment entities.
 */
public class ShipmentCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter reference;
    private StringFilter trackingNumber;
    private StringFilter carrier;
    private InstantFilter shippingDate;
    private InstantFilter estimatedDeliveryDate;
    private InstantFilter actualDeliveryDate;
    private StringFilter status;
    private BigDecimalFilter shippingCost;
    private DoubleFilter weight;
    private StringFilter notes;
    private StringFilter yalidineShipmentId;
    private StringFilter yalidineTrackingUrl;
    private LongFilter saleOrderId;
    private LongFilter addressId;
    private LongFilter clientAccountId;
    private BooleanFilter isYalidine;
    private InstantFilter fromDate;
    private InstantFilter toDate;

    // Constructors
    public ShipmentCriteria() {}

    public ShipmentCriteria(ShipmentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.reference = other.reference == null ? null : other.reference.copy();
        this.trackingNumber = other.trackingNumber == null ? null : other.trackingNumber.copy();
        this.carrier = other.carrier == null ? null : other.carrier.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.clientAccountId = other.clientAccountId == null ? null : other.clientAccountId.copy();
        this.isYalidine = other.isYalidine == null ? null : other.isYalidine.copy();
    }

    public ShipmentCriteria copy() {
        return new ShipmentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getReference() {
        return reference;
    }

    public void setReference(StringFilter reference) {
        this.reference = reference;
    }

    public StringFilter getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(StringFilter trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public StringFilter getCarrier() {
        return carrier;
    }

    public void setCarrier(StringFilter carrier) {
        this.carrier = carrier;
    }

    public InstantFilter getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(InstantFilter shippingDate) {
        this.shippingDate = shippingDate;
    }

    public InstantFilter getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(InstantFilter estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public InstantFilter getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(InstantFilter actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public StringFilter getStatus() {
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public BigDecimalFilter getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimalFilter shippingCost) {
        this.shippingCost = shippingCost;
    }

    public DoubleFilter getWeight() {
        return weight;
    }

    public void setWeight(DoubleFilter weight) {
        this.weight = weight;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public StringFilter getYalidineShipmentId() {
        return yalidineShipmentId;
    }

    public void setYalidineShipmentId(StringFilter yalidineShipmentId) {
        this.yalidineShipmentId = yalidineShipmentId;
    }

    public StringFilter getYalidineTrackingUrl() {
        return yalidineTrackingUrl;
    }

    public void setYalidineTrackingUrl(StringFilter yalidineTrackingUrl) {
        this.yalidineTrackingUrl = yalidineTrackingUrl;
    }

    public LongFilter getSaleOrderId() {
        return saleOrderId;
    }

    public void setSaleOrderId(LongFilter saleOrderId) {
        this.saleOrderId = saleOrderId;
    }

    public LongFilter getAddressId() {
        return addressId;
    }

    public void setAddressId(LongFilter addressId) {
        this.addressId = addressId;
    }

    public LongFilter getClientAccountId() {
        return clientAccountId;
    }

    public void setClientAccountId(LongFilter clientAccountId) {
        this.clientAccountId = clientAccountId;
    }

    public BooleanFilter getIsYalidine() {
        return isYalidine;
    }

    public void setIsYalidine(BooleanFilter isYalidine) {
        this.isYalidine = isYalidine;
    }

    public InstantFilter getFromDate() {
        return fromDate;
    }

    public void setFromDate(InstantFilter fromDate) {
        this.fromDate = fromDate;
    }

    public InstantFilter getToDate() {
        return toDate;
    }

    public void setToDate(InstantFilter toDate) {
        this.toDate = toDate;
    }
}
