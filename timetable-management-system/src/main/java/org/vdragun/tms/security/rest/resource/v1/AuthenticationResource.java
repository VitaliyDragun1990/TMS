package org.vdragun.tms.security.rest.resource.v1;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vdragun.tms.security.rest.service.RestAuthenticationService;
import org.vdragun.tms.security.rest.service.SigninRequest;
import org.vdragun.tms.security.rest.service.SigninResponse;
import org.vdragun.tms.security.rest.service.SignupRequest;
import org.vdragun.tms.security.rest.service.SignupResponse;
import org.vdragun.tms.ui.rest.exception.ApiError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * RESTful resource responsible for authentication application user
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(path = AuthenticationResource.BASE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "user", description = "the Authentication API")
public class AuthenticationResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationResource.class);

    public static final String BASE_URL = "/api/v1/auth";

    private RestAuthenticationService authService;

    public AuthenticationResource(RestAuthenticationService authService) {
        this.authService = authService;
    }

    @SecurityRequirements
    @Operation(summary = "Login exiting user", tags = { "user" })
    @ApiResponse(
            responseCode = "200",
            description = "User logged in",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SigninResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @PostMapping("/signin")
    @ResponseStatus(OK)
    public SigninResponse signin(
            @Parameter(
                    description = "Data to login existing user. Cannot be null or empty.",
                    required = true,
                    schema = @Schema(implementation = SigninRequest.class))
            @Valid @RequestBody SigninRequest request) {
        LOG.trace("IN signin - Received POST request to sign in user with username: {}, URL: {}",
                request.getUsername(), getRequestUri());

        return authService.processSignInRequest(request);
    }

    @SecurityRequirements
    @Operation(summary = "Register new user", tags = { "user" })
    @ApiResponse(
            responseCode = "201",
            description = "User registered",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SignupResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @PostMapping("/signup")
    @ResponseStatus(CREATED)
    public SignupResponse signup(
            @Parameter(
                    description = "Data to register new user. Cannot be null or empty.",
                    required = true,
                    schema = @Schema(implementation = SignupRequest.class))
            @Valid @RequestBody SignupRequest request) {
        LOG.trace("IN signup - Received POST request to sign up new user with data: {}, URL: {}",
                request, getRequestUri());

        return authService.processSignUpRequest(request);
    }

    private String getRequestUri() {
        ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
        return uriBuilder.toUriString();
    }

}
