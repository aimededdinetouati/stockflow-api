package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.ImportErrorType;
import com.adeem.stockflow.domain.enumeration.ImportStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A ProductImportJob.
 */
@Entity
@Table(name = "product_import_job")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductImportJob extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "job_execution_id")
    private Long jobExecutionId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ImportStatus status;

    @Column(name = "total_rows")
    private Integer totalRows;

    @Column(name = "successful_rows")
    private Integer successfulRows;

    @Column(name = "failed_rows")
    private Integer failedRows;

    @Column(name = "header_row_number")
    private Integer headerRowNumber;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "current_phase")
    private String currentPhase;

    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "address", "quota", "subscriptions" }, allowSetters = true)
    private ClientAccount clientAccount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "importJob", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "importJob" }, allowSetters = true)
    private Set<ProductImportError> errors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductImportJob id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobExecutionId() {
        return this.jobExecutionId;
    }

    public ProductImportJob jobExecutionId(Long jobExecutionId) {
        this.setJobExecutionId(jobExecutionId);
        return this;
    }

    public void setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public ProductImportJob fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public ProductImportJob fileSize(Long fileSize) {
        this.setFileSize(fileSize);
        return this;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public ImportStatus getStatus() {
        return this.status;
    }

    public ProductImportJob status(ImportStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public Integer getTotalRows() {
        return this.totalRows;
    }

    public ProductImportJob totalRows(Integer totalRows) {
        this.setTotalRows(totalRows);
        return this;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public Integer getSuccessfulRows() {
        return this.successfulRows;
    }

    public ProductImportJob successfulRows(Integer successfulRows) {
        this.setSuccessfulRows(successfulRows);
        return this;
    }

    public void setSuccessfulRows(Integer successfulRows) {
        this.successfulRows = successfulRows;
    }

    public Integer getFailedRows() {
        return this.failedRows;
    }

    public ProductImportJob failedRows(Integer failedRows) {
        this.setFailedRows(failedRows);
        return this;
    }

    public void setFailedRows(Integer failedRows) {
        this.failedRows = failedRows;
    }

    public Integer getHeaderRowNumber() {
        return this.headerRowNumber;
    }

    public ProductImportJob headerRowNumber(Integer headerRowNumber) {
        this.setHeaderRowNumber(headerRowNumber);
        return this;
    }

    public void setHeaderRowNumber(Integer headerRowNumber) {
        this.headerRowNumber = headerRowNumber;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public ProductImportJob startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public ProductImportJob endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getCurrentPhase() {
        return this.currentPhase;
    }

    public ProductImportJob currentPhase(String currentPhase) {
        this.setCurrentPhase(currentPhase);
        return this;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
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

    public ProductImportJob setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public ClientAccount getClientAccount() {
        return this.clientAccount;
    }

    public void setClientAccount(ClientAccount clientAccount) {
        this.clientAccount = clientAccount;
    }

    public ProductImportJob clientAccount(ClientAccount clientAccount) {
        this.setClientAccount(clientAccount);
        return this;
    }

    public Set<ProductImportError> getErrors() {
        return this.errors;
    }

    public void setErrors(Set<ProductImportError> productImportErrors) {
        if (this.errors != null) {
            this.errors.forEach(i -> i.setImportJob(null));
        }
        if (productImportErrors != null) {
            productImportErrors.forEach(i -> i.setImportJob(this));
        }
        this.errors = productImportErrors;
    }

    public ProductImportJob errors(Set<ProductImportError> productImportErrors) {
        this.setErrors(productImportErrors);
        return this;
    }

    public ProductImportJob addError(ProductImportError productImportError) {
        this.errors.add(productImportError);
        productImportError.setImportJob(this);
        return this;
    }

    public ProductImportJob removeError(ProductImportError productImportError) {
        this.errors.remove(productImportError);
        productImportError.setImportJob(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductImportJob)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductImportJob) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "ProductImportJob{" +
            "id=" +
            getId() +
            ", jobExecutionId=" +
            getJobExecutionId() +
            ", fileName='" +
            getFileName() +
            "'" +
            ", fileSize=" +
            getFileSize() +
            ", status='" +
            getStatus() +
            "'" +
            ", totalRows=" +
            getTotalRows() +
            ", successfulRows=" +
            getSuccessfulRows() +
            ", failedRows=" +
            getFailedRows() +
            ", headerRowNumber=" +
            getHeaderRowNumber() +
            ", startTime='" +
            getStartTime() +
            "'" +
            ", endTime='" +
            getEndTime() +
            "'" +
            ", currentPhase='" +
            getCurrentPhase() +
            "'" +
            "}"
        );
    }
}
