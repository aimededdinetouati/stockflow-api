package com.adeem.stockflow.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuotaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuotaDTO.class);
        QuotaDTO quotaDTO1 = new QuotaDTO();
        quotaDTO1.setId(1L);
        QuotaDTO quotaDTO2 = new QuotaDTO();
        assertThat(quotaDTO1).isNotEqualTo(quotaDTO2);
        quotaDTO2.setId(quotaDTO1.getId());
        assertThat(quotaDTO1).isEqualTo(quotaDTO2);
        quotaDTO2.setId(2L);
        assertThat(quotaDTO1).isNotEqualTo(quotaDTO2);
        quotaDTO1.setId(null);
        assertThat(quotaDTO1).isNotEqualTo(quotaDTO2);
    }
}
