package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.AttachmentTestSamples.*;
import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.CustomerTestSamples.*;
import static com.adeem.stockflow.domain.PaymentTestSamples.*;
import static com.adeem.stockflow.domain.SaleOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Payment.class);
        Payment payment1 = getPaymentSample1();
        Payment payment2 = new Payment();
        assertThat(payment1).isNotEqualTo(payment2);

        payment2.setId(payment1.getId());
        assertThat(payment1).isEqualTo(payment2);

        payment2 = getPaymentSample2();
        assertThat(payment1).isNotEqualTo(payment2);
    }

    @Test
    void attachmentsTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        Attachment attachmentBack = getAttachmentRandomSampleGenerator();

        payment.addAttachments(attachmentBack);
        assertThat(payment.getAttachments()).containsOnly(attachmentBack);
        assertThat(attachmentBack.getPayment()).isEqualTo(payment);

        payment.removeAttachments(attachmentBack);
        assertThat(payment.getAttachments()).doesNotContain(attachmentBack);
        assertThat(attachmentBack.getPayment()).isNull();

        payment.attachments(new HashSet<>(Set.of(attachmentBack)));
        assertThat(payment.getAttachments()).containsOnly(attachmentBack);
        assertThat(attachmentBack.getPayment()).isEqualTo(payment);

        payment.setAttachments(new HashSet<>());
        assertThat(payment.getAttachments()).doesNotContain(attachmentBack);
        assertThat(attachmentBack.getPayment()).isNull();
    }

    @Test
    void clientAccountTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        payment.setClientAccount(clientAccountBack);
        assertThat(payment.getClientAccount()).isEqualTo(clientAccountBack);

        payment.clientAccount(null);
        assertThat(payment.getClientAccount()).isNull();
    }

    @Test
    void customerTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        payment.setCustomer(customerBack);
        assertThat(payment.getCustomer()).isEqualTo(customerBack);

        payment.customer(null);
        assertThat(payment.getCustomer()).isNull();
    }

    @Test
    void saleOrderTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        SaleOrder saleOrderBack = getSaleOrderRandomSampleGenerator();

        payment.setSaleOrder(saleOrderBack);
        assertThat(payment.getSaleOrder()).isEqualTo(saleOrderBack);
        assertThat(saleOrderBack.getPayment()).isEqualTo(payment);

        payment.saleOrder(null);
        assertThat(payment.getSaleOrder()).isNull();
        assertThat(saleOrderBack.getPayment()).isNull();
    }
}
