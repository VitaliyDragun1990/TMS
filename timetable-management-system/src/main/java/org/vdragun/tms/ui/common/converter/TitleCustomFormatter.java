package org.vdragun.tms.ui.common.converter;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;
import org.vdragun.tms.core.domain.Title;

/**
 * Responsible for converting string values into {@link Title} instances and
 * vice versa using different patterns according to user's locale
 * 
 * @author Vitaliy Dragun
 *
 */
public class TitleCustomFormatter implements Formatter<Title> {

    @Override
    public String print(Title title, Locale locale) {
        if (title != null) {
            return title.name();
        }
        return "";
    }

    @Override
    public Title parse(String text, Locale locale) throws ParseException {
        if (text != null) {
            return Title.valueOf(text.trim().toUpperCase());
        }
        return null;
    }

}
