package com.adeem.stockflow.service.dto.batch;

import com.adeem.stockflow.domain.enumeration.ImportStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for tracking import progress.
 */
public class ImportProgressDTO implements Serializable {

    private Long jobId;
    private ImportStatus status;
    private int totalRows;
    private int processedRows;
    private int successfulRows;
    private int errorRows;
    private String currentPhase;
    private Instant startTime;
    private Instant estimatedCompletion;
    private String fileName;
    private Long fileSize;

    public ImportProgressDTO() {}

    public ImportProgressDTO(Long jobId, ImportStatus status) {
        this.jobId = jobId;
        this.status = status;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getProcessedRows() {
        return processedRows;
    }

    public void setProcessedRows(int processedRows) {
        this.processedRows = processedRows;
    }

    public int getSuccessfulRows() {
        return successfulRows;
    }

    public void setSuccessfulRows(int successfulRows) {
        this.successfulRows = successfulRows;
    }

    public int getErrorRows() {
        return errorRows;
    }

    public void setErrorRows(int errorRows) {
        this.errorRows = errorRows;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEstimatedCompletion() {
        return estimatedCompletion;
    }

    public void setEstimatedCompletion(Instant estimatedCompletion) {
        this.estimatedCompletion = estimatedCompletion;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImportProgressDTO)) return false;
        ImportProgressDTO that = (ImportProgressDTO) o;
        return Objects.equals(jobId, that.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId);
    }

    @Override
    public String toString() {
        return (
            "ImportProgressDTO{" +
            "jobId=" +
            jobId +
            ", status=" +
            status +
            ", totalRows=" +
            totalRows +
            ", processedRows=" +
            processedRows +
            ", successfulRows=" +
            successfulRows +
            ", errorRows=" +
            errorRows +
            ", currentPhase='" +
            currentPhase +
            "'" +
            ", startTime=" +
            startTime +
            ", fileName='" +
            fileName +
            "'" +
            "}"
        );
    }
}
