package org.vdragun.tms.ui.rest.api.v1.converter;

import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableDTO;

/**
 * Custom converter to convert {@link Timetable} domain entity into
 * {@link TimetableDTO}
 * 
 * @author Vitaliy Dragun
 *
 */
@Component
public class TimetableToTimetableDTOConverter implements Converter<Timetable, TimetableDTO> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private CourseToCourseDTOConverter courseConverter;

    public TimetableToTimetableDTOConverter(CourseToCourseDTOConverter courseConverter) {
        this.courseConverter = courseConverter;
    }

    @Override
    public TimetableDTO convert(Timetable timetable) {
        return new TimetableDTO(
                timetable.getId(),
                DATE_FORMATTER.format(timetable.getStartTime()),
                timetable.getDurationInMinutes(),
                courseConverter.convert(timetable.getCourse()),
                timetable.getClassroom().getId(),
                timetable.getClassroom().getCapacity());
    }

}
