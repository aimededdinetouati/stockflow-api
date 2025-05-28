package com.adeem.stockflow.batch.writer;

import com.adeem.stockflow.domain.ProductImportError;
import com.adeem.stockflow.domain.ProductImportJob;
import com.adeem.stockflow.domain.enumeration.ImportStatus;
import com.adeem.stockflow.repository.ProductImportErrorRepository;
import com.adeem.stockflow.repository.ProductImportJobRepository;
import com.adeem.stockflow.service.dto.batch.ImportErrorDTO;
import com.adeem.stockflow.service.dto.batch.ProductCreationResult;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Batch ItemWriter for handling ProductCreationResult objects.
 * Updates import job progress and saves errors to database.
 */
@Component
public class ProductImportWriter implements ItemWriter<ProductCreationResult> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductImportWriter.class);

    private final ProductImportJobRepository importJobRepository;
    private final ProductImportErrorRepository importErrorRepository;

    @Value("#{jobParameters['importJobId']}")
    private Long importJobId;

    @Value("#{jobParameters['clientAccountId']}")
    private Long clientAccountId;

    // Counters for tracking progress
    private final AtomicInteger totalProcessed = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger errorCount = new AtomicInteger(0);

    private StepExecution stepExecution;

    public ProductImportWriter(ProductImportJobRepository importJobRepository, ProductImportErrorRepository importErrorRepository) {
        this.importJobRepository = importJobRepository;
        this.importErrorRepository = importErrorRepository;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    @Transactional
    public void write(Chunk<? extends ProductCreationResult> chunk) throws Exception {
        LOG.debug("Writing batch of {} results for import job {}", chunk.size(), importJobId);

        List<ProductImportError> errorsToSave = new ArrayList<>();
        int batchSuccessCount = 0;
        int batchErrorCount = 0;

        // Process each result in the chunk
        for (ProductCreationResult result : chunk) {
            if (result.isSuccess()) {
                batchSuccessCount++;
                LOG.debug("Successfully created product: {} at row {}", result.getProductCode(), result.getRowNumber());
            } else {
                batchErrorCount++;
                LOG.debug("Failed to create product at row {} with {} errors", result.getRowNumber(), result.getErrors().size());

                // Convert errors to entities and add to batch
                for (ImportErrorDTO errorDTO : result.getErrors()) {
                    ProductImportError errorEntity = convertToEntity(errorDTO);
                    errorsToSave.add(errorEntity);
                }
            }
        }

        // Save all errors in batch
        if (!errorsToSave.isEmpty()) {
            importErrorRepository.saveAll(errorsToSave);
            LOG.debug("Saved {} errors to database", errorsToSave.size());
        }

        // Update counters
        totalProcessed.addAndGet(chunk.size());
        successCount.addAndGet(batchSuccessCount);
        errorCount.addAndGet(batchErrorCount);

        // Update import job progress
        updateImportJobProgress();

        LOG.debug("Processed batch: {} total, {} success, {} errors", chunk.size(), batchSuccessCount, batchErrorCount);
    }

    /**
     * Update the import job progress in the database.
     */
    private void updateImportJobProgress() {
        try {
            int currentTotal = totalProcessed.get();
            int currentSuccess = successCount.get();
            int currentErrors = errorCount.get();

            // Get estimated total rows from step execution context
            int estimatedTotal = getEstimatedTotalRows();

            String currentPhase = String.format(
                "Processing products (%d/%d)",
                currentTotal,
                estimatedTotal > 0 ? estimatedTotal : currentTotal
            );

            importJobRepository.updateJobProgress(
                importJobId,
                estimatedTotal > 0 ? estimatedTotal : currentTotal,
                currentSuccess,
                currentErrors,
                currentPhase
            );

            LOG.debug(
                "Updated import job progress: {}/{} processed, {} successful, {} errors",
                currentTotal,
                estimatedTotal,
                currentSuccess,
                currentErrors
            );
        } catch (Exception e) {
            LOG.warn("Failed to update import job progress: {}", e.getMessage());
        }
    }

    /**
     * Get estimated total rows from step execution context.
     */
    private int getEstimatedTotalRows() {
        if (stepExecution != null && stepExecution.getJobExecution() != null) {
            Object totalRows = stepExecution.getJobExecution().getExecutionContext().get("estimatedDataRows");
            if (totalRows instanceof Integer) {
                return (Integer) totalRows;
            }
        }
        return 0;
    }

    /**
     * Convert ImportErrorDTO to ProductImportError entity.
     */
    private ProductImportError convertToEntity(ImportErrorDTO errorDTO) {
        ProductImportError error = new ProductImportError();

        error.setRowNumber(errorDTO.getRowNumber());
        error.setDataRowNumber(errorDTO.getDataRowNumber());
        error.setFieldName(errorDTO.getFieldName());
        error.setFieldValue(errorDTO.getFieldValue());
        error.setErrorType(errorDTO.getErrorType());
        error.setErrorMessage(errorDTO.getErrorMessage());
        error.setSuggestion(errorDTO.getSuggestion());

        // Set the import job reference
        ProductImportJob importJob = new ProductImportJob();
        importJob.setId(importJobId);
        error.setImportJob(importJob);

        return error;
    }

    /**
     * Get current progress statistics.
     */
    public ProgressStats getProgressStats() {
        return new ProgressStats(totalProcessed.get(), successCount.get(), errorCount.get());
    }

    /**
     * Reset counters (useful for testing or restarting jobs).
     */
    public void resetCounters() {
        totalProcessed.set(0);
        successCount.set(0);
        errorCount.set(0);
    }

    /**
     * Progress statistics holder.
     */
    public static class ProgressStats {

        private final int totalProcessed;
        private final int successCount;
        private final int errorCount;

        public ProgressStats(int totalProcessed, int successCount, int errorCount) {
            this.totalProcessed = totalProcessed;
            this.successCount = successCount;
            this.errorCount = errorCount;
        }

        public int getTotalProcessed() {
            return totalProcessed;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getErrorCount() {
            return errorCount;
        }

        public double getSuccessRate() {
            return totalProcessed > 0 ? (double) successCount / totalProcessed : 0.0;
        }

        public double getErrorRate() {
            return totalProcessed > 0 ? (double) errorCount / totalProcessed : 0.0;
        }

        @Override
        public String toString() {
            return String.format(
                "ProgressStats{total=%d, success=%d, errors=%d, successRate=%.2f%%}",
                totalProcessed,
                successCount,
                errorCount,
                getSuccessRate() * 100
            );
        }
    }
}
