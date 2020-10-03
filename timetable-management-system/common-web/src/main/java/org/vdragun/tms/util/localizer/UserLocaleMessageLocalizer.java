package org.vdragun.tms.util.localizer;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * Default implementation of {@link MessageLocalizer} which uses current user's
 * locale to provide localized message
 * 
 * @author Vitaliy Dragun
 *
 */
public class UserLocaleMessageLocalizer implements MessageLocalizer {

    private final MessageSource messageSource;

    public UserLocaleMessageLocalizer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getLocalizedMessage(String messageCode, Object... args) {
        return messageSource.getMessage(messageCode, args, getLocale());
    }

    private Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

}
