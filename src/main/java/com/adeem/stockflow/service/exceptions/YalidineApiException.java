package com.adeem.stockflow.service.exceptions;

/**
 * Exception thrown when there's an error with Yalidine API communication.
 */
public class YalidineApiException extends RuntimeException {

    public YalidineApiException(String message) {
        super(message);
    }

    public YalidineApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
