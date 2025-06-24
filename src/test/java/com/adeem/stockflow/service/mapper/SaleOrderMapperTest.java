package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.SaleOrderAsserts.*;
import static com.adeem.stockflow.domain.SaleOrderTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaleOrderMapperTest {

    private SaleOrderMapper saleOrderMapper;

    @BeforeEach
    void setUp() {
        saleOrderMapper = new SaleOrderMapperImpl();
    }

    //@Test
    void shouldConvertToDtoAndBack() {
        var expected = getSaleOrderSample1();
        var actual = saleOrderMapper.toEntity(saleOrderMapper.toDto(expected));
        assertSaleOrderAllPropertiesEquals(expected, actual);
    }
}
