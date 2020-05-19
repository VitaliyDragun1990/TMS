package org.vdragun.tms.ui.rest.api.v1.converter;

import static java.util.stream.Collectors.toList;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.api.v1.model.CourseDTO;
import org.vdragun.tms.ui.rest.api.v1.model.StudentDTO;

/**
 * Custom converter to convert {@link Student} domain entity into
 * {@link StudentDTO}
 * 
 * @author Vitaliy Dragun
 *
 */
@Component
public class StudentToStudentDTOConverter implements Converter<Student, StudentDTO> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private CourseToCourseDTOConverter courseConverter;

    public StudentToStudentDTOConverter(CourseToCourseDTOConverter courseConverter) {
        this.courseConverter = courseConverter;
    }

    @Override
    public StudentDTO convert(Student student) {
        return new StudentDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getGroup() != null ? student.getGroup().getName() : null,
                DATE_FORMATTER.format(student.getEnrollmentDate()),
                convertToDTO(student.getCourses()));
    }

    private List<CourseDTO> convertToDTO(Collection<Course> courses) {
        return courses.stream()
                .map(courseConverter::convert)
                .collect(toList());
    }

}
