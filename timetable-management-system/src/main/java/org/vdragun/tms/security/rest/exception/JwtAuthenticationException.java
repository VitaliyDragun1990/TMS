package org.vdragun.tms.security.rest.jwt;

import org.springframework.security.core.AuthenticationException;

/**
 * Signals about failed authentication using JWT token
 * 
 * @author Vitaliy Dragun
 *
 */
public class JwtAuthenticationException extends AuthenticationException {
    private static final long serialVersionUID = -8672578190915929526L;

    public JwtAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public JwtAuthenticationException(String msg) {
        super(msg);
    }

}
