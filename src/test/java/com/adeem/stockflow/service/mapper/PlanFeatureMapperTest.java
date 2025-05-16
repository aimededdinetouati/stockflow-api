package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.PlanFeatureAsserts.*;
import static com.adeem.stockflow.domain.PlanFeatureTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlanFeatureMapperTest {

    private PlanFeatureMapper planFeatureMapper;

    @BeforeEach
    void setUp() {
        planFeatureMapper = new PlanFeatureMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPlanFeatureSample1();
        var actual = planFeatureMapper.toEntity(planFeatureMapper.toDto(expected));
        assertPlanFeatureAllPropertiesEquals(expected, actual);
    }
}
