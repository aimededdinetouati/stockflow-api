package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.PlanFormulaTestSamples.*;
import static com.adeem.stockflow.domain.QuotaTestSamples.*;
import static com.adeem.stockflow.domain.SubscriptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SubscriptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Subscription.class);
        Subscription subscription1 = getSubscriptionSample1();
        Subscription subscription2 = new Subscription();
        assertThat(subscription1).isNotEqualTo(subscription2);

        subscription2.setId(subscription1.getId());
        assertThat(subscription1).isEqualTo(subscription2);

        subscription2 = getSubscriptionSample2();
        assertThat(subscription1).isNotEqualTo(subscription2);
    }

    @Test
    void quotaTest() {
        Subscription subscription = getSubscriptionRandomSampleGenerator();
        Quota quotaBack = getQuotaRandomSampleGenerator();

        subscription.addQuota(quotaBack);
        assertThat(subscription.getQuotas()).containsOnly(quotaBack);
        assertThat(quotaBack.getSubscription()).isEqualTo(subscription);

        subscription.removeQuota(quotaBack);
        assertThat(subscription.getQuotas()).doesNotContain(quotaBack);
        assertThat(quotaBack.getSubscription()).isNull();

        subscription.quotas(new HashSet<>(Set.of(quotaBack)));
        assertThat(subscription.getQuotas()).containsOnly(quotaBack);
        assertThat(quotaBack.getSubscription()).isEqualTo(subscription);

        subscription.setQuotas(new HashSet<>());
        assertThat(subscription.getQuotas()).doesNotContain(quotaBack);
        assertThat(quotaBack.getSubscription()).isNull();
    }

    @Test
    void planFormulaTest() {
        Subscription subscription = getSubscriptionRandomSampleGenerator();
        PlanFormula planFormulaBack = getPlanFormulaRandomSampleGenerator();

        subscription.setPlanFormula(planFormulaBack);
        assertThat(subscription.getPlanFormula()).isEqualTo(planFormulaBack);

        subscription.planFormula(null);
        assertThat(subscription.getPlanFormula()).isNull();
    }

    @Test
    void clientAccountTest() {
        Subscription subscription = getSubscriptionRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        subscription.setClientAccount(clientAccountBack);
        assertThat(subscription.getClientAccount()).isEqualTo(clientAccountBack);

        subscription.clientAccount(null);
        assertThat(subscription.getClientAccount()).isNull();
    }
}
