package com.adeem.stockflow.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER_ADMIN = "ROLE_USER_ADMIN";

    public static final String USER_CUSTOMER = "ROLE_USER_CUSTOMER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {}
}
