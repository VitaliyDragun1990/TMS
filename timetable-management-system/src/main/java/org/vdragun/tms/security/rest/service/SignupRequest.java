package org.vdragun.tms.security.rest.service;

import java.util.ArrayList;
import java.util.List;

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

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<String> roles = new ArrayList<>();

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
