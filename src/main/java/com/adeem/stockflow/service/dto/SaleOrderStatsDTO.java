package com.adeem.stockflow.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for sale order statistics.
 */
public class SaleOrderStatsDTO implements Serializable {

    // Order counts
    private Long totalOrders;
    private Long draftedOrders;
    private Long confirmedOrders;
    private Long shippedOrders;
    private Long completedOrders;
    private Long cancelledOrders;

    // Financial metrics
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private BigDecimal totalShippingRevenue;

    // Order type breakdown
    private Long deliveryOrders;
    private Long pickupOrders;

    // Timing metrics
    private Double averageFulfillmentTime; // Hours from confirmed to completed
    private Long expiredReservations;

    // Customer metrics
    private Long uniqueCustomers;
    private Long managedCustomerOrders;
    private Long independentCustomerOrders;

    // Getters and setters
    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getDraftedOrders() {
        return draftedOrders;
    }

    public void setDraftedOrders(Long draftedOrders) {
        this.draftedOrders = draftedOrders;
    }

    public Long getConfirmedOrders() {
        return confirmedOrders;
    }

    public void setConfirmedOrders(Long confirmedOrders) {
        this.confirmedOrders = confirmedOrders;
    }

    public Long getShippedOrders() {
        return shippedOrders;
    }

    public void setShippedOrders(Long shippedOrders) {
        this.shippedOrders = shippedOrders;
    }

    public Long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(Long completedOrders) {
        this.completedOrders = completedOrders;
    }

    public Long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(Long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public BigDecimal getTotalShippingRevenue() {
        return totalShippingRevenue;
    }

    public void setTotalShippingRevenue(BigDecimal totalShippingRevenue) {
        this.totalShippingRevenue = totalShippingRevenue;
    }

    public Long getDeliveryOrders() {
        return deliveryOrders;
    }

    public void setDeliveryOrders(Long deliveryOrders) {
        this.deliveryOrders = deliveryOrders;
    }

    public Long getPickupOrders() {
        return pickupOrders;
    }

    public void setPickupOrders(Long pickupOrders) {
        this.pickupOrders = pickupOrders;
    }

    public Double getAverageFulfillmentTime() {
        return averageFulfillmentTime;
    }

    public void setAverageFulfillmentTime(Double averageFulfillmentTime) {
        this.averageFulfillmentTime = averageFulfillmentTime;
    }

    public Long getExpiredReservations() {
        return expiredReservations;
    }

    public void setExpiredReservations(Long expiredReservations) {
        this.expiredReservations = expiredReservations;
    }

    public Long getUniqueCustomers() {
        return uniqueCustomers;
    }

    public void setUniqueCustomers(Long uniqueCustomers) {
        this.uniqueCustomers = uniqueCustomers;
    }

    public Long getManagedCustomerOrders() {
        return managedCustomerOrders;
    }

    public void setManagedCustomerOrders(Long managedCustomerOrders) {
        this.managedCustomerOrders = managedCustomerOrders;
    }

    public Long getIndependentCustomerOrders() {
        return independentCustomerOrders;
    }

    public void setIndependentCustomerOrders(Long independentCustomerOrders) {
        this.independentCustomerOrders = independentCustomerOrders;
    }
}
