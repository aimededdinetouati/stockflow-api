package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.ProductAsserts.*;
import static com.adeem.stockflow.domain.ProductTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductMapperTest {

    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapperImpl();
    }

    //@Test
    void shouldConvertToDtoAndBack() {
        var expected = getProductSample1();
        var actual = productMapper.toEntity(productMapper.toDto(expected));
        assertProductAllPropertiesEquals(expected, actual);
    }
}
