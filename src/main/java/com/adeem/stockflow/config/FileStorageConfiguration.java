package com.adeem.stockflow.batch.config;

import com.adeem.stockflow.config.ImportConfigurationProperties;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfiguration {

    @Autowired
    private ImportConfigurationProperties importConfig;

    @Bean
    public Path tempFileStoragePath() {
        String tempDir = importConfig.getFileStorage().getTempDirectory();
        Path path = Paths.get(tempDir);

        // Create directory if it doesn't exist
        File directory = path.toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return path;
    }
}
