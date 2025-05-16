package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.PlanFeatureTestSamples.*;
import static com.adeem.stockflow.domain.PlanFormulaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlanFeatureTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlanFeature.class);
        PlanFeature planFeature1 = getPlanFeatureSample1();
        PlanFeature planFeature2 = new PlanFeature();
        assertThat(planFeature1).isNotEqualTo(planFeature2);

        planFeature2.setId(planFeature1.getId());
        assertThat(planFeature1).isEqualTo(planFeature2);

        planFeature2 = getPlanFeatureSample2();
        assertThat(planFeature1).isNotEqualTo(planFeature2);
    }

    @Test
    void planFormulaTest() {
        PlanFeature planFeature = getPlanFeatureRandomSampleGenerator();
        PlanFormula planFormulaBack = getPlanFormulaRandomSampleGenerator();

        planFeature.setPlanFormula(planFormulaBack);
        assertThat(planFeature.getPlanFormula()).isEqualTo(planFormulaBack);

        planFeature.planFormula(null);
        assertThat(planFeature.getPlanFormula()).isNull();
    }
}
