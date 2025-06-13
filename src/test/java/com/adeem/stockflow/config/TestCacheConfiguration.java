package com.adeem.stockflow.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test-specific cache configuration that uses in-memory caches
 * and handles cache lifecycle more gracefully during tests.
 */
@TestConfiguration
@EnableCaching
@Profile("test")
public class TestCacheConfiguration {

    /**
     * Provides a simple concurrent map cache manager for tests
     * that's more resilient to context reloading
     */
    @Bean
    @Primary
    public CacheManager testCacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();

        // Pre-create all the caches that your application uses
        cacheManager.setCacheNames(
            java.util.Arrays.asList(
                "usersByLogin",
                "usersByEmail",
                "inventoryStatsCache",
                "supplierStatsCache",
                "customerStatsCache",
                "customerValidationCache",
                "associationStatsCache",
                "marketplaceStatsCache"
            )
        );

        // Allow dynamic cache creation
        cacheManager.setAllowNullValues(false);

        return cacheManager;
    }
}
