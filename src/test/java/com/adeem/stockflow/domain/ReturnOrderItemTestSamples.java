package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReturnOrderItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ReturnOrderItem getReturnOrderItemSample1() {
        return new ReturnOrderItem().id(1L).notes("notes1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static ReturnOrderItem getReturnOrderItemSample2() {
        return new ReturnOrderItem().id(2L).notes("notes2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static ReturnOrderItem getReturnOrderItemRandomSampleGenerator() {
        return new ReturnOrderItem()
            .id(longCount.incrementAndGet())
            .notes(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
