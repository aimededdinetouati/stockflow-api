package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.SaleOrderItem;
import com.adeem.stockflow.domain.enumeration.OrderStatus;
import com.adeem.stockflow.domain.enumeration.OrderType;
import com.adeem.stockflow.domain.enumeration.SaleType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.SaleOrder} entity.
 * Enhanced to support delivery/pickup orders with inventory reservation.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleOrderDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private ZonedDateTime date;

    private ZonedDateTime dueDate;

    private String notes;

    @NotNull
    private OrderStatus status;

    private boolean tvaApplied;

    private boolean stampApplied;

    private BigDecimal tvaRate;

    private BigDecimal stampRate;

    private BigDecimal discountRate;

    private BigDecimal tvaAmount;

    private BigDecimal stampAmount;

    private BigDecimal discountAmount;

    private BigDecimal subTotal;

    private BigDecimal total;

    private SaleType saleType;

    // NEW FIELDS
    @NotNull
    private OrderType orderType;

    private ZonedDateTime reservationExpiresAt;

    private String customerNotes;

    private BigDecimal shippingCost;

    private Set<SaleOrderItemDTO> orderItems = new HashSet<>();

    // Relationships
    private PaymentDTO payment;

    private Long clientAccountId;

    private CustomerDTO customer;

    private ShipmentDTO shipment;

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

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public boolean isTvaApplied() {
        return tvaApplied;
    }

    public void setTvaApplied(boolean tvaApplied) {
        this.tvaApplied = tvaApplied;
    }

    public boolean isStampApplied() {
        return stampApplied;
    }

    public void setStampApplied(boolean stampApplied) {
        this.stampApplied = stampApplied;
    }

    public BigDecimal getTvaRate() {
        return tvaRate;
    }

    public void setTvaRate(BigDecimal tvaRate) {
        this.tvaRate = tvaRate;
    }

    public BigDecimal getStampRate() {
        return stampRate;
    }

    public void setStampRate(BigDecimal stampRate) {
        this.stampRate = stampRate;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getTvaAmount() {
        return tvaAmount;
    }

    public void setTvaAmount(BigDecimal tvaAmount) {
        this.tvaAmount = tvaAmount;
    }

    public BigDecimal getStampAmount() {
        return stampAmount;
    }

    public void setStampAmount(BigDecimal stampAmount) {
        this.stampAmount = stampAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public SaleType getSaleType() {
        return saleType;
    }

    public void setSaleType(SaleType saleType) {
        this.saleType = saleType;
    }

    // NEW FIELD GETTERS/SETTERS
    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public ZonedDateTime getReservationExpiresAt() {
        return reservationExpiresAt;
    }

    public void setReservationExpiresAt(ZonedDateTime reservationExpiresAt) {
        this.reservationExpiresAt = reservationExpiresAt;
    }

    public String getCustomerNotes() {
        return customerNotes;
    }

    public void setCustomerNotes(String customerNotes) {
        this.customerNotes = customerNotes;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Set<SaleOrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<SaleOrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public void setPayment(PaymentDTO payment) {
        this.payment = payment;
    }

    public Long getClientAccountId() {
        return clientAccountId;
    }

    public void setClientAccountId(Long clientAccountId) {
        this.clientAccountId = clientAccountId;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public ShipmentDTO getShipment() {
        return shipment;
    }

    public void setShipment(ShipmentDTO shipment) {
        this.shipment = shipment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SaleOrderDTO)) {
            return false;
        }

        SaleOrderDTO saleOrderDTO = (SaleOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, saleOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleOrderDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", date='" + getDate() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", notes='" + getNotes() + "'" +
            ", status='" + getStatus() + "'" +
            ", orderType='" + getOrderType() + "'" +
            ", reservationExpiresAt='" + getReservationExpiresAt() + "'" +
            ", customerNotes='" + getCustomerNotes() + "'" +
            ", shippingCost=" + getShippingCost() +
            ", tvaRate=" + getTvaRate() +
            ", stampRate=" + getStampRate() +
            ", discountRate=" + getDiscountRate() +
            ", tvaAmount=" + getTvaAmount() +
            ", stampAmount=" + getStampAmount() +
            ", discountAmount=" + getDiscountAmount() +
            ", subTotal=" + getSubTotal() +
            ", total=" + getTotal() +
            ", saleType='" + getSaleType() + "'" +
            ", payment=" + getPayment() +
            ", clientAccountId=" + getClientAccountId() +
            ", customer=" + getCustomer() +
            ", shipment=" + getShipment() +
            "}";
    }
}
