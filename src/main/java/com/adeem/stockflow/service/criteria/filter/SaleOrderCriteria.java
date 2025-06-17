package com.adeem.stockflow.service.criteria.filter;

import com.adeem.stockflow.domain.enumeration.OrderStatus;
import com.adeem.stockflow.domain.enumeration.OrderType;
import com.adeem.stockflow.domain.enumeration.PaymentStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for filtering SaleOrder entities.
 * Provides comprehensive filtering options for sale orders.
 */
public class SaleOrderCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter reference;
    private ZonedDateTimeFilter date;
    private ZonedDateTimeFilter dueDate;
    private StringFilter notes;
    private StringFilter status;
    private StringFilter orderType;
    private ZonedDateTimeFilter reservationExpiresAt;
    private StringFilter customerNotes;
    private BigDecimalFilter shippingCost;
    private BigDecimalFilter tvaRate;
    private BigDecimalFilter stampRate;
    private BigDecimalFilter discountRate;
    private BigDecimalFilter tvaAmount;
    private BigDecimalFilter stampAmount;
    private BigDecimalFilter discountAmount;
    private BigDecimalFilter subTotal;
    private BigDecimalFilter total;
    private StringFilter saleType;
    private LongFilter paymentId;
    private LongFilter clientAccountId;
    private LongFilter customerId;
    private LongFilter shipmentId;
    private StringFilter paymentStatus;
    private StringFilter customerName;
    private ZonedDateTimeFilter fromDate;
    private ZonedDateTimeFilter toDate;
    private BigDecimalFilter minTotal;
    private BigDecimalFilter maxTotal;
    private BooleanFilter hasShipment;
    private BooleanFilter hasPayment;

    // Constructors
    public SaleOrderCriteria() {}

    public SaleOrderCriteria(SaleOrderCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.reference = other.reference == null ? null : other.reference.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.dueDate = other.dueDate == null ? null : other.dueDate.copy();
        this.notes = other.notes == null ? null : other.notes.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.orderType = other.orderType == null ? null : other.orderType.copy();
        this.reservationExpiresAt = other.reservationExpiresAt == null ? null : other.reservationExpiresAt.copy();
        this.customerNotes = other.customerNotes == null ? null : other.customerNotes.copy();
        this.shippingCost = other.shippingCost == null ? null : other.shippingCost.copy();
        this.total = other.total == null ? null : other.total.copy();
        this.customerId = other.customerId == null ? null : other.customerId.copy();
        this.clientAccountId = other.clientAccountId == null ? null : other.clientAccountId.copy();
        this.paymentStatus = other.paymentStatus == null ? null : other.paymentStatus.copy();
        this.fromDate = other.fromDate == null ? null : other.fromDate.copy();
        this.toDate = other.toDate == null ? null : other.toDate.copy();
        this.minTotal = other.minTotal == null ? null : other.minTotal.copy();
    }

    public SaleOrderCriteria copy() {
        return new SaleOrderCriteria(this);
    }

    // Getters and setters
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

    public ZonedDateTimeFilter getDate() {
        return date;
    }

    public void setDate(ZonedDateTimeFilter date) {
        this.date = date;
    }

    public ZonedDateTimeFilter getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTimeFilter dueDate) {
        this.dueDate = dueDate;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public StringFilter getStatus() {
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public StringFilter getOrderType() {
        return orderType;
    }

    public void setOrderType(StringFilter orderType) {
        this.orderType = orderType;
    }

    public ZonedDateTimeFilter getReservationExpiresAt() {
        return reservationExpiresAt;
    }

    public void setReservationExpiresAt(ZonedDateTimeFilter reservationExpiresAt) {
        this.reservationExpiresAt = reservationExpiresAt;
    }

    public StringFilter getCustomerNotes() {
        return customerNotes;
    }

    public void setCustomerNotes(StringFilter customerNotes) {
        this.customerNotes = customerNotes;
    }

    public BigDecimalFilter getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimalFilter shippingCost) {
        this.shippingCost = shippingCost;
    }

    public BigDecimalFilter getTotal() {
        return total;
    }

    public void setTotal(BigDecimalFilter total) {
        this.total = total;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getClientAccountId() {
        return clientAccountId;
    }

    public void setClientAccountId(LongFilter clientAccountId) {
        this.clientAccountId = clientAccountId;
    }

    public StringFilter getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(StringFilter paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public ZonedDateTimeFilter getFromDate() {
        return fromDate;
    }

    public void setFromDate(ZonedDateTimeFilter fromDate) {
        this.fromDate = fromDate;
    }

    public ZonedDateTimeFilter getToDate() {
        return toDate;
    }

    public void setToDate(ZonedDateTimeFilter toDate) {
        this.toDate = toDate;
    }

    public BigDecimalFilter getMinTotal() {
        return minTotal;
    }

    public void setMinTotal(BigDecimalFilter minTotal) {
        this.minTotal = minTotal;
    }

    public BigDecimalFilter getTvaRate() {
        return tvaRate;
    }

    public void setTvaRate(BigDecimalFilter tvaRate) {
        this.tvaRate = tvaRate;
    }

    public BigDecimalFilter getStampRate() {
        return stampRate;
    }

    public void setStampRate(BigDecimalFilter stampRate) {
        this.stampRate = stampRate;
    }

    public BigDecimalFilter getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimalFilter discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimalFilter getTvaAmount() {
        return tvaAmount;
    }

    public void setTvaAmount(BigDecimalFilter tvaAmount) {
        this.tvaAmount = tvaAmount;
    }

    public BigDecimalFilter getStampAmount() {
        return stampAmount;
    }

    public void setStampAmount(BigDecimalFilter stampAmount) {
        this.stampAmount = stampAmount;
    }

    public BigDecimalFilter getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimalFilter discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimalFilter getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimalFilter subTotal) {
        this.subTotal = subTotal;
    }

    public StringFilter getSaleType() {
        return saleType;
    }

    public void setSaleType(StringFilter saleType) {
        this.saleType = saleType;
    }

    public LongFilter getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(LongFilter paymentId) {
        this.paymentId = paymentId;
    }

    public LongFilter getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(LongFilter shipmentId) {
        this.shipmentId = shipmentId;
    }

    public StringFilter getCustomerName() {
        return customerName;
    }

    public void setCustomerName(StringFilter customerName) {
        this.customerName = customerName;
    }

    public BigDecimalFilter getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(BigDecimalFilter maxTotal) {
        this.maxTotal = maxTotal;
    }

    public BooleanFilter getHasShipment() {
        return hasShipment;
    }

    public void setHasShipment(BooleanFilter hasShipment) {
        this.hasShipment = hasShipment;
    }

    public BooleanFilter getHasPayment() {
        return hasPayment;
    }

    public void setHasPayment(BooleanFilter hasPayment) {
        this.hasPayment = hasPayment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleOrderCriteria that = (SaleOrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(status, that.status) &&
            Objects.equals(orderType, that.orderType) &&
            Objects.equals(customerId, that.customerId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, status, orderType, customerId);
    }
}
