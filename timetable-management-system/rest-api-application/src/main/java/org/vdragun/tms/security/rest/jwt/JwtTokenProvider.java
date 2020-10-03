package org.vdragun.tms.security.rest.jwt;

import static java.util.stream.Collectors.toList;

import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.vdragun.tms.security.rest.exception.JwtAuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Component responsible for processing of JWT tokens
 * 
 * @author Vitaliy Dragun
 *
 */
@Component
public class JwtTokenProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JwtTokenProvider.class);

    private String secret;
    private long tokenDurationInMillis;
    private UserDetailsService userDetailsService;

    public JwtTokenProvider(
            @Value("${jwt.token.secret}") String secret,
            @Value("${jwt.token.expired}") long tokenDurationInMillis,
            UserDetailsService userDetailsService) {
        this.secret = Base64.getEncoder().encodeToString(secret.getBytes());
        this.tokenDurationInMillis = tokenDurationInMillis;
        this.userDetailsService = userDetailsService;
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        List<String> roleNames = getRoleNames(authorities);
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roleNames);

        Date now = new Date();
        Date tokenValidTime = new Date(now.getTime() + tokenDurationInMillis);

        String token = Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(tokenValidTime)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        LOG.debug("IN generateToken - JWT token for username: {} and roles: {} successfully generated: [{}]",
                username, roleNames, token);
        return token;
    }

    public Authentication getAuthentication(String token) {
        LOG.debug("IN getAuthentication - Providing authentication for JWT token: [{}]", token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        String username = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();

        LOG.debug("IN getUsername - Username: {} found for JWT token: [{}]", username, token);
        return username;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && (bearerToken.startsWith("Bearer_") || bearerToken.startsWith("Bearer "))) {
            String token = bearerToken.substring(7);

            LOG.debug("IN resolveToken - JWT token [{}] resolved successfully from request", token);
            return token;
        }

        LOG.debug("IN resolveToken - Provided request does not contain JWT token");
        return null;
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            LOG.debug("IN validateToken - Specified JWT token: [{}] is valid", token);

        } catch (JwtException | IllegalArgumentException e) {
            LOG.warn("IN validateToken - Specified JWT token: [{}] is invalid", token, e);
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }

    private List<String> getRoleNames(Collection<? extends GrantedAuthority> roles) {
        return roles.stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .collect(toList());
    }

}
