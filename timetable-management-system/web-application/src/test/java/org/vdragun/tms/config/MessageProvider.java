package org.vdragun.tms.config;

import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Component that provides messages to facilitate testing
 * 
 * @author Vitaliy Dragun
 *
 */
public class MessageProvider {

    private final MessageSource messageSource;

    public MessageProvider(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }

    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.US);
    }
}
