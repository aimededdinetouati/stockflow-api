package com.adeem.stockflow.domain.enumeration;

/**
 * The OrderStatus enumeration.
 * Extended to support the complete order lifecycle for delivery and pickup orders.
 */
public enum OrderStatus {
    DRAFTED, // Initial state, can be modified
    CONFIRMED, // Inventory reserved, order locked
    SHIPPED, // For DELIVERY orders - with Yalidine or other carrier
    PICKED_UP, // For STORE_PICKUP orders
    COMPLETED, // Final successful state
    CANCELLED, // Can happen at any stage before COMPLETED
    RETURNED,
}
