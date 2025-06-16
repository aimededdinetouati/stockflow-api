package com.adeem.stockflow.repository.projection;

import com.adeem.stockflow.domain.enumeration.AssociationType;

/**
 * Projection interface for association statistics.
 * Used for efficient querying of aggregated association data.
 */
public interface AssociationStatsProjection {
    /**
     * Get the association type
     */
    AssociationType getAssociationType();

    /**
     * Get the count of associations for this type
     */
    Long getCount();
}
