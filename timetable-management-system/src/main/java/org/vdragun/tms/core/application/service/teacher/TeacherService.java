package org.vdragun.tms.core.application.service.teacher;

import java.util.List;

import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Teacher;

/**
 * Application entry point to work with {@link Teacher}
 * 
 * @author Vitaliy Dragun
 *
 */
public interface TeacherService {

    /**
     * Registers new teacher using provided data
     * 
     * @param teacherData data to register new teacher
     * @return newly registered teacher instance
     */
    Teacher registerNewTeacher(TeacherData teacherData);

    /**
     * Returns existing teacher instance by its identifier
     * 
     * @param teacherId existing teacher identifier
     * @return teacher with specified identifier
     * @throws ResourceNotFoundException if no teacher with specified identifier
     */
    Teacher findTeacherById(Integer teacherId);

    /**
     * Finds all teachers available
     * 
     * @return list of all available teachers
     */
    List<Teacher> findAllTeachers();

    /**
     * Returns teacher assigned to course with specified identifier
     * 
     * @return teacher assigned to course
     * @throws ResourceNotFoundException if no course with given identifier exists
     */
    Teacher findTeacherForCourse(Integer courseId);
}
