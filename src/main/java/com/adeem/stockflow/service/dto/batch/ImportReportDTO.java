package com.adeem.stockflow.service.dto.batch;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A DTO for import report.
 */
public class ImportReportDTO implements Serializable {

    private ImportSummaryDTO summary;
    private List<ImportErrorDTO> errors;
    private FileInfoDTO fileInfo;

    public ImportReportDTO() {}

    public ImportSummaryDTO getSummary() {
        return summary;
    }

    public void setSummary(ImportSummaryDTO summary) {
        this.summary = summary;
    }

    public List<ImportErrorDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ImportErrorDTO> errors) {
        this.errors = errors;
    }

    public FileInfoDTO getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfoDTO fileInfo) {
        this.fileInfo = fileInfo;
    }

    public static class ImportSummaryDTO implements Serializable {

        private int totalRows;
        private int successfulRows;
        private int failedRows;
        private Duration executionTime;
        private Instant startTime;
        private Instant endTime;

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

        public Duration getExecutionTime() {
            return executionTime;
        }

        public void setExecutionTime(Duration executionTime) {
            this.executionTime = executionTime;
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
    }

    public static class FileInfoDTO implements Serializable {

        private String fileName;
        private Long fileSize;
        private Integer headerRowNumber;
        private Map<String, String> detectedColumns;

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

        public Integer getHeaderRowNumber() {
            return headerRowNumber;
        }

        public void setHeaderRowNumber(Integer headerRowNumber) {
            this.headerRowNumber = headerRowNumber;
        }

        public Map<String, String> getDetectedColumns() {
            return detectedColumns;
        }

        public void setDetectedColumns(Map<String, String> detectedColumns) {
            this.detectedColumns = detectedColumns;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImportReportDTO)) return false;
        ImportReportDTO that = (ImportReportDTO) o;
        return Objects.equals(summary, that.summary) && Objects.equals(fileInfo, that.fileInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summary, fileInfo);
    }
}
