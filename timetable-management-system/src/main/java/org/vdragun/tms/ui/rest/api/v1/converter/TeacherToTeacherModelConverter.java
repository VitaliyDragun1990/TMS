package org.vdragun.tms.ui.rest.api.v1.converter;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.rest.resource.v1.teacher.SearchTeacherResource;

/**
 * Custom converter to convert {@link Teacher} domain entity into
 * {@link TeacherModel}
 * 
 * @author Vitaliy Dragun
 *
 */
@Component
public class TeacherToTeacherModelConverter implements Converter<Teacher, TeacherModel> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private CourseToCourseModelConverter courseConverter;

    public TeacherToTeacherModelConverter(CourseToCourseModelConverter courseConverter) {
        this.courseConverter = courseConverter;
    }

    @Override
    public TeacherModel convert(Teacher teacher) {
        TeacherModel model = new TeacherModel(
                teacher.getId(),
                teacher.getFirstName(),
                teacher.getLastName(),
                teacher.getTitle().name(),
                DATE_FORMATTER.format(teacher.getDateHired()),
                convertToDTO(teacher.getCourses()));

        model.add(
                linkTo(methodOn(SearchTeacherResource.class).getTeacherById(model.getId())).withSelfRel(),
                linkTo(methodOn(SearchTeacherResource.class).getAllTeachers()).withRel("teachers"));

        return model;
    }

    private List<CourseModel> convertToDTO(List<Course> courses) {
        return courses.stream()
                .map(courseConverter::convert)
                .collect(toList());
    }

}
