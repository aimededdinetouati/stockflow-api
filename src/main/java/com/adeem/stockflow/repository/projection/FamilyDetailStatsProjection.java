package com.adeem.stockflow.repository.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface FamilyDetailStatsProjection {
    Long getFamilyId();
    String getFamilyName();
    Long getProductCount();
    BigDecimal getTotalValue();
    Long getTotalQuantity();
    Long getLowStockProducts();
    Long getOutOfStockProducts();
    Instant getLastModifiedDate();
}
