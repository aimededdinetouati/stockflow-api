package com.adeem.stockflow.service.exceptions;

/**
 * Exception thrown when a reservation has expired.
 */
public class ReservationExpiredException extends RuntimeException {

    public ReservationExpiredException(String message) {
        super(message);
    }

    public ReservationExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
