package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.batch.ProductImportService;
import com.adeem.stockflow.service.dto.batch.*;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Product Import operations.
 */
@RestController
@RequestMapping("/api/products/import")
public class ProductImportResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductImportResource.class);

    private static final String ENTITY_NAME = "productImport";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductImportService productImportService;

    public ProductImportResource(ProductImportService productImportService) {
        this.productImportService = productImportService;
    }

    /**
     * POST /api/products/import/upload : Upload Excel file for product import.
     *
     * @param file the Excel file to import
     * @return the ResponseEntity with status 201 (Created) and import job details
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('ROLE_USER_ADMIN')")
    public ResponseEntity<ImportUploadResponse> uploadProductsFile(@RequestParam("file") MultipartFile file) {
        LOG.debug("REST request to upload products file: {}", file.getOriginalFilename());

        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        ImportUploadResponse response = productImportService.uploadAndStartImport(file, clientAccountId);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products/import/status/{jobId} : Get import job status.
     *
     * @param jobId the ID of the import job
     * @return the ResponseEntity with status 200 (OK) and the progress information
     */
    @GetMapping("/status/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_USER_ADMIN')")
    public ResponseEntity<ImportProgressDTO> getImportStatus(@PathVariable Long jobId) {
        LOG.debug("REST request to get import status: {}", jobId);

        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Optional<ImportProgressDTO> progress = productImportService.getImportProgress(jobId, clientAccountId);

        return ResponseUtil.wrapOrNotFound(progress);
    }

    /**
     * GET /api/products/import/report/{jobId} : Get import job report.
     *
     * @param jobId the ID of the import job
     * @return the ResponseEntity with status 200 (OK) and the report
     */
    @GetMapping("/report/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_USER_ADMIN')")
    public ResponseEntity<ImportReportDTO> getImportReport(@PathVariable Long jobId) {
        LOG.debug("REST request to get import report: {}", jobId);

        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Optional<ImportReportDTO> report = productImportService.getImportReport(jobId, clientAccountId);

        return ResponseUtil.wrapOrNotFound(report);
    }

    /**
     * GET /api/products/import/report/{jobId}/errors : Get import errors with pagination.
     *
     * @param jobId the ID of the import job
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of errors
     */
    @GetMapping("/report/{jobId}/errors")
    @PreAuthorize("hasAuthority('ROLE_USER_ADMIN')")
    public ResponseEntity<List<ImportErrorDTO>> getImportErrors(@PathVariable Long jobId, Pageable pageable) {
        LOG.debug("REST request to get import errors: {}", jobId);

        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Page<ImportErrorDTO> page = productImportService.getImportErrors(jobId, clientAccountId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET /api/products/import/template : Download Excel template for product import.
     *
     * @return the ResponseEntity with status 200 (OK) and the Excel template file
     */
    @GetMapping("/template")
    @PreAuthorize("hasAuthority('ROLE_USER_ADMIN')")
    public ResponseEntity<Resource> downloadTemplate() {
        LOG.debug("REST request to download import template");

        Resource template = productImportService.generateTemplate();

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"stockflow-product-import-template.xlsx\"")
            .body(template);
    }

    /**
     * GET /api/products/import/jobs : Get import job history.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of import jobs
     */
    @GetMapping("/jobs")
    @PreAuthorize("hasAuthority('ROLE_USER_ADMIN')")
    public ResponseEntity<List<ImportJobSummaryDTO>> getImportJobs(Pageable pageable) {
        LOG.debug("REST request to get import jobs");

        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Page<ImportJobSummaryDTO> page = productImportService.getImportJobHistory(clientAccountId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * DELETE /api/products/import/jobs/{jobId} : Cancel or delete import job.
     *
     * @param jobId the ID of the import job
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/jobs/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_USER_ADMIN')")
    public ResponseEntity<Void> cancelImportJob(@PathVariable Long jobId) {
        LOG.debug("REST request to cancel import job: {}", jobId);

        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
        productImportService.cancelImportJob(jobId, clientAccountId);

        return ResponseEntity.noContent().build();
    }
}
