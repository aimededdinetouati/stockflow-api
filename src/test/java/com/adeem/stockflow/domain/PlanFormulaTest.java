package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.PlanFeatureTestSamples.*;
import static com.adeem.stockflow.domain.PlanFormulaTestSamples.*;
import static com.adeem.stockflow.domain.ResourceLimitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PlanFormulaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlanFormula.class);
        PlanFormula planFormula1 = getPlanFormulaSample1();
        PlanFormula planFormula2 = new PlanFormula();
        assertThat(planFormula1).isNotEqualTo(planFormula2);

        planFormula2.setId(planFormula1.getId());
        assertThat(planFormula1).isEqualTo(planFormula2);

        planFormula2 = getPlanFormulaSample2();
        assertThat(planFormula1).isNotEqualTo(planFormula2);
    }

    @Test
    void planFeaturesTest() {
        PlanFormula planFormula = getPlanFormulaRandomSampleGenerator();
        PlanFeature planFeatureBack = getPlanFeatureRandomSampleGenerator();

        planFormula.addPlanFeatures(planFeatureBack);
        assertThat(planFormula.getPlanFeatures()).containsOnly(planFeatureBack);
        assertThat(planFeatureBack.getPlanFormula()).isEqualTo(planFormula);

        planFormula.removePlanFeatures(planFeatureBack);
        assertThat(planFormula.getPlanFeatures()).doesNotContain(planFeatureBack);
        assertThat(planFeatureBack.getPlanFormula()).isNull();

        planFormula.planFeatures(new HashSet<>(Set.of(planFeatureBack)));
        assertThat(planFormula.getPlanFeatures()).containsOnly(planFeatureBack);
        assertThat(planFeatureBack.getPlanFormula()).isEqualTo(planFormula);

        planFormula.setPlanFeatures(new HashSet<>());
        assertThat(planFormula.getPlanFeatures()).doesNotContain(planFeatureBack);
        assertThat(planFeatureBack.getPlanFormula()).isNull();
    }

    @Test
    void resourceLimitsTest() {
        PlanFormula planFormula = getPlanFormulaRandomSampleGenerator();
        ResourceLimit resourceLimitBack = getResourceLimitRandomSampleGenerator();

        planFormula.addResourceLimits(resourceLimitBack);
        assertThat(planFormula.getResourceLimits()).containsOnly(resourceLimitBack);
        assertThat(resourceLimitBack.getPlanFormula()).isEqualTo(planFormula);

        planFormula.removeResourceLimits(resourceLimitBack);
        assertThat(planFormula.getResourceLimits()).doesNotContain(resourceLimitBack);
        assertThat(resourceLimitBack.getPlanFormula()).isNull();

        planFormula.resourceLimits(new HashSet<>(Set.of(resourceLimitBack)));
        assertThat(planFormula.getResourceLimits()).containsOnly(resourceLimitBack);
        assertThat(resourceLimitBack.getPlanFormula()).isEqualTo(planFormula);

        planFormula.setResourceLimits(new HashSet<>());
        assertThat(planFormula.getResourceLimits()).doesNotContain(resourceLimitBack);
        assertThat(resourceLimitBack.getPlanFormula()).isNull();
    }
}
