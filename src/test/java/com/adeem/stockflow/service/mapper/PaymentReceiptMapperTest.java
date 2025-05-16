package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.PaymentReceiptAsserts.*;
import static com.adeem.stockflow.domain.PaymentReceiptTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentReceiptMapperTest {

    private PaymentReceiptMapper paymentReceiptMapper;

    @BeforeEach
    void setUp() {
        paymentReceiptMapper = new PaymentReceiptMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPaymentReceiptSample1();
        var actual = paymentReceiptMapper.toEntity(paymentReceiptMapper.toDto(expected));
        assertPaymentReceiptAllPropertiesEquals(expected, actual);
    }
}
