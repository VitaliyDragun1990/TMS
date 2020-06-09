package org.vdragun.tms.security.rest.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.vdragun.tms.core.application.validation.PersonName;
import org.vdragun.tms.security.rest.validation.Password;
import org.vdragun.tms.security.rest.validation.UniqueUsername;
import org.vdragun.tms.security.rest.validation.Username;
import org.vdragun.tms.security.rest.validation.ValidRole;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains user's sign up request data
 * 
 * @author Vitaliy Dragun
 *
 */
public class SignupRequest {

    @NotNull
    @Username
    @UniqueUsername
    private String username;

    @NotNull
    @PersonName
    private String firstName;

    @NotNull
    @PersonName
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Password
    private String password;

    @NotEmpty
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
