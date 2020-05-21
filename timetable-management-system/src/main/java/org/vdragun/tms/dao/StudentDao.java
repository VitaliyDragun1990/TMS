package org.vdragun.tms.dao;

import java.util.List;
import java.util.Optional;

import org.vdragun.tms.core.domain.Student;

/**
 * Defines CRUD operations to access {@link Student} objects in the persistent
 * storage
 * 
 * @author Vitaliy Dragun
 *
 */
public interface StudentDao {

    /**
     * Saves specified student instance. Saved object receives unique identifier
     */
    void save(Student student);

    /**
     * Saves all specified student instances. Each saved object receives unique
     * identifier.
     */
    void saveAll(Iterable<Student> students);

    /**
     * Returns student with given identifier if any.
     */
    Optional<Student> findById(Integer studentId);

    /**
     * Finds all available students.
     */
    List<Student> findAll();

    /**
     * Finds all students assigned to the course with specified identifier.
     */
    List<Student> findByCourseId(Integer courseId);

    /**
     * Finds all students assigned to the group with specified identifier.
     */
    List<Student> findByGroupId(Integer groupId);

    /**
     * Assigns student with specified student identifier to course with specified
     * course identifier
     */
    void addToCourse(Integer studentId, Integer courseId);

    /**
     * Removes assignment for student to given course if any.
     */
    void removeFromCourse(Integer studentId, Integer courseId);

    /**
     * Assigns student with specified identifier to group with specified identifier
     */
    void addToGroup(Integer studentId, Integer groupId);

    /**
     * Removes assignment for student to current group if any.
     */
    void removeFromGroup(Integer studentId);

    /**
     * Removes student with specified identifier from all assigned courses, if any
     */
    void removeFromAllCourses(Integer studentId);

    /**
     * Checks whether student with provided identifier exists
     * 
     * @return {@code true} if such student exists, {@code false} otherwise
     */
    boolean existsById(Integer studentId);

    /**
     * Deletes student with given identifier, if any
     */
    void deleteById(Integer studentId);

}
