package org.vdragun.tms.security.rest.service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.vdragun.tms.security.validation.Username;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains user's sign in request data
 * 
 * @author Vitaliy Dragun
 *
 */
@Schema(description = "DTO (input model) containing information necessary to login existing user")
public class SigninRequest {

    @NotNull
    @Username
    @Schema(
            description = "Existing user's username",
            pattern = "^[A-Za-z]{1}[A-Za-z0-9]+$",
            minLength = 5,
            maxLength = 25,
            example = "student",
            required = true)
    private String username;

    @NotBlank
    @Schema(
            description = "Existing user's password",
            pattern = "^[A-Za-z]{1}[A-Za-z0-9]+$",
            example = "password",
            required = true)
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
