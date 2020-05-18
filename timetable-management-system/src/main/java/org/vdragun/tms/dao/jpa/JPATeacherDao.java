package org.vdragun.tms.dao.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.TeacherDao;

/**
 * Implementation of {@link TeacherDao} using JPA
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JPATeacherDao implements TeacherDao {

    private static final Logger LOG = LoggerFactory.getLogger(JPATeacherDao.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Value("${hibernate.jdbc.batch_size}")
    private int batchSize;

    @Override
    public void save(Teacher teacher) {
        LOG.debug("Saving new teacher to the database: {}", teacher);
        entityManager.persist(teacher);
    }

    @Override
    public void saveAll(List<Teacher> teachers) {
        LOG.debug("Saving {} new teachers to the database", teachers.size());
        for (int i = 0; i < teachers.size(); i++) {
            // saves teacher instance into active persistence context (but not in the database)
            entityManager.persist(teachers.get(i));
            if (i % batchSize == 0 || i == teachers.size() - 1) {
                // flush changes to the database (actually save teachers into the database)
                entityManager.flush();
                // clear persistence context from all persisted entities
                entityManager.clear();
            }
        }
    }

    @Override
    public Optional<Teacher> findById(Integer teacherId) {
        LOG.debug("Searching for teacher with id={} in the database", teacherId);
        // Select all teachers with or without any courses and eagerly loads all appropriate
        // courses for each such teacher in one select
        TypedQuery<Teacher> query = entityManager.createQuery(
                "SELECT t FROM Teacher t LEFT JOIN FETCH t.courses WHERE t.id = ?1",
                Teacher.class);
        query.setParameter(1, teacherId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Teacher> findAll() {
        LOG.debug("Retrieving all teachers from the database");
        // Select all teachers with or without any courses and eagerly loads all appropriate
        // courses for each such teacher in one select
        TypedQuery<Teacher> query = entityManager.createQuery(
                "SELECT DISTINCT t FROM Teacher t LEFT JOIN FETCH t.courses",
                Teacher.class);
        return query.getResultList();
    }

    @Override
    public Optional<Teacher> findForCourse(Integer courseId) {
        LOG.debug("Searching for teacher assigned to course with id={} in the database", courseId);
        // Select teacher assigned to course with given identity if any
        // returns such teachers and all their courses eagerly loaded in one select
        TypedQuery<Teacher> query = entityManager.createQuery(
                "SELECT t FROM Course c JOIN c.teacher t JOIN FETCH t.courses WHERE c.id = ?1",
                Teacher.class);
        query.setParameter(1, courseId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public boolean existsById(Integer teacherId) {
        LOG.debug("Checking whether teacher with id={} exists in the database", teacherId);
        TypedQuery<Integer> query = entityManager.createQuery(
                "SELECT t.id FROM Teacher t WHERE t.id = ?1",
                Integer.class);
        query.setParameter(1, teacherId);
        return !query.getResultList().isEmpty();
    }

}
