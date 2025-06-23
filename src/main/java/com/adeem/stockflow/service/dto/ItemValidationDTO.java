package com.adeem.stockflow.service.dto;

import java.util.List;

public class ItemValidationDTO {

    private boolean valid;
    private InventoryValidationDTO.InventoryValidationErrorDTO error;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public InventoryValidationDTO.InventoryValidationErrorDTO getError() {
        return error;
    }

    public void setError(InventoryValidationDTO.InventoryValidationErrorDTO error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ItemValidationDTO{" + "valid=" + valid + ", error=" + error + '}';
    }
}
