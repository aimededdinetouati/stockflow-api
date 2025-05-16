package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.AddressTestSamples.*;
import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.SupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SupplierTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Supplier.class);
        Supplier supplier1 = getSupplierSample1();
        Supplier supplier2 = new Supplier();
        assertThat(supplier1).isNotEqualTo(supplier2);

        supplier2.setId(supplier1.getId());
        assertThat(supplier1).isEqualTo(supplier2);

        supplier2 = getSupplierSample2();
        assertThat(supplier1).isNotEqualTo(supplier2);
    }

    @Test
    void addressTest() {
        Supplier supplier = getSupplierRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        supplier.setAddress(addressBack);
        assertThat(supplier.getAddress()).isEqualTo(addressBack);

        supplier.address(null);
        assertThat(supplier.getAddress()).isNull();
    }

    @Test
    void clientAccountTest() {
        Supplier supplier = getSupplierRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        supplier.setClientAccount(clientAccountBack);
        assertThat(supplier.getClientAccount()).isEqualTo(clientAccountBack);

        supplier.clientAccount(null);
        assertThat(supplier.getClientAccount()).isNull();
    }
}
