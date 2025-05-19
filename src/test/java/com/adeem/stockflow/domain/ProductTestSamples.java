package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProductTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Product getProductSample1() {
        return new Product()
            .id(1L)
            .name("name1")
            .description("description1")
            .code("code1")
            .manufacturerCode("manufacturerCode1")
            .upc("upc1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Product getProductSample2() {
        return new Product()
            .id(2L)
            .name("name2")
            .description("description2")
            .code("code2")
            .manufacturerCode("manufacturerCode2")
            .upc("upc2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Product getProductRandomSampleGenerator() {
        return new Product()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .manufacturerCode(UUID.randomUUID().toString())
            .upc(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
