package com.adeem.stockflow.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestCacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        return new NoOpCacheManager();
    }
}
