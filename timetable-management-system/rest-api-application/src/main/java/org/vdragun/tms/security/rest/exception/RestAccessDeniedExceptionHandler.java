package org.vdragun.tms.security.rest.exception;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.vdragun.tms.util.WebUtil.getRequestUri;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.vdragun.tms.ui.rest.exception.ApiError;
import org.vdragun.tms.config.Constants.Message;
import org.vdragun.tms.util.localizer.MessageLocalizer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom implementation of {@link AccessDeniedHandler} for handling
 * {@link AccessDeniedException}
 * 
 * @author Vitaliy Dragun
 *
 */
public class RestAccessDeniedExceptionHandler implements AccessDeniedHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestAccessDeniedExceptionHandler.class);

    private ObjectMapper mapper;
    private MessageLocalizer messageLocalizer;

    public RestAccessDeniedExceptionHandler(ObjectMapper mapper, MessageLocalizer messageLocalizer) {
        this.mapper = mapper;
        this.messageLocalizer = messageLocalizer;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            LOG.warn("IN handle - User '{}' attempted to access the protected URL: {}",
                    auth.getName(), getRequestUri());
        }

        String errorMsg = messageLocalizer.getLocalizedMessage(Message.ACCESS_DENIED);
        ApiError error = new ApiError(FORBIDDEN);
        error.setMessage(errorMsg);

        response.setContentType("application/json");
        response.setStatus(SC_FORBIDDEN);

        ServletOutputStream out = response.getOutputStream();
        mapper.writeValue(out, error);
        out.flush();
    }

}
