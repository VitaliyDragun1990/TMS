package org.vdragun.tms.dao.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

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
public class JPAClassroomDao extends BaseJPADao implements ClassroomDao {

    public JPAClassroomDao(EntityManagerFactory factory) {
        super(factory);
    }

    @Override
    public void save(Classroom classroom) {
        log.debug("Saving new classroom to the database: {}", classroom);
        execute(entityManager -> entityManager.persist(classroom));
    }

    @Override
    public Optional<Classroom> findById(Integer classroomId) {
        log.debug("Searching for classroom with id={} in the database", classroomId);
        return query(entityManager -> Optional.ofNullable(entityManager.find(Classroom.class, classroomId)));
    }

    @Override
    public List<Classroom> findAll() {
        log.debug("Retrieving all classrooms from the database");
        return query(entityManager -> entityManager.createQuery("SELECT c FROM Classroom c", Classroom.class)
                .getResultList());
    }

    @Override
    public boolean existsById(Integer classroomId) {
        log.debug("Checking whether classroom with id={} exists in the database", classroomId);
        return query(entityManager -> {
            TypedQuery<Integer> query = entityManager.createQuery("SELECT c.id FROM Classroom c WHERE c.id = ?1",
                    Integer.class);
            query.setParameter(1, classroomId);
            return !query.getResultList().isEmpty();
        });
    }

}
