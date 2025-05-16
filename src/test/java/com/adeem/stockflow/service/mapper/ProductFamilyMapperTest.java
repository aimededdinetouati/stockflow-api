package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.ProductFamilyAsserts.*;
import static com.adeem.stockflow.domain.ProductFamilyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductFamilyMapperTest {

    private ProductFamilyMapper productFamilyMapper;

    @BeforeEach
    void setUp() {
        productFamilyMapper = new ProductFamilyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProductFamilySample1();
        var actual = productFamilyMapper.toEntity(productFamilyMapper.toDto(expected));
        assertProductFamilyAllPropertiesEquals(expected, actual);
    }
}
