package org.vdragun.tms.ui.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.vdragun.tms.ui.common.util.Constants.Message;

/**
 * Default implementation of the {@link Translator}
 * 
 * @author Vitaliy Dragun
 *
 */
public class DefaultTranslator implements Translator {

    private MessageSource messageSource;

    public DefaultTranslator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getLocalizedMessage(String messageCode, Object... args) {
        return messageSource.getMessage(messageCode, args, getLocale());
    }

    @Override
    public String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getLocalizedMessage(Message.DATE_FORMAT));
        return date.format(formatter);
    }

    @Override
    public String formatDateDefault(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getLocalizedMessage(Message.DATE_FORMAT_DEFAULT));
        return date.format(formatter);
    }

    @Override
    public String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getLocalizedMessage(Message.DATE_TIME_FORMAT));
        return dateTime.format(formatter);
    }
    
    @Override
    public String formatDateTimeDefault(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                getLocalizedMessage(Message.DATE_TIME_FORMAT_DEFAULT));
        return dateTime.format(formatter);
    }

    @Override
    public String formatTimestamp(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getLocalizedMessage(Message.TIMESTAMP_FORMAT));
        return timestamp.format(formatter);
    }

    @Override
    public String formatTimestampDefault(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                getLocalizedMessage(Message.TIMESTAMP_FORMAT_DEFAULT));
        return timestamp.format(formatter);
    }

    @Override
    public String formatMonth(Month month) {
        return month.getDisplayName(TextStyle.FULL, Locale.US);
    }

    private Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

}
