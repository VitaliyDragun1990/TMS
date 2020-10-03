package org.vdragun.tms.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.vdragun.tms.security.rest.jwt.JwtTokenProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Responsible for generating JWT tokens for testing purposes
 *
 * @author Vitaliy Dragun
 */
public class TestTokenGenerator {

    private final Map<String, String> tokenCache = new HashMap<>();

    private JwtTokenProvider tokenProvider;

    public TestTokenGenerator(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public String generateToken(String username, String... userRoles) {
        return tokenCache.computeIfAbsent(
                username, k ->
                        tokenProvider.generateToken(username, toAuthorities(userRoles)));
    }

    private List<SimpleGrantedAuthority> toAuthorities(String... userRoles) {
        return Arrays.stream(userRoles)
                .map(SimpleGrantedAuthority::new)
                .collect(toList());
    }
}
