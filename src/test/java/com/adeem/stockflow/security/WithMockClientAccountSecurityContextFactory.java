package com.adeem.stockflow.security;

import com.adeem.stockflow.security.AuthoritiesConstants;
import com.adeem.stockflow.security.SecurityUtils;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * Factory for creating a security context with a JWT token that includes client account ID.
 */
public class WithMockClientAccountSecurityContextFactory implements WithSecurityContextFactory<WithMockClientAccount> {

    @Override
    public SecurityContext createSecurityContext(WithMockClientAccount annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Create JWT with necessary claims
        Instant now = Instant.now();
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", JwsAlgorithms.HS512);

        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityUtils.USER_ID_CLAIM, 1L);
        claims.put(SecurityUtils.CLIENT_ACCOUNT_ID_CLAIM, annotation.value());
        claims.put(SecurityUtils.AUTHORITIES_CLAIM, Collections.singletonList(AuthoritiesConstants.USER_ADMIN));

        Jwt jwt = Jwt.withTokenValue("mock-token")
            .headers(h -> h.putAll(headers))
            .claims(c -> c.putAll(claims))
            .subject(annotation.username())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(300))
            .build();

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(AuthoritiesConstants.USER_ADMIN));

        Authentication auth = new JwtAuthenticationToken(jwt, authorities);
        context.setAuthentication(auth);

        return context;
    }
}
