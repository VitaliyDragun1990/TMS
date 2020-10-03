package org.vdragun.tms.ui.rest.api.v1.mapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.resource.v1.course.CourseResource;
import org.vdragun.tms.ui.rest.resource.v1.teacher.TeacherResource;

/**
 * @author Vitaliy Dragun
 *
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseModelMapper extends RepresentationModelMapper<Course, CourseModel> {

    CourseModelMapper INSTANCE = Mappers.getMapper(CourseModelMapper.class);

    @Mapping(source = "category.code", target = "categoryCode")
    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(
            expression = "java(getFullName(course.getTeacher()))",
            target = "teacherFullName")
    CourseModel map(Course course);

    default String getFullName(Teacher teacher) {
        return teacher.getFirstName() + " " + teacher.getLastName();
    }

    @AfterMapping
    default void addLinks(@MappingTarget CourseModel model, Course course) {
        model.add(
                linkTo(methodOn(CourseResource.class).getCourseById(model.getId())).withSelfRel(),
                linkTo(methodOn(TeacherResource.class).getTeacherById(model.getTeacherId())).withRel("teacher"),
                linkTo(CourseResource.class).withRel("courses"),
                linkTo(CourseResource.class).slash("/all").withRel("allCourses")
                );
    }
}
