package com.adeem.stockflow.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for cart validation issues.
 */
public class CartValidationIssueDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum IssueType {
        OUT_OF_STOCK,
        INSUFFICIENT_STOCK,
        PRICE_CHANGED,
        PRODUCT_DISCONTINUED,
        PRODUCT_NOT_VISIBLE,
    }

    private Long cartItemId;
    private Long productId;
    private String productName;
    private IssueType issueType;
    private String message;
    private BigDecimal requestedQuantity;
    private BigDecimal availableQuantity;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;

    public CartValidationIssueDTO() {}

    // Getters and Setters
    public Long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
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

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(BigDecimal requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }
}
