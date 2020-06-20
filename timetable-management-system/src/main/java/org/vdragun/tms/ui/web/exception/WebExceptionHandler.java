package org.vdragun.tms.ui.web.exception;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.Constants.View;
import org.vdragun.tms.util.localizer.MessageLocalizer;

/**
 * Responsible for handling application exceptions in web layer by forwarding to
 * respectful error pages
 * 
 * @author Vitaliy Dragun
 *
 */
@ControllerAdvice(basePackages = {
        "org.vdragun.tms.ui.web.controller",
        "org.vdragun.tms.security.web.controller" })
public class WebExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebExceptionHandler.class);

    @Autowired
    private MessageLocalizer messageLocalizer;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ ResourceNotFoundException.class })
    public String handleNotFoundException(
            ResourceNotFoundException exception,
            Model model,
            HttpServletRequest request) {
        LOG.warn("Handling resource not found exception", exception);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.REQUESTED_RESOURCE, getRequestUrl(request)));

        return View.NOT_FOUND;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ NoHandlerFoundException.class })
    public String handleNoHandlerFoundException(NoHandlerFoundException exception, Model model) {
        LOG.warn("Handling handler not found exception", exception);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.REQUESTED_RESOURCE, exception.getRequestURL()));

        return View.NOT_FOUND;
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

        return View.BAD_REQUEST;
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

        return View.BAD_REQUEST;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ Exception.class })
    public String handleException(Exception exception, Model model, HttpServletRequest request) {
        LOG.error("Handling application exception", exception);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.REQUESTED_RESOURCE, getRequestUrl(request)));

        return View.SERVER_ERROR;
    }

    private String getRequestUrl(HttpServletRequest req) {
        String requestUri = req.getRequestURI();
        String queryString = req.getQueryString();
        return requestUri + (queryString != null ? "?" + queryString : "");
    }

    private String getMessage(String code, Object... args) {
        return messageLocalizer.getLocalizedMessage(code, args);
    }

    private String extractMessage(Exception rootException) {
        return rootException.getMessage().split(":")[1].trim();
    }
}
