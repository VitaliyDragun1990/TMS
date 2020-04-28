package org.vdragun.tms.dao;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import org.vdragun.tms.core.domain.Timetable;

/**
 * Defines CRUD operations to access {@link Timetable} objects in the persistent
 * storage
 * 
 * @author Vitaliy Dragun
 *
 */
public interface TimetableDao {

    /**
     * Saves specified timetable instance. Saved object receives unique identifier.
     */
    void save(Timetable timetable);

    /**
     * Saves all specified timetable instances. Each saved object receives unique
     * identifier.
     */
    void saveAll(List<Timetable> timetables);

    /**
     * Returns timetable with given identifier if any
     */
    Optional<Timetable> findById(Integer timetableId);

    /**
     * Returns all timetables
     */
    List<Timetable> findAll();

    /**
     * Finds all timetables available for a student with specified identifier for
     * specified date.
     */
    List<Timetable> findDailyForStudent(Integer studentId, LocalDate date);

    /**
     * Finds all timetables available for a teacher with specified identifier for
     * specified date.
     */
    List<Timetable> findDailyForTeacher(Integer teacherId, LocalDate date);

    /**
     * Finds all timetables available for a student with specified identifier for
     * specified month.
     */
    List<Timetable> findMonthlyForStudent(Integer studentId, Month month);

    /**
     * Finds all timetables available for a teacher with specified identifier for
     * specified month.
     */
    List<Timetable> findMonthlyForTeacher(Integer teacherId, Month month);
}
