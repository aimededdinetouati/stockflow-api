package com.adeem.stockflow.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlanFormulaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlanFormulaDTO.class);
        PlanFormulaDTO planFormulaDTO1 = new PlanFormulaDTO();
        planFormulaDTO1.setId(1L);
        PlanFormulaDTO planFormulaDTO2 = new PlanFormulaDTO();
        assertThat(planFormulaDTO1).isNotEqualTo(planFormulaDTO2);
        planFormulaDTO2.setId(planFormulaDTO1.getId());
        assertThat(planFormulaDTO1).isEqualTo(planFormulaDTO2);
        planFormulaDTO2.setId(2L);
        assertThat(planFormulaDTO1).isNotEqualTo(planFormulaDTO2);
        planFormulaDTO1.setId(null);
        assertThat(planFormulaDTO1).isNotEqualTo(planFormulaDTO2);
    }
}
