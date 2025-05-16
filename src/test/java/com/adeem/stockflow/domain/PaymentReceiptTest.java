package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.PaymentReceiptTestSamples.*;
import static com.adeem.stockflow.domain.PaymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentReceiptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentReceipt.class);
        PaymentReceipt paymentReceipt1 = getPaymentReceiptSample1();
        PaymentReceipt paymentReceipt2 = new PaymentReceipt();
        assertThat(paymentReceipt1).isNotEqualTo(paymentReceipt2);

        paymentReceipt2.setId(paymentReceipt1.getId());
        assertThat(paymentReceipt1).isEqualTo(paymentReceipt2);

        paymentReceipt2 = getPaymentReceiptSample2();
        assertThat(paymentReceipt1).isNotEqualTo(paymentReceipt2);
    }

    @Test
    void paymentTest() {
        PaymentReceipt paymentReceipt = getPaymentReceiptRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        paymentReceipt.setPayment(paymentBack);
        assertThat(paymentReceipt.getPayment()).isEqualTo(paymentBack);

        paymentReceipt.payment(null);
        assertThat(paymentReceipt.getPayment()).isNull();
    }
}
