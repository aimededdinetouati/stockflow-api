package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SubscriptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Subscription getSubscriptionSample1() {
        return new Subscription().id(1L).paymentMethod("paymentMethod1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static Subscription getSubscriptionSample2() {
        return new Subscription().id(2L).paymentMethod("paymentMethod2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static Subscription getSubscriptionRandomSampleGenerator() {
        return new Subscription()
            .id(longCount.incrementAndGet())
            .paymentMethod(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
