package org.vdragun.tms.ui.rest.exception;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.boot.jackson.JsonComponent;
import org.vdragun.tms.ui.common.util.Translator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom Jackson JSON serializer to serializer {@link LocalDateTime} value as
 * locale-dependent time-stamp
 * 
 * @author Vitaliy Dragun
 *
 */
@JsonComponent
public class CustomTimestampSerializer extends JsonSerializer<LocalDateTime> {

    private Translator translator;

    public CustomTimestampSerializer(Translator translator) {
        this.translator = translator;
    }

    @Override
    public void serialize(LocalDateTime timestamp, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeString(translator.formatTimestamp(timestamp));

    }

}
