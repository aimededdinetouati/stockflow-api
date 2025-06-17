package com.adeem.stockflow.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO for canceling an order.
 */
public class CancelOrderDTO implements Serializable {

    @NotNull
    private String reason;

    private String notes;

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
