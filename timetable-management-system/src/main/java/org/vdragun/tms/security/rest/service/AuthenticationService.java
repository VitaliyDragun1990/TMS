package org.vdragun.tms.security.rest.service;

/**
 * Service component responsible for user authentication
 * 
 * @author Vitaliy Dragun
 *
 */
public interface AuthenticationService {

    /**
     * Sign in user using specified {@link SigninRequest} request
     */
    SigninResponse processSignInRequest(SigninRequest request);
}
