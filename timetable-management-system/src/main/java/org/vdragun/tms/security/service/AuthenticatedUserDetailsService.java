package org.vdragun.tms.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.vdragun.tms.security.model.User;
import org.vdragun.tms.security.rest.jwt.JwtUserFactory;

/**
 * Custom implementation of {@link UserDetailsService} which looks for
 * {@link AuthenticatedUser} instance by username
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
public class AuthenticatedUserDetailsService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticatedUserDetailsService.class);

    private UserService userService;

    public AuthenticatedUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userService.findByUsername(username);

        if (user == null) {
            LOG.warn("IN loadUserByUsername - User with username: {} not found", username);
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }
        LOG.debug("IN loadUserByUsername - User with username: {} successfully loaded", username);

        return JwtUserFactory.create(user);
    }

}
