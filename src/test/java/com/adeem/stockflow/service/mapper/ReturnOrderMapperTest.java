package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.ReturnOrderAsserts.*;
import static com.adeem.stockflow.domain.ReturnOrderTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReturnOrderMapperTest {

    private ReturnOrderMapper returnOrderMapper;

    @BeforeEach
    void setUp() {
        returnOrderMapper = new ReturnOrderMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReturnOrderSample1();
        var actual = returnOrderMapper.toEntity(returnOrderMapper.toDto(expected));
        assertReturnOrderAllPropertiesEquals(expected, actual);
    }
}
