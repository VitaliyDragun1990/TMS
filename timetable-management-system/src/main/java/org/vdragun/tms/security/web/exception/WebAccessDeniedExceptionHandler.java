package org.vdragun.tms.security.web.exception;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.localizer.MessageLocalizer;

/**
 * @author Vitaliy Dragun
 *
 */
public class WebAccessDeniedExceptionHandler implements AccessDeniedHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebAccessDeniedExceptionHandler.class);

    private MessageLocalizer messageLocalizer;

    public WebAccessDeniedExceptionHandler(MessageLocalizer messageLocalizer) {
        this.messageLocalizer = messageLocalizer;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            LOG.warn("IN handle - User '{}' attempted to access the protected URL: {}",
                    auth.getName(), getRequestUrl(request));
        }

        request.getSession().setAttribute(
                Attribute.ACCESS_DENIED_MSG,
                getMessage(Message.REQUESTED_RESOURCE, getRequestUrl(request)));

        response.sendRedirect(request.getContextPath() + "/403");
    }

    private String getMessage(String code, Object... args) {
        return messageLocalizer.getLocalizedMessage(code, args);
    }

    private String getRequestUrl(HttpServletRequest req) {
        String requestUri = req.getRequestURI();
        String queryString = req.getQueryString();
        return requestUri + (queryString != null ? "?" + queryString : "");
    }

}
