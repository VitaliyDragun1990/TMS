package org.vdragun.tms.security.rest.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains email check response data
 * 
 * @author Vitaliy Dragun
 *
 */
@Schema(description = "DTO (input model) containing information about email check")
public class EmailCheckResponse {

    @Schema(
            description = "Email address to check",
            example = "john125@gmail.com")
    private String email;

    @Schema(
            description = "Whether specified email is availabe for registration",
            example = "true",
            allowableValues = { "true", "false" })
    private boolean available;

    @JsonCreator(mode = Mode.PROPERTIES)
    public EmailCheckResponse(
            @JsonProperty("email") String email,
            @JsonProperty("available") boolean available) {
        this.email = email;
        this.available = available;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public String toString() {
        return "EmailCheckResponse [email=" + email + ", available=" + available + "]";
    }

}
