package com.adeem.stockflow.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentConfigurationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentConfigurationDTO.class);
        PaymentConfigurationDTO paymentConfigurationDTO1 = new PaymentConfigurationDTO();
        paymentConfigurationDTO1.setId(1L);
        PaymentConfigurationDTO paymentConfigurationDTO2 = new PaymentConfigurationDTO();
        assertThat(paymentConfigurationDTO1).isNotEqualTo(paymentConfigurationDTO2);
        paymentConfigurationDTO2.setId(paymentConfigurationDTO1.getId());
        assertThat(paymentConfigurationDTO1).isEqualTo(paymentConfigurationDTO2);
        paymentConfigurationDTO2.setId(2L);
        assertThat(paymentConfigurationDTO1).isNotEqualTo(paymentConfigurationDTO2);
        paymentConfigurationDTO1.setId(null);
        assertThat(paymentConfigurationDTO1).isNotEqualTo(paymentConfigurationDTO2);
    }
}
