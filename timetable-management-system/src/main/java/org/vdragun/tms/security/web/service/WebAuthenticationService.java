package org.vdragun.tms.security.web.service;

import java.util.List;

import org.vdragun.tms.security.model.Role;

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
     * Returns {@link Role}s available to register new user
     */
    List<Role> findRols();

}
