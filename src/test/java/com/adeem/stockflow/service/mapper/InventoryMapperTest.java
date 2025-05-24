package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.InventoryAsserts.*;
import static com.adeem.stockflow.domain.InventoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryMapperTest {

    private InventoryMapper inventoryMapper;

    @BeforeEach
    void setUp() {
        inventoryMapper = new InventoryMapperImpl();
    }

    //@Test
    void shouldConvertToDtoAndBack() {
        var expected = getInventorySample1();
        var actual = inventoryMapper.toEntity(inventoryMapper.toDto(expected));
        assertInventoryAllPropertiesEquals(expected, actual);
    }
}
