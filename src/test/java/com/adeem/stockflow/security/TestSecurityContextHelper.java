package com.adeem.stockflow.security;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Utility class for testing security context with client account ID.
 */
public class TestSecurityContextHelper {

    private static final String DEFAULT_USERNAME = "user";

    /**
     * Sets up the security context with the given client account ID.
     * This method can be called directly in tests to programmatically set up
     * the security context with a specific client account ID.
     *
     * @param clientAccountId the client account ID to set in the security context
     * @param userId the user ID to set in the security context (defaults to 1L if null)
     * @param username the username to set in the security context (defaults to "user" if null)
     */
    public static void setSecurityContextWithClientAccountId(Long clientAccountId, Long userId, String username) {
        if (clientAccountId == null) {
            throw new IllegalArgumentException("Client account ID cannot be null");
        }

        Long effectiveUserId = userId != null ? userId : 1L;
        String effectiveUsername = username != null ? username : DEFAULT_USERNAME;

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Create JWT with necessary claims
        Instant now = Instant.now();
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", JwsAlgorithms.HS512);

        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityUtils.USER_ID_CLAIM, effectiveUserId);
        claims.put(SecurityUtils.CLIENT_ACCOUNT_ID_CLAIM, clientAccountId);
        claims.put(SecurityUtils.AUTHORITIES_CLAIM, Collections.singletonList(AuthoritiesConstants.USER_ADMIN));

        Jwt jwt = Jwt.withTokenValue("mock-token")
            .headers(h -> h.putAll(headers))
            .claims(c -> c.putAll(claims))
            .subject(effectiveUsername)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(300))
            .build();

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(AuthoritiesConstants.USER_ADMIN));

        Authentication auth = new JwtAuthenticationToken(jwt, authorities);
        context.setAuthentication(auth);

        SecurityContextHolder.setContext(context);
    }

    public static void setSecurityContextWithUser(Long userId, String username) {
        Long effectiveUserId = userId != null ? userId : 1L;
        String effectiveUsername = username != null ? username : DEFAULT_USERNAME;

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Create JWT with necessary claims
        Instant now = Instant.now();
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", JwsAlgorithms.HS512);

        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityUtils.USER_ID_CLAIM, effectiveUserId);
        claims.put(SecurityUtils.AUTHORITIES_CLAIM, Collections.singletonList(AuthoritiesConstants.USER_ADMIN));

        Jwt jwt = Jwt.withTokenValue("mock-token")
            .headers(h -> h.putAll(headers))
            .claims(c -> c.putAll(claims))
            .subject(effectiveUsername)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(300))
            .build();

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(AuthoritiesConstants.USER_CUSTOMER)
        );

        Authentication auth = new JwtAuthenticationToken(jwt, authorities);
        context.setAuthentication(auth);

        SecurityContextHolder.setContext(context);
    }

    /**
     * Sets up the security context with the given client account ID.
     * Simplified version that uses default values for userId and username.
     *
     * @param clientAccountId the client account ID to set in the security context
     */
    public static void setSecurityContextWithClientAccountId(Long clientAccountId) {
        setSecurityContextWithClientAccountId(clientAccountId, 1L, DEFAULT_USERNAME);
    }

    public static void setSecurityContextWithUserId(Long userId) {
        setSecurityContextWithUser(userId, DEFAULT_USERNAME);
    }

    /**
     * Clears the security context.
     * This should be called in @AfterEach methods to clean up the security context.
     */
    public static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
