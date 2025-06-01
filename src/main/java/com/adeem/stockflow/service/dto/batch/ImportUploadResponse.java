package com.adeem.stockflow.service.dto.batch;

import com.adeem.stockflow.domain.enumeration.ImportStatus;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for upload response.
 */
public class ImportUploadResponse implements Serializable {

    private Long jobId;
    private ImportStatus status;
    private String message;

    public ImportUploadResponse() {}

    public ImportUploadResponse(Long jobId, ImportStatus status, String message) {
        this.jobId = jobId;
        this.status = status;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImportUploadResponse)) return false;
        ImportUploadResponse that = (ImportUploadResponse) o;
        return Objects.equals(jobId, that.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId);
    }

    @Override
    public String toString() {
        return "ImportUploadResponse{" + "jobId=" + jobId + ", status=" + status + ", message='" + message + "'" + "}";
    }
}
