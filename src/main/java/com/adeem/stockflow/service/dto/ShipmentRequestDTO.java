package com.adeem.stockflow.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO for creating shipments.
 */
public class ShipmentRequestDTO implements Serializable {

    private Long id;

    @NotNull
    private String carrier;

    private String notes;
    private Double weight;
    private Long addressId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}
