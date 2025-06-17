package com.adeem.stockflow.service.exceptions;

/**
 * Exception thrown when Yalidine integration is not properly configured.
 */
public class YalidineConfigurationException extends RuntimeException {

    public YalidineConfigurationException(String message) {
        super(message);
    }

    public YalidineConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
