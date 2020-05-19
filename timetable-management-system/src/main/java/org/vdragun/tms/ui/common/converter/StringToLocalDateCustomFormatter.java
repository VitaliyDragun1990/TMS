package org.vdragun.tms.ui.common.converter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;
import org.vdragun.tms.ui.common.util.Constants.Message;

/**
 * Responsible for converting string values into {@link LocalDate} instances and
 * vice versa using different patterns according to user's locale
 * 
 * @author Vitaliy Dragun
 *
 */
public class StringToLocalDateCustomFormatter implements Formatter<LocalDate> {

    private MessageSource messageSource;

    public StringToLocalDateCustomFormatter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return getFormatter(locale).format(object);
    }

    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        return LocalDate.parse(text, getFormatter(locale));
    }

    private DateTimeFormatter getFormatter(Locale locale) {
        String pattern = messageSource.getMessage(Message.DATE_FORMAT, null, locale);
        return DateTimeFormatter.ofPattern(pattern);
    }
}
