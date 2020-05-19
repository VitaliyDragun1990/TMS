package org.vdragun.tms.ui.rest.api.v1.converter;

import static java.util.stream.Collectors.toList;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.rest.api.v1.model.CourseDTO;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherDTO;

/**
 * Custom converter to convert {@link Teacher} domain entity into
 * {@link TeacherDTO}
 * 
 * @author Vitaliy Dragun
 *
 */
@Component
public class TeacherToTeacherDTOConverter implements Converter<Teacher, TeacherDTO> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private CourseToCourseDTOConverter courseConverter;

    public TeacherToTeacherDTOConverter(CourseToCourseDTOConverter courseConverter) {
        this.courseConverter = courseConverter;
    }

    @Override
    public TeacherDTO convert(Teacher teacher) {
        return new TeacherDTO(
                teacher.getId(),
                teacher.getFirstName(),
                teacher.getLastName(),
                teacher.getTitle().name(),
                DATE_FORMATTER.format(teacher.getDateHired()),
                convertToDTO(teacher.getCourses()));
    }

    private List<CourseDTO> convertToDTO(List<Course> courses) {
        return courses.stream()
                .map(courseConverter::convert)
                .collect(toList());
    }

}
