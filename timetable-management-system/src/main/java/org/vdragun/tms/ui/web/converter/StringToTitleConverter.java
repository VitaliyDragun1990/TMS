package org.vdragun.tms.ui.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.vdragun.tms.core.domain.Title;

/**
 * Responsible for converting string values into {@link Title} instances
 * 
 * @author Vitaliy Dragun
 *
 */
public class StringToTitleConverter implements Converter<String, Title> {

    @Override
    public Title convert(String source) {
        return Title.fromString(source.trim());
    }

}
