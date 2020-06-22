package org.vdragun.tms.dao.data;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.TimetableDao;

/**
 * Implementation of {@link TimetableDao} using Spring Data
 * 
 * @author Vitaliy Dragun
 *
 */
@RepositoryDefinition(domainClass = Timetable.class, idClass = Integer.class)
public interface SpringDataTimetableDao extends TimetableDao, TimetableDaoFragment {

    @Override
    @Query("SELECT t FROM Timetable t JOIN t.course tc WHERE tc.id IN "
            + "(SELECT sc.id FROM Student s JOIN s.courses sc WHERE s.id = ?1) AND "
            + "EXTRACT (DAY FROM t.startTime) = EXTRACT(DAY FROM CAST (?2 AS LocalDate)) "
            + "AND EXTRACT (MONTH FROM t.startTime) = EXTRACT (MONTH FROM CAST (?2 AS LocalDate))"
            + "ORDER BY t.startTime")
    List<Timetable> findDailyForStudent(Integer studentId, LocalDate date);

    @Override
    @Query("SELECT t FROM Timetable t JOIN t.course tc WHERE tc.id IN "
            + "(SELECT sc.id FROM Student s JOIN s.courses sc WHERE s.id = ?1) AND "
            + "EXTRACT (DAY FROM t.startTime) = EXTRACT(DAY FROM CAST (?2 AS LocalDate)) "
            + "AND EXTRACT (MONTH FROM t.startTime) = EXTRACT (MONTH FROM CAST (?2 AS LocalDate))"
            + "ORDER BY t.startTime")
    Page<Timetable> findDailyForStudent(Integer studentId, LocalDate date, Pageable pageable);

    @Override
    @Query("SELECT t FROM Timetable t JOIN t.teacher tt WHERE tt.id = ?1 "
            + "AND EXTRACT (DAY FROM t.startTime) = EXTRACT(DAY FROM CAST (?2 AS LocalDate)) "
            + "AND EXTRACT (MONTH FROM t.startTime) = EXTRACT (MONTH FROM CAST (?2 AS LocalDate))"
            + "ORDER BY t.startTime")
    List<Timetable> findDailyForTeacher(Integer teacherId, LocalDate date);

    @Override
    @Query("SELECT t FROM Timetable t JOIN t.teacher tt WHERE tt.id = ?1 "
            + "AND EXTRACT (DAY FROM t.startTime) = EXTRACT (DAY FROM CAST (?2 AS LocalDate)) "
            + "AND EXTRACT (MONTH FROM t.startTime) = EXTRACT (MONTH FROM CAST (?2 AS LocalDate))"
            + "ORDER BY t.startTime")
    Page<Timetable> findDailyForTeacher(Integer teacherId, LocalDate date, Pageable pageable);

    @Override
    @Query("SELECT t FROM Timetable t JOIN t.course tc WHERE tc.id IN "
            + "(SELECT sc.id FROM Student s JOIN s.courses sc WHERE s.id = ?1) AND "
            + "EXTRACT (MONTH FROM t.startTime) = ?#{[1].value}")
    List<Timetable> findMonthlyForStudent(Integer studentId, Month month);

    @Override
    @Query("SELECT t FROM Timetable t JOIN t.course tc WHERE tc.id IN "
            + "(SELECT sc.id FROM Student s JOIN s.courses sc WHERE s.id = ?1) AND "
            + "EXTRACT (MONTH FROM t.startTime) = ?#{[1].value}")
    Page<Timetable> findMonthlyForStudent(Integer studentId, Month month, Pageable pageable);

    @Override
    @Query("SELECT t FROM Timetable t JOIN t.teacher tt WHERE tt.id = ?1 "
            + "AND EXTRACT (MONTH FROM t.startTime) = ?#{[1].value}")
    List<Timetable> findMonthlyForTeacher(Integer teacherId, Month month);

    @Override
    @Query("SELECT t FROM Timetable t JOIN t.teacher tt WHERE tt.id = ?1 "
            + "AND EXTRACT (MONTH FROM t.startTime) = ?#{[1].value}")
    Page<Timetable> findMonthlyForTeacher(Integer teacherId, Month month, Pageable pageable);
}
