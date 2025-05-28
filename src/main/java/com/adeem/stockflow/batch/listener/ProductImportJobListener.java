package com.adeem.stockflow.batch.listener;

import com.adeem.stockflow.batch.reader.ExcelProductItemReader;
import com.adeem.stockflow.domain.enumeration.ImportStatus;
import com.adeem.stockflow.repository.ProductImportJobRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Job execution listener for product import jobs.
 */
@Component
public class ProductImportJobListener implements JobExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(ProductImportJobListener.class);

    private final ProductImportJobRepository importJobRepository;

    @Value("#{jobParameters['importJobId']}")
    private Long importJobId;

    public ProductImportJobListener(ProductImportJobRepository importJobRepository) {
        this.importJobRepository = importJobRepository;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        LOG.info("Starting product import job with ID: {}", importJobId);

        try {
            // Update job status to processing
            importJobRepository.updateJobStatus(importJobId, ImportStatus.PROCESSING, "Initializing import process");

            // Store job execution ID for reference
            jobExecution.getExecutionContext().putLong("importJobId", importJobId);
        } catch (Exception e) {
            LOG.error("Error in beforeJob for import job {}: {}", importJobId, e.getMessage(), e);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LOG.info("Completed product import job with ID: {} - Status: {}", importJobId, jobExecution.getStatus());

        try {
            ImportStatus finalStatus;
            String finalPhase;

            switch (jobExecution.getStatus()) {
                case COMPLETED:
                    finalStatus = ImportStatus.COMPLETED;
                    finalPhase = "Import completed successfully";
                    break;
                case FAILED:
                    finalStatus = ImportStatus.FAILED;
                    finalPhase = "Import failed: " + getFailureReason(jobExecution);
                    break;
                case STOPPED:
                    finalStatus = ImportStatus.CANCELLED;
                    finalPhase = "Import was cancelled";
                    break;
                default:
                    finalStatus = ImportStatus.FAILED;
                    finalPhase = "Import ended with status: " + jobExecution.getStatus();
                    break;
            }

            // Update final job status
            importJobRepository.completeJob(importJobId, finalStatus, Instant.now(), finalPhase);

            LOG.info("Updated import job {} with final status: {}", importJobId, finalStatus);
        } catch (Exception e) {
            LOG.error("Error in afterJob for import job {}: {}", importJobId, e.getMessage(), e);
        }
    }

    /**
     * Extract failure reason from job execution.
     */
    private String getFailureReason(JobExecution jobExecution) {
        if (jobExecution.getAllFailureExceptions().isEmpty()) {
            return "Unknown error";
        }

        Throwable firstException = jobExecution.getAllFailureExceptions().get(0);
        return firstException.getMessage() != null ? firstException.getMessage() : firstException.getClass().getSimpleName();
    }
}
