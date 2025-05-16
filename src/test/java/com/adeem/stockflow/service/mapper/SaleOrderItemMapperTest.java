package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.SaleOrderItemAsserts.*;
import static com.adeem.stockflow.domain.SaleOrderItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaleOrderItemMapperTest {

    private SaleOrderItemMapper saleOrderItemMapper;

    @BeforeEach
    void setUp() {
        saleOrderItemMapper = new SaleOrderItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSaleOrderItemSample1();
        var actual = saleOrderItemMapper.toEntity(saleOrderItemMapper.toDto(expected));
        assertSaleOrderItemAllPropertiesEquals(expected, actual);
    }
}
