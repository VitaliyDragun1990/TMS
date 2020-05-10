package org.vdragun.tms.dao.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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
public class JPAStudentDao extends BaseJPADao implements StudentDao {

    public JPAStudentDao(EntityManagerFactory factory) {
        super(factory);
    }

    @Override
    public void save(Student student) {
        log.debug("Saving new student to the database: {}", student);
        execute(entityManager -> entityManager.persist(student));
    }

    @Override
    public void saveAll(List<Student> students) {
        log.debug("Saving {} new students to the database", students.size());
        int batchSize = getBatchSize();
        execute(entityManager -> {
            for (int i = 0; i < students.size(); i++) {
                // saves student instance into active persistence context (but not in the database)
                entityManager.persist(students.get(i));
                if (i % batchSize == 0 || i == students.size() - 1) {
                    // flush changes to the database (actually save students into the database)
                    entityManager.flush();
                    // clear persistence context from all persisted entities
                    entityManager.clear();
                }
            }
        });
    }

    @Override
    public Optional<Student> findById(Integer studentId) {
        log.debug("Searching for student with id={} in the database", studentId);
        return query(entityManager -> {
            TypedQuery<Student> query = entityManager.createQuery(
                    "SELECT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = ?1",
                    Student.class);
            query.setParameter(1, studentId);
            return query.getResultList().stream().findFirst();
        });
    }

    @Override
    public List<Student> findAll() {
        log.debug("Retrieving all students from the database");
        return query(entityManager -> {
            TypedQuery<Student> query = entityManager.createQuery(
                    "SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses",
                    Student.class);
            return query.getResultList();
        });
    }

    @Override
    public List<Student> findForCourse(Integer courseId) {
        log.debug("Retrieving all students registered for course with id={} from the database", courseId);
        return query(entityManager -> {
            TypedQuery<Student> query = entityManager.createQuery(
                    "SELECT DISTINCT s FROM Student s JOIN FETCH s.courses "
                    + "WHERE s.id IN (SELECT st.id FROM Student st JOIN st.courses c WHERE c.id = ?1)",
                    Student.class);
            query.setParameter(1, courseId);
            return query.getResultList();
        });
    }

    @Override
    public List<Student> findForGroup(Integer groupId) {
        log.debug("Retrieving all students assigned to group with id={} from the database", groupId);
        return query(entityManager -> {
            TypedQuery<Student> query = entityManager.createQuery(
                    "SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses c WHERE s.group.id = ?1",
                    Student.class);
            query.setParameter(1, groupId);
            return query.getResultList();
        });
    }

    @Override
    public void addToCourse(Integer studentId, Integer courseId) {
        log.debug("Adding student with id={} to course with id={} in the database", studentId, courseId);
        execute(entityManager -> {
            Student student = entityManager.find(Student.class, studentId);
            student.addCourse(new Course(courseId));
        });
    }

    @Override
    public void removeFromCourse(Integer studentId, Integer courseId) {
        log.debug("Removing student with id={} from course with id={} in the database", studentId, courseId);
        execute(entityManager -> {
            Student student = entityManager.find(Student.class, studentId);
            student.removeCourse(new Course(courseId));
        });
    }

    @Override
    public void addToGroup(Integer studentId, Integer groupId) {
        log.debug("Adding student with id={} to group with id={} in the database", studentId, groupId);
        execute(entityManager -> {
            Student student = entityManager.find(Student.class, studentId);
            student.setGroup(new Group(groupId));
        });
    }

    @Override
    public void removeFromGroup(Integer studentId) {
        log.debug("Removing student with id={} from current group in the database", studentId);
        execute(entityManager -> {
            Student student = entityManager.find(Student.class, studentId);
            student.setGroup(null);
        });
    }

    @Override
    public void removeFromAllCourses(Integer studentId) {
        log.debug("Removing student with id={} from all assigned courses in the database", studentId);
        execute(entityManager -> {
            Student student = entityManager.find(Student.class, studentId);
            student.removeAllCourses();
        });
    }

    @Override
    public boolean existsById(Integer studentId) {
        log.debug("Checking whether student with id={} exists in the database", studentId);
        return query(entityManager -> {
            TypedQuery<Integer> query = entityManager.createQuery(
                    "SELECT s.id FROM Student s WHERE s.id = ?1",
                    Integer.class);
            query.setParameter(1, studentId);
            return !query.getResultList().isEmpty();
        });
    }

    @Override
    public void deleteById(Integer studentId) {
        log.debug("Deleting student with id={} from the database", studentId);
        execute(entityManager -> {
            Query query = entityManager.createQuery("DELETE FROM Student s WHERE s.id = ?1");
            query.setParameter(1, studentId);
            query.executeUpdate();
        });
    }

}
