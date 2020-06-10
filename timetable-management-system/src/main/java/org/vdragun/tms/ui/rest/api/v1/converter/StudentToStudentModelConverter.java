package org.vdragun.tms.ui.rest.api.v1.converter;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.rest.resource.v1.student.StudentResource;
import org.vdragun.tms.util.localizer.TemporalLocalizer;

/**
 * Custom converter to convert {@link Student} domain entity into
 * {@link StudentModel}
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentToStudentModelConverter implements Converter<Student, StudentModel> {

    private CourseToCourseModelConverter courseConverter;
    private TemporalLocalizer temporalLocalizer;

    public StudentToStudentModelConverter(CourseToCourseModelConverter courseConverter,
            TemporalLocalizer temporalLocalizer) {
        this.courseConverter = courseConverter;
        this.temporalLocalizer = temporalLocalizer;
    }

    @Override
    public StudentModel convert(Student student) {
        StudentModel model = new StudentModel(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getGroup() != null ? student.getGroup().getName() : null,
                temporalLocalizer.localizeDateDefault(student
                        .getEnrollmentDate()),
                convertToDTO(student.getCourses()));

        model.add(
                linkTo(methodOn(StudentResource.class).getStudentById(model.getId())).withSelfRel(),
                linkTo(methodOn(StudentResource.class).getAllStudents()).withRel("students"));

        return model;
    }

    private List<CourseModel> convertToDTO(Collection<Course> courses) {
        return courses.stream()
                .map(courseConverter::convert)
                .collect(toList());
    }

}
