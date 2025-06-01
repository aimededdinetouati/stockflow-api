package com.adeem.stockflow.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class BatchTestConfiguration {

    @Bean
    @Primary
    public JobLauncherTestUtils jobLauncherTestUtils(JobLauncher jobLauncher, @Qualifier("productImportJob") Job job) {
        JobLauncherTestUtils testUtils = new JobLauncherTestUtils();
        testUtils.setJobLauncher(jobLauncher);
        testUtils.setJob(job);
        return testUtils;
    }
}
