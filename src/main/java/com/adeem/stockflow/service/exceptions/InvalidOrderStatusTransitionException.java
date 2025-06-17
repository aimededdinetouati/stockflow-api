package com.adeem.stockflow.service.exceptions;

/**
 * Exception thrown when an invalid order status transition is attempted.
 */
public class InvalidOrderStatusTransitionException extends RuntimeException {

    public InvalidOrderStatusTransitionException(String message) {
        super(message);
    }

    public InvalidOrderStatusTransitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
