package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.PurchaseOrderItemAsserts.*;
import static com.adeem.stockflow.domain.PurchaseOrderItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseOrderItemMapperTest {

    private PurchaseOrderItemMapper purchaseOrderItemMapper;

    @BeforeEach
    void setUp() {
        purchaseOrderItemMapper = new PurchaseOrderItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPurchaseOrderItemSample1();
        var actual = purchaseOrderItemMapper.toEntity(purchaseOrderItemMapper.toDto(expected));
        assertPurchaseOrderItemAllPropertiesEquals(expected, actual);
    }
}
