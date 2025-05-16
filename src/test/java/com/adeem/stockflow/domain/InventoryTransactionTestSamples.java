package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InventoryTransactionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static InventoryTransaction getInventoryTransactionSample1() {
        return new InventoryTransaction()
            .id(1L)
            .referenceNumber("referenceNumber1")
            .notes("notes1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static InventoryTransaction getInventoryTransactionSample2() {
        return new InventoryTransaction()
            .id(2L)
            .referenceNumber("referenceNumber2")
            .notes("notes2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static InventoryTransaction getInventoryTransactionRandomSampleGenerator() {
        return new InventoryTransaction()
            .id(longCount.incrementAndGet())
            .referenceNumber(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
