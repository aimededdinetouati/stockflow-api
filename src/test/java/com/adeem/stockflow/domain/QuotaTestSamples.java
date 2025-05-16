package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QuotaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Quota getQuotaSample1() {
        return new Quota()
            .id(1L)
            .resourceType("resourceType1")
            .usedAmount(1)
            .maxAmount(1)
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Quota getQuotaSample2() {
        return new Quota()
            .id(2L)
            .resourceType("resourceType2")
            .usedAmount(2)
            .maxAmount(2)
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Quota getQuotaRandomSampleGenerator() {
        return new Quota()
            .id(longCount.incrementAndGet())
            .resourceType(UUID.randomUUID().toString())
            .usedAmount(intCount.incrementAndGet())
            .maxAmount(intCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
