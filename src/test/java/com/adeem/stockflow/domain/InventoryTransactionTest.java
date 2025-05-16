package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.InventoryTransactionTestSamples.*;
import static com.adeem.stockflow.domain.ReturnOrderItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InventoryTransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InventoryTransaction.class);
        InventoryTransaction inventoryTransaction1 = getInventoryTransactionSample1();
        InventoryTransaction inventoryTransaction2 = new InventoryTransaction();
        assertThat(inventoryTransaction1).isNotEqualTo(inventoryTransaction2);

        inventoryTransaction2.setId(inventoryTransaction1.getId());
        assertThat(inventoryTransaction1).isEqualTo(inventoryTransaction2);

        inventoryTransaction2 = getInventoryTransactionSample2();
        assertThat(inventoryTransaction1).isNotEqualTo(inventoryTransaction2);
    }

    @Test
    void returnOrderItemTest() {
        InventoryTransaction inventoryTransaction = getInventoryTransactionRandomSampleGenerator();
        ReturnOrderItem returnOrderItemBack = getReturnOrderItemRandomSampleGenerator();

        inventoryTransaction.setReturnOrderItem(returnOrderItemBack);
        assertThat(inventoryTransaction.getReturnOrderItem()).isEqualTo(returnOrderItemBack);

        inventoryTransaction.returnOrderItem(null);
        assertThat(inventoryTransaction.getReturnOrderItem()).isNull();
    }
}
