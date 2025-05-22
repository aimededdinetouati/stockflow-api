package com.adeem.stockflow.service.dto;

import jakarta.validation.Valid;
import java.io.Serializable;
import java.util.Objects;

/**
 * Data Transfer Object for Product with its Inventory information.
 */
public class ProductWithInventoryDTO implements Serializable {

    private ProductDTO product;
    private InventoryDTO inventory;

    public ProductWithInventoryDTO() {
        // Empty constructor needed for Jackson.
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public InventoryDTO getInventory() {
        return inventory;
    }

    public void setInventory(InventoryDTO inventory) {
        this.inventory = inventory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductWithInventoryDTO)) {
            return false;
        }

        ProductWithInventoryDTO that = (ProductWithInventoryDTO) o;
        return Objects.equals(getProduct(), that.getProduct());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProduct());
    }

    @Override
    public String toString() {
        return "ProductWithInventoryDTO{" + "product=" + getProduct() + ", inventory=" + getInventory() + '}';
    }
}
