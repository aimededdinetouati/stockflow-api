package com.adeem.stockflow.service.exceptions;

/**
 * Exception thrown when a customer is not allowed to perform an operation.
 */
public class CustomerNotAllowedException extends RuntimeException {

    public CustomerNotAllowedException(String message) {
        super(message);
    }

    public CustomerNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
