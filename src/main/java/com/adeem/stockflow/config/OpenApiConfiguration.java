package com.adeem.stockflow.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.apidoc.customizer.JHipsterOpenApiCustomizer;

@Configuration
@Profile(JHipsterConstants.SPRING_PROFILE_API_DOCS)
public class OpenApiConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "allApisGroupedOpenAPI")
    public GroupedOpenApi allApisGroupedOpenAPI(
        JHipsterOpenApiCustomizer jhipsterOpenApiCustomizer,
        JHipsterProperties jHipsterProperties
    ) {
        JHipsterProperties.ApiDocs properties = jHipsterProperties.getApiDocs();
        return GroupedOpenApi.builder()
            .group("stockflow-api")
            .addOpenApiCustomizer(jhipsterOpenApiCustomizer)
            .packagesToScan("com.adeem.stockflow.web.rest")
            .pathsToMatch(properties.getDefaultIncludePattern())
            .build();
    }
}
