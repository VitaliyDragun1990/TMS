package org.vdragun.tms.ui.rest.api.v1.converter;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.core.convert.converter.Converter;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.resource.v1.course.CourseResource;
import org.vdragun.tms.ui.rest.resource.v1.teacher.TeacherResource;

/**
 * Custom converter to convert {@link Course} domain entity into
 * {@link CourseModel}
 * 
 * @author Vitaliy Dragun
 *
 */
public class CourseToCourseModelConverter implements Converter<Course, CourseModel> {

    @Override
    public CourseModel convert(Course course) {
        CourseModel model = new CourseModel(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getCategory().getCode(),
                course.getTeacher().getId(),
                course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName());

        model.add(
                linkTo(methodOn(CourseResource.class).getCourseById(model.getId())).withSelfRel(),
                linkTo(methodOn(TeacherResource.class).getTeacherById(model.getTeacherId())).withRel("teacher"),
                linkTo(methodOn(CourseResource.class).getAllCourses()).withRel("courses"));

        return model;
    }

}