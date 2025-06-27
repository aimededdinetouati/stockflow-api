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

    // Supplier Errors
    public static final String EMAIL_ALREADY_EXISTS = "E022";
    public static final String PHONE_ALREADY_EXISTS = "E023";
    public static final String TAX_ID_ALREADY_EXISTS = "E024";
    public static final String FIRST_NAME_REQUIRED = "E025";
    public static final String LAST_NAME_REQUIRED = "E026";
    public static final String PHONE_REQUIRED = "E027";
    public static final String SUPPLIER_HAS_ACTIVE_ORDERS = "E028";

    // Address Errors
    public static final String STREET_ADDRESS_REQUIRED = "E029";
    public static final String CITY_REQUIRED = "E030";
    public static final String STATE_REQUIRED = "E031";
    public static final String POSTAL_CODE_REQUIRED = "E032";
    public static final String COUNTRY_REQUIRED = "E033";
    public static final String INVALID_ADDRESS_TYPE = "E034";

    public static final String IMPORT_FAILED = "E035";
    public static final String USER_MUST_HAVE_ONE_AUTHORITY = "E036";
    public static final String REQUIRED_ORDER_ITEMS = "E037";
    public static final String REQUIRED_UNIT_PRICE = "E038";
    public static final String INSUFFICIENT_INVENTORY = "E039";
    public static final String INVALID_ORDER_TRANSITION = "E040";
    public static final String REQUIRED_ADDRESS = "E041";
    public static final String REQUIRED_CUSTOMER = "E042";
    public static final String GUEST_CART_NOT_FOUND = "E043";

    private ErrorConstants() {}
}
