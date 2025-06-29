package com.adeem.stockflow.repository.projection;

/**
 * Projection interface for cart statistics.
 */
public interface CartStatsProjection {
    Long getTotalCarts();
    Long getActiveCarts();
    Double getAvgItemsPerCart();
    Double getAvgCartValue();
}
