package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.PaymentConfigurationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentConfigurationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentConfiguration.class);
        PaymentConfiguration paymentConfiguration1 = getPaymentConfigurationSample1();
        PaymentConfiguration paymentConfiguration2 = new PaymentConfiguration();
        assertThat(paymentConfiguration1).isNotEqualTo(paymentConfiguration2);

        paymentConfiguration2.setId(paymentConfiguration1.getId());
        assertThat(paymentConfiguration1).isEqualTo(paymentConfiguration2);

        paymentConfiguration2 = getPaymentConfigurationSample2();
        assertThat(paymentConfiguration1).isNotEqualTo(paymentConfiguration2);
    }

    @Test
    void clientAccountTest() {
        PaymentConfiguration paymentConfiguration = getPaymentConfigurationRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        paymentConfiguration.setClientAccount(clientAccountBack);
        assertThat(paymentConfiguration.getClientAccount()).isEqualTo(clientAccountBack);

        paymentConfiguration.clientAccount(null);
        assertThat(paymentConfiguration.getClientAccount()).isNull();
    }
}
