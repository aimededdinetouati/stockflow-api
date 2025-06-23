package com.adeem.stockflow.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for inventory validation results.
 */
public class InventoryValidationDTO implements Serializable {

    private boolean valid;
    private List<InventoryValidationErrorDTO> errors;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<InventoryValidationErrorDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<InventoryValidationErrorDTO> errors) {
        this.errors = errors;
    }

    public static class InventoryValidationErrorDTO {

        private Long productId;
        private String productName;
        private BigDecimal requestedQuantity;
        private BigDecimal availableQuantity;
        private String message;

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

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
