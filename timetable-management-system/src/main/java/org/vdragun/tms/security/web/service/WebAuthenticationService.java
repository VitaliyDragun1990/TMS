package org.vdragun.tms.security.web.service;

/**
 * Service component responsible for user authentication
 * 
 * @author Vitaliy Dragun
 *
 */
public interface WebAuthenticationService {

    /**
     * Sign up new user using specified form data
     */
    void processSignUp(SignupForm form);

    /**
     * Sign in existing user using specified form data
     */
    // TODO: may be obsolete because Spring Security handle login itself
    void processSignIn(SigninForm form);
}
