package org.vdragun.tms.security.rest.resource.v1;

import static org.springframework.http.HttpStatus.OK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vdragun.tms.security.rest.service.AuthenticationService;
import org.vdragun.tms.security.rest.service.SigninRequest;
import org.vdragun.tms.security.rest.service.SigninResponse;

/**
 * RESTful resource responsible for authentication application user
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(path = AuthenticationResource.BASE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationResource.class);

    public static final String BASE_URL = "/api/v1/auth";

    private AuthenticationService authService;

    public AuthenticationResource(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    @ResponseStatus(OK)
    public SigninResponse signin(@RequestBody SigninRequest request) {
        LOG.trace("IN signin - Received POST request to sign in user with username: {}, URL: {}",
                request.getUsername(), getRequestUri());

        return authService.processSignInRequest(request);
    }

    private String getRequestUri() {
        ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
        return uriBuilder.toUriString();
    }

}
