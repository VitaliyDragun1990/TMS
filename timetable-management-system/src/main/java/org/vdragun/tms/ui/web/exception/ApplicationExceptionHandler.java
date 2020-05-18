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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * Responsible for handling application exceptions by forwarding to respectful
 * error pages
 * 
 * @author Vitaliy Dragun
 *
 */
@ControllerAdvice(basePackages = "org.vdragun.tms.ui.web.controller")
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
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.REQUESTED_RESOURCE, getRequestUrl(request)));

        return Page.NOT_FOUND;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ NoHandlerFoundException.class })
    public String handleNoHandlerFoundException(NoHandlerFoundException exception, Model model) {
        LOG.warn("Handling handler not found exception", exception);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.REQUESTED_RESOURCE, exception.getRequestURL()));

        return Page.NOT_FOUND;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public String handleMethodArgumentMismatchException(
            MethodArgumentTypeMismatchException exception,
            Model model,
            HttpServletRequest request) {
        LOG.warn("Handling method argument type mismatch exception", exception.getRootCause());

        String errMsg = "";
        if (exception.getRootCause() instanceof NumberFormatException) {
            errMsg = extractMessage((Exception) exception.getRootCause());
        }
        model.addAttribute(Attribute.ERROR, errMsg);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.REQUESTED_RESOURCE, getRequestUrl(request)));

        return Page.BAD_REQUEST;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ MissingServletRequestParameterException.class })
    public String handleMissingRequestParameterException(
            MissingServletRequestParameterException exception,
            Model model,
            HttpServletRequest request) {
        LOG.warn("Handling missing request parameter exception", exception);

        String errMsg = getMessage(Message.REQUIRED_REQUEST_PARAMETER, exception.getParameterName());
        model.addAttribute(Attribute.ERROR, errMsg);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.REQUESTED_RESOURCE, getRequestUrl(request)));

        return Page.BAD_REQUEST;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ Exception.class })
    public String handleException(Exception exception, Model model, HttpServletRequest request) {
        LOG.error("Handling application exception", exception);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.REQUESTED_RESOURCE, getRequestUrl(request)));

        return Page.SERVER_ERROR;
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

    private String extractMessage(Exception rootException) {
        return rootException.getMessage().split(":")[1].trim();
    }
}
