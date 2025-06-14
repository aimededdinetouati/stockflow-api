package com.adeem.stockflow.service;

import com.adeem.stockflow.config.ApplicationProperties;
import com.adeem.stockflow.config.Constants;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.ProductImportJob;
import com.adeem.stockflow.domain.enumeration.ImportStatus;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.repository.ProductImportErrorRepository;
import com.adeem.stockflow.repository.ProductImportJobRepository;
import com.adeem.stockflow.service.dto.batch.*;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing Product Import operations.
 */
@Service
public class ProductImportService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductImportService.class);

    private final ProductImportJobRepository importJobRepository;
    private final ProductImportErrorRepository importErrorRepository;
    private final ClientAccountRepository clientAccountRepository;
    private final JobLauncher asyncJobLauncher;
    private final Job productImportJob;
    private final ExcelTemplateService excelTemplateService;
    private final Path tempFileStoragePath;
    private final ApplicationProperties applicationProperties;

    public ProductImportService(
        ProductImportJobRepository importJobRepository,
        ProductImportErrorRepository importErrorRepository,
        ClientAccountRepository clientAccountRepository,
        JobLauncher asyncJobLauncher,
        Job productImportJob,
        ExcelTemplateService excelTemplateService,
        Path tempFileStoragePath,
        ApplicationProperties applicationProperties
    ) {
        this.importJobRepository = importJobRepository;
        this.importErrorRepository = importErrorRepository;
        this.clientAccountRepository = clientAccountRepository;
        this.asyncJobLauncher = asyncJobLauncher;
        this.productImportJob = productImportJob;
        this.excelTemplateService = excelTemplateService;
        this.tempFileStoragePath = tempFileStoragePath;
        this.applicationProperties = applicationProperties;
    }

    /**
     * Upload file and start import process.
     */
    public ImportUploadResponse uploadAndStartImport(MultipartFile file, Long clientAccountId) {
        LOG.info("Starting product import for client account: {} with file: {}", clientAccountId, file.getOriginalFilename());
        validateUploadedFile(file);
        try {
            // Save file temporarily
            String tempFileName = saveTemporaryFile(file);

            // Create import job record
            ProductImportJob importJob = createImportJobRecord(file, clientAccountId, tempFileName);

            // Start batch job
            JobExecution jobExecution = startBatchJob(importJob, tempFileName, clientAccountId);

            // Update job with execution ID
            importJob.setJobExecutionId(jobExecution.getId());
            importJobRepository.save(importJob);

            return new ImportUploadResponse(importJob.getId(), ImportStatus.STARTED, "Import job started successfully");
        } catch (Exception e) {
            LOG.error("Error starting import job for client account {}: {}", clientAccountId, e.getMessage(), e);
            throw new BadRequestAlertException("Failed to start import: " + e.getMessage(), "productImport", ErrorConstants.IMPORT_FAILED);
        }
    }

    /**
     * Get import progress.
     */
    @Transactional(readOnly = true)
    public Optional<ImportProgressDTO> getImportProgress(Long jobId, Long clientAccountId) {
        LOG.debug("Getting import progress for job: {} and client account: {}", jobId, clientAccountId);

        return importJobRepository.findByIdAndClientAccountId(jobId, clientAccountId).map(this::convertToProgressDTO);
    }

    /**
     * Get import report with summary and errors.
     */
    @Transactional(readOnly = true)
    public Optional<ImportReportDTO> getImportReport(Long jobId, Long clientAccountId) {
        LOG.debug("Getting import report for job: {} and client account: {}", jobId, clientAccountId);

        return importJobRepository
            .findByIdAndClientAccountId(jobId, clientAccountId)
            .map(job -> {
                ImportReportDTO report = new ImportReportDTO();

                // Set summary
                ImportReportDTO.ImportSummaryDTO summary = new ImportReportDTO.ImportSummaryDTO();
                summary.setTotalRows(job.getTotalRows() != null ? job.getTotalRows() : 0);
                summary.setSuccessfulRows(job.getSuccessfulRows() != null ? job.getSuccessfulRows() : 0);
                summary.setFailedRows(job.getFailedRows() != null ? job.getFailedRows() : 0);
                summary.setStartTime(job.getStartTime());
                summary.setEndTime(job.getEndTime());

                if (job.getStartTime() != null && job.getEndTime() != null) {
                    summary.setExecutionTime(Duration.between(job.getStartTime(), job.getEndTime()));
                }

                report.setSummary(summary);

                // Set file info
                ImportReportDTO.FileInfoDTO fileInfo = new ImportReportDTO.FileInfoDTO();
                fileInfo.setFileName(job.getFileName());
                fileInfo.setFileSize(job.getFileSize());
                fileInfo.setHeaderRowNumber(job.getHeaderRowNumber());
                report.setFileInfo(fileInfo);

                // Get errors (limit to first 100 for summary)
                List<ImportErrorDTO> errors = importErrorRepository
                    .findByImportJobIdOrderByRowNumberAsc(jobId)
                    .stream()
                    .limit(100)
                    .map(this::convertToErrorDTO)
                    .toList();
                report.setErrors(errors);

                return report;
            });
    }

    /**
     * Get import errors with pagination.
     */
    @Transactional(readOnly = true)
    public Page<ImportErrorDTO> getImportErrors(Long jobId, Long clientAccountId, Pageable pageable) {
        LOG.debug("Getting import errors for job: {} and client account: {}", jobId, clientAccountId);

        // Verify job belongs to client account
        importJobRepository
            .findByIdAndClientAccountId(jobId, clientAccountId)
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        return importErrorRepository.findByImportJobIdOrderByRowNumberAsc(jobId, pageable).map(this::convertToErrorDTO);
    }

    /**
     * Generate Excel template.
     */
    @Transactional
    public Resource generateTemplate() {
        LOG.debug("Generating Excel template");
        return excelTemplateService.generateTemplate();
    }

    /**
     * Get import job history.
     */
    @Transactional(readOnly = true)
    public Page<ImportJobSummaryDTO> getImportJobHistory(Long clientAccountId, Pageable pageable) {
        LOG.debug("Getting import job history for client account: {}", clientAccountId);

        return importJobRepository.findByClientAccountIdOrderByStartTimeDesc(clientAccountId, pageable).map(this::convertToJobSummaryDTO);
    }

    /**
     * Cancel import job.
     */
    @Transactional
    public void cancelImportJob(Long jobId, Long clientAccountId) {
        LOG.debug("Cancelling import job: {} for client account: {}", jobId, clientAccountId);

        ProductImportJob job = importJobRepository
            .findByIdAndClientAccountId(jobId, clientAccountId)
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        if (job.getStatus() == ImportStatus.STARTED || job.getStatus() == ImportStatus.PROCESSING) {
            job.setStatus(ImportStatus.CANCELLED);
            job.setEndTime(Instant.now());
            job.setCurrentPhase("Cancelled by user");
            importJobRepository.save(job);
        }
    }

    // Private helper methods

    private void validateUploadedFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestAlertException("File is empty", "productImport", ErrorConstants.FILE_EMPTY);
        }

        if (file.getSize() > applicationProperties.getImport().getMaxFileSize()) {
            throw new BadRequestAlertException("File size exceeds limit", "productImport", ErrorConstants.FILE_TOO_LARGE);
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !isValidFileFormat(fileName)) {
            throw new BadRequestAlertException("Invalid file format", "productImport", ErrorConstants.FILE_INVALID_FORMAT);
        }
    }

    private boolean isValidFileFormat(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return Arrays.asList(applicationProperties.getImport().getSupportedFormats()).contains(extension);
    }

    private String saveTemporaryFile(MultipartFile file) throws IOException {
        String tempFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path tempFilePath = tempFileStoragePath.resolve(tempFileName);

        Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);

        LOG.debug("Saved temporary file: {}", tempFilePath);
        return tempFileName;
    }

    private ProductImportJob createImportJobRecord(MultipartFile file, Long clientAccountId, String tempFileName) {
        ClientAccount clientAccount = clientAccountRepository
            .findById(clientAccountId)
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        ProductImportJob importJob = new ProductImportJob();
        importJob.setFileName(file.getOriginalFilename());
        importJob.setFileSize(file.getSize());
        importJob.setStatus(ImportStatus.STARTED);
        importJob.setStartTime(Instant.now());
        importJob.setCurrentPhase("Initializing import");
        importJob.setClientAccount(clientAccount);

        return importJobRepository.save(importJob);
    }

    private JobExecution startBatchJob(ProductImportJob importJob, String tempFileName, Long clientAccountId) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("fileName", tempFileName)
            .addLong("importJobId", importJob.getId())
            .addLong("clientAccountId", clientAccountId)
            .addLong("timestamp", System.currentTimeMillis()) // for uniqueness
            .toJobParameters();

        return asyncJobLauncher.run(productImportJob, jobParameters);
    }

    private ImportProgressDTO convertToProgressDTO(ProductImportJob job) {
        ImportProgressDTO dto = new ImportProgressDTO();
        dto.setJobId(job.getId());
        dto.setStatus(job.getStatus());
        dto.setTotalRows(job.getTotalRows() != null ? job.getTotalRows() : 0);
        dto.setSuccessfulRows(job.getSuccessfulRows() != null ? job.getSuccessfulRows() : 0);
        dto.setErrorRows(job.getFailedRows() != null ? job.getFailedRows() : 0);
        dto.setCurrentPhase(job.getCurrentPhase());
        dto.setStartTime(job.getStartTime());
        dto.setFileName(job.getFileName());
        dto.setFileSize(job.getFileSize());

        // Calculate processed rows
        int successfulRows = job.getSuccessfulRows() != null ? job.getSuccessfulRows() : 0;
        int errorRows = job.getFailedRows() != null ? job.getFailedRows() : 0;
        dto.setProcessedRows(successfulRows + errorRows);

        return dto;
    }

    private ImportErrorDTO convertToErrorDTO(com.adeem.stockflow.domain.ProductImportError error) {
        ImportErrorDTO dto = new ImportErrorDTO();
        dto.setId(error.getId());
        dto.setRowNumber(error.getRowNumber() != null ? error.getRowNumber() : 0);
        dto.setDataRowNumber(error.getDataRowNumber() != null ? error.getDataRowNumber() : 0);
        dto.setFieldName(error.getFieldName());
        dto.setFieldValue(error.getFieldValue());
        dto.setErrorType(error.getErrorType());
        dto.setErrorMessage(error.getErrorMessage());
        dto.setSuggestion(error.getSuggestion());
        return dto;
    }

    private ImportJobSummaryDTO convertToJobSummaryDTO(ProductImportJob job) {
        ImportJobSummaryDTO dto = new ImportJobSummaryDTO();
        dto.setJobId(job.getId());
        dto.setFileName(job.getFileName());
        dto.setStatus(job.getStatus());
        dto.setStartTime(job.getStartTime());
        dto.setEndTime(job.getEndTime());
        dto.setTotalRows(job.getTotalRows() != null ? job.getTotalRows() : 0);
        dto.setSuccessfulRows(job.getSuccessfulRows() != null ? job.getSuccessfulRows() : 0);
        dto.setFailedRows(job.getFailedRows() != null ? job.getFailedRows() : 0);
        return dto;
    }
}
