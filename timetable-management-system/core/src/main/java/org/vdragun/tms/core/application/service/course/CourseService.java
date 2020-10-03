package org.vdragun.tms.core.application.service.course;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Course;

/**
 * Application entry point to work with {@link Course}
 * 
 * @author Vitaliy Dragun
 *
 */
public interface CourseService {

    /**
     * Registers new course using provided data
     * 
     * @param courseData data to register new course
     * @return newly registered course
     */
    Course registerNewCourse(CourseData courseData);

    /**
     * Returns existing course using provided identifier
     * 
     * @param courseId existing course identifier
     * @return course with specified identifier
     * @throws ResourceNotFoundException if no course with provided identifier
     */
    Course findCourseById(Integer courseId);

    /**
     * Finds all courses available
     * 
     * @return list of all available courses
     */
    List<Course> findAllCourses();

    /**
     * Finds courses according to provided pageable data
     * 
     * @return page with courses
     */
    Page<Course> findCourses(Pageable pageable);

    /**
     * Finds all courses belonging to category with provided identifier
     * 
     * @return list of all courses belonging to category
     */
    List<Course> findCoursesByCategory(Integer categoryId);

}
