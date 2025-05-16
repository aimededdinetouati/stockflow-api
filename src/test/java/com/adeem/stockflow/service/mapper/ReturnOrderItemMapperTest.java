package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.ReturnOrderItemAsserts.*;
import static com.adeem.stockflow.domain.ReturnOrderItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReturnOrderItemMapperTest {

    private ReturnOrderItemMapper returnOrderItemMapper;

    @BeforeEach
    void setUp() {
        returnOrderItemMapper = new ReturnOrderItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReturnOrderItemSample1();
        var actual = returnOrderItemMapper.toEntity(returnOrderItemMapper.toDto(expected));
        assertReturnOrderItemAllPropertiesEquals(expected, actual);
    }
}
