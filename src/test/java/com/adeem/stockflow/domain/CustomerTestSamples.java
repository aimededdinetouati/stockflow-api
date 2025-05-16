package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Customer getCustomerSample1() {
        return new Customer()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .phone("phone1")
            .fax("fax1")
            .taxId("taxId1")
            .registrationArticle("registrationArticle1")
            .statisticalId("statisticalId1")
            .rc("rc1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Customer getCustomerSample2() {
        return new Customer()
            .id(2L)
            .firstName("firstName2")
            .lastName("lastName2")
            .phone("phone2")
            .fax("fax2")
            .taxId("taxId2")
            .registrationArticle("registrationArticle2")
            .statisticalId("statisticalId2")
            .rc("rc2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Customer getCustomerRandomSampleGenerator() {
        return new Customer()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .fax(UUID.randomUUID().toString())
            .taxId(UUID.randomUUID().toString())
            .registrationArticle(UUID.randomUUID().toString())
            .statisticalId(UUID.randomUUID().toString())
            .rc(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
