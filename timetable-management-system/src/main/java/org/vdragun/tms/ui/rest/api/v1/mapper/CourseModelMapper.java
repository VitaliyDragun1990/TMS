package org.vdragun.tms.ui.rest.api.v1.mapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.vdragun.tms.util.Constants.Attribute.REQUEST_URI;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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
@Mapper
public interface CourseModelMapper extends RepresentationModelMapper<Course, CourseModel> {

    CourseModelMapper INSTANCE = Mappers.getMapper(CourseModelMapper.class);

    @Mapping(source = "category.code", target = "categoryCode")
    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(
            expression = "java(getFullName(course.getTeacher()))",
            target = "teacherFullName")
    @Mapping(target = "add", ignore = true)
    CourseModel map(Course course);

    default String getFullName(Teacher teacher) {
        return teacher.getFirstName() + " " + teacher.getLastName();
    }

    @AfterMapping
    default void addLinks(@MappingTarget CourseModel model, Course course) {
        model.add(
                linkTo(methodOn(CourseResource.class).getCourseById(model.getId(), REQUEST_URI)).withSelfRel(),
                linkTo(methodOn(TeacherResource.class).getTeacherById(model.getTeacherId(), REQUEST_URI)).withRel("teacher"),
                linkTo(methodOn(CourseResource.class).getAllCourses(REQUEST_URI)).withRel("courses"));
    }
}
