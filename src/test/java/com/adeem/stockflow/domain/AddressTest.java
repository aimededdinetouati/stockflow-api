package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.AddressTestSamples.*;
import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.CustomerTestSamples.*;
import static com.adeem.stockflow.domain.SupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Address.class);
        Address address1 = getAddressSample1();
        Address address2 = new Address();
        assertThat(address1).isNotEqualTo(address2);

        address2.setId(address1.getId());
        assertThat(address1).isEqualTo(address2);

        address2 = getAddressSample2();
        assertThat(address1).isNotEqualTo(address2);
    }

    @Test
    void customerTest() {
        Address address = getAddressRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        address.setCustomer(customerBack);
        assertThat(address.getCustomer()).isEqualTo(customerBack);

        address.customer(null);
        assertThat(address.getCustomer()).isNull();
    }

    @Test
    void clientAccountTest() {
        Address address = getAddressRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        address.setClientAccount(clientAccountBack);
        assertThat(address.getClientAccount()).isEqualTo(clientAccountBack);
        assertThat(clientAccountBack.getAddress()).isEqualTo(address);

        address.clientAccount(null);
        assertThat(address.getClientAccount()).isNull();
        assertThat(clientAccountBack.getAddress()).isNull();
    }

    @Test
    void supplierTest() {
        Address address = getAddressRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        address.setSupplier(supplierBack);
        assertThat(address.getSupplier()).isEqualTo(supplierBack);
        assertThat(supplierBack.getAddress()).isEqualTo(address);

        address.supplier(null);
        assertThat(address.getSupplier()).isNull();
        assertThat(supplierBack.getAddress()).isNull();
    }
}
