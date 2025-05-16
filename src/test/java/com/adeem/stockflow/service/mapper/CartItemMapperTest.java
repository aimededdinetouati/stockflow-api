package com.adeem.stockflow.service.mapper;

import static com.adeem.stockflow.domain.CartItemAsserts.*;
import static com.adeem.stockflow.domain.CartItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartItemMapperTest {

    private CartItemMapper cartItemMapper;

    @BeforeEach
    void setUp() {
        cartItemMapper = new CartItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCartItemSample1();
        var actual = cartItemMapper.toEntity(cartItemMapper.toDto(expected));
        assertCartItemAllPropertiesEquals(expected, actual);
    }
}
