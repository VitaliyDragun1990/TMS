package org.vdragun.tms.security.rest.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains user's sign in request data
 * 
 * @author Vitaliy Dragun
 *
 */
public class SigninRequest {

    private String username;
    private String password;

    @JsonCreator(mode = Mode.PROPERTIES)
    public SigninRequest(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "SigninRequest [username=" + username + "]";
    }

}
