package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.UserRoleAsserts.*;
import static com.adeem.stockflow.domain.UserRoleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserRoleMapperTest {

    private UserRoleMapper userRoleMapper;

    @BeforeEach
    void setUp() {
        userRoleMapper = new UserRoleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserRoleSample1();
        var actual = userRoleMapper.toEntity(userRoleMapper.toDto(expected));
        assertUserRoleAllPropertiesEquals(expected, actual);
    }
}
