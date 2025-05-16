package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.AddressTestSamples.*;
import static com.adeem.stockflow.domain.SaleOrderTestSamples.*;
import static com.adeem.stockflow.domain.ShipmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShipmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Shipment.class);
        Shipment shipment1 = getShipmentSample1();
        Shipment shipment2 = new Shipment();
        assertThat(shipment1).isNotEqualTo(shipment2);

        shipment2.setId(shipment1.getId());
        assertThat(shipment1).isEqualTo(shipment2);

        shipment2 = getShipmentSample2();
        assertThat(shipment1).isNotEqualTo(shipment2);
    }

    @Test
    void saleOrderTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        SaleOrder saleOrderBack = getSaleOrderRandomSampleGenerator();

        shipment.setSaleOrder(saleOrderBack);
        assertThat(shipment.getSaleOrder()).isEqualTo(saleOrderBack);

        shipment.saleOrder(null);
        assertThat(shipment.getSaleOrder()).isNull();
    }

    @Test
    void addressTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        shipment.setAddress(addressBack);
        assertThat(shipment.getAddress()).isEqualTo(addressBack);

        shipment.address(null);
        assertThat(shipment.getAddress()).isNull();
    }
}
