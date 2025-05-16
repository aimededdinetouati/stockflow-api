package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.AttachmentTestSamples.*;
import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.InventoryTestSamples.*;
import static com.adeem.stockflow.domain.ProductFamilyTestSamples.*;
import static com.adeem.stockflow.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void imagesTest() {
        Product product = getProductRandomSampleGenerator();
        Attachment attachmentBack = getAttachmentRandomSampleGenerator();

        product.addImages(attachmentBack);
        assertThat(product.getImages()).containsOnly(attachmentBack);
        assertThat(attachmentBack.getProduct()).isEqualTo(product);

        product.removeImages(attachmentBack);
        assertThat(product.getImages()).doesNotContain(attachmentBack);
        assertThat(attachmentBack.getProduct()).isNull();

        product.images(new HashSet<>(Set.of(attachmentBack)));
        assertThat(product.getImages()).containsOnly(attachmentBack);
        assertThat(attachmentBack.getProduct()).isEqualTo(product);

        product.setImages(new HashSet<>());
        assertThat(product.getImages()).doesNotContain(attachmentBack);
        assertThat(attachmentBack.getProduct()).isNull();
    }

    @Test
    void inventoriesTest() {
        Product product = getProductRandomSampleGenerator();
        Inventory inventoryBack = getInventoryRandomSampleGenerator();

        product.addInventories(inventoryBack);
        assertThat(product.getInventories()).containsOnly(inventoryBack);
        assertThat(inventoryBack.getProduct()).isEqualTo(product);

        product.removeInventories(inventoryBack);
        assertThat(product.getInventories()).doesNotContain(inventoryBack);
        assertThat(inventoryBack.getProduct()).isNull();

        product.inventories(new HashSet<>(Set.of(inventoryBack)));
        assertThat(product.getInventories()).containsOnly(inventoryBack);
        assertThat(inventoryBack.getProduct()).isEqualTo(product);

        product.setInventories(new HashSet<>());
        assertThat(product.getInventories()).doesNotContain(inventoryBack);
        assertThat(inventoryBack.getProduct()).isNull();
    }

    @Test
    void clientAccountTest() {
        Product product = getProductRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        product.setClientAccount(clientAccountBack);
        assertThat(product.getClientAccount()).isEqualTo(clientAccountBack);

        product.clientAccount(null);
        assertThat(product.getClientAccount()).isNull();
    }

    @Test
    void productFamilyTest() {
        Product product = getProductRandomSampleGenerator();
        ProductFamily productFamilyBack = getProductFamilyRandomSampleGenerator();

        product.setProductFamily(productFamilyBack);
        assertThat(product.getProductFamily()).isEqualTo(productFamilyBack);

        product.productFamily(null);
        assertThat(product.getProductFamily()).isNull();
    }
}
