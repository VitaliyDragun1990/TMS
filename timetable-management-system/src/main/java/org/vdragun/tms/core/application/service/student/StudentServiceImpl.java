package org.vdragun.tms.core.application.service.student;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Group;
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
@Transactional
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
    public Student registerNewStudent(CreateStudentData studentData) {
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
    public Student updateExistingStudent(UpdateStudentData studentData) {
        LOG.debug("Updating existing student using data: {}", studentData);

        Student student = getStudent(studentData.getStudentId());
        student.setFirstName(studentData.getFirstName());
        student.setLastName(studentData.getLastName());
        student.setGroup(getGroup(studentData.getGroupId()));
        student.setCourses(getCourses(studentData.getCourseIds()));

        studentDao.save(student);

        LOG.debug("Student with id={} has been successfully updated", studentData.getStudentId());
        return student;
    }

    @Override
    @Transactional(readOnly = true)
    public Student findStudentById(Integer studentId) {
        LOG.debug("Searching for student with id={}", studentId);

        return studentDao.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with id=%d not found", studentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAllStudents() {
        LOG.debug("Retrieving all students");

        List<Student> result = studentDao.findAll();

        LOG.debug("Found {} students", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findStudentsForCourse(Integer courseId) {
        assertCourseExists(courseId);

        return studentDao.findByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findStudentsForGroup(Integer groupId) {
        LOG.debug("Searching for students assigned to group with id={}", groupId);
        assertGroupExists(groupId);

        List<Student> result = studentDao.findByGroupId(groupId);

        LOG.debug("Found {} students assigned to group with id={}", result.size(), groupId);
        return result;
    }

    @Override
    public void deleteStudentById(Integer studentId) {
        LOG.debug("Deleting student with id={}", studentId);
        assertStudentExists(studentId);

        studentDao.deleteById(studentId);
    }

    private void assertCourseExists(Integer courseId) {
        if (!courseDao.existsById(courseId)) {
            throw new ResourceNotFoundException("Course with id=%d does not exist", courseId);
        }
    }

    private void assertGroupExists(Integer groupId) {
        if (!groupDao.existsById(groupId)) {
            throw new ResourceNotFoundException("Group with id=%d does not exist", groupId);
        }
    }

    private void assertStudentExists(Integer studentId) {
        if (!studentDao.existsById(studentId)) {
            throw new ResourceNotFoundException("Student with id=%d does not exist", studentId);
        }
    }

    private Student getStudent(Integer studentId) {
        return studentDao
                .findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with id=%d does not exist", studentId));
    }

    private Group getGroup(Integer groupId) {
        if (groupId != null) {
            return groupDao
                    .findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Group with id=%d does not exist", groupId));
        }
        return null;
    }

    private Set<Course> getCourses(List<Integer> courseIds) {
        Set<Course> result = new HashSet<>();
        for (Integer courseId : courseIds) {
            Course course = courseDao
                    .findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course with id=%d does not exist", courseId));
            result.add(course);
        }
        return result;
    }

}
