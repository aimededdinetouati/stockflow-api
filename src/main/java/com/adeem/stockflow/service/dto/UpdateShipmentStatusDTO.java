package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.ShippingStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO for updating shipment status.
 */
public class UpdateShipmentStatusDTO implements Serializable {

    @NotNull
    private ShippingStatus status;

    private String notes;

    public ShippingStatus getStatus() {
        return status;
    }

    public void setStatus(ShippingStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
