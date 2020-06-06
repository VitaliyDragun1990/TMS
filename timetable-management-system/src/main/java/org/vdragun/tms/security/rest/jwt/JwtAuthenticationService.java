package org.vdragun.tms.security.rest.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.vdragun.tms.security.rest.service.AuthenticationService;
import org.vdragun.tms.security.rest.service.SigninRequest;
import org.vdragun.tms.security.rest.service.SigninResponse;

/**
 * Implementation of {@link AuthenticationService} that operates with JWT tokens
 * to authenticate application user
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
public class JwtAuthenticationService implements AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationService.class);

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public SigninResponse processSignInRequest(SigninRequest request) {
        String username = request.getUsername();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword()));
        String name = authentication.getName();

        String token = jwtTokenProvider.generateToken(name, authentication.getAuthorities());

        LOG.debug("IN processSignInRequest - User with username: {} successfully authenticated", name);
        return new SigninResponse(username, token);
    }

}
