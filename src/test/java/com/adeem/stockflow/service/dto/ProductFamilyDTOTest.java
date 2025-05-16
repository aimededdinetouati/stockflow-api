package com.adeem.stockflow.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductFamilyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductFamilyDTO.class);
        ProductFamilyDTO productFamilyDTO1 = new ProductFamilyDTO();
        productFamilyDTO1.setId(1L);
        ProductFamilyDTO productFamilyDTO2 = new ProductFamilyDTO();
        assertThat(productFamilyDTO1).isNotEqualTo(productFamilyDTO2);
        productFamilyDTO2.setId(productFamilyDTO1.getId());
        assertThat(productFamilyDTO1).isEqualTo(productFamilyDTO2);
        productFamilyDTO2.setId(2L);
        assertThat(productFamilyDTO1).isNotEqualTo(productFamilyDTO2);
        productFamilyDTO1.setId(null);
        assertThat(productFamilyDTO1).isNotEqualTo(productFamilyDTO2);
    }
}
