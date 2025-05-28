package com.adeem.stockflow.service.dto.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A DTO representing the result of creating a product during import.
 */
public class ProductCreationResult implements Serializable {

    private boolean success;
    private int rowNumber;
    private int dataRowNumber;
    private List<ImportErrorDTO> errors = new ArrayList<>();
    private Long productId; // for successful creations
    private String productCode;

    public ProductCreationResult() {}

    public ProductCreationResult(int rowNumber, int dataRowNumber) {
        this.rowNumber = rowNumber;
        this.dataRowNumber = dataRowNumber;
    }

    public static ProductCreationResult success(int rowNumber, int dataRowNumber, Long productId, String code) {
        ProductCreationResult result = new ProductCreationResult(rowNumber, dataRowNumber);
        result.success = true;
        result.productId = productId;
        result.productCode = code;
        return result;
    }

    public static ProductCreationResult failed(int rowNumber, int dataRowNumber, List<ImportErrorDTO> errors) {
        ProductCreationResult result = new ProductCreationResult(rowNumber, dataRowNumber);
        result.success = false;
        result.errors = errors != null ? errors : new ArrayList<>();
        return result;
    }

    public static ProductCreationResult failed(int rowNumber, int dataRowNumber, ImportErrorDTO error) {
        ProductCreationResult result = new ProductCreationResult(rowNumber, dataRowNumber);
        result.success = false;
        result.errors = new ArrayList<>();
        result.errors.add(error);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public List<ImportErrorDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ImportErrorDTO> errors) {
        this.errors = errors;
    }

    public void addError(ImportErrorDTO error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
        this.success = false;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCreationResult)) return false;
        ProductCreationResult that = (ProductCreationResult) o;
        return rowNumber == that.rowNumber && Objects.equals(productCode, that.productCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowNumber, productCode);
    }

    @Override
    public String toString() {
        return (
            "ProductCreationResult{" +
            "success=" +
            success +
            ", rowNumber=" +
            rowNumber +
            ", dataRowNumber=" +
            dataRowNumber +
            ", errors=" +
            errors.size() +
            ", productId=" +
            productId +
            ", productCode='" +
            productCode +
            "'" +
            "}"
        );
    }
}
