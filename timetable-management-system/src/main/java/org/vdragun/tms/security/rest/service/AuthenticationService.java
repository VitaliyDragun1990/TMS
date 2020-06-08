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

    /**
     * Sign up new user using specified {@link SignupRequest} request
     */
    SignupResponse processSignUpRequest(SignupRequest request);
}
