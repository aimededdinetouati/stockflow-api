package com.adeem.stockflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Stockflow Api.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final Import importConfig = new Import();

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public Import getImport() {
        return importConfig;
    }

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    public static class Import {

        private int chunkSize;
        private long maxFileSize;
        private int tempFileRetentionHours;
        private String[] supportedFormats;
        private String defaultInventoryStatus;

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

            private int maxRowsToCheck;
            private int minMandatoryFields;

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

            private int updateIntervalSeconds;

            public int getUpdateIntervalSeconds() {
                return updateIntervalSeconds;
            }

            public void setUpdateIntervalSeconds(int updateIntervalSeconds) {
                this.updateIntervalSeconds = updateIntervalSeconds;
            }
        }

        public static class FileStorage {

            private String tempDirectory;
            private boolean cleanupEnabled;

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
}
