package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PurchaseOrderItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PurchaseOrderItem getPurchaseOrderItemSample1() {
        return new PurchaseOrderItem().id(1L).createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static PurchaseOrderItem getPurchaseOrderItemSample2() {
        return new PurchaseOrderItem().id(2L).createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static PurchaseOrderItem getPurchaseOrderItemRandomSampleGenerator() {
        return new PurchaseOrderItem()
            .id(longCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
