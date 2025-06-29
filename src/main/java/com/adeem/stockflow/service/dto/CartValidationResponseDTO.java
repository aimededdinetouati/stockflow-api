package com.adeem.stockflow.service.dto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for cart validation response.
 */
public class CartValidationResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean isValid;
    private List<CartValidationIssueDTO> issues;
    private CartWithTotalsDTO updatedCart;

    public CartValidationResponseDTO() {}

    // Getters and Setters
    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public List<CartValidationIssueDTO> getIssues() {
        return issues;
    }

    public void setIssues(List<CartValidationIssueDTO> issues) {
        this.issues = issues;
    }

    public CartWithTotalsDTO getUpdatedCart() {
        return updatedCart;
    }

    public void setUpdatedCart(CartWithTotalsDTO updatedCart) {
        this.updatedCart = updatedCart;
    }
}
