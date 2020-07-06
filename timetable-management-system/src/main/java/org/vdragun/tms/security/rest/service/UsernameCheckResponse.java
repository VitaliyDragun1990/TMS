package org.vdragun.tms.security.rest.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains username check response data
 * 
 * @author Vitaliy Dragun
 *
 */
@Schema(description = "DTO (input model) containing information about username check")
public class UsernameCheckResponse {

    @Schema(
            description = "Username address to check",
            example = "jack125")
    private String username;

    @Schema(
            description = "Whether specified username is availabe for registration",
            example = "true",
            allowableValues = { "true", "false" })
    private boolean available;

    @JsonCreator(mode = Mode.PROPERTIES)
    public UsernameCheckResponse(
            @JsonProperty("username") String username,
            @JsonProperty("available") boolean available) {
        this.username = username;
        this.available = available;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public String toString() {
        return "UsernameCheckResponse [username=" + username + ", available=" + available + "]";
    }

}
