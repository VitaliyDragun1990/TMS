package org.vdragun.tms.core.application.service.student;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Student registerNewStudent(CreateStudentData studentData);

    /**
     * Updates existing student using provided data
     * 
     * @param studentData data to update existing student
     * @return update student
     * @throws ResourceNotFoundException if student and/or any specified resource
     *                                   intended for update is not found
     */
    Student updateExistingStudent(UpdateStudentData studentData);

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
     * Finds students according to provided pageable data.
     * 
     * @return page with students
     */
    Page<Student> findStudents(Pageable pageable);

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
     * Deletes student with specified identifier
     * 
     * @param studentId student identifier
     * @throws ResourceNotFoundException if no student with specified identifier
     */
    void deleteStudentById(Integer studentId);
}
