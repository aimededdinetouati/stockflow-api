package com.adeem.stockflow.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for cart items grouped by company.
 */
public class CompanyCartItemsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long companyId;
    private String companyName;
    private List<CartItemDetailDTO> items;
    private BigDecimal subtotal;

    public CompanyCartItemsDTO() {}

    // Getters and Setters
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

    public List<CartItemDetailDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDetailDTO> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
