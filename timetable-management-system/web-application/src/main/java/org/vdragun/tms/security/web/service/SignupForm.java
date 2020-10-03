package org.vdragun.tms.security.web.service;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.vdragun.tms.core.application.validation.PersonName;
import org.vdragun.tms.security.validation.Password;
import org.vdragun.tms.security.validation.UniqueEmail;
import org.vdragun.tms.security.validation.UniqueUsername;
import org.vdragun.tms.security.validation.Username;
import org.vdragun.tms.security.validation.ValidRole;

/**
 * Contains user's sign up request data
 * 
 * @author Vitaliy Dragun
 *
 */
public class SignupForm {

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
    @Password
    private String password;

    @NotNull
    @Password
    private String confirmPassword;

    @NotNull
    @Email
    @UniqueEmail
    private String email;

    @NotNull
    @ValidRole
    private String role;

    public SignupForm() {
    }

    public SignupForm(
            String username,
            String firstName,
            String lastName,
            String password,
            String confirmPassword,
            String email,
            String role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "SignupForm [username=" + username + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
                + email + ", role=" + role + "]";
    }

}
