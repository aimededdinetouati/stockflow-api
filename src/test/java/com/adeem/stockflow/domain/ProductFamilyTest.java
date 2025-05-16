package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.ProductFamilyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductFamilyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductFamily.class);
        ProductFamily productFamily1 = getProductFamilySample1();
        ProductFamily productFamily2 = new ProductFamily();
        assertThat(productFamily1).isNotEqualTo(productFamily2);

        productFamily2.setId(productFamily1.getId());
        assertThat(productFamily1).isEqualTo(productFamily2);

        productFamily2 = getProductFamilySample2();
        assertThat(productFamily1).isNotEqualTo(productFamily2);
    }
}
