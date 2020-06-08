package org.vdragun.tms.security.rest.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains sign up response data
 * 
 * @author Vitaliy Dragun
 *
 */
public class SignupResponse {

    private String username;
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
