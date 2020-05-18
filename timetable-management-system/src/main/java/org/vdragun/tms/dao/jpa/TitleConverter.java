package org.vdragun.tms.dao.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.vdragun.tms.core.domain.Title;

/**
 * Custom JPA attribute converter to convert {@link Title} into string
 * representation for database persistence
 * 
 * @author Vitaliy Dragun
 *
 */
@Converter(autoApply = true)
public class TitleConverter implements AttributeConverter<Title, String> {

    @Override
    public String convertToDatabaseColumn(Title title) {
        return title.asString();
    }

    @Override
    public Title convertToEntityAttribute(String dbData) {
        return Title.fromString(dbData);
    }

}
