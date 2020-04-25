package org.vdragun.tms.dao;

import java.util.List;
import java.util.Optional;

import org.vdragun.tms.core.domain.Teacher;

/**
 * Defines CRUD operations to access {@link Teacher} objects in the persistent
 * storage
 * 
 * @author Vitaliy Dragun
 *
 */
public interface TeacherDao {

    /**
     * Saves specified teacher instance. Saved object receives unique identifier
     */
    void save(Teacher teachers);

    /**
     * Saves all specified teacher instances. Each saved object receives unique
     * identifier.
     */
    void saveAll(List<Teacher> teachers);

    /**
     * Returns teacher with given identifier if any.
     */
    Optional<Teacher> findById(Integer teacherId);

    /**
     * Finds all available teachers.
     */
    List<Teacher> findAll();

    /**
     * Finds teacher assigned to the course with given identifier if any.
     */
    Optional<Teacher> findForCourse(Integer courseId);

}
