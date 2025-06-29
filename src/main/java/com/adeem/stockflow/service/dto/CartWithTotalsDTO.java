package com.adeem.stockflow.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * DTO for Cart with calculated totals and grouping information.
 */
public class CartWithTotalsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant createdDate;
    private Instant lastModifiedDate;
    private List<CartItemDetailDTO> items;
    private Map<String, CompanyOrderSummaryDTO> ordersByCompany;
    private BigDecimal grandTotal;
    private BigDecimal totalShipping;
    private Integer totalItems;
    private Integer totalQuantity;

    public CartWithTotalsDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public List<CartItemDetailDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDetailDTO> items) {
        this.items = items;
    }

    public Map<String, CompanyOrderSummaryDTO> getOrdersByCompany() {
        return ordersByCompany;
    }

    public void setOrdersByCompany(Map<String, CompanyOrderSummaryDTO> ordersByCompany) {
        this.ordersByCompany = ordersByCompany;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public BigDecimal getTotalShipping() {
        return totalShipping;
    }

    public void setTotalShipping(BigDecimal totalShipping) {
        this.totalShipping = totalShipping;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
