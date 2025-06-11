package com.adeem.stockflow.domain.enumeration;

/**
 * The AssociationStatus enumeration.
 * Defines the status of a customer-company association.
 */
public enum AssociationStatus {
    /**
     * Association is active and functional
     */
    ACTIVE("ACTIVE"),

    /**
     * Association has been deactivated
     */
    INACTIVE("INACTIVE"),

    /**
     * Association is pending approval (future use)
     */
    PENDING("PENDING");

    private final String value;

    AssociationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
