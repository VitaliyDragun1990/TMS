package org.vdragun.tms.ui.common.converter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;
import org.vdragun.tms.ui.common.util.Constants.Message;

/**
 * Responsible for converting string values into {@link LocalDateTime} instances
 * and vice versa using different patterns according to user's locale
 * 
 * @author Vitaliy Dragun
 *
 */
public class LocalDateTimeCustomFormatter implements Formatter<LocalDateTime> {

    private MessageSource messageSource;

    public LocalDateTimeCustomFormatter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        return getFormatter(locale).format(object);
    }

    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        return LocalDateTime.parse(text, getFormatter(locale));
    }

    private DateTimeFormatter getFormatter(Locale locale) {
        String pattern = messageSource.getMessage(Message.DATE_TIME_FORMAT, null, locale);
        return DateTimeFormatter.ofPattern(pattern);
    }

}
