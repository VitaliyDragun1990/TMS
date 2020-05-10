package org.vdragun.tms.dao.hibernate;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.TeacherDao;

/**
 * Implementation of {@link TeacherDao} using Hibernate
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class HibernateTeacherDao extends BaseHibernateDao implements TeacherDao {

    public HibernateTeacherDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void save(Teacher teacher) {
        log.debug("Saving new teacher to the database: {}", teacher);
        execute(session -> session.save(teacher));
    }

    @Override
    public void saveAll(List<Teacher> teachers) {
        log.debug("Saving {} new teachers to the database", teachers.size());
        int batchSize = getBatchSize();
        execute(session -> {
            for (int i = 0; i < teachers.size(); i++) {
                // saves teacher instance into active session (but not in the database)
                session.persist(teachers.get(i));
                if (i % batchSize == 0 || i == teachers.size() - 1) {
                    // flush changes to the database (actually save teachers into the database)
                    session.flush();
                    // clear session from all persisted entities
                    session.clear();
                }
            }
        });
    }

    @Override
    public Optional<Teacher> findById(Integer teacherId) {
        log.debug("Searching for teacher with id={} in the database", teacherId);
        return query(session -> {
            // Select all teachers with or without any courses and eagerly loads all appropriate
            // courses for each such teacher in one select
            Query<Teacher> query = session.createQuery(
                    "SELECT t FROM Teacher t LEFT JOIN FETCH t.courses WHERE t.id = ?1",
                    Teacher.class);
            query.setParameter(1, teacherId);
            return query.list().stream().findFirst();
        });
    }

    @Override
    public List<Teacher> findAll() {
        log.debug("Retrieving all teachers from the database");
        return query(session -> {
            // Select all teachers with or without any courses and eagerly loads all appropriate
            // courses for each such teacher in one select
            Query<Teacher> query = session.createQuery(
                    "SELECT DISTINCT t FROM Teacher t LEFT JOIN FETCH t.courses",
                    Teacher.class);
            return query.list();
        });
    }

    @Override
    public Optional<Teacher> findForCourse(Integer courseId) {
        log.debug("Searching for teacher assigned to course with id={} in the database", courseId);
        return query(session -> {
            // Select teacher assigned to course with given identity if any
            // returns such teachers and all their courses eagerly loaded in one select
            Query<Teacher> query = session.createQuery(
                    "SELECT t FROM Course c JOIN c.teacher t JOIN FETCH t.courses WHERE c.id = ?1",
                    Teacher.class);
            query.setParameter(1, courseId);
            return query.list().stream().findFirst();
        });
    }

    @Override
    public boolean existsById(Integer teacherId) {
        log.debug("Checking whether teacher with id={} exists in the database", teacherId);
        return query(session -> {
            Query<Integer> query = session.createQuery(
                    "SELECT t.id FROM Teacher t WHERE t.id = ?1",
                    Integer.class);
            query.setParameter(1, teacherId);
            return !query.list().isEmpty();
        });
    }

}
