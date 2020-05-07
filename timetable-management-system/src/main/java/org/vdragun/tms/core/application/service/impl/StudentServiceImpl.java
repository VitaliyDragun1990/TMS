package org.vdragun.tms.core.application.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(StudentServiceImpl.class);

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
        LOG.debug("Registering new student using data: {}", studentData);

        Student student = new Student(
                studentData.getFirstName(),
                studentData.getLastName(),
                studentData.getEnrollmentDate());
        studentDao.save(student);

        LOG.debug("New student has been registered: {}", student);
        return student;
    }

    @Override
    public Student findStudentById(Integer studentId) {
        LOG.debug("Searching for student with id={}", studentId);

        return studentDao.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with id=%d not found", studentId));
    }

    @Override
    public List<Student> findAllStudents() {
        LOG.debug("Retrieving all students");

        List<Student> result = studentDao.findAll();

        LOG.debug("Found {} students", result.size());
        return result;
    }

    @Override
    public List<Student> findStudentsForCourse(Integer courseId) {
        assertCourseExists(courseId, "Fail to find students for course");

        return studentDao.findForCourse(courseId);
    }

    @Override
    public List<Student> findStudentsForGroup(Integer groupId) {
        LOG.debug("Searching for students assigned to group with id={}", groupId);
        assertGroupExists(groupId, "Fail to find students for group");

        List<Student> result = studentDao.findForGroup(groupId);

        LOG.debug("Found {} students assigned to group with id={}", result.size(), groupId);
        return result;
    }

    @Override
    public void addStudentToGroup(Integer studentId, Integer groupId) {
        LOG.debug("Adding student with id={} to group with id={}", studentId, groupId);
        assertGroupExists(groupId, "Fail to add student to group");
        assertStudentExists(studentId, "Fail to add student to group");

        studentDao.addToGroup(studentId, groupId);

        LOG.debug("Student with id={} has been added to group with id={}", studentId, groupId);
    }

    @Override
    public void removeStudentFromGroup(Integer studentId) {
        LOG.debug("Removing student wiht id={} from current group", studentId);
        assertStudentExists(studentId, "Fail to remove student from group");

        studentDao.removeFromGroup(studentId);

        LOG.debug("Student with id={} has been removed from current group", studentId);
    }

    @Override
    public void setStudentCourses(Integer studentId, List<Integer> courseIds) {
        LOG.debug("Assigning student with id={} to courses with ids={}", studentId, courseIds);
        assertStudentExists(studentId, "Fail to add student to courses");
        courseIds.forEach(courseId -> assertCourseExists(courseId, "Fail add student to course"));

        removeStudentFromAllCourses(studentId);
        courseIds.forEach(courseId -> studentDao.addToCourse(studentId, courseId));

        LOG.debug("Student with id={} has been assign to courses with ids={}", studentId, courseIds);
    }

    @Override
    public void removeStudentFromAllCourses(Integer studentId) {
        LOG.debug("Removing student with id={} from all currently assigned courses", studentId);
        assertStudentExists(studentId, "Fail to remove student from courses");

        studentDao.removeFromAllCourses(studentId);

        LOG.debug("Student with id={} has been removed from all assigned courses", studentId);
    }

    @Override
    public void deleteStudentById(Integer studentId) {
        LOG.debug("Deleting student with id={}", studentId);
        assertStudentExists(studentId, "Fail to delete student");

        studentDao.deleteById(studentId);
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
