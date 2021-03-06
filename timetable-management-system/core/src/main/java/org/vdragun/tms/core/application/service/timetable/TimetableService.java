package org.vdragun.tms.core.application.service.timetable;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vdragun.tms.core.application.exception.InvalidPageNumberException;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Timetable;

/**
 * Application entry point to work with {@link Timetable}
 * 
 * @author Vitaliy Dragun
 *
 */
public interface TimetableService {

    /**
     * Register new timetable using provided data
     * 
     * @param timetableData data to register new timetable
     * @return newly registered timetable
     */
    Timetable registerNewTimetable(CreateTimetableData timetableData);

    /**
     * Update existing timetable using provided data
     * 
     * @param timetableData data to update existing timetable
     * @return updated timetable
     * @throws ResourceNotFoundException if timetable and/or any specified resource
     *                                   intended for update is not found
     */
    Timetable updateExistingTimetable(UpdateTimetableData timetableData);

    /**
     * Returns existing timetable using provided identifier
     * 
     * @param timetableId existing timetable identifier
     * @return timetable with specified identifier
     * @throws ResourceNotFoundException if no timetable with provided identifier
     */
    Timetable findTimetableById(Integer timetableId);

    /**
     * Finds all timetables available
     * 
     * @return list of all available timetables
     */
    List<Timetable> findAllTimetables();

    /**
     * Finds timetables according to provided pageable data.
     * 
     * @return page with timetables
     * @throws InvalidPageNumberException if specified page number is invalid
     */
    Page<Timetable> findTimetables(Pageable pageable);

    /**
     * Finds all timetables available for specified day for student with provided
     * identifier
     * 
     * @param studentId student identifier
     * @param date      day for which timetables should be found
     * @throws ResourceNotFoundException if no student with specified identifier
     */
    List<Timetable> findDailyTimetablesForStudent(Integer studentId, LocalDate date);

    /**
     * Finds all timetables available for specified day for student with provided
     * identifier and pageable data
     * 
     * @param studentId student identifier
     * @param date      day for which timetables should be found
     * @throws ResourceNotFoundException  if no student with specified identifier
     * @throws InvalidPageNumberException if specified page number is invalid
     */
    Page<Timetable> findDailyTimetablesForStudent(Integer studentId, LocalDate date, Pageable pageable);

    /**
     * Finds all timetables available for specified month for student with provided
     * identifier
     * 
     * @param studentId student identifier
     * @param month     month for which timetables should be found
     * @throws ResourceNotFoundException if no student with specified identifier
     */
    List<Timetable> findMonthlyTimetablesForStudent(Integer studentId, Month month);

    /**
     * Finds all timetables available for specified month for student with provided
     * identifier and pageable data
     * 
     * @param studentId student identifier
     * @param month     month for which timetables should be found
     * @throws ResourceNotFoundException  if no student with specified identifier
     * @throws InvalidPageNumberException if specified page number is invalid
     */
    Page<Timetable> findMonthlyTimetablesForStudent(Integer studentId, Month month, Pageable pageable);

    /**
     * Finds all timetables available for specified day for teacher with provided
     * identifier
     * 
     * @param teacherId teacher identifier
     * @param date      day for which timetables should be found
     * @throws ResourceNotFoundException if no teacher with specified identifier
     */
    List<Timetable> findDailyTimetablesForTeacher(Integer teacherId, LocalDate date);

    /**
     * Finds timetables available for specified day for teacher with provided
     * identifier according to provided pageable data
     * 
     * @param teacherId teacher identifier
     * @param date      day for which timetables should be found
     * @param pageable  page related data
     * @throws ResourceNotFoundException  if no teacher with specified identifier
     * @throws InvalidPageNumberException if specified page number is invalid
     */
    Page<Timetable> findDailyTimetablesForTeacher(Integer teacherId, LocalDate date, Pageable pageable);

    /**
     * Finds all timetables available for specified month for teacher with provided
     * identifier
     * 
     * @param teacherId teacher identifier
     * @param month     month for which timetables should be found
     * @throws ResourceNotFoundException if no teacher with specified identifier
     */
    List<Timetable> findMonthlyTimetablesForTeacher(Integer teacherId, Month month);

    /**
     * Finds all timetables available for specified month for teacher with provided
     * identifier and pageable data
     * 
     * @param teacherId teacher identifier
     * @param month     month for which timetables should be found
     * @throws ResourceNotFoundException  if no teacher with specified identifier
     * @throws InvalidPageNumberException if specified page number is invalid
     */
    Page<Timetable> findMonthlyTimetablesForTeacher(Integer teacherId, Month month, Pageable pageable);

    /**
     * Deletes timetable with specified identifier
     * 
     * @param timetableId timetable identifier
     * @throws ResourceNotFoundException if no timetable with specified identifier
     */
    void deleteTimetableById(Integer timetableId);

}
