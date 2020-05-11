package org.vdragun.tms.dao.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.dao.ClassroomDao;

/**
 * Implementation of {@link ClassroomDao} using Hibernate
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JPAClassroomDao implements ClassroomDao {

    private static final Logger LOG = LoggerFactory.getLogger(JPAClassroomDao.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Classroom classroom) {
        LOG.debug("Saving new classroom to the database: {}", classroom);
        entityManager.persist(classroom);
    }

    @Override
    public Optional<Classroom> findById(Integer classroomId) {
        LOG.debug("Searching for classroom with id={} in the database", classroomId);
        return Optional.ofNullable(entityManager.find(Classroom.class, classroomId));
    }

    @Override
    public List<Classroom> findAll() {
        LOG.debug("Retrieving all classrooms from the database");
        return entityManager.createQuery("SELECT c FROM Classroom c", Classroom.class).getResultList();
    }

    @Override
    public boolean existsById(Integer classroomId) {
        LOG.debug("Checking whether classroom with id={} exists in the database", classroomId);
        TypedQuery<Integer> query = entityManager.createQuery("SELECT c.id FROM Classroom c WHERE c.id = ?1",
                Integer.class);
        query.setParameter(1, classroomId);
        return !query.getResultList().isEmpty();
    }

}
