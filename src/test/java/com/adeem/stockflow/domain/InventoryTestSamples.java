package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InventoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Inventory getInventorySample1() {
        return new Inventory().id(1L).location("location1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static Inventory getInventorySample2() {
        return new Inventory().id(2L).location("location2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static Inventory getInventoryRandomSampleGenerator() {
        return new Inventory()
            .id(longCount.incrementAndGet())
            .location(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
