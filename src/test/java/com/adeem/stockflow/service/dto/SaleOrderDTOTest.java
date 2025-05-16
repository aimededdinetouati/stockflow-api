package com.adeem.stockflow.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SaleOrderDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleOrderDTO.class);
        SaleOrderDTO saleOrderDTO1 = new SaleOrderDTO();
        saleOrderDTO1.setId(1L);
        SaleOrderDTO saleOrderDTO2 = new SaleOrderDTO();
        assertThat(saleOrderDTO1).isNotEqualTo(saleOrderDTO2);
        saleOrderDTO2.setId(saleOrderDTO1.getId());
        assertThat(saleOrderDTO1).isEqualTo(saleOrderDTO2);
        saleOrderDTO2.setId(2L);
        assertThat(saleOrderDTO1).isNotEqualTo(saleOrderDTO2);
        saleOrderDTO1.setId(null);
        assertThat(saleOrderDTO1).isNotEqualTo(saleOrderDTO2);
    }
}
