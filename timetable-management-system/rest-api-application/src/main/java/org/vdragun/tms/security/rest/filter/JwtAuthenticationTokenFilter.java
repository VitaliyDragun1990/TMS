package org.vdragun.tms.security.rest.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;
import org.vdragun.tms.security.rest.jwt.JwtTokenProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Checks each incoming request for JWT token. If specified token is found and
 * valid, authenticates appropriate user.
 *
 * @author Vitaliy Dragun
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
        LOG.debug("IN doFilter - Looking for JWT token in current request...");
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        try {
            if (token != null) {
                jwtTokenProvider.validateToken(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                LOG.debug("IN doFilter - JWT token found: successfully authenticated user with username: {}",
                        authentication.getName());
            } else {
                LOG.debug("IN doFilter - Current request does not contain JWT token");
            }
            chain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            authEntryPoint.commence((HttpServletRequest) request, (HttpServletResponse) response, ex);
        }
    }

}
