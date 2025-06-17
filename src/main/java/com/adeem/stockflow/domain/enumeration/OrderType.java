package com.adeem.stockflow.domain.enumeration;

/**
 * The OrderType enumeration.
 * Defines whether an order requires delivery or store pickup.
 */
public enum OrderType {
    DELIVERY, // Requires shipping via Yalidine or other carrier
    STORE_PICKUP, // Customer picks up from physical store
}
