package com.adeem.stockflow.service.exceptions;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");

    // Business logic error codes (E001-E999)
    public static final String COMPANY_NAME_EXISTS = "E001";
    public static final String PRODUCT_CODE_EXISTS = "E002";
    public static final String PRODUCT_FAMILY_DOES_NOT_EXIST = "E003";

    // Common error keys for entity operations
    public static final String ID_EXISTS = "E004";
    public static final String ID_NULL = "E005";
    public static final String ID_INVALID = "E006";
    public static final String ID_NOT_FOUND = "E007";
    public static final String ENTITY_NOT_FOUND = "E008";
    public static final String NOT_FOUND = "E009";

    // User management error keys
    public static final String USER_EXISTS = "E010";
    public static final String EMAIL_EXISTS = "E011";

    // Product family specific error keys
    public static final String PRODUCT_FAMILY_NAME_EXISTS = "E012";
    public static final String PRODUCT_FAMILY_HAS_PRODUCTS = "E013";

    // Inventory specific error keys
    public static final String QUANTITY_INVALID = "E014";
    public static final String INVALID_AVAILABLE_QUANTITY = "E015";
    public static final String INVALID_TYPE = "E015";

    // Product specific error keys
    public static final String APPLY_TVA = "E016";
    public static final String NAME_NULL = "E017";
    public static final String CODE_NULL = "E018";
    public static final String FILE_EMPTY = "E019";
    public static final String FILE_TOO_LARGE = "E020";
    public static final String FILE_INVALID_FORMAT = "E021";
    public static final String IMPORT_FAILED = "E022";
    public static final String USER_MUST_HAVE_ONE_AUTHORITY = "E023";

    private ErrorConstants() {}
}
