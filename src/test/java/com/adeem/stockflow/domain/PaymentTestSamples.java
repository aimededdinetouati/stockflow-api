package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Payment getPaymentSample1() {
        return new Payment()
            .id(1L)
            .reference("reference1")
            .chargilyCheckoutUrl("chargilyCheckoutUrl1")
            .chargilyTransactionId("chargilyTransactionId1")
            .bankName("bankName1")
            .accountNumber("accountNumber1")
            .transferReferenceNumber("transferReferenceNumber1")
            .reconciledBy("reconciledBy1")
            .notes("notes1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Payment getPaymentSample2() {
        return new Payment()
            .id(2L)
            .reference("reference2")
            .chargilyCheckoutUrl("chargilyCheckoutUrl2")
            .chargilyTransactionId("chargilyTransactionId2")
            .bankName("bankName2")
            .accountNumber("accountNumber2")
            .transferReferenceNumber("transferReferenceNumber2")
            .reconciledBy("reconciledBy2")
            .notes("notes2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Payment getPaymentRandomSampleGenerator() {
        return new Payment()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .chargilyCheckoutUrl(UUID.randomUUID().toString())
            .chargilyTransactionId(UUID.randomUUID().toString())
            .bankName(UUID.randomUUID().toString())
            .accountNumber(UUID.randomUUID().toString())
            .transferReferenceNumber(UUID.randomUUID().toString())
            .reconciledBy(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
