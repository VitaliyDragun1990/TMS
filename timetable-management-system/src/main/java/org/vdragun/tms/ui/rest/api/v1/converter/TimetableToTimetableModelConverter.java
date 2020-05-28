package org.vdragun.tms.ui.rest.api.v1.converter;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.core.convert.converter.Converter;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.common.util.Translator;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.ui.rest.resource.v1.timetable.TimetableResource;

/**
 * Custom converter to convert {@link Timetable} domain entity into
 * {@link TimetableModel}
 * 
 * @author Vitaliy Dragun
 *
 */
public class TimetableToTimetableModelConverter implements Converter<Timetable, TimetableModel> {

    private CourseToCourseModelConverter courseConverter;
    private Translator translator;

    public TimetableToTimetableModelConverter(CourseToCourseModelConverter courseConverter, Translator translator) {
        this.courseConverter = courseConverter;
        this.translator = translator;
    }

    @Override
    public TimetableModel convert(Timetable timetable) {
        TimetableModel model = new TimetableModel(
                timetable.getId(),
                translator.formatDateTimeDefault(timetable.getStartTime()),
                timetable.getDurationInMinutes(),
                courseConverter.convert(timetable.getCourse()),
                timetable.getClassroom().getId(),
                timetable.getClassroom().getCapacity());

        model.add(
                linkTo(methodOn(TimetableResource.class).getTimetableById(model.getId())).withSelfRel(),
                linkTo(methodOn(TimetableResource.class).getAllTimetables()).withRel("timetables"));

        return model;
    }

}
