package org.vdragun.tms.dao.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.dao.StudentDao;

/**
 * Implementation of {@link StudentDao} using JPA
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JPAStudentDao implements StudentDao {

    private static final Logger LOG = LoggerFactory.getLogger(JPAStudentDao.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${hibernate.jdbc.batch_size}")
    private int batchSize;

    @Override
    public void save(Student student) {
        LOG.debug("Saving new student to the database: {}", student);
        entityManager.persist(student);
    }

    @Override
    public void saveAll(List<Student> students) {
        LOG.debug("Saving {} new students to the database", students.size());
        for (int i = 0; i < students.size(); i++) {
            // saves student instance into active persistence context (but not in the
            // database)
            entityManager.persist(students.get(i));
            if (i % batchSize == 0 || i == students.size() - 1) {
                // flush changes to the database (actually save students into the database)
                entityManager.flush();
                // clear persistence context from all persisted entities
                entityManager.clear();
            }
        }
    }

    @Override
    public Optional<Student> findById(Integer studentId) {
        LOG.debug("Searching for student with id={} in the database", studentId);
        TypedQuery<Student> query = entityManager.createQuery(
                "SELECT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = ?1",
                Student.class);
        query.setParameter(1, studentId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Student> findAll() {
        LOG.debug("Retrieving all students from the database");
        TypedQuery<Student> query = entityManager.createQuery(
                "SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses",
                Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> findForCourse(Integer courseId) {
        LOG.debug("Retrieving all students registered for course with id={} from the database", courseId);
        TypedQuery<Student> query = entityManager.createQuery(
                "SELECT DISTINCT s FROM Student s JOIN FETCH s.courses "
                        + "WHERE s.id IN (SELECT st.id FROM Student st JOIN st.courses c WHERE c.id = ?1)",
                Student.class);
        query.setParameter(1, courseId);
        return query.getResultList();
    }

    @Override
    public List<Student> findForGroup(Integer groupId) {
        LOG.debug("Retrieving all students assigned to group with id={} from the database", groupId);
        TypedQuery<Student> query = entityManager.createQuery(
                "SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses c WHERE s.group.id = ?1",
                Student.class);
        query.setParameter(1, groupId);
        return query.getResultList();
    }

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
        student.removeCourse(new Course(courseId));
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

    @Override
    public boolean existsById(Integer studentId) {
        LOG.debug("Checking whether student with id={} exists in the database", studentId);
        TypedQuery<Integer> query = entityManager.createQuery(
                "SELECT s.id FROM Student s WHERE s.id = ?1",
                Integer.class);
        query.setParameter(1, studentId);
        return !query.getResultList().isEmpty();
    }

    @Override
    public void deleteById(Integer studentId) {
        LOG.debug("Deleting student with id={} from the database", studentId);
        Query query = entityManager.createQuery("DELETE FROM Student s WHERE s.id = ?1");
        query.setParameter(1, studentId);
        query.executeUpdate();
    }

}
