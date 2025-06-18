package com.adeem.stockflow.service.exceptions;

/**
 * Exception thrown when an invalid order status transition is attempted.
 */
public class InvalidOrderStatusTransitionException extends BadRequestAlertException {

    public InvalidOrderStatusTransitionException(String message) {
        super(message, "SaleOrder", ErrorConstants.INVALID_ORDER_TRANSITION);
    }
}
