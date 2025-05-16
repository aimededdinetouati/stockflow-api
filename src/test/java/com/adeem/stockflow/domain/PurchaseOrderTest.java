package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.AdminTestSamples.*;
import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.PurchaseOrderItemTestSamples.*;
import static com.adeem.stockflow.domain.PurchaseOrderTestSamples.*;
import static com.adeem.stockflow.domain.SupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PurchaseOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchaseOrder.class);
        PurchaseOrder purchaseOrder1 = getPurchaseOrderSample1();
        PurchaseOrder purchaseOrder2 = new PurchaseOrder();
        assertThat(purchaseOrder1).isNotEqualTo(purchaseOrder2);

        purchaseOrder2.setId(purchaseOrder1.getId());
        assertThat(purchaseOrder1).isEqualTo(purchaseOrder2);

        purchaseOrder2 = getPurchaseOrderSample2();
        assertThat(purchaseOrder1).isNotEqualTo(purchaseOrder2);
    }

    @Test
    void orderItemsTest() {
        PurchaseOrder purchaseOrder = getPurchaseOrderRandomSampleGenerator();
        PurchaseOrderItem purchaseOrderItemBack = getPurchaseOrderItemRandomSampleGenerator();

        purchaseOrder.addOrderItems(purchaseOrderItemBack);
        assertThat(purchaseOrder.getOrderItems()).containsOnly(purchaseOrderItemBack);
        assertThat(purchaseOrderItemBack.getPurchaseOrder()).isEqualTo(purchaseOrder);

        purchaseOrder.removeOrderItems(purchaseOrderItemBack);
        assertThat(purchaseOrder.getOrderItems()).doesNotContain(purchaseOrderItemBack);
        assertThat(purchaseOrderItemBack.getPurchaseOrder()).isNull();

        purchaseOrder.orderItems(new HashSet<>(Set.of(purchaseOrderItemBack)));
        assertThat(purchaseOrder.getOrderItems()).containsOnly(purchaseOrderItemBack);
        assertThat(purchaseOrderItemBack.getPurchaseOrder()).isEqualTo(purchaseOrder);

        purchaseOrder.setOrderItems(new HashSet<>());
        assertThat(purchaseOrder.getOrderItems()).doesNotContain(purchaseOrderItemBack);
        assertThat(purchaseOrderItemBack.getPurchaseOrder()).isNull();
    }

    @Test
    void clientAccountTest() {
        PurchaseOrder purchaseOrder = getPurchaseOrderRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        purchaseOrder.setClientAccount(clientAccountBack);
        assertThat(purchaseOrder.getClientAccount()).isEqualTo(clientAccountBack);

        purchaseOrder.clientAccount(null);
        assertThat(purchaseOrder.getClientAccount()).isNull();
    }

    @Test
    void adminTest() {
        PurchaseOrder purchaseOrder = getPurchaseOrderRandomSampleGenerator();
        Admin adminBack = getAdminRandomSampleGenerator();

        purchaseOrder.setAdmin(adminBack);
        assertThat(purchaseOrder.getAdmin()).isEqualTo(adminBack);

        purchaseOrder.admin(null);
        assertThat(purchaseOrder.getAdmin()).isNull();
    }

    @Test
    void supplierTest() {
        PurchaseOrder purchaseOrder = getPurchaseOrderRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        purchaseOrder.setSupplier(supplierBack);
        assertThat(purchaseOrder.getSupplier()).isEqualTo(supplierBack);

        purchaseOrder.supplier(null);
        assertThat(purchaseOrder.getSupplier()).isNull();
    }
}
