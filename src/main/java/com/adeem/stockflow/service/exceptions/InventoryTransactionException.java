package com.adeem.stockflow.service.exceptions;

/**
 * Exception thrown when inventory transaction fails.
 */
public class InventoryTransactionException extends RuntimeException {

    public InventoryTransactionException(String message) {
        super(message);
    }

    public InventoryTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
