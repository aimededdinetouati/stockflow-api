package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.ResourceLimitAsserts.*;
import static com.adeem.stockflow.domain.ResourceLimitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResourceLimitMapperTest {

    private ResourceLimitMapper resourceLimitMapper;

    @BeforeEach
    void setUp() {
        resourceLimitMapper = new ResourceLimitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getResourceLimitSample1();
        var actual = resourceLimitMapper.toEntity(resourceLimitMapper.toDto(expected));
        assertResourceLimitAllPropertiesEquals(expected, actual);
    }
}
