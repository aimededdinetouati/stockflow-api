package com.adeem.stockflow.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.config.ApplicationProperties;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.ProductImportJob;
import com.adeem.stockflow.domain.enumeration.ImportStatus;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.repository.ProductImportJobRepository;
import com.adeem.stockflow.repository.ProductRepository;
import com.adeem.stockflow.security.TestSecurityContextHelper;
import com.adeem.stockflow.service.dto.batch.ImportUploadResponse;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductImportResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class ProductImportResourceIT {

    private static final String ENTITY_API_URL = "/api/products/import";

    @Autowired
    private MockMvc restProductImportMockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImportJobRepository importJobRepository;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private Path tempFileStoragePath;

    private ClientAccount clientAccount;

    @BeforeEach
    void setUp() {
        // Create client account for testing
        clientAccount = new ClientAccount();
        clientAccount.setCompanyName("Test Company");
        clientAccount.setEmail("test@example.com");
        clientAccount.setPhone("1234567890");
        clientAccount.setStatus(com.adeem.stockflow.domain.enumeration.AccountStatus.ENABLED);
        clientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        // Set security context
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());

        // Ensure temp directory exists
        try {
            Files.createDirectories(tempFileStoragePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp directory", e);
        }
    }

    @AfterEach
    void tearDown() {
        TestSecurityContextHelper.clearSecurityContext();

        // Clean up test data
        importJobRepository.deleteAll();
        productRepository.deleteAll();
        clientAccountRepository.deleteAll();

        // Clean up test files
        try {
            if (Files.exists(tempFileStoragePath)) {
                Files.walk(tempFileStoragePath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {}
                    });
            }
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }

    @Test
    void uploadValidExcelFile() throws Exception {
        MockMultipartFile file = createValidExcelFile();

        MvcResult result = restProductImportMockMvc
            .perform(multipart(ENTITY_API_URL + "/upload").file(file).contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("STARTED"))
            .andExpect(jsonPath("$.jobId").exists())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ImportUploadResponse response = objectMapper.readValue(responseContent, ImportUploadResponse.class);

        // Verify job was created in database
        Optional<ProductImportJob> jobOpt = importJobRepository.findById(response.getJobId());
        assertThat(jobOpt).isPresent();
        ProductImportJob job = jobOpt.get();
        assertThat(job.getClientAccount().getId()).isEqualTo(clientAccount.getId());
        assertThat(job.getStatus()).isEqualTo(ImportStatus.STARTED);
        assertThat(job.getFileName()).isEqualTo("test-products.xlsx");
    }

    @Test
    @Transactional
    void uploadFileWithInvalidFormat() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Not an Excel file".getBytes());

        restProductImportMockMvc
            .perform(multipart(ENTITY_API_URL + "/upload").file(file).contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ErrorConstants.FILE_INVALID_FORMAT));
    }

    @Test
    @Transactional
    void uploadEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "empty.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            new byte[0]
        );

        restProductImportMockMvc
            .perform(multipart(ENTITY_API_URL + "/upload").file(file).contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ErrorConstants.FILE_EMPTY));
    }

    @Test
    @Transactional
    void uploadFileTooLarge() throws Exception {
        // Create a file larger than the configured limit
        byte[] largeContent = new byte[(int) (applicationProperties.getImport().getMaxFileSize() + 1)];
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "large.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            largeContent
        );

        restProductImportMockMvc
            .perform(multipart(ENTITY_API_URL + "/upload").file(file).contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ErrorConstants.FILE_TOO_LARGE));
    }

    @Test
    @Transactional
    void getImportStatus() throws Exception {
        // Create a job first
        ProductImportJob job = createTestImportJob();
        job = importJobRepository.saveAndFlush(job);

        restProductImportMockMvc
            .perform(get(ENTITY_API_URL + "/status/{jobId}", job.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jobId").value(job.getId()))
            .andExpect(jsonPath("$.status").value("PROCESSING"))
            .andExpect(jsonPath("$.fileName").value("test-file.xlsx"))
            .andExpect(jsonPath("$.totalRows").value(10))
            .andExpect(jsonPath("$.successfulRows").value(8))
            .andExpect(jsonPath("$.errorRows").value(2));
    }

    @Test
    @Transactional
    void getImportStatusForNonExistentJob() throws Exception {
        restProductImportMockMvc.perform(get(ENTITY_API_URL + "/status/{jobId}", 999L)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void getImportReport() throws Exception {
        // Create a completed job
        ProductImportJob job = createTestImportJob();
        job.setStatus(ImportStatus.COMPLETED);
        job.setEndTime(Instant.now());
        job = importJobRepository.saveAndFlush(job);

        restProductImportMockMvc
            .perform(get(ENTITY_API_URL + "/report/{jobId}", job.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.summary.totalRows").value(10))
            .andExpect(jsonPath("$.summary.successfulRows").value(8))
            .andExpect(jsonPath("$.summary.failedRows").value(2))
            .andExpect(jsonPath("$.fileInfo.fileName").value("test-file.xlsx"))
            .andExpect(jsonPath("$.fileInfo.fileSize").value(1024));
    }

    @Test
    @Transactional
    void getImportErrors() throws Exception {
        // Create a job with errors
        ProductImportJob job = createTestImportJob();
        job = importJobRepository.saveAndFlush(job);

        restProductImportMockMvc
            .perform(get(ENTITY_API_URL + "/report/{jobId}/errors", job.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @Transactional
    void downloadTemplate() throws Exception {
        restProductImportMockMvc
            .perform(get(ENTITY_API_URL + "/template"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"stockflow-product-import-template.xlsx\""))
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    @Transactional
    void getImportJobs() throws Exception {
        // Create several jobs
        for (int i = 0; i < 3; i++) {
            ProductImportJob job = createTestImportJob();
            job.setFileName("test-file-" + i + ".xlsx");
            importJobRepository.saveAndFlush(job);
        }

        restProductImportMockMvc
            .perform(get(ENTITY_API_URL + "/jobs"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @Transactional
    void cancelImportJob() throws Exception {
        // Create a running job
        ProductImportJob job = createTestImportJob();
        job.setStatus(ImportStatus.PROCESSING);
        job = importJobRepository.saveAndFlush(job);

        restProductImportMockMvc.perform(delete(ENTITY_API_URL + "/jobs/{jobId}", job.getId())).andExpect(status().isNoContent());

        // Verify job was cancelled
        ProductImportJob updatedJob = importJobRepository.findById(job.getId()).orElseThrow();
        assertThat(updatedJob.getStatus()).isEqualTo(ImportStatus.CANCELLED);
    }

    // Helper methods

    private MockMultipartFile createValidExcelFile() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Code");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Quantity");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return new MockMultipartFile(
                "file",
                "test-products.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray()
            );
        }
    }

    private MockMultipartFile createValidExcelFileWithProducts() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Code");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Quantity");
            headerRow.createCell(3).setCellValue("Category");
            headerRow.createCell(4).setCellValue("Price");

            // Create data rows
            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("PROD001");
            dataRow1.createCell(1).setCellValue("Test Product 1");
            dataRow1.createCell(2).setCellValue(10);
            dataRow1.createCell(3).setCellValue("ELECTRONICS");
            dataRow1.createCell(4).setCellValue(99.99);

            Row dataRow2 = sheet.createRow(2);
            dataRow2.createCell(0).setCellValue("PROD002");
            dataRow2.createCell(1).setCellValue("Test Product 2");
            dataRow2.createCell(2).setCellValue(5);
            dataRow2.createCell(3).setCellValue("COMPUTERS");
            dataRow2.createCell(4).setCellValue(199.99);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return new MockMultipartFile(
                "file",
                "test-products-with-data.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray()
            );
        }
    }

    private MockMultipartFile createInvalidExcelFile() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Code");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Quantity");

            // Create invalid data rows
            Row dataRow1 = sheet.createRow(1);
            // Missing required fields
            dataRow1.createCell(0).setCellValue("");
            dataRow1.createCell(1).setCellValue("Product without code");
            dataRow1.createCell(2).setCellValue("invalid_quantity");

            Row dataRow2 = sheet.createRow(2);
            dataRow2.createCell(0).setCellValue("PROD001"); // Duplicate if run after valid test
            dataRow2.createCell(1).setCellValue("");
            dataRow2.createCell(2).setCellValue(-5); // Negative quantity

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return new MockMultipartFile(
                "file",
                "test-products-invalid.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray()
            );
        }
    }

    private ProductImportJob createTestImportJob() {
        ProductImportJob job = new ProductImportJob();
        job.setFileName("test-file.xlsx");
        job.setFileSize(1024L);
        job.setStatus(ImportStatus.PROCESSING);
        job.setTotalRows(10);
        job.setSuccessfulRows(8);
        job.setFailedRows(2);
        job.setStartTime(Instant.now());
        job.setCurrentPhase("Processing products");
        job.setClientAccount(clientAccount);
        return job;
    }

    private MockMultipartFile getExcelFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("import-samples/" + fileName + ".xlsx");

        return new MockMultipartFile(
            "file",
            fileName + ".xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            resource.getInputStream()
        );
    }
}
