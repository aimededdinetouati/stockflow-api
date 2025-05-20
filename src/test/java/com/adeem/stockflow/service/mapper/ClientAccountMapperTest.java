package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.ClientAccountAsserts.*;
import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientAccountMapperTest {

    private ClientAccountMapper clientAccountMapper;

    @BeforeEach
    void setUp() {
        clientAccountMapper = new ClientAccountMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClientAccountSample1();
        var actual = clientAccountMapper.toEntity(clientAccountMapper.toDto(expected));
        assertClientAccountAllPropertiesEquals(expected, actual);
    }

    @Test
    void shouldCreateClientAccountFromId() {
        Long id = 1L;
        var clientAccount = clientAccountMapper.fromId(id);

        assert clientAccount != null;
        assert clientAccount.getId().equals(id);
    }
}
