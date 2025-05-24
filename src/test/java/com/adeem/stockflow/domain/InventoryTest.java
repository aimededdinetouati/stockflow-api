package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.InventoryTestSamples.*;
import static com.adeem.stockflow.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InventoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Inventory.class);
        Inventory inventory1 = getInventorySample1();
        Inventory inventory2 = new Inventory();
        assertThat(inventory1).isNotEqualTo(inventory2);

        inventory2.setId(inventory1.getId());
        assertThat(inventory1).isEqualTo(inventory2);

        inventory2 = getInventorySample2();
        assertThat(inventory1).isNotEqualTo(inventory2);
    }

    @Test
    void clientAccountTest() {
        Inventory inventory = getInventoryRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        inventory.setClientAccount(clientAccountBack);
        assertThat(inventory.getClientAccount()).isEqualTo(clientAccountBack);

        inventory.clientAccount(null);
        assertThat(inventory.getClientAccount()).isNull();
    }

    @Test
    void productTest() {
        Inventory inventory = getInventoryRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        inventory.setProduct(productBack);
        assertThat(inventory.getProduct()).isEqualTo(productBack);

        inventory.product(null);
        assertThat(inventory.getProduct()).isNull();
    }
}
