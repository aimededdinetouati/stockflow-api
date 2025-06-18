package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.ShippingStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for updating shipment status.
 */
public class UpdateShipmentStatusDTO implements Serializable {

    @NotNull
    private ShippingStatus status;

    private LocalDateTime actualDeliveryDate;

    public ShippingStatus getStatus() {
        return status;
    }

    public void setStatus(ShippingStatus status) {
        this.status = status;
    }

    public LocalDateTime getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }
}
