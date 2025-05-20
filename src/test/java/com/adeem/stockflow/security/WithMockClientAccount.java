package com.adeem.stockflow.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * Annotation to be used on integration tests where authentication with client account ID is needed.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockClientAccountSecurityContextFactory.class)
public @interface WithMockClientAccount {
    long value() default 1L;

    String username() default "user";
}
