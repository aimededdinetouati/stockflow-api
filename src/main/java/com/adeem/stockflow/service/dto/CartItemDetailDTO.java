package com.adeem.stockflow.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for CartItem with product details and availability information.
 */
public class CartItemDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal currentPrice;
    private Instant addedDate;
    private Boolean priceChanged;

    // Product details
    private Long productId;
    private String productName;
    private String productDescription;
    private String productImageUrl;
    private String productSku;

    // Company details
    private Long companyId;
    private String companyName;

    // Availability details
    private BigDecimal availableQuantity;
    private Boolean inStock;
    private Boolean availabilityChanged;

    // Calculated fields
    private BigDecimal lineTotal;

    public CartItemDetailDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Instant getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Instant addedDate) {
        this.addedDate = addedDate;
    }

    public Boolean getPriceChanged() {
        return priceChanged;
    }

    public void setPriceChanged(Boolean priceChanged) {
        this.priceChanged = priceChanged;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public Boolean getAvailabilityChanged() {
        return availabilityChanged;
    }

    public void setAvailabilityChanged(Boolean availabilityChanged) {
        this.availabilityChanged = availabilityChanged;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
