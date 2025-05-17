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
            .users(1)
            .products(1)
            .productFamilies(1)
            .showcasedProducts(1)
            .saleOrders(1)
            .purchaseOrders(1)
            .customers(1)
            .suppliers(1)
            .shipments(1)
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Quota getQuotaSample2() {
        return new Quota()
            .id(2L)
            .users(2)
            .products(2)
            .productFamilies(2)
            .showcasedProducts(2)
            .saleOrders(2)
            .purchaseOrders(2)
            .customers(2)
            .suppliers(2)
            .shipments(2)
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Quota getQuotaRandomSampleGenerator() {
        return new Quota()
            .id(longCount.incrementAndGet())
            .users(intCount.incrementAndGet())
            .products(intCount.incrementAndGet())
            .productFamilies(intCount.incrementAndGet())
            .showcasedProducts(intCount.incrementAndGet())
            .saleOrders(intCount.incrementAndGet())
            .purchaseOrders(intCount.incrementAndGet())
            .customers(intCount.incrementAndGet())
            .suppliers(intCount.incrementAndGet())
            .shipments(intCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
