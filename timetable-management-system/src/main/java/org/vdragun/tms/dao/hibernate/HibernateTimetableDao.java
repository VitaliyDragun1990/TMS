package org.vdragun.tms.dao.hibernate;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
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
public class HibernateTimetableDao extends BaseHibernateDao implements TimetableDao {

    public HibernateTimetableDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void save(Timetable timetable) {
        log.debug("Saving new timetable to the database: {}", timetable);
        execute(session -> session.save(timetable));
    }

    @Override
    public void update(Timetable timetable) {
        log.debug("Updating timetable with id={} with data={} in the database", timetable.getId(), timetable);
        execute(session -> session.update(timetable));
    }

    @Override
    public void saveAll(List<Timetable> timetables) {
        log.debug("Saving {} new timetables to the database", timetables.size());
        int batchSize = getBatchSize();
        execute(session -> {
            for (int i = 0; i < timetables.size(); i++) {
                // saves timetable instance into active session (but not in the database)
                session.persist(timetables.get(i));
                if (i % batchSize == 0 || i == timetables.size() - 1) {
                    // flush changes to the database (actually save timetables into the database)
                    session.flush();
                    // clear session from all persisted entities
                    session.clear();
                }
            }
        });
    }

    @Override
    public Optional<Timetable> findById(Integer timetableId) {
        log.debug("Searching for timetable with id={} in the database", timetableId);
        return query(session -> Optional.ofNullable(session.get(Timetable.class, timetableId)));
    }

    @Override
    public List<Timetable> findAll() {
        log.debug("Retrieving all timetables from the database");
        return query(session -> session.createQuery("SELECT t FROM Timetable t", Timetable.class).list());
    }

    @Override
    public List<Timetable> findDailyForStudent(Integer studentId, LocalDate date) {
        log.debug("Retrieving daily timetables for student with id={} for date={} from the database",
                studentId, date);
        return query(session -> {
            Query<Timetable> query = session.createQuery(
                    "SELECT t FROM Timetable t JOIN t.course tc WHERE tc.id IN "
                    + "(SELECT sc.id FROM Student s JOIN s.courses sc WHERE s.id = ?1) AND "
                    + "EXTRACT (DAY FROM t.startTime) = EXTRACT(DAY FROM ?2)",
                    Timetable.class);
            query.setParameter(1, studentId);
            query.setParameter(2, date);
            return query.list();
        });
    }

    @Override
    public List<Timetable> findDailyForTeacher(Integer teacherId, LocalDate date) {
        log.debug("Retrieving daily timetables for teacher with id={} for date={} from the database",
                teacherId, date);
        return query(session -> {
            Query<Timetable> query = session.createQuery(
                    "SELECT t FROM Timetable t JOIN t.teacher tt WHERE tt.id = ?1 "
                    + "AND EXTRACT (DAY FROM t.startTime) = EXTRACT(DAY FROM ?2)",
                    Timetable.class);
            query.setParameter(1, teacherId);
            query.setParameter(2, date);
            return query.list();
        });
    }

    @Override
    public List<Timetable> findMonthlyForStudent(Integer studentId, Month month) {
        log.debug("Retrieving monthly timetables for student with id={} for month={} from the database",
                studentId, month);
        return query(session -> {
            Query<Timetable> query = session.createQuery(
                    "SELECT t FROM Timetable t JOIN t.course tc WHERE tc.id IN "
                    + "(SELECT sc.id FROM Student s JOIN s.courses sc WHERE s.id = ?1) AND "
                    + "EXTRACT (MONTH FROM t.startTime) = ?2",
                    Timetable.class);
            query.setParameter(1, studentId);
            query.setParameter(2, month.getValue());
            return query.list();
        });
    }

    @Override
    public List<Timetable> findMonthlyForTeacher(Integer teacherId, Month month) {
        log.debug("Retrieving monthly timetables for teacher with id={} for month={} from the database",
                teacherId, month);
        return query(session -> {
            Query<Timetable> query = session.createQuery(
                    "SELECT t FROM Timetable t JOIN t.teacher tt WHERE tt.id = ?1 "
                    + "AND EXTRACT (MONTH FROM t.startTime) = ?2",
                    Timetable.class);
            query.setParameter(1, teacherId);
            query.setParameter(2, month.getValue());
            return query.list();
        });
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void deleteById(Integer timetableId) {
        log.debug("Deleting timetable with id={} from the database", timetableId);
        execute(session -> {
            Query query = session.createQuery("DELETE FROM Timetable t WHERE t.id = ?1");
            query.setParameter(1, timetableId);
            query.executeUpdate();
        });
    }

    @Override
    public boolean existsById(Integer timetableId) {
        log.debug("Checking whether timetable with id={} exists in the database", timetableId);
        return query(session -> {
            Query<Integer> query = session.createQuery(
                    "SELECT t.id FROM Timetable t WHERE t.id = ?1",
                    Integer.class);
            query.setParameter(1, timetableId);
            return !query.list().isEmpty();
        });
    }

}
