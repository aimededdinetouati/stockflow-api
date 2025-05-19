package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClientAccountTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ClientAccount getClientAccountSample1() {
        return new ClientAccount()
            .id(1L)
            .companyName("companyName1")
            .phone("phone1")
            .email("email1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static ClientAccount getClientAccountSample2() {
        return new ClientAccount()
            .id(2L)
            .companyName("companyName2")
            .phone("phone2")
            .email("email2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static ClientAccount getClientAccountRandomSampleGenerator() {
        return new ClientAccount()
            .id(longCount.incrementAndGet())
            .companyName(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
