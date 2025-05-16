package com.adeem.stockflow.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SaleOrderItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleOrderItemDTO.class);
        SaleOrderItemDTO saleOrderItemDTO1 = new SaleOrderItemDTO();
        saleOrderItemDTO1.setId(1L);
        SaleOrderItemDTO saleOrderItemDTO2 = new SaleOrderItemDTO();
        assertThat(saleOrderItemDTO1).isNotEqualTo(saleOrderItemDTO2);
        saleOrderItemDTO2.setId(saleOrderItemDTO1.getId());
        assertThat(saleOrderItemDTO1).isEqualTo(saleOrderItemDTO2);
        saleOrderItemDTO2.setId(2L);
        assertThat(saleOrderItemDTO1).isNotEqualTo(saleOrderItemDTO2);
        saleOrderItemDTO1.setId(null);
        assertThat(saleOrderItemDTO1).isNotEqualTo(saleOrderItemDTO2);
    }
}
