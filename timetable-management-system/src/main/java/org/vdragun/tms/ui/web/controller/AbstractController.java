package org.vdragun.tms.ui.web.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vdragun.tms.ui.web.util.Constants.Message;

/**
 * Parent class with common functionality for all application controllers.
 * 
 * @author Vitaliy Dragun
 *
 */
public abstract class AbstractController {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageSource messageSource;

    protected String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, getLocale());
    }

    protected String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getMessage(Message.DATE_FORMAT));
        return date.format(formatter);
    }

    protected String formatMonth(Month month) {
        return month.getDisplayName(TextStyle.FULL, Locale.US);
    }

    protected Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    protected String getRequestUri() {
        ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
        return uriBuilder.toUriString();
    }

    protected String redirectTo(String targetPage) {
        return "redirect:/" + targetPage;
    }

}
