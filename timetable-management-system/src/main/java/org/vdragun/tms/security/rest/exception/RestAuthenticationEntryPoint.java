package org.vdragun.tms.security.rest.exception;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.vdragun.tms.ui.rest.exception.ApiError;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Vitaliy Dragun
 *
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper mapper;

    public RestAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex) throws IOException, ServletException {
        ApiError error = new ApiError(UNAUTHORIZED);
        error.setMessage(ex.getMessage());

        response.setContentType("application/json");
        response.setStatus(SC_UNAUTHORIZED);

        ServletOutputStream out = response.getOutputStream();
        mapper.writeValue(out, error);
        out.flush();
    }

}
