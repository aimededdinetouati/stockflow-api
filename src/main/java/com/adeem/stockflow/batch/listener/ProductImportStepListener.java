package com.adeem.stockflow.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Step execution listener for product import steps.
 */
@Component
public class ProductImportStepListener implements StepExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(ProductImportStepListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        LOG.debug("Starting step: {}", stepExecution.getStepName());

        // Store step start time for progress calculation
        stepExecution.getExecutionContext().putLong("stepStartTime", System.currentTimeMillis());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LOG.debug("Completed step: {} with status: {}", stepExecution.getStepName(), stepExecution.getStatus());

        // Log step statistics
        int readCount = Math.toIntExact(stepExecution.getReadCount());
        int writeCount = Math.toIntExact(stepExecution.getWriteCount());
        int skipCount = Math.toIntExact(stepExecution.getSkipCount());

        LOG.info("Step {} statistics: Read={}, Written={}, Skipped={}", stepExecution.getStepName(), readCount, writeCount, skipCount);

        // Store final statistics in execution context
        stepExecution.getExecutionContext().putInt("finalReadCount", readCount);
        stepExecution.getExecutionContext().putInt("finalWriteCount", writeCount);
        stepExecution.getExecutionContext().putInt("finalSkipCount", skipCount);

        return stepExecution.getExitStatus();
    }
}
