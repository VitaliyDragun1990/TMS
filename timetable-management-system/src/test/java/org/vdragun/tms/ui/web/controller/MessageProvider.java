package org.vdragun.tms.ui.web.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Component that provides messages to facilitate testing
 * 
 * @author Vitaliy Dragun
 *
 */
public class MessageProvider {

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String code, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }

    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.US);
    }
}
