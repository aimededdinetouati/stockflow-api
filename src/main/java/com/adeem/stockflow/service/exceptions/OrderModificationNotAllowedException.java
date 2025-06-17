package com.adeem.stockflow.service.exceptions;

/**
 * Exception thrown when an order cannot be modified.
 */
public class OrderModificationNotAllowedException extends RuntimeException {

    public OrderModificationNotAllowedException(String message) {
        super(message);
    }

    public OrderModificationNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
