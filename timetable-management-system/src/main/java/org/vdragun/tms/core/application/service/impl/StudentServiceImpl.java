package org.vdragun.tms.core.application.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.StudentData;
import org.vdragun.tms.core.application.service.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.dao.GroupDao;
import org.vdragun.tms.dao.StudentDao;

/**
 * Default implementation of {@link StudentService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
public class StudentServiceImpl implements StudentService {

    private StudentDao studentDao;
    private GroupDao groupDao;
    private CourseDao courseDao;

    public StudentServiceImpl(StudentDao studentDao, GroupDao groupDao, CourseDao courseDao) {
        this.studentDao = studentDao;
        this.groupDao = groupDao;
        this.courseDao = courseDao;
    }

    @Override
    public Student registerNewStudent(StudentData studentData) {
        Student student = new Student(
                studentData.getFirstName(),
                studentData.getLastName(),
                studentData.getEnrollmentDate());
        studentDao.save(student);
        return student;
    }

    @Override
    public Student findStudentById(Integer studentId) {
        return studentDao.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with id=%d not found", studentId));
    }

    @Override
    public List<Student> findAllStudents() {
        return studentDao.findAll();
    }

    @Override
    public List<Student> findStudentsForCourse(Integer courseId) {
        assertCourseExists(courseId, "Fail to find students for course");

        return studentDao.findForCourse(courseId);
    }

    @Override
    public List<Student> findStudentsForGroup(Integer groupId) {
        assertGroupExists(groupId, "Fail to find students for group");

        return studentDao.findForGroup(groupId);
    }

    @Override
    public void addStudentToGroup(Integer studentId, Integer groupId) {
        assertGroupExists(groupId, "Fail to add student to group");
        assertStudentExists(studentId, "Fail to add student to group");

        studentDao.addToGroup(studentId, groupId);
    }

    @Override
    public void removeStudentFromGroup(Integer studentId) {
        assertStudentExists(studentId, "Fail to remove student from group");

        studentDao.removeFromGroup(studentId);
    }

    @Override
    public void setStudentCourses(Integer studentId, List<Integer> courseIds) {
        assertStudentExists(studentId, "Fail to add student to courses");
        courseIds.forEach(courseId -> assertCourseExists(courseId, "Fail add student to course"));

        studentDao.removeFromAllCourses(studentId);
        courseIds.forEach(courseId -> studentDao.addToCourse(studentId, courseId));
    }

    @Override
    public void removeStudentFromAllCourses(Integer studentId) {
        assertStudentExists(studentId, "Fail to remove student from courses");

        studentDao.removeFromAllCourses(studentId);
    }

    private void assertCourseExists(Integer courseId, String msg) {
        if (!courseDao.existsById(courseId)) {
            throw new ResourceNotFoundException("%s: course with id=%d does not exist", msg, courseId);
        }
    }

    private void assertGroupExists(Integer groupId, String msg) {
        if (!groupDao.existsById(groupId)) {
            throw new ResourceNotFoundException("%s: group with id=%d does not exist", msg, groupId);
        }
    }

    private void assertStudentExists(Integer studentId, String msg) {
        if (!studentDao.existsById(studentId)) {
            throw new ResourceNotFoundException("%s: student with id=%d does not exist", msg, studentId);
        }
    }

}
