package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.RolePermissionAsserts.*;
import static com.adeem.stockflow.domain.RolePermissionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RolePermissionMapperTest {

    private RolePermissionMapper rolePermissionMapper;

    @BeforeEach
    void setUp() {
        rolePermissionMapper = new RolePermissionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRolePermissionSample1();
        var actual = rolePermissionMapper.toEntity(rolePermissionMapper.toDto(expected));
        assertRolePermissionAllPropertiesEquals(expected, actual);
    }
}
