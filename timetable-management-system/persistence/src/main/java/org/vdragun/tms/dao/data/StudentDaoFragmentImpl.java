package org.vdragun.tms.dao.data;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;

/**
 * Implementation of {@link StudentDaoFragment} using JPA
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentDaoFragmentImpl implements StudentDaoFragment {

    private static final Logger LOG = LoggerFactory.getLogger(StudentDaoFragmentImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addToCourse(Integer studentId, Integer courseId) {
        LOG.debug("Adding student with id={} to course with id={} in the database", studentId, courseId);
        Student student = entityManager.find(Student.class, studentId);
        student.addCourse(entityManager.find(Course.class, courseId));
    }

    @Override
    public void removeFromCourse(Integer studentId, Integer courseId) {
        LOG.debug("Removing student with id={} from course with id={} in the database", studentId, courseId);
        Student student = entityManager.find(Student.class, studentId);
        student.removeCourse(entityManager.find(Course.class, courseId));
    }

    @Override
    public void addToGroup(Integer studentId, Integer groupId) {
        LOG.debug("Adding student with id={} to group with id={} in the database", studentId, groupId);
        Student student = entityManager.find(Student.class, studentId);
        student.setGroup(entityManager.find(Group.class, groupId));
    }

    @Override
    public void removeFromGroup(Integer studentId) {
        LOG.debug("Removing student with id={} from current group in the database", studentId);
        Student student = entityManager.find(Student.class, studentId);
        student.setGroup(null);
    }

    @Override
    public void removeFromAllCourses(Integer studentId) {
        LOG.debug("Removing student with id={} from all assigned courses in the database", studentId);
        Student student = entityManager.find(Student.class, studentId);
        student.removeAllCourses();

    }

}
