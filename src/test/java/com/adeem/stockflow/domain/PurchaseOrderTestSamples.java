package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PurchaseOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PurchaseOrder getPurchaseOrderSample1() {
        return new PurchaseOrder().id(1L).reference("reference1").notes("notes1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static PurchaseOrder getPurchaseOrderSample2() {
        return new PurchaseOrder().id(2L).reference("reference2").notes("notes2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static PurchaseOrder getPurchaseOrderRandomSampleGenerator() {
        return new PurchaseOrder()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
