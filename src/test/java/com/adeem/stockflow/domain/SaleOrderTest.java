package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.CustomerTestSamples.*;
import static com.adeem.stockflow.domain.PaymentTestSamples.*;
import static com.adeem.stockflow.domain.SaleOrderItemTestSamples.*;
import static com.adeem.stockflow.domain.SaleOrderTestSamples.*;
import static com.adeem.stockflow.domain.ShipmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SaleOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleOrder.class);
        SaleOrder saleOrder1 = getSaleOrderSample1();
        SaleOrder saleOrder2 = new SaleOrder();
        assertThat(saleOrder1).isNotEqualTo(saleOrder2);

        saleOrder2.setId(saleOrder1.getId());
        assertThat(saleOrder1).isEqualTo(saleOrder2);

        saleOrder2 = getSaleOrderSample2();
        assertThat(saleOrder1).isNotEqualTo(saleOrder2);
    }

    @Test
    void paymentTest() {
        SaleOrder saleOrder = getSaleOrderRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        saleOrder.setPayment(paymentBack);
        assertThat(saleOrder.getPayment()).isEqualTo(paymentBack);

        saleOrder.payment(null);
        assertThat(saleOrder.getPayment()).isNull();
    }

    @Test
    void orderItemsTest() {
        SaleOrder saleOrder = getSaleOrderRandomSampleGenerator();
        SaleOrderItem saleOrderItemBack = getSaleOrderItemRandomSampleGenerator();

        saleOrder.addOrderItems(saleOrderItemBack);
        assertThat(saleOrder.getOrderItems()).containsOnly(saleOrderItemBack);
        assertThat(saleOrderItemBack.getSaleOrder()).isEqualTo(saleOrder);

        saleOrder.removeOrderItems(saleOrderItemBack);
        assertThat(saleOrder.getOrderItems()).doesNotContain(saleOrderItemBack);
        assertThat(saleOrderItemBack.getSaleOrder()).isNull();

        saleOrder.orderItems(new HashSet<>(Set.of(saleOrderItemBack)));
        assertThat(saleOrder.getOrderItems()).containsOnly(saleOrderItemBack);
        assertThat(saleOrderItemBack.getSaleOrder()).isEqualTo(saleOrder);

        saleOrder.setOrderItems(new HashSet<>());
        assertThat(saleOrder.getOrderItems()).doesNotContain(saleOrderItemBack);
        assertThat(saleOrderItemBack.getSaleOrder()).isNull();
    }

    @Test
    void clientAccountTest() {
        SaleOrder saleOrder = getSaleOrderRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        saleOrder.setClientAccount(clientAccountBack);
        assertThat(saleOrder.getClientAccount()).isEqualTo(clientAccountBack);

        saleOrder.clientAccount(null);
        assertThat(saleOrder.getClientAccount()).isNull();
    }

    @Test
    void customerTest() {
        SaleOrder saleOrder = getSaleOrderRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        saleOrder.setCustomer(customerBack);
        assertThat(saleOrder.getCustomer()).isEqualTo(customerBack);

        saleOrder.customer(null);
        assertThat(saleOrder.getCustomer()).isNull();
    }

    @Test
    void shipmentTest() {
        SaleOrder saleOrder = getSaleOrderRandomSampleGenerator();
        Shipment shipmentBack = getShipmentRandomSampleGenerator();

        saleOrder.setShipment(shipmentBack);
        assertThat(saleOrder.getShipment()).isEqualTo(shipmentBack);
        assertThat(shipmentBack.getSaleOrder()).isEqualTo(saleOrder);

        saleOrder.shipment(null);
        assertThat(saleOrder.getShipment()).isNull();
        assertThat(shipmentBack.getSaleOrder()).isNull();
    }
}
