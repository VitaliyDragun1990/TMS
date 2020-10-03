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
    @Query("SELECT timetable FROM Timetable timetable JOIN timetable.course timetable_course WHERE timetable_course.id IN "
            + "(SELECT student_courses.id FROM Student student JOIN student.courses student_courses WHERE student.id = ?1) AND "
            + "EXTRACT (DAY FROM timetable.startTime) = EXTRACT(DAY FROM CAST (?2 AS LocalDate)) "
            + "AND EXTRACT (MONTH FROM timetable.startTime) = EXTRACT (MONTH FROM CAST (?2 AS LocalDate)) "
            + "ORDER BY timetable.startTime")
    List<Timetable> findDailyForStudent(Integer studentId, LocalDate date);

    @Override
    @Query("SELECT timetable FROM Timetable timetable JOIN timetable.course timetable_course WHERE timetable_course.id IN "
            + "(SELECT student_courses.id FROM Student student JOIN student.courses student_courses WHERE student.id = ?1) AND "
            + "EXTRACT (DAY FROM timetable.startTime) = EXTRACT(DAY FROM CAST (?2 AS LocalDate)) "
            + "AND EXTRACT (MONTH FROM timetable.startTime) = EXTRACT (MONTH FROM CAST (?2 AS LocalDate))")
    Page<Timetable> findDailyForStudent(Integer studentId, LocalDate date, Pageable pageable);

    @Override
    @Query("SELECT timetable FROM Timetable timetable JOIN timetable.course.teacher course_teacher WHERE course_teacher.id = ?1 "
            + "AND EXTRACT (DAY FROM timetable.startTime) = EXTRACT(DAY FROM CAST (?2 AS LocalDate)) "
            + "AND EXTRACT (MONTH FROM timetable.startTime) = EXTRACT (MONTH FROM CAST (?2 AS LocalDate)) "
            + "ORDER BY timetable.startTime")
    List<Timetable> findDailyForTeacher(Integer teacherId, LocalDate date);

    @Override
    @Query("SELECT timetable FROM Timetable timetable JOIN timetable.course.teacher course_teacher WHERE course_teacher.id = ?1 "
            + "AND EXTRACT (DAY FROM timetable.startTime) = EXTRACT (DAY FROM CAST (?2 AS LocalDate)) "
            + "AND EXTRACT (MONTH FROM timetable.startTime) = EXTRACT (MONTH FROM CAST (?2 AS LocalDate))")
    Page<Timetable> findDailyForTeacher(Integer teacherId, LocalDate date, Pageable pageable);

    @Override
    @Query("SELECT timetable FROM Timetable timetable JOIN timetable.course timetable_course WHERE timetable_course.id IN "
            + "(SELECT student_courses.id FROM Student student JOIN student.courses student_courses WHERE student.id = ?1) AND "
            + "EXTRACT (MONTH FROM timetable.startTime) = ?#{[1].value} "
            + "ORDER BY timetable.startTime")
    List<Timetable> findMonthlyForStudent(Integer studentId, Month month);

    @Override
    @Query("SELECT timetable FROM Timetable timetable JOIN timetable.course timetable_course WHERE timetable_course.id IN "
            + "(SELECT student_courses FROM Student student JOIN student.courses student_courses WHERE student.id = ?1) AND "
            + "EXTRACT (MONTH FROM timetable.startTime) = ?#{[1].value}")
    Page<Timetable> findMonthlyForStudent(Integer studentId, Month month, Pageable pageable);

    @Override
    @Query("SELECT timetable FROM Timetable timetable JOIN timetable.course.teacher course_teacher WHERE course_teacher.id = ?1 "
            + "AND EXTRACT (MONTH FROM timetable.startTime) = ?#{[1].value} "
            + "ORDER BY timetable.startTime")
    List<Timetable> findMonthlyForTeacher(Integer teacherId, Month month);

    @Override
    @Query("SELECT timetable FROM Timetable timetable JOIN timetable.course.teacher course_teacher WHERE course_teacher.id = ?1 "
            + "AND EXTRACT (MONTH FROM timetable.startTime) = ?#{[1].value}")
    Page<Timetable> findMonthlyForTeacher(Integer teacherId, Month month, Pageable pageable);
}
