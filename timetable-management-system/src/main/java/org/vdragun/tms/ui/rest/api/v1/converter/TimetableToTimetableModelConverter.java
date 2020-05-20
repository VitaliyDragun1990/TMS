package org.vdragun.tms.ui.rest.api.v1.converter;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.ui.rest.resource.v1.timetable.SearchTimetableResource;

/**
 * Custom converter to convert {@link Timetable} domain entity into
 * {@link TimetableModel}
 * 
 * @author Vitaliy Dragun
 *
 */
@Component
public class TimetableToTimetableModelConverter implements Converter<Timetable, TimetableModel> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private CourseToCourseModelConverter courseConverter;

    public TimetableToTimetableModelConverter(CourseToCourseModelConverter courseConverter) {
        this.courseConverter = courseConverter;
    }

    @Override
    public TimetableModel convert(Timetable timetable) {
        TimetableModel model = new TimetableModel(
                timetable.getId(),
                DATE_FORMATTER.format(timetable.getStartTime()),
                timetable.getDurationInMinutes(),
                courseConverter.convert(timetable.getCourse()),
                timetable.getClassroom().getId(),
                timetable.getClassroom().getCapacity());

        model.add(
                linkTo(methodOn(SearchTimetableResource.class).getTimetableById(model.getId())).withSelfRel(),
                linkTo(methodOn(SearchTimetableResource.class).getAllTimetables()).withRel("timetables"));

        return model;
    }

}
