package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.AdminAsserts.*;
import static com.adeem.stockflow.domain.AdminTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminMapperTest {

    private AdminMapper adminMapper;

    @BeforeEach
    void setUp() {
        adminMapper = new AdminMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAdminSample1();
        var actual = adminMapper.toEntity(adminMapper.toDto(expected));
        assertAdminAllPropertiesEquals(expected, actual);
    }
}
