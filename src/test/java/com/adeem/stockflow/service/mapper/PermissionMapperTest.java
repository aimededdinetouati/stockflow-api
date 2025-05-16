package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.PermissionAsserts.*;
import static com.adeem.stockflow.domain.PermissionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermissionMapperTest {

    private PermissionMapper permissionMapper;

    @BeforeEach
    void setUp() {
        permissionMapper = new PermissionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPermissionSample1();
        var actual = permissionMapper.toEntity(permissionMapper.toDto(expected));
        assertPermissionAllPropertiesEquals(expected, actual);
    }
}
