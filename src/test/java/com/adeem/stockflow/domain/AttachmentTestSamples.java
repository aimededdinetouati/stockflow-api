package com.adeem.stockflow.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AttachmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Attachment getAttachmentSample1() {
        return new Attachment()
            .id(1L)
            .url("url1")
            .fileSize(1L)
            .altText("altText1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Attachment getAttachmentSample2() {
        return new Attachment()
            .id(2L)
            .url("url2")
            .fileSize(2L)
            .altText("altText2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Attachment getAttachmentRandomSampleGenerator() {
        return new Attachment()
            .id(longCount.incrementAndGet())
            .url(UUID.randomUUID().toString())
            .fileSize(longCount.incrementAndGet())
            .altText(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
