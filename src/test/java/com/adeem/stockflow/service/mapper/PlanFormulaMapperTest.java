package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.PlanFormulaAsserts.*;
import static com.adeem.stockflow.domain.PlanFormulaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlanFormulaMapperTest {

    private PlanFormulaMapper planFormulaMapper;

    @BeforeEach
    void setUp() {
        planFormulaMapper = new PlanFormulaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPlanFormulaSample1();
        var actual = planFormulaMapper.toEntity(planFormulaMapper.toDto(expected));
        assertPlanFormulaAllPropertiesEquals(expected, actual);
    }
}
