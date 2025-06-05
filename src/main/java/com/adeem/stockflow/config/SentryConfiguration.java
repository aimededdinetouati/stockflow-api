package com.adeem.stockflow.config;

import io.sentry.Sentry;
import io.sentry.SentryOptions;
import io.sentry.spring.jakarta.SentryTaskDecorator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;

@Configuration
public class SentryConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SentryConfiguration.class);

    @Value("${sentry.dsn:}")
    private String sentryDsn;

    @Value("${spring.application.name}")
    private String applicationName;

    @PostConstruct
    public void configureSentry() {
        if (sentryDsn != null && !sentryDsn.isEmpty()) {
            Sentry.configureScope(scope -> {
                scope.setTag("application", applicationName);
                scope.setTag("component", "stockflow-api");
            });
            log.info("Sentry configured for application: {}", applicationName);
        } else {
            log.warn("Sentry DSN not configured, error tracking disabled");
        }
    }

    @Bean
    public TaskDecorator sentryTaskDecorator() {
        return new SentryTaskDecorator();
    }
}
