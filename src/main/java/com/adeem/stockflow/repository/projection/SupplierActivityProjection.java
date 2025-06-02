package com.adeem.stockflow.repository.projection;

import java.time.Instant;

/**
 * Projection interface for recent supplier activities.
 * Used to get supplier activity data efficiently.
 */
public interface SupplierActivityProjection {
    String getAction();
    String getDisplayName();
    Instant getActivityDate();
    String getNotes();
}
