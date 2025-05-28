package com.adeem.stockflow.service.dto.batch;

import com.adeem.stockflow.domain.enumeration.ImportErrorType;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for import errors.
 */
public class ImportErrorDTO implements Serializable {

    private Long id;
    private int rowNumber;
    private int dataRowNumber;
    private String fieldName;
    private String fieldValue;
    private ImportErrorType errorType;
    private String errorMessage;
    private String suggestion;

    public ImportErrorDTO() {}

    public ImportErrorDTO(
        int rowNumber,
        int dataRowNumber,
        String fieldName,
        String fieldValue,
        ImportErrorType errorType,
        String errorMessage,
        String suggestion
    ) {
        this.rowNumber = rowNumber;
        this.dataRowNumber = dataRowNumber;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.suggestion = suggestion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public ImportErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ImportErrorType errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImportErrorDTO)) return false;
        ImportErrorDTO that = (ImportErrorDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return (
            "ImportErrorDTO{" +
            "id=" +
            id +
            ", rowNumber=" +
            rowNumber +
            ", dataRowNumber=" +
            dataRowNumber +
            ", fieldName='" +
            fieldName +
            "'" +
            ", fieldValue='" +
            fieldValue +
            "'" +
            ", errorType=" +
            errorType +
            ", errorMessage='" +
            errorMessage +
            "'" +
            ", suggestion='" +
            suggestion +
            "'" +
            "}"
        );
    }
}
