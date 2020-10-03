package org.vdragun.tms.security.rest.service;

/**
 * Contains email check request data
 * 
 * @author Vitaliy Dragun
 *
 */
public class EmailCheckRequest {

    private String email;

    public EmailCheckRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "EmailCheckRequest [email=" + email + "]";
    }

}
