package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AddressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Address getAddressSample1() {
        return new Address()
            .id(1L)
            .streetAddress("streetAddress1")
            .city("city1")
            .state("state1")
            .postalCode("postalCode1")
            .country("country1")
            .phoneNumber("phoneNumber1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Address getAddressSample2() {
        return new Address()
            .id(2L)
            .streetAddress("streetAddress2")
            .city("city2")
            .state("state2")
            .postalCode("postalCode2")
            .country("country2")
            .phoneNumber("phoneNumber2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Address getAddressRandomSampleGenerator() {
        return new Address()
            .id(longCount.incrementAndGet())
            .streetAddress(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .state(UUID.randomUUID().toString())
            .postalCode(UUID.randomUUID().toString())
            .country(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
