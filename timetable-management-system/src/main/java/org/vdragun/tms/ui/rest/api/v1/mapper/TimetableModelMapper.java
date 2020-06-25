package org.vdragun.tms.ui.rest.api.v1.mapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.ui.rest.resource.v1.course.CourseResource;
import org.vdragun.tms.ui.rest.resource.v1.teacher.TeacherResource;
import org.vdragun.tms.ui.rest.resource.v1.timetable.TimetableResource;

/**
 * @author Vitaliy Dragun
 *
 */
@Mapper(
        uses = CourseModelMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TimetableModelMapper extends RepresentationModelMapper<Timetable, TimetableModel> {

    TimetableModelMapper INSTANCE = Mappers.getMapper(TimetableModelMapper.class);

    @Mapping(source = "classroom.id", target = "classroomId")
    @Mapping(source = "classroom.capacity", target = "classroomCapacity")
    @Mapping(source = "durationInMinutes", target = "duration")
    @Mapping(source = "startTime", target = "startTime", dateFormat = "yyyy-MM-dd HH:mm")
    TimetableModel map(Timetable timetable);

    @AfterMapping
    default void addLinks(@MappingTarget TimetableModel model, Timetable timetable) {
        model.add(
                linkTo(methodOn(TimetableResource.class).getTimetableById(model.getId())).withSelfRel(),
                linkTo(methodOn(CourseResource.class).getCourseById(
                        timetable.getCourse().getId())).withRel("course"),
                linkTo(methodOn(TeacherResource.class).getTeacherById(
                        timetable.getCourse().getTeacher().getId()))
                    .withRel("teacher"),
                linkTo(methodOn(TimetableResource.class).getAllTimetables()).withRel("timetables"));
    }
}
