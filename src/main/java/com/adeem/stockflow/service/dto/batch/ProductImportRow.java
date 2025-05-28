package com.adeem.stockflow.service.dto.batch;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO representing a row from the Excel import file.
 */
public class ProductImportRow implements Serializable {

    private int rowNumber;
    private int dataRowNumber; // Row number excluding headers
    private String code;
    private String name;
    private BigDecimal quantity;
    private String family;
    private String category;
    private BigDecimal price;
    private String description;
    private String manufacturer;
    private String upc;
    private String manufacturerCode;
    private BigDecimal minimumStockLevel;
    private Boolean applyTva;
    private Boolean isVisibleToCustomers;

    public ProductImportRow() {}

    public ProductImportRow(int rowNumber, int dataRowNumber) {
        this.rowNumber = rowNumber;
        this.dataRowNumber = dataRowNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getDataRowNumber() {
        return dataRowNumber;
    }

    public void setDataRowNumber(int dataRowNumber) {
        this.dataRowNumber = dataRowNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public BigDecimal getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(BigDecimal minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public Boolean getApplyTva() {
        return applyTva;
    }

    public void setApplyTva(Boolean applyTva) {
        this.applyTva = applyTva;
    }

    public Boolean getIsVisibleToCustomers() {
        return isVisibleToCustomers;
    }

    public void setIsVisibleToCustomers(Boolean isVisibleToCustomers) {
        this.isVisibleToCustomers = isVisibleToCustomers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductImportRow)) return false;
        ProductImportRow that = (ProductImportRow) o;
        return rowNumber == that.rowNumber && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowNumber, code);
    }

    @Override
    public String toString() {
        return (
            "ProductImportRow{" +
            "rowNumber=" +
            rowNumber +
            ", dataRowNumber=" +
            dataRowNumber +
            ", code='" +
            code +
            "'" +
            ", name='" +
            name +
            "'" +
            ", quantity=" +
            quantity +
            ", family='" +
            family +
            "'" +
            ", category='" +
            category +
            "'" +
            ", price=" +
            price +
            ", description='" +
            description +
            "'" +
            ", manufacturer='" +
            manufacturer +
            "'" +
            "}"
        );
    }
}
