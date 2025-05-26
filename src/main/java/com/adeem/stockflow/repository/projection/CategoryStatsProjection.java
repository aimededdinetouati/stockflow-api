package com.adeem.stockflow.repository.projection;

import java.math.BigDecimal;

public interface CategoryStatsProjection {
    String getCategory();
    Long getFamilyCount();
    Long getProductCount();
    BigDecimal getTotalValue();
}
