package org.vdragun.tms.security.rest.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Checks each incoming request for JWT token. If specified token is found and
 * valid, authenticates appropriate user.
 * 
 * @author Vitaliy Dragun
 *
 */
public class JwtAuthenticationTokenFilter extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    private JwtTokenProvider jwtTokenProvider;
    private AuthenticationEntryPoint authEntryPoint;

    public JwtAuthenticationTokenFilter(JwtTokenProvider jwtTokenProvider, AuthenticationEntryPoint authEntryPoint) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authEntryPoint = authEntryPoint;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        try {
            if (token != null) {
                jwtTokenProvider.validateToken(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                LOG.debug("IN doFilter - Successfully authenticated user with username: {}",
                        authentication.getName());
            }
            chain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            authEntryPoint.commence((HttpServletRequest) request, (HttpServletResponse) response, ex);
        }
    }

}
