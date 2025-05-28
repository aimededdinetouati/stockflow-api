package com.adeem.stockflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.import")
public class ImportConfigurationProperties {

    private int chunkSize = 100;
    private long maxFileSize = 52428800L; // 50MB
    private int tempFileRetentionHours = 24;
    private String[] supportedFormats = { "xlsx", "xls" };
    private String defaultInventoryStatus = "AVAILABLE";

    private HeaderDetection headerDetection = new HeaderDetection();
    private ProgressTracking progressTracking = new ProgressTracking();
    private FileStorage fileStorage = new FileStorage();

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public int getTempFileRetentionHours() {
        return tempFileRetentionHours;
    }

    public void setTempFileRetentionHours(int tempFileRetentionHours) {
        this.tempFileRetentionHours = tempFileRetentionHours;
    }

    public String[] getSupportedFormats() {
        return supportedFormats;
    }

    public void setSupportedFormats(String[] supportedFormats) {
        this.supportedFormats = supportedFormats;
    }

    public String getDefaultInventoryStatus() {
        return defaultInventoryStatus;
    }

    public void setDefaultInventoryStatus(String defaultInventoryStatus) {
        this.defaultInventoryStatus = defaultInventoryStatus;
    }

    public HeaderDetection getHeaderDetection() {
        return headerDetection;
    }

    public void setHeaderDetection(HeaderDetection headerDetection) {
        this.headerDetection = headerDetection;
    }

    public ProgressTracking getProgressTracking() {
        return progressTracking;
    }

    public void setProgressTracking(ProgressTracking progressTracking) {
        this.progressTracking = progressTracking;
    }

    public FileStorage getFileStorage() {
        return fileStorage;
    }

    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    public static class HeaderDetection {

        private int maxRowsToCheck = 10;
        private int minMandatoryFields = 3;

        public int getMaxRowsToCheck() {
            return maxRowsToCheck;
        }

        public void setMaxRowsToCheck(int maxRowsToCheck) {
            this.maxRowsToCheck = maxRowsToCheck;
        }

        public int getMinMandatoryFields() {
            return minMandatoryFields;
        }

        public void setMinMandatoryFields(int minMandatoryFields) {
            this.minMandatoryFields = minMandatoryFields;
        }
    }

    public static class ProgressTracking {

        private int updateIntervalSeconds = 5;

        public int getUpdateIntervalSeconds() {
            return updateIntervalSeconds;
        }

        public void setUpdateIntervalSeconds(int updateIntervalSeconds) {
            this.updateIntervalSeconds = updateIntervalSeconds;
        }
    }

    public static class FileStorage {

        private String tempDirectory = System.getProperty("java.io.tmpdir") + "/stockflow-imports";
        private boolean cleanupEnabled = true;

        public String getTempDirectory() {
            return tempDirectory;
        }

        public void setTempDirectory(String tempDirectory) {
            this.tempDirectory = tempDirectory;
        }

        public boolean isCleanupEnabled() {
            return cleanupEnabled;
        }

        public void setCleanupEnabled(boolean cleanupEnabled) {
            this.cleanupEnabled = cleanupEnabled;
        }
    }
}
