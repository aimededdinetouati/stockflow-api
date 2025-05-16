package com.adeem.stockflow.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlanFeatureDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlanFeatureDTO.class);
        PlanFeatureDTO planFeatureDTO1 = new PlanFeatureDTO();
        planFeatureDTO1.setId(1L);
        PlanFeatureDTO planFeatureDTO2 = new PlanFeatureDTO();
        assertThat(planFeatureDTO1).isNotEqualTo(planFeatureDTO2);
        planFeatureDTO2.setId(planFeatureDTO1.getId());
        assertThat(planFeatureDTO1).isEqualTo(planFeatureDTO2);
        planFeatureDTO2.setId(2L);
        assertThat(planFeatureDTO1).isNotEqualTo(planFeatureDTO2);
        planFeatureDTO1.setId(null);
        assertThat(planFeatureDTO1).isNotEqualTo(planFeatureDTO2);
    }
}
