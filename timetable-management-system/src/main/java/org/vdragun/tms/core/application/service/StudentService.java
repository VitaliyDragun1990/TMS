package org.vdragun.tms.core.application.service;

import java.util.List;

import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Student;

/**
 * Application entry point to work with {@link Student}
 * 
 * @author Vitaliy Dragun
 *
 */
public interface StudentService {

    /**
     * Registers new student using provided data
     * 
     * @param studentData data to register new student
     * @return newly registered student
     */
    Student registerNewStudent(StudentData studentData);

    /**
     * Returns existing student instance by its identifier
     * 
     * @param studentId existing student identifier
     * @return student with specified identifier
     * @throws ResourceNotFoundException if no student with specified identifier
     */
    Student findStudentById(Integer studentId);

    /**
     * Finds all students available.
     * 
     * @return list of all available students
     */
    List<Student> findAllStudents();

    /**
     * Finds all students assigned to course with specified identifier.
     * 
     * @param courseId existing course identifier
     * @return list of all students assigned to course
     */
    List<Student> findStudentsForCourse(Integer courseId);

    /**
     * Finds all students assigned to group with specified identifier.
     * 
     * @param groupId existing group identifier
     * @return list of all students assigned to group
     */
    List<Student> findStudentsForGroup(Integer groupId);

    /**
     * Adds student with specified student identifier to group with specified group
     * identifier. If student is assigned to any other group, such assignment will
     * be revoked and student will be assigned group with specified id.
     * 
     * @param studentId existing student identifier
     * @param groupId   existing group identifier
     */
    void addStudentToGroup(Integer studentId, Integer groupId);

    /**
     * Removes student with specified identifier from currently assigned group, if
     * any
     * 
     * @param studentId existing student identifier
     */
    void removeStudentFromGroup(Integer studentId);

    /**
     * Adds student with specified student identifier to courses with specified
     * course identifiers. Before adding student to specified courses any previously
     * added courses, if any, will be removed from student.
     * 
     * @param studentId existing student identifier
     * @param courseIds list of existing course identifiers
     */
    void setStudentCourses(Integer studentId, List<Integer> courseIds);

    /**
     * Removes student from any courses, if any.
     * 
     * @param studentId existing student identifier
     */
    void removeStudentFromAllCourses(Integer studentId);
}
