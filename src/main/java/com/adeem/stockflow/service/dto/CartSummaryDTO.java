package com.adeem.stockflow.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for cart summary information.
 */
public class CartSummaryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long cartId;
    private Integer itemCount;
    private BigDecimal total;
    private Integer totalQuantity;

    public CartSummaryDTO() {}

    // Getters and Setters
    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
