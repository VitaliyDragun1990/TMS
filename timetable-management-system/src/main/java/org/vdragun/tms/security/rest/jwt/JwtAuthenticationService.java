package org.vdragun.tms.security.rest.jwt;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vdragun.tms.security.dao.RoleDao;
import org.vdragun.tms.security.model.Role;
import org.vdragun.tms.security.model.User;
import org.vdragun.tms.security.rest.service.RestAuthenticationService;
import org.vdragun.tms.security.rest.service.SigninRequest;
import org.vdragun.tms.security.rest.service.SigninResponse;
import org.vdragun.tms.security.rest.service.SignupRequest;
import org.vdragun.tms.security.rest.service.SignupResponse;
import org.vdragun.tms.security.service.UserService;

/**
 * Implementation of {@link RestAuthenticationService} that operates with JWT tokens
 * to authenticate application user
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
public class JwtAuthenticationService implements RestAuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationService.class);

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private UserService userService;
    private RoleDao roleDao;
    private PasswordEncoder passwordEncoder;


    public JwtAuthenticationService(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserService userService,
            RoleDao roleDao,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
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

    @Override
    public SignupResponse processSignUpRequest(SignupRequest request) {
        User user = new User(
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()));

        for (String roleName : request.getRoles()) {
            // Handle role not found & role ADMIN is forbidden
            Role role = roleDao.findByName(roleName.toUpperCase());
            user.addRole(role);
        }

        userService.register(user);

        String username = user.getUsername();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword()));

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String token = jwtTokenProvider.generateToken(username, authorities);

        LOG.debug("IN processSignUpRequest - New user with username: {} and authorities: {} has been registered",
                username, authorities);
        return new SignupResponse(username, token);
    }

}
