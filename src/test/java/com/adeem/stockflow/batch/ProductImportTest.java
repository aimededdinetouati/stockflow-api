package com.adeem.stockflow.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.*;
import com.adeem.stockflow.repository.*;
import com.adeem.stockflow.security.TestSecurityContextHelper;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

/**
 * Fixed Spring Batch integration test for Product Import Job.
 * Properly configures JobLauncherTestUtils with JobLauncher.
 */
@IntegrationTest
@SpringBatchTest
@TestPropertySource(
    properties = {
        "spring.batch.job.enabled=false",
        "application.import.chunk-size=5",
        "application.import.file-storage.temp-directory=${java.io.tmpdir}/stockflow-imports",
    }
)
class ProductImportBatchTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("productImportJob")
    private Job productImportJob;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductFamilyRepository productFamilyRepository;

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Autowired
    private ProductImportJobRepository importJobRepository;

    @Autowired
    private ProductImportErrorRepository importErrorRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    private ClientAccount clientAccount;
    private ProductImportJob importJob;
    private Path batchFilePath;

    @BeforeEach
    void setUp() throws IOException {
        // Configure JobLauncherTestUtils manually
        jobLauncherTestUtils.setJobLauncher(jobLauncher);
        jobLauncherTestUtils.setJob(productImportJob);

        // Clean up any existing data
        cleanupTestData();

        // Create test client account
        clientAccount = new ClientAccount();
        clientAccount.setCompanyName("Test Company");
        clientAccount.setEmail("test@example.com");
        clientAccount.setPhone("1234567890");
        clientAccount.setStatus(AccountStatus.ENABLED);
        clientAccount = clientAccountRepository.saveAndFlush(clientAccount);

        // Create import job record
        importJob = new ProductImportJob();
        importJob.setFileName("valid-import.xlsx");
        importJob.setFileSize(1024L);
        importJob.setStatus(ImportStatus.STARTED);
        importJob.setStartTime(Instant.now());
        importJob.setCurrentPhase("Testing");
        importJob.setClientAccount(clientAccount);
        importJob = importJobRepository.saveAndFlush(importJob);

        // Set security context
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(clientAccount.getId());
    }

    @AfterEach
    void tearDown() throws IOException {
        TestSecurityContextHelper.clearSecurityContext();

        // Clean up test files
        if (batchFilePath != null && Files.exists(batchFilePath)) {
            Files.deleteIfExists(batchFilePath);
        }

        cleanupTestData();
    }

    private void cleanupTestData() {
        // Clean up in proper order due to foreign key constraints
        try {
            inventoryTransactionRepository.deleteAll();
            inventoryRepository.deleteAll();
            productRepository.deleteAll();
            productFamilyRepository.deleteAll();
            importErrorRepository.deleteAll();
            importJobRepository.deleteAll();
        } catch (Exception e) {
            // Ignore cleanup errors in tests
            System.out.println("Cleanup warning: " + e.getMessage());
        }
    }

    @Test
    void testProductImportFromExistingFile() throws Exception {
        // Verify JobLauncherTestUtils is properly configured
        assertThat(jobLauncherTestUtils.getJobLauncher()).isNotNull();
        assertThat(jobLauncherTestUtils.getJob()).isNotNull();

        // Get the Excel file from test resources
        ClassPathResource resource = new ClassPathResource("import-samples/valid-import.xlsx");
        assertThat(resource.exists()).withFailMessage("Excel file not found at: import-samples/valid-import.xlsx").isTrue();

        // Create batch temp directory and copy the file
        Path batchTempDir = createBatchTempDirectory();
        String fileName = "valid-import.xlsx";
        batchFilePath = batchTempDir.resolve(fileName);
        Files.copy(resource.getInputStream(), batchFilePath, StandardCopyOption.REPLACE_EXISTING);

        // Verify file was copied
        assertThat(Files.exists(batchFilePath)).isTrue();
        assertThat(Files.size(batchFilePath)).isGreaterThan(0);

        // Execute batch job
        JobParameters jobParameters = createJobParameters(fileName);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Check job execution status
        assertThat(jobExecution.getStatus()).withFailMessage("Job should complete successfully").isEqualTo(BatchStatus.COMPLETED);

        // Check all products
        List<Product> allProducts = productRepository.findAll();

        // If job completed successfully, check for products
        if (jobExecution.getStatus() == BatchStatus.COMPLETED && !jobExecution.getStepExecutions().isEmpty()) {
            StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
            if (stepExecution.getReadCount() > 0) {
                assertThat(allProducts).withFailMessage("Expected products to be created for client account").isNotEmpty();

                Map<String, BigDecimal> productQuantities = new HashMap<>();
                productQuantities.put("T6508-500ML", BigDecimal.valueOf(60));
                productQuantities.put("CHLORID 500GR", BigDecimal.valueOf(13));
                productQuantities.put("NDS.500G", BigDecimal.valueOf(11));
                productQuantities.put("32201-2.5L", BigDecimal.valueOf(250));
                productQuantities.put("07102-2.5 L", BigDecimal.valueOf(0));
                productQuantities.put("04243-1Kg", BigDecimal.valueOf(110));
                productQuantities.put("H20022", BigDecimal.valueOf(6));
                productQuantities.put("H20021", BigDecimal.valueOf(9));
                productQuantities.put("c818", BigDecimal.valueOf(10));
                productQuantities.put("071-202-23", BigDecimal.valueOf(1));
                productQuantities.put("123.223.01", BigDecimal.valueOf(1));

                allProducts.forEach(product -> {
                    assertThat(productQuantities)
                        .withFailMessage("Expected product code in map: " + product.getCode())
                        .containsKey(product.getCode());

                    var inventoryOpt = inventoryRepository.findByProductId(product.getId());
                    assertThat(inventoryOpt).withFailMessage("Expected inventory for product: " + product.getCode()).isPresent();
                    assertThat(inventoryOpt.get().getQuantity().compareTo(productQuantities.get(product.getCode())))
                        .withFailMessage(
                            "Expected inventory quantity for product: " +
                            product.getCode() +
                            " to match import quantity: " +
                            productQuantities.get(product.getCode()) +
                            " but found: " +
                            inventoryOpt.get().getQuantity()
                        )
                        .isZero();
                });
            }
        }
    }

    private Path createBatchTempDirectory() throws IOException {
        Path batchTempDir = Path.of(System.getProperty("java.io.tmpdir"), "stockflow-imports");
        if (!Files.exists(batchTempDir)) {
            Files.createDirectories(batchTempDir);
        }
        System.out.println("Batch temp directory: " + batchTempDir);
        return batchTempDir;
    }

    private JobParameters createJobParameters(String fileName) {
        return new JobParametersBuilder()
            .addString("fileName", fileName)
            .addLong("importJobId", importJob.getId())
            .addLong("clientAccountId", clientAccount.getId())
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();
    }
}
