package org.vdragun.tms.util.localizer;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.vdragun.tms.config.Constants.Message;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Default implementation of {@link TemporalLocalizer} which uses current user's
 * locale to provide string representation for specified temporal value
 * 
 * @author Vitaliy Dragun
 *
 */
public class UserLocaleTemporalLocalizer implements TemporalLocalizer {

    private final MessageSource messageSource;

    public UserLocaleTemporalLocalizer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String localizeDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getMessage(Message.DATE_FORMAT));
        return date.format(formatter);
    }

    @Override
    public String localizeDateDefault(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getMessage(Message.DATE_FORMAT_DEFAULT));
        return date.format(formatter);
    }

    @Override
    public String localizeDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getMessage(Message.DATE_TIME_FORMAT));
        return dateTime.format(formatter);
    }

    @Override
    public String localizeDateTimeDefault(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getMessage(Message.DATE_TIME_FORMAT_DEFAULT));
        return dateTime.format(formatter);
    }

    @Override
    public String localizeTimestamp(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getMessage(Message.TIMESTAMP_FORMAT));
        return timestamp.format(formatter);
    }

    @Override
    public String localizeTimestampDefault(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getMessage(Message.TIMESTAMP_FORMAT_DEFAULT));
        return timestamp.format(formatter);
    }

    @Override
    public String localizeMonth(Month month) {
        return month.getDisplayName(TextStyle.FULL, Locale.US);
    }

    private String getMessage(String messageCode, Object... args) {
        return messageSource.getMessage(messageCode, args, getLocale());
    }

    private Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

}
