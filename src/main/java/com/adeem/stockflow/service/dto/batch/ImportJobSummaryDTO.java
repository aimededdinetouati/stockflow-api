package com.adeem.stockflow.service.dto.batch;

import com.adeem.stockflow.domain.enumeration.ImportStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for import job summary.
 */
public class ImportJobSummaryDTO implements Serializable {

    private Long jobId;
    private String fileName;
    private ImportStatus status;
    private Instant startTime;
    private Instant endTime;
    private int totalRows;
    private int successfulRows;
    private int failedRows;

    public ImportJobSummaryDTO() {}

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getSuccessfulRows() {
        return successfulRows;
    }

    public void setSuccessfulRows(int successfulRows) {
        this.successfulRows = successfulRows;
    }

    public int getFailedRows() {
        return failedRows;
    }

    public void setFailedRows(int failedRows) {
        this.failedRows = failedRows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImportJobSummaryDTO)) return false;
        ImportJobSummaryDTO that = (ImportJobSummaryDTO) o;
        return Objects.equals(jobId, that.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId);
    }

    @Override
    public String toString() {
        return (
            "ImportJobSummaryDTO{" +
            "jobId=" +
            jobId +
            ", fileName='" +
            fileName +
            "'" +
            ", status=" +
            status +
            ", startTime=" +
            startTime +
            ", endTime=" +
            endTime +
            ", totalRows=" +
            totalRows +
            ", successfulRows=" +
            successfulRows +
            ", failedRows=" +
            failedRows +
            "}"
        );
    }
}
