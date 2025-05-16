package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.ProductTestSamples.*;
import static com.adeem.stockflow.domain.SaleOrderItemTestSamples.*;
import static com.adeem.stockflow.domain.SaleOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SaleOrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleOrderItem.class);
        SaleOrderItem saleOrderItem1 = getSaleOrderItemSample1();
        SaleOrderItem saleOrderItem2 = new SaleOrderItem();
        assertThat(saleOrderItem1).isNotEqualTo(saleOrderItem2);

        saleOrderItem2.setId(saleOrderItem1.getId());
        assertThat(saleOrderItem1).isEqualTo(saleOrderItem2);

        saleOrderItem2 = getSaleOrderItemSample2();
        assertThat(saleOrderItem1).isNotEqualTo(saleOrderItem2);
    }

    @Test
    void productTest() {
        SaleOrderItem saleOrderItem = getSaleOrderItemRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        saleOrderItem.setProduct(productBack);
        assertThat(saleOrderItem.getProduct()).isEqualTo(productBack);

        saleOrderItem.product(null);
        assertThat(saleOrderItem.getProduct()).isNull();
    }

    @Test
    void saleOrderTest() {
        SaleOrderItem saleOrderItem = getSaleOrderItemRandomSampleGenerator();
        SaleOrder saleOrderBack = getSaleOrderRandomSampleGenerator();

        saleOrderItem.setSaleOrder(saleOrderBack);
        assertThat(saleOrderItem.getSaleOrder()).isEqualTo(saleOrderBack);

        saleOrderItem.saleOrder(null);
        assertThat(saleOrderItem.getSaleOrder()).isNull();
    }
}
