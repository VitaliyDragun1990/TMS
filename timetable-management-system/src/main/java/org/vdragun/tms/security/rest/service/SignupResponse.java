package org.vdragun.tms.security.rest.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains sign up response data
 * 
 * @author Vitaliy Dragun
 *
 */
@Schema(description = "DTO (input model) containing information about registered user")
public class SignupResponse {

    @Schema(
            description = "Registered user's username",
            example = "student")
    private String username;

    @Schema(description = "Authentication token")
    private String token;

    @JsonCreator(mode = Mode.PROPERTIES)
    public SignupResponse(
            @JsonProperty("username") String username,
            @JsonProperty("token") String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "SigninResponse [username=" + username + "]";
    }

}
