package com.adeem.stockflow.service.exceptions;

/**
 * Exception thrown when multi-tenant security validation fails.
 */
public class TenantSecurityException extends RuntimeException {

    public TenantSecurityException(String message) {
        super(message);
    }

    public TenantSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
