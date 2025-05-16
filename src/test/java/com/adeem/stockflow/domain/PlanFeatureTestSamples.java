package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PlanFeatureTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PlanFeature getPlanFeatureSample1() {
        return new PlanFeature()
            .id(1L)
            .featureName("featureName1")
            .description("description1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static PlanFeature getPlanFeatureSample2() {
        return new PlanFeature()
            .id(2L)
            .featureName("featureName2")
            .description("description2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static PlanFeature getPlanFeatureRandomSampleGenerator() {
        return new PlanFeature()
            .id(longCount.incrementAndGet())
            .featureName(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
