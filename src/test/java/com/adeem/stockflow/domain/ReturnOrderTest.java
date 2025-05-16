package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.AdminTestSamples.*;
import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.CustomerTestSamples.*;
import static com.adeem.stockflow.domain.PurchaseOrderTestSamples.*;
import static com.adeem.stockflow.domain.ReturnOrderItemTestSamples.*;
import static com.adeem.stockflow.domain.ReturnOrderTestSamples.*;
import static com.adeem.stockflow.domain.SaleOrderTestSamples.*;
import static com.adeem.stockflow.domain.SupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ReturnOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnOrder.class);
        ReturnOrder returnOrder1 = getReturnOrderSample1();
        ReturnOrder returnOrder2 = new ReturnOrder();
        assertThat(returnOrder1).isNotEqualTo(returnOrder2);

        returnOrder2.setId(returnOrder1.getId());
        assertThat(returnOrder1).isEqualTo(returnOrder2);

        returnOrder2 = getReturnOrderSample2();
        assertThat(returnOrder1).isNotEqualTo(returnOrder2);
    }

    @Test
    void itemsTest() {
        ReturnOrder returnOrder = getReturnOrderRandomSampleGenerator();
        ReturnOrderItem returnOrderItemBack = getReturnOrderItemRandomSampleGenerator();

        returnOrder.addItems(returnOrderItemBack);
        assertThat(returnOrder.getItems()).containsOnly(returnOrderItemBack);
        assertThat(returnOrderItemBack.getReturnOrder()).isEqualTo(returnOrder);

        returnOrder.removeItems(returnOrderItemBack);
        assertThat(returnOrder.getItems()).doesNotContain(returnOrderItemBack);
        assertThat(returnOrderItemBack.getReturnOrder()).isNull();

        returnOrder.items(new HashSet<>(Set.of(returnOrderItemBack)));
        assertThat(returnOrder.getItems()).containsOnly(returnOrderItemBack);
        assertThat(returnOrderItemBack.getReturnOrder()).isEqualTo(returnOrder);

        returnOrder.setItems(new HashSet<>());
        assertThat(returnOrder.getItems()).doesNotContain(returnOrderItemBack);
        assertThat(returnOrderItemBack.getReturnOrder()).isNull();
    }

    @Test
    void clientAccountTest() {
        ReturnOrder returnOrder = getReturnOrderRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        returnOrder.setClientAccount(clientAccountBack);
        assertThat(returnOrder.getClientAccount()).isEqualTo(clientAccountBack);

        returnOrder.clientAccount(null);
        assertThat(returnOrder.getClientAccount()).isNull();
    }

    @Test
    void processedByTest() {
        ReturnOrder returnOrder = getReturnOrderRandomSampleGenerator();
        Admin adminBack = getAdminRandomSampleGenerator();

        returnOrder.setProcessedBy(adminBack);
        assertThat(returnOrder.getProcessedBy()).isEqualTo(adminBack);

        returnOrder.processedBy(null);
        assertThat(returnOrder.getProcessedBy()).isNull();
    }

    @Test
    void customerTest() {
        ReturnOrder returnOrder = getReturnOrderRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        returnOrder.setCustomer(customerBack);
        assertThat(returnOrder.getCustomer()).isEqualTo(customerBack);

        returnOrder.customer(null);
        assertThat(returnOrder.getCustomer()).isNull();
    }

    @Test
    void supplierTest() {
        ReturnOrder returnOrder = getReturnOrderRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        returnOrder.setSupplier(supplierBack);
        assertThat(returnOrder.getSupplier()).isEqualTo(supplierBack);

        returnOrder.supplier(null);
        assertThat(returnOrder.getSupplier()).isNull();
    }

    @Test
    void originalSaleOrderTest() {
        ReturnOrder returnOrder = getReturnOrderRandomSampleGenerator();
        SaleOrder saleOrderBack = getSaleOrderRandomSampleGenerator();

        returnOrder.setOriginalSaleOrder(saleOrderBack);
        assertThat(returnOrder.getOriginalSaleOrder()).isEqualTo(saleOrderBack);

        returnOrder.originalSaleOrder(null);
        assertThat(returnOrder.getOriginalSaleOrder()).isNull();
    }

    @Test
    void originalPurchaseOrderTest() {
        ReturnOrder returnOrder = getReturnOrderRandomSampleGenerator();
        PurchaseOrder purchaseOrderBack = getPurchaseOrderRandomSampleGenerator();

        returnOrder.setOriginalPurchaseOrder(purchaseOrderBack);
        assertThat(returnOrder.getOriginalPurchaseOrder()).isEqualTo(purchaseOrderBack);

        returnOrder.originalPurchaseOrder(null);
        assertThat(returnOrder.getOriginalPurchaseOrder()).isNull();
    }
}
