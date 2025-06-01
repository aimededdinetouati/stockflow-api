package com.adeem.stockflow.config.batch;

import com.adeem.stockflow.config.ApplicationProperties;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfiguration {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean
    public Path tempFileStoragePath() {
        String tempDir = applicationProperties.getImport().getFileStorage().getTempDirectory();
        Path path = Paths.get(tempDir);

        // Create directory if it doesn't exist
        File directory = path.toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return path;
    }
}
