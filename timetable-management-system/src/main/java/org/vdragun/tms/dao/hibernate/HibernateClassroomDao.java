package org.vdragun.tms.dao.hibernate;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
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
public class HibernateClassroomDao extends BaseHibernateDao implements ClassroomDao {

    public HibernateClassroomDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void save(Classroom classroom) {
        log.debug("Saving new classroom to the database: {}", classroom);
        execute(session -> session.save(classroom));
    }

    @Override
    public Optional<Classroom> findById(Integer classroomId) {
        log.debug("Searching for classroom with id={} in the database", classroomId);
        return query(session -> Optional.ofNullable(session.get(Classroom.class, classroomId)));
    }

    @Override
    public List<Classroom> findAll() {
        log.debug("Retrieving all classrooms from the database");
        return query(session -> session.createQuery("SELECT c FROM Classroom c", Classroom.class).list());
    }

    @Override
    public boolean existsById(Integer classroomId) {
        log.debug("Checking whether classroom with id={} exists in the database", classroomId);
        return query(session -> {
            Query<Integer> query = session.createQuery(
                    "SELECT c.id FROM Classroom c WHERE c.id = ?1",
                    Integer.class);
            query.setParameter(1, classroomId);
            return !query.list().isEmpty();
        });
    }
}
