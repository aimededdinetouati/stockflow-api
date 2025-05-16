package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.PurchaseOrderAsserts.*;
import static com.adeem.stockflow.domain.PurchaseOrderTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseOrderMapperTest {

    private PurchaseOrderMapper purchaseOrderMapper;

    @BeforeEach
    void setUp() {
        purchaseOrderMapper = new PurchaseOrderMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPurchaseOrderSample1();
        var actual = purchaseOrderMapper.toEntity(purchaseOrderMapper.toDto(expected));
        assertPurchaseOrderAllPropertiesEquals(expected, actual);
    }
}
