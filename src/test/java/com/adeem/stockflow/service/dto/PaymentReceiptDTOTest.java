package com.adeem.stockflow.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentReceiptDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentReceiptDTO.class);
        PaymentReceiptDTO paymentReceiptDTO1 = new PaymentReceiptDTO();
        paymentReceiptDTO1.setId(1L);
        PaymentReceiptDTO paymentReceiptDTO2 = new PaymentReceiptDTO();
        assertThat(paymentReceiptDTO1).isNotEqualTo(paymentReceiptDTO2);
        paymentReceiptDTO2.setId(paymentReceiptDTO1.getId());
        assertThat(paymentReceiptDTO1).isEqualTo(paymentReceiptDTO2);
        paymentReceiptDTO2.setId(2L);
        assertThat(paymentReceiptDTO1).isNotEqualTo(paymentReceiptDTO2);
        paymentReceiptDTO1.setId(null);
        assertThat(paymentReceiptDTO1).isNotEqualTo(paymentReceiptDTO2);
    }
}
