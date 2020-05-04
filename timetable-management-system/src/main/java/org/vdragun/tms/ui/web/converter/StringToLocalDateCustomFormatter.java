package org.vdragun.tms.ui.web.converter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;

/**
 * Responsible for converting string values into {@link LocalDate} instances and
 * vice versa using different patterns depending on current user's locale
 * 
 * @author Vitaliy Dragun
 *
 */
public class StringToLocalDateCustomFormatter implements Formatter<LocalDate> {
    private static final String DATE_PATTERN = "format.date";

    private MessageSource messageSource;

    public StringToLocalDateCustomFormatter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        String pattern = messageSource.getMessage(DATE_PATTERN, null, locale);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(object);
    }

    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        String pattern = messageSource.getMessage(DATE_PATTERN, null, locale);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(text, formatter);
    }

}
