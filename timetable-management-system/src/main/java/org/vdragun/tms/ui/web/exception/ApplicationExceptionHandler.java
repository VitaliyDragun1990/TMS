package org.vdragun.tms.ui.web.exception;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.ui.web.util.Constants.Message;

/**
 * Responsible for handling application exceptions by forwarding to respectful
 * error pages
 * 
 * @author Vitaliy Dragun
 *
 */
@ControllerAdvice
public class ApplicationExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ ResourceNotFoundException.class })
    public String handleNotFoundException(
            ResourceNotFoundException exception,
            Model model,
            HttpServletRequest request) {

        LOG.warn("Handling resource not found exception", exception);
        model.addAttribute("msg", getMessage(Message.REQUESTED_RESOURCE, getRequestUrl(request)));

        return "404";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ NoHandlerFoundException.class })
    public String handleNoHandlerFoundException(NoHandlerFoundException exception, Model model) {
        LOG.warn("Handling handler not found exception", exception);
        model.addAttribute("msg", getMessage(Message.REQUESTED_RESOURCE, exception.getRequestURL()));

        return "404";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public String handlemethodArgumentMismatchException(
            MethodArgumentTypeMismatchException exception,
            Model model,
            HttpServletRequest request) {

        if (exception.getRootCause() instanceof NumberFormatException) {
            Exception rootException = (Exception) exception.getRootCause();
            LOG.warn("Handling number format exception", rootException);
            String errMsg = rootException.getMessage().split(":")[1].trim();
            model.addAttribute("error", errMsg);
            model.addAttribute("msg", getMessage(Message.REQUESTED_RESOURCE, getRequestUrl(request)));

            return "400";
        } else {
            return handleException(exception, model, request);
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ Exception.class })
    public String handleException(Exception exception, Model model, HttpServletRequest request) {
        LOG.error("Handling application exception", exception);
        model.addAttribute("msg", getMessage(Message.REQUESTED_RESOURCE, getRequestUrl(request)));

        return "500";
    }

    private String getRequestUrl(HttpServletRequest req) {
        String requestUri = req.getRequestURI();
        String queryString = req.getQueryString();
        return requestUri + (queryString != null ? "?" + queryString : "");
    }

    private String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }
}
