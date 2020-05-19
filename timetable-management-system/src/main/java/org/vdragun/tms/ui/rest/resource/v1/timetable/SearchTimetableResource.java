package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.springframework.http.HttpStatus.OK;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableDTO;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes timetable-related search requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/timetables")
public class SearchTimetableResource extends AbstractResource {

    @Autowired
    private TimetableService timetableService;

    public SearchTimetableResource(ConversionService conversionService) {
        super(conversionService);
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<TimetableDTO> getAllTimetables() {
        log.trace("Received GET request to retrieve all timetables, URI={}", getRequestUri());
        return convertList(timetableService.findAllTimetables(), Timetable.class, TimetableDTO.class);
    }

    @GetMapping("/{timetableId}")
    @ResponseStatus(OK)
    public TimetableDTO getTimetableById(@PathVariable("timetableId") Integer timetableId) {
        log.trace("Received GET request to retrieve timetable with id={}, URI={}", timetableId, getRequestUri());
        return convert(timetableService.findTimetableById(timetableId), TimetableDTO.class);
    }

    @GetMapping("/teacher/{teacherId}/day")
    @ResponseStatus(OK)
    public List<TimetableDTO> getDailyTimetablesForTeacher(
            @PathVariable("teacherId") Integer teacherId,
            @RequestParam("targetDate") LocalDate targetDate) {
        log.trace("Received GET request to retrieve daily timetables for teacher with id={} for date={}, URI={}",
                teacherId, targetDate, getRequestUri());

        return convertList(
                timetableService.findDailyTimetablesForTeacher(teacherId, targetDate),
                Timetable.class,
                TimetableDTO.class);
    }

    @GetMapping("/teacher/{teacherId}/month")
    @ResponseStatus(OK)
    public List<TimetableDTO> getMonthlyTimetablesForTeacher(
            @PathVariable("teacherId") Integer teacherId,
            @RequestParam("targetMonth") Month targetMonth) {
        log.trace("Received GET request to retrieve monthly timetables for teacher with id={} for month={}, URI={}",
                teacherId, targetMonth, getRequestUri());

        return convertList(
                timetableService.findMonthlyTimetablesForTeacher(teacherId, targetMonth),
                Timetable.class,
                TimetableDTO.class);
    }

    @GetMapping("/student/{studentId}/day")
    @ResponseStatus(OK)
    public List<TimetableDTO> getDailyTimetablesForStudent(
            @PathVariable("studentId") Integer studentId,
            @RequestParam("targetDate") LocalDate targetDate) {
        log.trace("Received GET request to retrieve daily timetables for student with id={} for date={}, URI={}",
                studentId, targetDate, getRequestUri());

        return convertList(
                timetableService.findDailyTimetablesForStudent(studentId, targetDate),
                Timetable.class,
                TimetableDTO.class);
    }

    @GetMapping("/student/{studentId}/month")
    @ResponseStatus(OK)
    public List<TimetableDTO> getMonthlyTimetablesForStudent(
            @PathVariable("studentId") Integer studentId,
            @RequestParam("targetMonth") Month targetMonth) {
        log.trace("Received GET request to retrieve monthly timetables for student with id={} for month={}, URI={}",
                studentId, targetMonth, getRequestUri());

        return convertList(
                timetableService.findMonthlyTimetablesForStudent(studentId, targetMonth),
                Timetable.class,
                TimetableDTO.class);
    }

}
