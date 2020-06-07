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
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.translator.Translator;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom implementation of {@link AccessDeniedHandler} for handling
 * {@link AccessDeniedException}
 * 
 * @author Vitaliy Dragun
 *
 */
@Component
public class AccessDeniedExceptionHandler implements AccessDeniedHandler {

    private ObjectMapper mapper;
    private Translator translator;

    public AccessDeniedExceptionHandler(ObjectMapper mapper, Translator translator) {
        this.mapper = mapper;
        this.translator = translator;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex) throws IOException, ServletException {
        String errorMsg = translator.getLocalizedMessage(Message.ACCESS_DENIED);
        ApiError error = new ApiError(FORBIDDEN);
        error.setMessage(errorMsg);

        response.setContentType("application/json");
        response.setStatus(SC_FORBIDDEN);

        ServletOutputStream out = response.getOutputStream();
        mapper.writeValue(out, error);
        out.flush();
    }

}
