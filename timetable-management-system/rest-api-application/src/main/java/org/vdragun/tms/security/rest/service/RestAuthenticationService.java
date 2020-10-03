package org.vdragun.tms.security.rest.service;

/**
 * Service component responsible for user authentication
 * 
 * @author Vitaliy Dragun
 *
 */
public interface RestAuthenticationService {

    /**
     * Sign in user using specified {@link SigninRequest} request
     */
    SigninResponse processSignInRequest(SigninRequest request);

    /**
     * Sign up new user using specified {@link SignupRequest} request
     */
    SignupResponse processSignUpRequest(SignupRequest request);

    /**
     * Determines whether email address specified in the request is available for
     * registration
     */
    EmailCheckResponse checkEmail(EmailCheckRequest request);

    /**
     * Determines whether username specified in the request is available for
     * registration
     */
    UsernameCheckResponse checkUsername(UsernameCheckRequest request);
}
