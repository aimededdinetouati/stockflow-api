package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.QuotaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuotaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Quota.class);
        Quota quota1 = getQuotaSample1();
        Quota quota2 = new Quota();
        assertThat(quota1).isNotEqualTo(quota2);

        quota2.setId(quota1.getId());
        assertThat(quota1).isEqualTo(quota2);

        quota2 = getQuotaSample2();
        assertThat(quota1).isNotEqualTo(quota2);
    }

    @Test
    void clientAccountTest() {
        Quota quota = getQuotaRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        quota.setClientAccount(clientAccountBack);
        assertThat(quota.getClientAccount()).isEqualTo(clientAccountBack);

        quota.clientAccount(null);
        assertThat(quota.getClientAccount()).isNull();
    }
}
