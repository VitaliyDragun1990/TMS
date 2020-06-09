package org.vdragun.tms.security.rest.service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.vdragun.tms.security.rest.validation.Username;

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

    @NotNull
    @Username
    private String username;

    @NotBlank
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
