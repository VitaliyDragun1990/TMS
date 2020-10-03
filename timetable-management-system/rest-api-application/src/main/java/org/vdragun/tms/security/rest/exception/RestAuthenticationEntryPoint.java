package org.vdragun.tms.security.rest.exception;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.vdragun.tms.util.WebUtil.getRequestUri;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.vdragun.tms.ui.rest.exception.ApiError;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.localizer.MessageLocalizer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom implementation of {@link AuthenticationEntryPoint} for handling
 * {@link AuthenticationException}
 * 
 * @author Vitaliy Dragun
 *
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOG = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

    private ObjectMapper mapper;
    private MessageLocalizer messageLocalizer;

    public RestAuthenticationEntryPoint(ObjectMapper mapper, MessageLocalizer messageLocalizer) {
        this.mapper = mapper;
        this.messageLocalizer = messageLocalizer;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex) throws IOException, ServletException {
        LOG.warn("IN commence - Failed to authenticate user reuqest, request URI:[{}]",
                getRequestUri());

        String errorMsg = getMessage(ex);
        ApiError error = new ApiError(UNAUTHORIZED);
        error.setMessage(errorMsg);

        response.setContentType("application/json");
        response.setStatus(SC_UNAUTHORIZED);

        ServletOutputStream out = response.getOutputStream();
        mapper.writeValue(out, error);
        out.flush();
    }

    private String getMessage(AuthenticationException ex) {
        if (ex instanceof JwtAuthenticationException) {
            return messageLocalizer.getLocalizedMessage(Message.INVALID_JWT_TOKEN);
        }
        return messageLocalizer.getLocalizedMessage(Message.AUTHENTICATION_REQUIRED);
    }

}
