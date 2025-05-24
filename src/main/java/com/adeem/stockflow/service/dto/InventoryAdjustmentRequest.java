package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.AdjustmentType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InventoryAdjustmentRequest {

    @NotNull
    private AdjustmentType type;

    @NotNull
    private BigDecimal quantity;

    @NotNull
    private String reason;

    private String notes;

    // Getters and setters
    public AdjustmentType getType() {
        return type;
    }

    public void setType(AdjustmentType type) {
        this.type = type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
