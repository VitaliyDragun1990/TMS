package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.OK;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.timetable.CreateTimetableData;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.application.service.timetable.UpdateTimetableData;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes timetable-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(path = TimetableResource.BASE_URL, produces = AbstractResource.APPLICATION_HAL_JSON)
@Validated
public class TimetableResource extends AbstractResource {

    public static final String BASE_URL = "/api/v1/timetables";

    @Autowired
    private TimetableService timetableService;

    public TimetableResource(ModelConverter converter) {
        super(converter);
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
                linkTo(methodOn(TimetableResource.class).getAllTimetables()).withSelfRel());
    }

    @GetMapping("/{timetableId}")
    @ResponseStatus(OK)
    public TimetableModel getTimetableById(
            @PathVariable("timetableId") @Positive(message = Message.POSITIVE_ID) Integer timetableId) {
        log.trace("Received GET request to retrieve timetable with id={}, URI={}", timetableId, getRequestUri());
        return convert(timetableService.findTimetableById(timetableId), TimetableModel.class);
    }

    @GetMapping("/teacher/{teacherId}/day")
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getDailyTimetablesForTeacher(
            @PathVariable("teacherId") @Positive(message = Message.POSITIVE_ID) Integer teacherId,
            @RequestParam("targetDate") LocalDate targetDate) {
        log.trace("Received GET request to retrieve daily timetables for teacher with id={} for date={}, URI={}",
                teacherId, targetDate, getRequestUri());

        List<TimetableModel> list = convertList(
                timetableService.findDailyTimetablesForTeacher(teacherId, targetDate),
                Timetable.class,
                TimetableModel.class);

        return new CollectionModel<>(
                list,
                linkTo(TimetableResource.class)
                        .slash("teacher")
                        .slash(teacherId)
                        .slash("day?targetDate=" + convert(targetDate, String.class))
                        .withSelfRel());
    }

    @GetMapping("/teacher/{teacherId}/month")
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getMonthlyTimetablesForTeacher(
            @PathVariable("teacherId") @Positive(message = Message.POSITIVE_ID) Integer teacherId,
            @RequestParam("targetMonth") Month targetMonth) {
        log.trace("Received GET request to retrieve monthly timetables for teacher with id={} for month={}, URI={}",
                teacherId, targetMonth, getRequestUri());

        List<TimetableModel> list = convertList(
                timetableService.findMonthlyTimetablesForTeacher(teacherId, targetMonth),
                Timetable.class,
                TimetableModel.class);

        return new CollectionModel<>(
                list,
                linkTo(TimetableResource.class)
                        .slash("teacher")
                        .slash(teacherId)
                        .slash("month?targetMonth=" + convert(targetMonth, String.class))
                        .withSelfRel());
    }

    @GetMapping("/student/{studentId}/day")
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getDailyTimetablesForStudent(
            @PathVariable("studentId") @Positive(message = Message.POSITIVE_ID) Integer studentId,
            @RequestParam("targetDate") LocalDate targetDate) {
        log.trace("Received GET request to retrieve daily timetables for student with id={} for date={}, URI={}",
                studentId, targetDate, getRequestUri());

        List<TimetableModel> list = convertList(
                timetableService.findDailyTimetablesForStudent(studentId, targetDate),
                Timetable.class,
                TimetableModel.class);

        return new CollectionModel<>(
                list,
                linkTo(TimetableResource.class)
                        .slash("student")
                        .slash(studentId)
                        .slash("day?targetDate=" + convert(targetDate, String.class))
                        .withSelfRel());
    }

    @GetMapping("/student/{studentId}/month")
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getMonthlyTimetablesForStudent(
            @PathVariable("studentId") @Positive(message = Message.POSITIVE_ID) Integer studentId,
            @RequestParam("targetMonth") Month targetMonth) {
        log.trace("Received GET request to retrieve monthly timetables for student with id={} for month={}, URI={}",
                studentId, targetMonth, getRequestUri());

        List<TimetableModel> list = convertList(
                timetableService.findMonthlyTimetablesForStudent(studentId, targetMonth),
                Timetable.class,
                TimetableModel.class);

        return new CollectionModel<>(
                list,
                linkTo(TimetableResource.class)
                        .slash("student")
                        .slash(studentId)
                        .slash("month?targetMonth=" + convert(targetMonth, String.class))
                        .withSelfRel());
    }

    @PostMapping
    public ResponseEntity<TimetableModel> registerNewTimetable(@RequestBody @Valid CreateTimetableData timetableData) {
        log.trace("Received POST request to register new timetable, data={}, URI={}", timetableData, getRequestUri());

        Timetable timetable = timetableService.registerNewTimetable(timetableData);
        TimetableModel timetableModel = convert(timetable, TimetableModel.class);

        return ResponseEntity
                .created(timetableModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(timetableModel);
    }

    @PutMapping(path = "/{timetableId}")
    @ResponseStatus(OK)
    public TimetableModel updateExistingTimetable(
            @PathVariable @Positive(message = "Positive.id") Integer timetableId,
            @RequestBody @Valid UpdateTimetableData timetableData) {
        log.trace("Received PUT request to update timetable with id={}, data={}, URI={}",
                timetableId, timetableData, getRequestUri());

        Timetable timetable = timetableService.updateExistingTimetable(timetableData);
        return convert(timetable, TimetableModel.class);
    }

    @DeleteMapping("/{timetableId}")
    @ResponseStatus(OK)
    public void deleteTimetable(@PathVariable("timetableId") @Positive(message = "Positive.id") Integer timetableId) {
        log.trace("Received POST reuqest to delete timetable with id={}, URI={}", timetableId, getRequestUri());
        timetableService.deleteTimetableById(timetableId);
    }

}
