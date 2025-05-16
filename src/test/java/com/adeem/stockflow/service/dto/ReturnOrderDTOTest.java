package com.adeem.stockflow.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnOrderDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnOrderDTO.class);
        ReturnOrderDTO returnOrderDTO1 = new ReturnOrderDTO();
        returnOrderDTO1.setId(1L);
        ReturnOrderDTO returnOrderDTO2 = new ReturnOrderDTO();
        assertThat(returnOrderDTO1).isNotEqualTo(returnOrderDTO2);
        returnOrderDTO2.setId(returnOrderDTO1.getId());
        assertThat(returnOrderDTO1).isEqualTo(returnOrderDTO2);
        returnOrderDTO2.setId(2L);
        assertThat(returnOrderDTO1).isNotEqualTo(returnOrderDTO2);
        returnOrderDTO1.setId(null);
        assertThat(returnOrderDTO1).isNotEqualTo(returnOrderDTO2);
    }
}
