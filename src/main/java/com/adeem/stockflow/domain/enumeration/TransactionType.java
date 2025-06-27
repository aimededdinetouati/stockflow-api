package com.adeem.stockflow.domain.enumeration;

/**
 * The TransactionType enumeration.
 */
public enum TransactionType {
    INITIAL,
    DELETION,
    PURCHASE,
    SALE,
    RETURN,
    ADJUSTMENT,
    TRANSFER_IN,
    TRANSFER_OUT,
    DAMAGED,
    EXPIRED,
    RESERVATION,
    RESERVATION_RELEASE, // Stock released from cancelled order
}
