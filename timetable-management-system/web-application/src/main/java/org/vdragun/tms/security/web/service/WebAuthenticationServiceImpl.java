package org.vdragun.tms.security.web.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vdragun.tms.security.dao.RoleDao;
import org.vdragun.tms.security.model.Role;
import org.vdragun.tms.security.model.User;
import org.vdragun.tms.security.service.UserService;

/**
 * Default implementation of {@link WebAuthenticationService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
public class WebAuthenticationServiceImpl implements WebAuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(WebAuthenticationServiceImpl.class);

    private RoleDao roleDao;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    public WebAuthenticationServiceImpl(
            RoleDao roleDao,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.roleDao = roleDao;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void processSignUp(SignupForm form) {
        User user = new User(
                form.getUsername(),
                form.getFirstName(),
                form.getLastName(),
                form.getEmail(),
                passwordEncoder.encode(form.getPassword()));

        Role role = roleDao.findByName(form.getRole());
        user.addRole(role);
        userService.register(user);

        LOG.debug("IN processSignUp - New user with username: {} and authorities: {} has been registered",
                user.getUsername(), user.getRoles());
    }

    @Override
    public List<Role> findRols() {
        return roleDao.findAll();
    }

}
