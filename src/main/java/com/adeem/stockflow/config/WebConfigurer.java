package com.adeem.stockflow.config;

import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.*;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import tech.jhipster.config.JHipsterProperties;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;
    private final JHipsterProperties jHipsterProperties;

    public WebConfigurer(Environment env, JHipsterProperties jHipsterProperties) {
        this.env = env;
        this.jHipsterProperties = jHipsterProperties;
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        if (env.getActiveProfiles().length != 0) {
            LOG.info("Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
        }
        LOG.info("Web application fully configured");
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = jHipsterProperties.getCors();

        // Enhanced CORS configuration for development
        if (!CollectionUtils.isEmpty(config.getAllowedOrigins()) || !CollectionUtils.isEmpty(config.getAllowedOriginPatterns())) {
            LOG.debug("Registering CORS filter with origins: {}", config.getAllowedOrigins());

            // Clone the configuration to avoid modifying the original
            CorsConfiguration enhancedConfig = new CorsConfiguration();
            enhancedConfig.setAllowedOrigins(config.getAllowedOrigins());
            enhancedConfig.setAllowedOriginPatterns(config.getAllowedOriginPatterns());
            enhancedConfig.setAllowedMethods(config.getAllowedMethods());
            enhancedConfig.setAllowedHeaders(config.getAllowedHeaders());
            enhancedConfig.setExposedHeaders(config.getExposedHeaders());
            enhancedConfig.setAllowCredentials(config.getAllowCredentials());
            enhancedConfig.setMaxAge(config.getMaxAge());

            // Ensure ALL HTTP methods are allowed (critical for Angular)
            if (CollectionUtils.isEmpty(enhancedConfig.getAllowedMethods())) {
                enhancedConfig.addAllowedMethod("*");
            }

            // Ensure ALL headers are allowed (critical for Angular HTTP interceptors)
            if (CollectionUtils.isEmpty(enhancedConfig.getAllowedHeaders())) {
                enhancedConfig.addAllowedHeader("*");
            }

            // Make sure credentials are allowed for authenticated requests
            if (enhancedConfig.getAllowCredentials() == null) {
                enhancedConfig.setAllowCredentials(true);
            }

            // Register CORS for all API endpoints
            source.registerCorsConfiguration("/api/**", enhancedConfig);
            source.registerCorsConfiguration("/management/**", enhancedConfig);
            source.registerCorsConfiguration("/v3/api-docs/**", enhancedConfig);
            source.registerCorsConfiguration("/swagger-ui/**", enhancedConfig);

            // Also register for websocket endpoints
            source.registerCorsConfiguration("/websocket/**", enhancedConfig);

            LOG.info("CORS filter registered for development with enhanced configuration");
        } else {
            LOG.warn("CORS configuration is empty - this may cause issues with frontend development");
        }

        return new CorsFilter(source);
    }
}
