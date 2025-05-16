package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.AttachmentTestSamples.*;
import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.PaymentTestSamples.*;
import static com.adeem.stockflow.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Attachment.class);
        Attachment attachment1 = getAttachmentSample1();
        Attachment attachment2 = new Attachment();
        assertThat(attachment1).isNotEqualTo(attachment2);

        attachment2.setId(attachment1.getId());
        assertThat(attachment1).isEqualTo(attachment2);

        attachment2 = getAttachmentSample2();
        assertThat(attachment1).isNotEqualTo(attachment2);
    }

    @Test
    void clientAccountTest() {
        Attachment attachment = getAttachmentRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        attachment.setClientAccount(clientAccountBack);
        assertThat(attachment.getClientAccount()).isEqualTo(clientAccountBack);

        attachment.clientAccount(null);
        assertThat(attachment.getClientAccount()).isNull();
    }

    @Test
    void paymentTest() {
        Attachment attachment = getAttachmentRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        attachment.setPayment(paymentBack);
        assertThat(attachment.getPayment()).isEqualTo(paymentBack);

        attachment.payment(null);
        assertThat(attachment.getPayment()).isNull();
    }

    @Test
    void productTest() {
        Attachment attachment = getAttachmentRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        attachment.setProduct(productBack);
        assertThat(attachment.getProduct()).isEqualTo(productBack);

        attachment.product(null);
        assertThat(attachment.getProduct()).isNull();
    }
}
