package com.adeem.stockflow.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnOrderItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnOrderItemDTO.class);
        ReturnOrderItemDTO returnOrderItemDTO1 = new ReturnOrderItemDTO();
        returnOrderItemDTO1.setId(1L);
        ReturnOrderItemDTO returnOrderItemDTO2 = new ReturnOrderItemDTO();
        assertThat(returnOrderItemDTO1).isNotEqualTo(returnOrderItemDTO2);
        returnOrderItemDTO2.setId(returnOrderItemDTO1.getId());
        assertThat(returnOrderItemDTO1).isEqualTo(returnOrderItemDTO2);
        returnOrderItemDTO2.setId(2L);
        assertThat(returnOrderItemDTO1).isNotEqualTo(returnOrderItemDTO2);
        returnOrderItemDTO1.setId(null);
        assertThat(returnOrderItemDTO1).isNotEqualTo(returnOrderItemDTO2);
    }
}
