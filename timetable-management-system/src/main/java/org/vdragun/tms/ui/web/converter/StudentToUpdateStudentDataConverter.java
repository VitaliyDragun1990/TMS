package org.vdragun.tms.ui.web.converter;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.vdragun.tms.core.application.service.UpdateStudentData;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;

/**
 * Converter to convert {@link Student} domain entity into
 * {@link UpdateStudentData} use case input model
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentToUpdateStudentDataConverter implements Converter<Student, UpdateStudentData> {

    @Override
    public UpdateStudentData convert(Student student) {
        if (student != null) {
            return new UpdateStudentData(
                    student.getId(),
                    getGroupId(student.getGroup()),
                    student.getFirstName(),
                    student.getLastName(),
                    getCourseIds(student.getCourses()));
        }
        return null;
    }

    private List<Integer> getCourseIds(Set<Course> courses) {
        return courses.stream().map(Course::getId).collect(toList());
    }

    private Integer getGroupId(Group group) {
        return group != null ? group.getId() : null;
    }

}
