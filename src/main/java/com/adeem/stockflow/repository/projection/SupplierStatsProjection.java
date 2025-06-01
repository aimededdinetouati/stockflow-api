package com.adeem.stockflow.repository.projection;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Projection interface for comprehensive supplier statistics.
 * Used to get most supplier statistics in a single optimized query.
 */
public interface SupplierStatsProjection {
    Long getTotalSuppliers();
    Long getActiveSuppliers();
    Long getInactiveSuppliers();
    Long getSuppliersWithAddresses();
    Long getSuppliersWithoutAddresses();
    Long getSuppliersAddedThisWeek();
    Long getSuppliersAddedThisMonth();
    Long getSuppliersWithPurchaseOrders();
    BigDecimal getTotalPurchaseOrderValue();
    Instant getLastSupplierCreated();
    Instant getLastSupplierModified();
}
