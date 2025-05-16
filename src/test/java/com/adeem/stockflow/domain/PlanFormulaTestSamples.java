package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PlanFormulaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PlanFormula getPlanFormulaSample1() {
        return new PlanFormula().id(1L).name("name1").description("description1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static PlanFormula getPlanFormulaSample2() {
        return new PlanFormula().id(2L).name("name2").description("description2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static PlanFormula getPlanFormulaRandomSampleGenerator() {
        return new PlanFormula()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
