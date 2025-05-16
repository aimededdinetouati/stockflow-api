package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SaleOrderItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SaleOrderItem getSaleOrderItemSample1() {
        return new SaleOrderItem().id(1L).createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static SaleOrderItem getSaleOrderItemSample2() {
        return new SaleOrderItem().id(2L).createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static SaleOrderItem getSaleOrderItemRandomSampleGenerator() {
        return new SaleOrderItem()
            .id(longCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
