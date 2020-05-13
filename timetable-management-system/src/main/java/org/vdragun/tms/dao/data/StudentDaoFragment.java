package org.vdragun.tms.dao.data;

import org.vdragun.tms.dao.StudentDao;

/**
 * Redefine methods from {@link StudentDao} interface that should be implemented
 * manually and plugged into {@link SpringDataStudentDao}
 * 
 * @author Vitaliy Dragun
 *
 */
public interface StudentDaoFragment {

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
}
