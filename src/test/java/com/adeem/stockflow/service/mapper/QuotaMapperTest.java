package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.QuotaAsserts.*;
import static com.adeem.stockflow.domain.QuotaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuotaMapperTest {

    private QuotaMapper quotaMapper;

    @BeforeEach
    void setUp() {
        quotaMapper = new QuotaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getQuotaSample1();
        var actual = quotaMapper.toEntity(quotaMapper.toDto(expected));
        assertQuotaAllPropertiesEquals(expected, actual);
    }
}
