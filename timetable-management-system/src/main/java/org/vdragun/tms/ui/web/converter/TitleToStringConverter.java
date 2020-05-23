package org.vdragun.tms.ui.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.vdragun.tms.core.domain.Title;

/**
 * Responsible for converting {@link Title} values into string representation
 * 
 * @author Vitaliy Dragun
 *
 */
public class TitleToStringConverter implements Converter<Title, String> {

    @Override
    public String convert(Title title) {
        if (title != null) {
            return title.name();
        }
        return "";
    }

}
