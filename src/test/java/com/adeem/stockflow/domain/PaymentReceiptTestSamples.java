package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentReceiptTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PaymentReceipt getPaymentReceiptSample1() {
        return new PaymentReceipt()
            .id(1L)
            .receiptNumber("receiptNumber1")
            .reviewNotes("reviewNotes1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static PaymentReceipt getPaymentReceiptSample2() {
        return new PaymentReceipt()
            .id(2L)
            .receiptNumber("receiptNumber2")
            .reviewNotes("reviewNotes2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static PaymentReceipt getPaymentReceiptRandomSampleGenerator() {
        return new PaymentReceipt()
            .id(longCount.incrementAndGet())
            .receiptNumber(UUID.randomUUID().toString())
            .reviewNotes(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
