package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReturnOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ReturnOrder getReturnOrderSample1() {
        return new ReturnOrder()
            .id(1L)
            .reference("reference1")
            .notes("notes1")
            .originalOrderReference("originalOrderReference1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static ReturnOrder getReturnOrderSample2() {
        return new ReturnOrder()
            .id(2L)
            .reference("reference2")
            .notes("notes2")
            .originalOrderReference("originalOrderReference2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static ReturnOrder getReturnOrderRandomSampleGenerator() {
        return new ReturnOrder()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString())
            .originalOrderReference(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
