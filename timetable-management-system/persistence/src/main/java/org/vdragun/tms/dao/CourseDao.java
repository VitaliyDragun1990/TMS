package org.vdragun.tms.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vdragun.tms.core.domain.Course;

/**
 * Defines CRUD operations to access {@link Course} objects in the persistent
 * storage
 * 
 * @author Vitaliy Dragun
 *
 */
public interface CourseDao {

    /**
     * Saves specified course instance. Saved object receives unique identifier
     */
    void save(Course course);

    /**
     * Saves all specified course instances. Each saved object receives unique
     * identifier.
     */
    void saveAll(Iterable<Course> courses);

    /**
     * Returns course with specified identifier if any.
     */
    Optional<Course> findById(Integer courseId);

    /**
     * Finds all available courses
     */
    List<Course> findAll();

    /**
     * Finds all available courses for given pageable data
     */
    Page<Course> findAll(Pageable pageable);

    /**
     * Finds all courses belonging to category with specified identifier
     */
    List<Course> findByCategoryId(Integer categoryId);

    /**
     * Checks whether course with specified identifier exists
     * 
     * @return {@code true} if such course exists, {@code false} otherwise
     */
    boolean existsById(Integer courseId);

}
