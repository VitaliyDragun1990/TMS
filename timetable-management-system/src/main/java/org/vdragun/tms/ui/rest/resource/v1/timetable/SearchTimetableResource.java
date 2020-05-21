package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.OK;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes timetable-related search requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(path = "/api/v1/timetables", produces = "application/hal+json")
@Validated
public class SearchTimetableResource extends AbstractResource {

    @Autowired
    private TimetableService timetableService;

    public SearchTimetableResource(ConversionService conversionService) {
        super(conversionService);
    }

    @GetMapping
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getAllTimetables() {
        log.trace("Received GET request to retrieve all timetables, URI={}", getRequestUri());
        List<TimetableModel> list = convertList(
                timetableService.findAllTimetables(),
                Timetable.class,
                TimetableModel.class);
        
        return new CollectionModel<>(
                list,
                linkTo(methodOn(SearchTimetableResource.class).getAllTimetables()).withSelfRel());
    }

    @GetMapping("/{timetableId}")
    @ResponseStatus(OK)
    public TimetableModel getTimetableById(
            @PathVariable("timetableId") @Positive(message = "Positive.id") Integer timetableId) {
        log.trace("Received GET request to retrieve timetable with id={}, URI={}", timetableId, getRequestUri());
        return convert(timetableService.findTimetableById(timetableId), TimetableModel.class);
    }

    @GetMapping("/teacher/{teacherId}/day")
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getDailyTimetablesForTeacher(
            @PathVariable("teacherId") @Positive(message = "Positive.id") Integer teacherId,
            @RequestParam("targetDate") LocalDate targetDate) {
        log.trace("Received GET request to retrieve daily timetables for teacher with id={} for date={}, URI={}",
                teacherId, targetDate, getRequestUri());

        List<TimetableModel> list = convertList(
                timetableService.findDailyTimetablesForTeacher(teacherId, targetDate),
                Timetable.class,
                TimetableModel.class);

        return new CollectionModel<>(
                list,
                linkTo(SearchTimetableResource.class)
                        .slash("teacher")
                        .slash(teacherId)
                        .slash("day?targetDate=" + convert(targetDate, String.class))
                        .withSelfRel());
    }

    @GetMapping("/teacher/{teacherId}/month")
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getMonthlyTimetablesForTeacher(
            @PathVariable("teacherId") @Positive(message = "Positive.id") Integer teacherId,
            @RequestParam("targetMonth") Month targetMonth) {
        log.trace("Received GET request to retrieve monthly timetables for teacher with id={} for month={}, URI={}",
                teacherId, targetMonth, getRequestUri());

        List<TimetableModel> list = convertList(
                timetableService.findMonthlyTimetablesForTeacher(teacherId, targetMonth),
                Timetable.class,
                TimetableModel.class);

        return new CollectionModel<>(
                list,
                linkTo(SearchTimetableResource.class)
                        .slash("teacher")
                        .slash(teacherId)
                        .slash("month?targetMonth=" + convert(targetMonth, String.class))
                        .withSelfRel());
    }

    @GetMapping("/student/{studentId}/day")
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getDailyTimetablesForStudent(
            @PathVariable("studentId") @Positive(message = "Positive.id") Integer studentId,
            @RequestParam("targetDate") LocalDate targetDate) {
        log.trace("Received GET request to retrieve daily timetables for student with id={} for date={}, URI={}",
                studentId, targetDate, getRequestUri());

        List<TimetableModel> list = convertList(
                timetableService.findDailyTimetablesForStudent(studentId, targetDate),
                Timetable.class,
                TimetableModel.class);

        return new CollectionModel<>(
                list,
                linkTo(SearchTimetableResource.class)
                        .slash("student")
                        .slash(studentId)
                        .slash("day?targetDate=" + convert(targetDate, String.class))
                        .withSelfRel());
    }

    @GetMapping("/student/{studentId}/month")
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getMonthlyTimetablesForStudent(
            @PathVariable("studentId") @Positive(message = "Positive.id") Integer studentId,
            @RequestParam("targetMonth") Month targetMonth) {
        log.trace("Received GET request to retrieve monthly timetables for student with id={} for month={}, URI={}",
                studentId, targetMonth, getRequestUri());

        List<TimetableModel> list = convertList(
                timetableService.findMonthlyTimetablesForStudent(studentId, targetMonth),
                Timetable.class,
                TimetableModel.class);

        return new CollectionModel<>(
                list,
                linkTo(SearchTimetableResource.class)
                        .slash("student")
                        .slash(studentId)
                        .slash("month?targetMonth=" + convert(targetMonth, String.class))
                        .withSelfRel());
    }

}
