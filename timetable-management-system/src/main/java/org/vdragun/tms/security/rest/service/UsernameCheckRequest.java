package org.vdragun.tms.security.rest.service;

/**
 * Contains username check request data
 * 
 * @author Vitaliy Dragun
 *
 */
public class UsernameCheckRequest {

    private String username;

    public UsernameCheckRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "UsernameCheckRequest [username=" + username + "]";
    }

}
