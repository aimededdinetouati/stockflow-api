package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PermissionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Permission getPermissionSample1() {
        return new Permission()
            .id(1L)
            .name("name1")
            .description("description1")
            .resourceType("resourceType1")
            .action("action1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Permission getPermissionSample2() {
        return new Permission()
            .id(2L)
            .name("name2")
            .description("description2")
            .resourceType("resourceType2")
            .action("action2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Permission getPermissionRandomSampleGenerator() {
        return new Permission()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .resourceType(UUID.randomUUID().toString())
            .action(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
