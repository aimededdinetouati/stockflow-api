package com.adeem.stockflow.repository.projection;

import java.math.BigDecimal;

/**
 * Projection interface for top suppliers by purchase orders and value.
 * Used to get supplier ranking data in a single query.
 */
public interface TopSuppliersProjection {
    Long getSupplierId();
    String getDisplayName();
    Long getOrderCount();
    BigDecimal getTotalValue();
}
