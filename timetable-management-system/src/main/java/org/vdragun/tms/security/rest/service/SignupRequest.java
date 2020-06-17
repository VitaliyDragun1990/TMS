package org.vdragun.tms.security.rest.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.vdragun.tms.core.application.validation.PersonName;
import org.vdragun.tms.security.validation.Password;
import org.vdragun.tms.security.validation.UniqueUsername;
import org.vdragun.tms.security.validation.Username;
import org.vdragun.tms.security.validation.ValidRole;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains user's sign up request data
 * 
 * @author Vitaliy Dragun
 *
 */
@Schema(description = "DTO (input model) containing information necessary to register new user")
public class SignupRequest {

    @NotNull
    @Username
    @UniqueUsername
    @Schema(
            description = "User's username",
            pattern = "^[A-Za-z]{1}[A-Za-z0-9]+$",
            minLength = 5,
            maxLength = 25,
            example = "john125",
            required = true)
    private String username;

    @NotNull
    @PersonName
    @Schema(
            description = "User's first name",
            pattern = "^[A-Z]{1}[a-z]+$",
            minLength = 2,
            maxLength = 50,
            example = "John",
            required = true)
    private String firstName;

    @NotNull
    @PersonName
    @Schema(
            description = "User's last name",
            pattern = "^[A-Z]{1}[a-z]+$",
            minLength = 2,
            maxLength = 50,
            example = "Smith",
            required = true)
    private String lastName;

    @NotNull
    @Email
    @Schema(
            description = "User's email address",
            example = "john125@gmail.com",
            required = true)
    private String email;

    @NotNull
    @Password
    @Schema(
            description = "User's password",
            pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
            minLength = 8,
            example = "Password125",
            required = true)
    private String password;

    @NotEmpty
    @ArraySchema(
            schema = @Schema(
                    description = "Role names user wants to obtain",
                    example = "ROLE_STUDENT",
                    required = true),
            minItems = 1,
            uniqueItems = true)
    private List<@NotNull @ValidRole String> roles = new ArrayList<>();

    @JsonCreator(mode = Mode.PROPERTIES)
    public SignupRequest(
            @JsonProperty("username") String username,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            @JsonProperty("roles") List<String> roles) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "SignupRequest [username=" + username + ", firstName=" + firstName + ", lastName=" + lastName
                + ", email=" + email + ", roles=" + roles + "]";
    }

}
