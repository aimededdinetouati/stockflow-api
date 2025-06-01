package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.ImportErrorType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A ProductImportError.
 */
@Entity
@Table(name = "product_import_error")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductImportError extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "row_number")
    private Integer rowNumber;

    @Column(name = "data_row_number")
    private Integer dataRowNumber;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "field_value", columnDefinition = "TEXT")
    private String fieldValue;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "error_type", nullable = false)
    private ImportErrorType errorType;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "suggestion", columnDefinition = "TEXT")
    private String suggestion;

    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "clientAccount", "errors" }, allowSetters = true)
    private ProductImportJob importJob;

    public Long getId() {
        return this.id;
    }

    public ProductImportError id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRowNumber() {
        return this.rowNumber;
    }

    public ProductImportError rowNumber(Integer rowNumber) {
        this.setRowNumber(rowNumber);
        return this;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Integer getDataRowNumber() {
        return this.dataRowNumber;
    }

    public ProductImportError dataRowNumber(Integer dataRowNumber) {
        this.setDataRowNumber(dataRowNumber);
        return this;
    }

    public void setDataRowNumber(Integer dataRowNumber) {
        this.dataRowNumber = dataRowNumber;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public ProductImportError fieldName(String fieldName) {
        this.setFieldName(fieldName);
        return this;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return this.fieldValue;
    }

    public ProductImportError fieldValue(String fieldValue) {
        this.setFieldValue(fieldValue);
        return this;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public ImportErrorType getErrorType() {
        return this.errorType;
    }

    public ProductImportError errorType(ImportErrorType errorType) {
        this.setErrorType(errorType);
        return this;
    }

    public void setErrorType(ImportErrorType errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public ProductImportError errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuggestion() {
        return this.suggestion;
    }

    public ProductImportError suggestion(String suggestion) {
        this.setSuggestion(suggestion);
        return this;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    @org.springframework.data.annotation.Transient
    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public ProductImportError setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public ProductImportJob getImportJob() {
        return this.importJob;
    }

    public void setImportJob(ProductImportJob productImportJob) {
        this.importJob = productImportJob;
    }

    public ProductImportError importJob(ProductImportJob productImportJob) {
        this.setImportJob(productImportJob);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductImportError)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductImportError) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "ProductImportError{" +
            "id=" +
            getId() +
            ", rowNumber=" +
            getRowNumber() +
            ", dataRowNumber=" +
            getDataRowNumber() +
            ", fieldName='" +
            getFieldName() +
            "'" +
            ", fieldValue='" +
            getFieldValue() +
            "'" +
            ", errorType='" +
            getErrorType() +
            "'" +
            ", errorMessage='" +
            getErrorMessage() +
            "'" +
            ", suggestion='" +
            getSuggestion() +
            "'" +
            "}"
        );
    }
}
