package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentConfigurationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PaymentConfiguration getPaymentConfigurationSample1() {
        return new PaymentConfiguration()
            .id(1L)
            .ccp("ccp1")
            .rip("rip1")
            .rib("rib1")
            .iban("iban1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static PaymentConfiguration getPaymentConfigurationSample2() {
        return new PaymentConfiguration()
            .id(2L)
            .ccp("ccp2")
            .rip("rip2")
            .rib("rib2")
            .iban("iban2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static PaymentConfiguration getPaymentConfigurationRandomSampleGenerator() {
        return new PaymentConfiguration()
            .id(longCount.incrementAndGet())
            .ccp(UUID.randomUUID().toString())
            .rip(UUID.randomUUID().toString())
            .rib(UUID.randomUUID().toString())
            .iban(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
