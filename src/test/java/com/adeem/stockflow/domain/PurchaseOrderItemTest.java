package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.ProductTestSamples.*;
import static com.adeem.stockflow.domain.PurchaseOrderItemTestSamples.*;
import static com.adeem.stockflow.domain.PurchaseOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PurchaseOrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchaseOrderItem.class);
        PurchaseOrderItem purchaseOrderItem1 = getPurchaseOrderItemSample1();
        PurchaseOrderItem purchaseOrderItem2 = new PurchaseOrderItem();
        assertThat(purchaseOrderItem1).isNotEqualTo(purchaseOrderItem2);

        purchaseOrderItem2.setId(purchaseOrderItem1.getId());
        assertThat(purchaseOrderItem1).isEqualTo(purchaseOrderItem2);

        purchaseOrderItem2 = getPurchaseOrderItemSample2();
        assertThat(purchaseOrderItem1).isNotEqualTo(purchaseOrderItem2);
    }

    @Test
    void productTest() {
        PurchaseOrderItem purchaseOrderItem = getPurchaseOrderItemRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        purchaseOrderItem.setProduct(productBack);
        assertThat(purchaseOrderItem.getProduct()).isEqualTo(productBack);

        purchaseOrderItem.product(null);
        assertThat(purchaseOrderItem.getProduct()).isNull();
    }

    @Test
    void purchaseOrderTest() {
        PurchaseOrderItem purchaseOrderItem = getPurchaseOrderItemRandomSampleGenerator();
        PurchaseOrder purchaseOrderBack = getPurchaseOrderRandomSampleGenerator();

        purchaseOrderItem.setPurchaseOrder(purchaseOrderBack);
        assertThat(purchaseOrderItem.getPurchaseOrder()).isEqualTo(purchaseOrderBack);

        purchaseOrderItem.purchaseOrder(null);
        assertThat(purchaseOrderItem.getPurchaseOrder()).isNull();
    }
}
