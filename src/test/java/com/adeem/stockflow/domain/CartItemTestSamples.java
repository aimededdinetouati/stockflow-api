package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CartItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CartItem getCartItemSample1() {
        return new CartItem().id(1L).createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static CartItem getCartItemSample2() {
        return new CartItem().id(2L).createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static CartItem getCartItemRandomSampleGenerator() {
        return new CartItem()
            .id(longCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
