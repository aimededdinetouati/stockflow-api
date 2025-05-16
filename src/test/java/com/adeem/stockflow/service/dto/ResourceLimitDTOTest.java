package com.adeem.stockflow.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResourceLimitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResourceLimitDTO.class);
        ResourceLimitDTO resourceLimitDTO1 = new ResourceLimitDTO();
        resourceLimitDTO1.setId(1L);
        ResourceLimitDTO resourceLimitDTO2 = new ResourceLimitDTO();
        assertThat(resourceLimitDTO1).isNotEqualTo(resourceLimitDTO2);
        resourceLimitDTO2.setId(resourceLimitDTO1.getId());
        assertThat(resourceLimitDTO1).isEqualTo(resourceLimitDTO2);
        resourceLimitDTO2.setId(2L);
        assertThat(resourceLimitDTO1).isNotEqualTo(resourceLimitDTO2);
        resourceLimitDTO1.setId(null);
        assertThat(resourceLimitDTO1).isNotEqualTo(resourceLimitDTO2);
    }
}
