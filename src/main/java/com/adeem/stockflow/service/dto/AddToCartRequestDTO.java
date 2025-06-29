package com.adeem.stockflow.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for add to cart request.
 */
public class AddToCartRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long productId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal quantity;

    public AddToCartRequestDTO() {}

    public AddToCartRequestDTO(Long productId, BigDecimal quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
