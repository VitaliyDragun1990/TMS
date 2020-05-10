package org.vdragun.tms.dao.jpa;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.TimetableDao;

/**
 * Implementation of {@link TimetableDao} using Hibernate
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JPATimetableDao extends BaseJPADao implements TimetableDao {

    public JPATimetableDao(EntityManagerFactory factory) {
        super(factory);
    }

    @Override
    public void save(Timetable timetable) {
        log.debug("Saving new timetable to the database: {}", timetable);
        execute(entityManager -> entityManager.persist(timetable));
    }

    @Override
    public void update(Timetable timetable) {
        log.debug("Updating timetable with id={} with data={} in the database", timetable.getId(), timetable);
        // In order to throw exception if entity to update does not exist
        execute(entityManager -> entityManager.unwrap(Session.class).update(timetable));
    }

    @Override
    public void saveAll(List<Timetable> timetables) {
        log.debug("Saving {} new timetables to the database", timetables.size());
        int batchSize = getBatchSize();
        execute(entityManager -> {
            for (int i = 0; i < timetables.size(); i++) {
                // saves timetable instance into active persistence context (but not in the database)
                entityManager.persist(timetables.get(i));
                if (i % batchSize == 0 || i == timetables.size() - 1) {
                    // flush changes to the database (actually save timetables into the database)
                    entityManager.flush();
                    // clear persistence context from all persisted entities
                    entityManager.clear();
                }
            }
        });
    }

    @Override
    public Optional<Timetable> findById(Integer timetableId) {
        log.debug("Searching for timetable with id={} in the database", timetableId);
        return query(entityManager -> Optional.ofNullable(entityManager.find(Timetable.class, timetableId)));
    }

    @Override
    public List<Timetable> findAll() {
        log.debug("Retrieving all timetables from the database");
        return query(entityManager -> entityManager.createQuery(
                "SELECT t FROM Timetable t ORDER BY t.startTime",
                Timetable.class).getResultList());
    }

    @Override
    public List<Timetable> findDailyForStudent(Integer studentId, LocalDate date) {
        log.debug("Retrieving daily timetables for student with id={} for date={} from the database",
                studentId, date);
        return query(entityManager -> {
            TypedQuery<Timetable> query = entityManager.createQuery(
                    "SELECT t FROM Timetable t JOIN t.course tc WHERE tc.id IN "
                    + "(SELECT sc.id FROM Student s JOIN s.courses sc WHERE s.id = ?1) AND "
                    + "EXTRACT (DAY FROM t.startTime) = EXTRACT(DAY FROM ?2) "
                    + "ORDER BY t.startTime",
                    Timetable.class);
            query.setParameter(1, studentId);
            query.setParameter(2, date);
            return query.getResultList();
        });
    }

    @Override
    public List<Timetable> findDailyForTeacher(Integer teacherId, LocalDate date) {
        log.debug("Retrieving daily timetables for teacher with id={} for date={} from the database",
                teacherId, date);
        return query(entityManager -> {
            TypedQuery<Timetable> query = entityManager.createQuery(
                    "SELECT t FROM Timetable t JOIN t.teacher tt WHERE tt.id = ?1 "
                    + "AND EXTRACT (DAY FROM t.startTime) = EXTRACT(DAY FROM ?2) "
                    + "ORDER BY t.startTime",
                    Timetable.class);
            query.setParameter(1, teacherId);
            query.setParameter(2, date);
            return query.getResultList();
        });
    }

    @Override
    public List<Timetable> findMonthlyForStudent(Integer studentId, Month month) {
        log.debug("Retrieving monthly timetables for student with id={} for month={} from the database",
                studentId, month);
        return query(entityManager -> {
            TypedQuery<Timetable> query = entityManager.createQuery(
                    "SELECT t FROM Timetable t JOIN t.course tc WHERE tc.id IN "
                    + "(SELECT sc.id FROM Student s JOIN s.courses sc WHERE s.id = ?1) AND "
                    + "EXTRACT (MONTH FROM t.startTime) = ?2 ORDER BY t.startTime",
                    Timetable.class);
            query.setParameter(1, studentId);
            query.setParameter(2, month.getValue());
            return query.getResultList();
        });
    }

    @Override
    public List<Timetable> findMonthlyForTeacher(Integer teacherId, Month month) {
        log.debug("Retrieving monthly timetables for teacher with id={} for month={} from the database",
                teacherId, month);
        return query(entityManager -> {
            TypedQuery<Timetable> query = entityManager.createQuery(
                    "SELECT t FROM Timetable t JOIN t.teacher tt WHERE tt.id = ?1 "
                    + "AND EXTRACT (MONTH FROM t.startTime) = ?2 ORDER BY t.startTime",
                    Timetable.class);
            query.setParameter(1, teacherId);
            query.setParameter(2, month.getValue());
            return query.getResultList();
        });
    }

    @Override
    public void deleteById(Integer timetableId) {
        log.debug("Deleting timetable with id={} from the database", timetableId);
        execute(entityManager -> {
            Query query = entityManager.createQuery("DELETE FROM Timetable t WHERE t.id = ?1");
            query.setParameter(1, timetableId);
            query.executeUpdate();
        });
    }

    @Override
    public boolean existsById(Integer timetableId) {
        log.debug("Checking whether timetable with id={} exists in the database", timetableId);
        return query(session -> {
            TypedQuery<Integer> query = session.createQuery(
                    "SELECT t.id FROM Timetable t WHERE t.id = ?1",
                    Integer.class);
            query.setParameter(1, timetableId);
            return !query.getResultList().isEmpty();
        });
    }

}
