package org.vdragun.tms.ui.rest.api.v1.converter;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.converter.Converter;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.common.converter.LocalDateTimeCustomFormatter;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.ui.rest.resource.v1.timetable.SearchTimetableResource;

/**
 * Custom converter to convert {@link Timetable} domain entity into
 * {@link TimetableModel}
 * 
 * @author Vitaliy Dragun
 *
 */
public class TimetableToTimetableModelConverter implements Converter<Timetable, TimetableModel> {

    private CourseToCourseModelConverter courseConverter;
    private LocalDateTimeCustomFormatter dateTimeFormatter;

    public TimetableToTimetableModelConverter(
            CourseToCourseModelConverter courseConverter,
            LocalDateTimeCustomFormatter dateTimeFormatter) {
        this.courseConverter = courseConverter;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public TimetableModel convert(Timetable timetable) {
        TimetableModel model = new TimetableModel(
                timetable.getId(),
                dateTimeFormatter.print(timetable.getStartTime(), LocaleContextHolder.getLocale()),
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
