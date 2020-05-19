package org.vdragun.tms.ui.rest.api.v1.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.rest.api.v1.model.CourseDTO;

/**
 * Custom converter to convert {@link Course} domain entity into
 * {@link CourseDTO}
 * 
 * @author Vitaliy Dragun
 *
 */
@Component
public class CourseToCourseDTOConverter implements Converter<Course, CourseDTO> {

    @Override
    public CourseDTO convert(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getCategory().getCode(),
                course.getTeacher().getId(),
                course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName());
    }

}
