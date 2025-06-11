package com.adeem.stockflow.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for Customer API.
 * Configures caching for customer statistics and frequently accessed data.
 */
@Configuration
@EnableCaching
public class CustomerCacheConfiguration {

    /**
     * Cache names used in the Customer API.
     */
    public static final String CUSTOMER_STATS_CACHE = "customerStats";
    public static final String CUSTOMER_VALIDATION_CACHE = "customerValidation";
    public static final String ASSOCIATION_STATS_CACHE = "associationStats";
    public static final String MARKETPLACE_STATS_CACHE = "marketplaceStats";

    /**
     * JCache customizer for Customer API caches.
     */
    @Bean
    public JCacheManagerCustomizer customerCacheCustomizer() {
        return cm -> {
            // Customer statistics cache - cache for 5 minutes
            createCache(cm, CUSTOMER_STATS_CACHE, 100, Duration.ofMinutes(5));

            // Customer validation cache - cache for 1 minute (phone/email uniqueness)
            createCache(cm, CUSTOMER_VALIDATION_CACHE, 1000, Duration.ofMinutes(1));

            // Association statistics cache - cache for 10 minutes
            createCache(cm, ASSOCIATION_STATS_CACHE, 100, Duration.ofMinutes(10));

            // Marketplace statistics cache - cache for 1 hour
            createCache(cm, MARKETPLACE_STATS_CACHE, 10, Duration.ofHours(1));
        };
    }

    /**
     * Helper method to create cache configuration.
     */
    private void createCache(javax.cache.CacheManager cm, String cacheName, long maxEntries, Duration ttl) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(
                cacheName,
                Eh107Configuration.fromEhcacheCacheConfiguration(
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(
                        Object.class,
                        Object.class,
                        ResourcePoolsBuilder.heap(maxEntries)
                    )
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(ttl))
                        .build()
                )
            );
        }
    }

    /**
     * Cache configuration for customer entities (if needed for heavy read operations).
     */
    @Bean
    public JCacheManagerCustomizer customerEntityCacheCustomizer() {
        return cm -> {
            // Customer entity cache - short TTL for data consistency
            createCache(cm, "com.adeem.stockflow.domain.Customer", 500, Duration.ofMinutes(2));

            // Customer associations cache
            createCache(cm, "com.adeem.stockflow.domain.CustomerClientAssociation", 1000, Duration.ofMinutes(5));

            // Customer by client account cache
            createCache(cm, "customersByClientAccount", 100, Duration.ofMinutes(3));
        };
    }
}
