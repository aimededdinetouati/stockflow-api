// File: src/main/java/com/adeem/stockflow/config/MarketplaceSecurityConfig.java
package com.adeem.stockflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for public marketplace APIs.
 * Allows anonymous access to marketplace browsing functionality.
 */
@Configuration
@EnableWebSecurity
public class MarketplaceSecurityConfig {

    /**
     * Security filter chain for public marketplace APIs.
     * This chain has higher priority (@Order(1)) than the main security config.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain publicApiFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/public/**")
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth ->
                auth
                    // Public product catalog endpoints
                    .requestMatchers("/api/public/products/**")
                    .permitAll()
                    // Public cart endpoints (guest cart functionality)
                    .requestMatchers("/api/public/cart/guest/**")
                    .permitAll()
                    // Public customer registration endpoints
                    .requestMatchers("/api/public/customers/register")
                    .permitAll()
                    .requestMatchers("/api/public/customers/validate-registration")
                    .permitAll()
                    .requestMatchers("/api/public/customers/reset-password")
                    .permitAll()
                    .requestMatchers("/api/public/customers/change-password")
                    .permitAll()
                    // Public company discovery endpoints
                    .requestMatchers("/api/public/companies/**")
                    .permitAll()
                    // Public marketplace statistics
                    .requestMatchers("/api/public/marketplace-stats")
                    .permitAll()
                    // Any other public endpoints require authentication
                    .anyRequest()
                    .authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
    }
}
