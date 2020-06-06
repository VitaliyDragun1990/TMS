package org.vdragun.tms.security.rest.exception;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.vdragun.tms.ui.rest.exception.ApiError;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Vitaliy Dragun
 *
 */
@Component
public class AccessDeniedExceptionHandler implements AccessDeniedHandler {

    private ObjectMapper mapper;

    public AccessDeniedExceptionHandler(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ApiError error = new ApiError(FORBIDDEN);
        error.setMessage("Access Denied");

        response.setContentType("application/json");
        response.setStatus(SC_FORBIDDEN);

        ServletOutputStream out = response.getOutputStream();
        mapper.writeValue(out, error);
        out.flush();
    }

}
