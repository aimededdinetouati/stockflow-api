package com.adeem.stockflow.repository.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface ProductFamilyStatsProjection {
    Long getTotalFamilies();
    Long getTotalProducts();
    Long getFamiliesWithProducts();
    Long getFamiliesWithLowStock();
    Long getFamiliesWithOutOfStock();
    BigDecimal getTotalInventoryValue();
    Long getFamiliesCreatedThisWeek();
    Long getFamiliesCreatedThisMonth();
    Instant getLastFamilyCreated();
    Instant getLastFamilyModified();
    BigDecimal getHighestFamilyValue();
    BigDecimal getLowestFamilyValue();
    Long getLargestFamilySize();
    Long getSmallestFamilySize();
}
