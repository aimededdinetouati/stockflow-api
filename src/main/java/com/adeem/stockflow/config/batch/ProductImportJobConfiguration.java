package com.adeem.stockflow.config.batch;

import com.adeem.stockflow.batch.listener.ProductImportJobListener;
import com.adeem.stockflow.batch.listener.ProductImportStepListener;
import com.adeem.stockflow.batch.reader.ExcelProductItemReader;
import com.adeem.stockflow.config.ApplicationProperties;
import com.adeem.stockflow.service.dto.batch.ProductCreationResult;
import com.adeem.stockflow.service.dto.batch.ProductImportRow;
import java.nio.file.Path;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ProductImportJobConfiguration {

    private final ApplicationProperties applicationProperties;
    private final Path tempFileStoragePath;

    public ProductImportJobConfiguration(ApplicationProperties applicationProperties, Path tempFileStoragePath) {
        this.applicationProperties = applicationProperties;
        this.tempFileStoragePath = tempFileStoragePath;
    }

    @Bean
    public Job productImportJob(JobRepository jobRepository, Step productImportStep, ProductImportJobListener jobListener) {
        return new JobBuilder("productImportJob", jobRepository).start(productImportStep).listener(jobListener).build();
    }

    @Bean
    public Step productImportStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ExcelProductItemReader excelProductItemReader,
        ItemProcessor<ProductImportRow, ProductCreationResult> productImportProcessor,
        ItemWriter<ProductCreationResult> productImportWriter,
        ProductImportStepListener stepListener
    ) {
        return new StepBuilder("productImportStep", jobRepository)
            .<ProductImportRow, ProductCreationResult>chunk(applicationProperties.getImport().getChunkSize(), transactionManager)
            .reader(excelProductItemReader)
            .processor(productImportProcessor)
            .writer(productImportWriter)
            .listener(stepListener)
            .faultTolerant()
            .skipLimit(Integer.MAX_VALUE)
            .skip(Exception.class)
            .build();
    }
}
