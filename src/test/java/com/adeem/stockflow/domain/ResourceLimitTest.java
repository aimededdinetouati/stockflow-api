package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.PlanFormulaTestSamples.*;
import static com.adeem.stockflow.domain.ResourceLimitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResourceLimitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResourceLimit.class);
        ResourceLimit resourceLimit1 = getResourceLimitSample1();
        ResourceLimit resourceLimit2 = new ResourceLimit();
        assertThat(resourceLimit1).isNotEqualTo(resourceLimit2);

        resourceLimit2.setId(resourceLimit1.getId());
        assertThat(resourceLimit1).isEqualTo(resourceLimit2);

        resourceLimit2 = getResourceLimitSample2();
        assertThat(resourceLimit1).isNotEqualTo(resourceLimit2);
    }

    @Test
    void planFormulaTest() {
        ResourceLimit resourceLimit = getResourceLimitRandomSampleGenerator();
        PlanFormula planFormulaBack = getPlanFormulaRandomSampleGenerator();

        resourceLimit.setPlanFormula(planFormulaBack);
        assertThat(resourceLimit.getPlanFormula()).isEqualTo(planFormulaBack);

        resourceLimit.planFormula(null);
        assertThat(resourceLimit.getPlanFormula()).isNull();
    }
}
