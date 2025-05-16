package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.PaymentConfigurationAsserts.*;
import static com.adeem.stockflow.domain.PaymentConfigurationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentConfigurationMapperTest {

    private PaymentConfigurationMapper paymentConfigurationMapper;

    @BeforeEach
    void setUp() {
        paymentConfigurationMapper = new PaymentConfigurationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPaymentConfigurationSample1();
        var actual = paymentConfigurationMapper.toEntity(paymentConfigurationMapper.toDto(expected));
        assertPaymentConfigurationAllPropertiesEquals(expected, actual);
    }
}
