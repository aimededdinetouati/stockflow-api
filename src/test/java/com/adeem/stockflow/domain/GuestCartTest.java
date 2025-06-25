package com.adeem.stockflow.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for GuestCart and GuestCartItem entities.
 */
class GuestCartTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GuestCart.class);
        GuestCart guestCart1 = getGuestCartSample1();
        GuestCart guestCart2 = new GuestCart();
        assertThat(guestCart1).isNotEqualTo(guestCart2);

        guestCart2.setSessionId(guestCart1.getSessionId());
        assertThat(guestCart1).isEqualTo(guestCart2);

        guestCart2 = getGuestCartSample2();
        assertThat(guestCart1).isNotEqualTo(guestCart2);
    }

    @Test
    void guestCartItemTest() throws Exception {
        GuestCart guestCart = getGuestCartRandomSampleGenerator();
        GuestCartItem guestCartItemBack = getGuestCartItemRandomSampleGenerator();

        guestCart.addItem(guestCartItemBack);
        assertThat(guestCart.getItems()).containsOnly(guestCartItemBack);
        assertThat(guestCartItemBack.getGuestCart()).isEqualTo(guestCart);

        guestCart.removeItem(guestCartItemBack);
        assertThat(guestCart.getItems()).doesNotContain(guestCartItemBack);
        assertThat(guestCartItemBack.getGuestCart()).isNull();

        guestCart.items(Set.of(guestCartItemBack));
        assertThat(guestCart.getItems()).containsOnly(guestCartItemBack);
        assertThat(guestCartItemBack.getGuestCart()).isEqualTo(guestCart);

        guestCart.setItems(new HashSet<>());
        assertThat(guestCart.getItems()).doesNotContain(guestCartItemBack);
        assertThat(guestCartItemBack.getGuestCart()).isNull();
    }

    public static GuestCart getGuestCartSample1() {
        return new GuestCart()
            .sessionId("sessionId1")
            .createdDate(Instant.parse("2024-01-01T00:00:00Z"))
            .expiresAt(Instant.parse("2024-01-02T00:00:00Z"));
    }

    public static GuestCart getGuestCartSample2() {
        return new GuestCart()
            .sessionId("sessionId2")
            .createdDate(Instant.parse("2024-01-01T12:00:00Z"))
            .expiresAt(Instant.parse("2024-01-02T12:00:00Z"));
    }

    public static GuestCart getGuestCartRandomSampleGenerator() {
        return new GuestCart()
            .sessionId(UUID.randomUUID().toString())
            .createdDate(Instant.now())
            .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
    }

    public static GuestCartItem getGuestCartItemRandomSampleGenerator() {
        return new GuestCartItem()
            .sessionId(UUID.randomUUID().toString())
            .quantity(BigDecimal.valueOf(1.0))
            .priceAtTime(BigDecimal.valueOf(10.0))
            .addedDate(Instant.now());
    }
}

/**
 * Unit tests for GuestCartItem entity.
 */
class GuestCartItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GuestCartItem.class);
        GuestCartItem guestCartItem1 = getGuestCartItemSample1();
        GuestCartItem guestCartItem2 = new GuestCartItem();
        assertThat(guestCartItem1).isNotEqualTo(guestCartItem2);

        guestCartItem2.setId(guestCartItem1.getId());
        assertThat(guestCartItem1).isEqualTo(guestCartItem2);

        guestCartItem2 = getGuestCartItemSample2();
        assertThat(guestCartItem1).isNotEqualTo(guestCartItem2);
    }

    public static GuestCartItem getGuestCartItemSample1() {
        return new GuestCartItem()
            .id(1L)
            .sessionId("sessionId1")
            .quantity(BigDecimal.valueOf(2.0))
            .priceAtTime(BigDecimal.valueOf(15.50))
            .addedDate(Instant.parse("2024-01-01T10:00:00Z"));
    }

    public static GuestCartItem getGuestCartItemSample2() {
        return new GuestCartItem()
            .id(2L)
            .sessionId("sessionId2")
            .quantity(BigDecimal.valueOf(1.0))
            .priceAtTime(BigDecimal.valueOf(25.99))
            .addedDate(Instant.parse("2024-01-01T11:00:00Z"));
    }
}
