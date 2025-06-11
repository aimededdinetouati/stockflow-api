package com.adeem.stockflow.domain.enumeration;

/**
 * The AssociationType enumeration.
 * Defines the type of association between a customer and a company.
 */
public enum AssociationType {
    /**
     * Customer follows company for updates and news
     */
    FOLLOWED("FOLLOWED"),

    /**
     * Customer prefers this supplier for their business
     */
    PREFERRED_SUPPLIER("PREFERRED_SUPPLIER"),

    /**
     * Formal business partnership relationship
     */
    BUSINESS_PARTNER("BUSINESS_PARTNER");

    private final String value;

    AssociationType(String value) {
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
