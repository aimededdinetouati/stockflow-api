package com.adeem.stockflow.service.exceptions;

/**
 * Exception thrown when there's insufficient inventory for an operation.
 */
public class InsufficientInventoryException extends BadRequestAlertException {

    public InsufficientInventoryException(String message) {
        super(message, "INVENTORY", ErrorConstants.INSUFFICIENT_INVENTORY);
    }
}
