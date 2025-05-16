package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.AddressTestSamples.*;
import static com.adeem.stockflow.domain.CartTestSamples.*;
import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.CustomerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void addressListTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        customer.addAddressList(addressBack);
        assertThat(customer.getAddressLists()).containsOnly(addressBack);
        assertThat(addressBack.getCustomer()).isEqualTo(customer);

        customer.removeAddressList(addressBack);
        assertThat(customer.getAddressLists()).doesNotContain(addressBack);
        assertThat(addressBack.getCustomer()).isNull();

        customer.addressLists(new HashSet<>(Set.of(addressBack)));
        assertThat(customer.getAddressLists()).containsOnly(addressBack);
        assertThat(addressBack.getCustomer()).isEqualTo(customer);

        customer.setAddressLists(new HashSet<>());
        assertThat(customer.getAddressLists()).doesNotContain(addressBack);
        assertThat(addressBack.getCustomer()).isNull();
    }

    @Test
    void cartsTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Cart cartBack = getCartRandomSampleGenerator();

        customer.addCarts(cartBack);
        assertThat(customer.getCarts()).containsOnly(cartBack);
        assertThat(cartBack.getCustomer()).isEqualTo(customer);

        customer.removeCarts(cartBack);
        assertThat(customer.getCarts()).doesNotContain(cartBack);
        assertThat(cartBack.getCustomer()).isNull();

        customer.carts(new HashSet<>(Set.of(cartBack)));
        assertThat(customer.getCarts()).containsOnly(cartBack);
        assertThat(cartBack.getCustomer()).isEqualTo(customer);

        customer.setCarts(new HashSet<>());
        assertThat(customer.getCarts()).doesNotContain(cartBack);
        assertThat(cartBack.getCustomer()).isNull();
    }

    @Test
    void clientAccountTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        customer.setClientAccount(clientAccountBack);
        assertThat(customer.getClientAccount()).isEqualTo(clientAccountBack);

        customer.clientAccount(null);
        assertThat(customer.getClientAccount()).isNull();
    }
}
