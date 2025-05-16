package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.ProductTestSamples.*;
import static com.adeem.stockflow.domain.PurchaseOrderItemTestSamples.*;
import static com.adeem.stockflow.domain.ReturnOrderItemTestSamples.*;
import static com.adeem.stockflow.domain.ReturnOrderTestSamples.*;
import static com.adeem.stockflow.domain.SaleOrderItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnOrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnOrderItem.class);
        ReturnOrderItem returnOrderItem1 = getReturnOrderItemSample1();
        ReturnOrderItem returnOrderItem2 = new ReturnOrderItem();
        assertThat(returnOrderItem1).isNotEqualTo(returnOrderItem2);

        returnOrderItem2.setId(returnOrderItem1.getId());
        assertThat(returnOrderItem1).isEqualTo(returnOrderItem2);

        returnOrderItem2 = getReturnOrderItemSample2();
        assertThat(returnOrderItem1).isNotEqualTo(returnOrderItem2);
    }

    @Test
    void productTest() {
        ReturnOrderItem returnOrderItem = getReturnOrderItemRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        returnOrderItem.setProduct(productBack);
        assertThat(returnOrderItem.getProduct()).isEqualTo(productBack);

        returnOrderItem.product(null);
        assertThat(returnOrderItem.getProduct()).isNull();
    }

    @Test
    void originalSaleOrderItemTest() {
        ReturnOrderItem returnOrderItem = getReturnOrderItemRandomSampleGenerator();
        SaleOrderItem saleOrderItemBack = getSaleOrderItemRandomSampleGenerator();

        returnOrderItem.setOriginalSaleOrderItem(saleOrderItemBack);
        assertThat(returnOrderItem.getOriginalSaleOrderItem()).isEqualTo(saleOrderItemBack);

        returnOrderItem.originalSaleOrderItem(null);
        assertThat(returnOrderItem.getOriginalSaleOrderItem()).isNull();
    }

    @Test
    void originalPurchaseOrderItemTest() {
        ReturnOrderItem returnOrderItem = getReturnOrderItemRandomSampleGenerator();
        PurchaseOrderItem purchaseOrderItemBack = getPurchaseOrderItemRandomSampleGenerator();

        returnOrderItem.setOriginalPurchaseOrderItem(purchaseOrderItemBack);
        assertThat(returnOrderItem.getOriginalPurchaseOrderItem()).isEqualTo(purchaseOrderItemBack);

        returnOrderItem.originalPurchaseOrderItem(null);
        assertThat(returnOrderItem.getOriginalPurchaseOrderItem()).isNull();
    }

    @Test
    void returnOrderTest() {
        ReturnOrderItem returnOrderItem = getReturnOrderItemRandomSampleGenerator();
        ReturnOrder returnOrderBack = getReturnOrderRandomSampleGenerator();

        returnOrderItem.setReturnOrder(returnOrderBack);
        assertThat(returnOrderItem.getReturnOrder()).isEqualTo(returnOrderBack);

        returnOrderItem.returnOrder(null);
        assertThat(returnOrderItem.getReturnOrder()).isNull();
    }
}
