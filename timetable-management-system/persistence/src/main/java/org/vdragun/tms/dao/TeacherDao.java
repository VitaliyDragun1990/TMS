package org.vdragun.tms.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    void save(Teacher teacher);

    /**
     * Saves all specified teacher instances. Each saved object receives unique
     * identifier.
     */
    void saveAll(Iterable<Teacher> teachers);

    /**
     * Returns teacher with given identifier if any.
     */
    Optional<Teacher> findById(Integer teacherId);

    /**
     * Finds all available teachers.
     */
    List<Teacher> findAll();

    /**
     * Finds all available teachers for given pageable data
     */
    Page<Teacher> findAll(Pageable pageable);

    /**
     * Finds teacher assigned to the course with given identifier if any.
     */
    Optional<Teacher> findForCourseWithId(Integer courseId);

    /**
     * Checks whether teacher with specified identifier exists
     * 
     * @return {@code true} if such teacher exists, {@code false} otherwise
     */
    boolean existsById(Integer teacherId);

}
