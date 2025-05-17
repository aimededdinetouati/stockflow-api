package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.AddressTestSamples.*;
import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.QuotaTestSamples.*;
import static com.adeem.stockflow.domain.SubscriptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClientAccountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientAccount.class);
        ClientAccount clientAccount1 = getClientAccountSample1();
        ClientAccount clientAccount2 = new ClientAccount();
        assertThat(clientAccount1).isNotEqualTo(clientAccount2);

        clientAccount2.setId(clientAccount1.getId());
        assertThat(clientAccount1).isEqualTo(clientAccount2);

        clientAccount2 = getClientAccountSample2();
        assertThat(clientAccount1).isNotEqualTo(clientAccount2);
    }

    @Test
    void addressTest() {
        ClientAccount clientAccount = getClientAccountRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        clientAccount.setAddress(addressBack);
        assertThat(clientAccount.getAddress()).isEqualTo(addressBack);

        clientAccount.address(null);
        assertThat(clientAccount.getAddress()).isNull();
    }

    @Test
    void quotaTest() {
        ClientAccount clientAccount = getClientAccountRandomSampleGenerator();
        Quota quotaBack = getQuotaRandomSampleGenerator();

        clientAccount.setQuota(quotaBack);
        assertThat(clientAccount.getQuota()).isEqualTo(quotaBack);

        clientAccount.quota(null);
        assertThat(clientAccount.getQuota()).isNull();
    }

    @Test
    void subscriptionsTest() {
        ClientAccount clientAccount = getClientAccountRandomSampleGenerator();
        Subscription subscriptionBack = getSubscriptionRandomSampleGenerator();

        clientAccount.addSubscriptions(subscriptionBack);
        assertThat(clientAccount.getSubscriptions()).containsOnly(subscriptionBack);
        assertThat(subscriptionBack.getClientAccount()).isEqualTo(clientAccount);

        clientAccount.removeSubscriptions(subscriptionBack);
        assertThat(clientAccount.getSubscriptions()).doesNotContain(subscriptionBack);
        assertThat(subscriptionBack.getClientAccount()).isNull();

        clientAccount.subscriptions(new HashSet<>(Set.of(subscriptionBack)));
        assertThat(clientAccount.getSubscriptions()).containsOnly(subscriptionBack);
        assertThat(subscriptionBack.getClientAccount()).isEqualTo(clientAccount);

        clientAccount.setSubscriptions(new HashSet<>());
        assertThat(clientAccount.getSubscriptions()).doesNotContain(subscriptionBack);
        assertThat(subscriptionBack.getClientAccount()).isNull();
    }
}
