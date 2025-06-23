package com.adeem.stockflow.service.exceptions;

/**
 * Exception thrown when a shipment operation fails.
 */
public class ShipmentException extends RuntimeException {

    public ShipmentException(String message) {
        super(message);
    }

    public ShipmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
