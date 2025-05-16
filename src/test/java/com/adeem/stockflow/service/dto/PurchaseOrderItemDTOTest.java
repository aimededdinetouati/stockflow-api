package com.adeem.stockflow.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PurchaseOrderItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchaseOrderItemDTO.class);
        PurchaseOrderItemDTO purchaseOrderItemDTO1 = new PurchaseOrderItemDTO();
        purchaseOrderItemDTO1.setId(1L);
        PurchaseOrderItemDTO purchaseOrderItemDTO2 = new PurchaseOrderItemDTO();
        assertThat(purchaseOrderItemDTO1).isNotEqualTo(purchaseOrderItemDTO2);
        purchaseOrderItemDTO2.setId(purchaseOrderItemDTO1.getId());
        assertThat(purchaseOrderItemDTO1).isEqualTo(purchaseOrderItemDTO2);
        purchaseOrderItemDTO2.setId(2L);
        assertThat(purchaseOrderItemDTO1).isNotEqualTo(purchaseOrderItemDTO2);
        purchaseOrderItemDTO1.setId(null);
        assertThat(purchaseOrderItemDTO1).isNotEqualTo(purchaseOrderItemDTO2);
    }
}
