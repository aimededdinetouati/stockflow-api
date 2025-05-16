package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ShipmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Shipment getShipmentSample1() {
        return new Shipment()
            .id(1L)
            .reference("reference1")
            .trackingNumber("trackingNumber1")
            .carrier("carrier1")
            .notes("notes1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Shipment getShipmentSample2() {
        return new Shipment()
            .id(2L)
            .reference("reference2")
            .trackingNumber("trackingNumber2")
            .carrier("carrier2")
            .notes("notes2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Shipment getShipmentRandomSampleGenerator() {
        return new Shipment()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .trackingNumber(UUID.randomUUID().toString())
            .carrier(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
