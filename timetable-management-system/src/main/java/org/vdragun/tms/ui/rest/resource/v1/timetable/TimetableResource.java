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
import org.vdragun.tms.ui.rest.exception.ApiError;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller that processes timetable-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(path = TimetableResource.BASE_URL, produces = AbstractResource.APPLICATION_HAL_JSON)
@Validated
@Tag(name = "timetable", description = "the Timetable API")
public class TimetableResource extends AbstractResource {

    public static final String BASE_URL = "/api/v1/timetables";

    @Autowired
    private TimetableService timetableService;

    public TimetableResource(ModelConverter converter) {
        super(converter);
    }

    @Operation(summary = "Find all timetables available", tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = @Content(
                    mediaType = AbstractResource.APPLICATION_HAL_JSON,
                    array = @ArraySchema(schema = @Schema(implementation = TimetableModel.class))))
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

    @Operation(summary = "Find timetable by ID", description = "Returns a single timetable", tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = @Content(
                    mediaType = AbstractResource.APPLICATION_HAL_JSON,
                    schema = @Schema(implementation = TimetableModel.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Timetable not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/{timetableId}")
    @ResponseStatus(OK)
    public TimetableModel getTimetableById(
            @Parameter(description = "Identifier of the timetable to be obtained. Cannot be null or empty.",
                    example = "1")
            @PathVariable("timetableId") @Positive(message = Message.POSITIVE_ID) Integer timetableId) {
        log.trace("Received GET request to retrieve timetable with id={}, URI={}", timetableId, getRequestUri());
        return convert(timetableService.findTimetableById(timetableId), TimetableModel.class);
    }

    @Operation(
            summary = "Find daily timetables for teacher",
            description = "Returns 0 or more timetables", tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = @Content(
                    mediaType = AbstractResource.APPLICATION_HAL_JSON,
                    schema = @Schema(implementation = TimetableModel.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Teacher with provided ID not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/teacher/{teacherId}/day")
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getDailyTimetablesForTeacher(
            @Parameter(
                    description = "Teacher identifier for whom timetables to be obtained. Cannot be null or empty.",
                    example = "1")
            @PathVariable("teacherId") @Positive(message = Message.POSITIVE_ID) Integer teacherId,
            @Parameter(
                    description = "Target date for which timetables to be obtained. Cannot be null or empty.",
                    example = "2020-05-22")
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

    @Operation(
            summary = "Find monthly timetables for teacher",
            description = "Returns 0 or more timetables", tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = @Content(
                    mediaType = AbstractResource.APPLICATION_HAL_JSON,
                    schema = @Schema(implementation = TimetableModel.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Teacher with provided ID not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/teacher/{teacherId}/month")
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getMonthlyTimetablesForTeacher(
            @Parameter(
                    description = "Teacher identifier for whom timetables to be obtained. Cannot be null or empty.",
                    example = "1")
            @PathVariable("teacherId") @Positive(message = Message.POSITIVE_ID) Integer teacherId,
            @Parameter(
                    description = "Target month for which timetables to be obtained. Cannot be null or empty.",
                    example = "May")
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

    @Operation(
            summary = "Find daily timetables for student",
            description = "Returns 0 or more timetables", tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = @Content(
                    mediaType = AbstractResource.APPLICATION_HAL_JSON,
                    schema = @Schema(implementation = TimetableModel.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Student with provided ID not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/student/{studentId}/day")
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getDailyTimetablesForStudent(
            @Parameter(
                    description = "Student identifier for whom timetables to be obtained. Cannot be null or empty.",
                    example = "1")
            @PathVariable("studentId") @Positive(message = Message.POSITIVE_ID) Integer studentId,
            @Parameter(
                    description = "Target date for which timetables to be obtained. Cannot be null or empty.",
                    example = "2020-05-22")
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

    @Operation(
            summary = "Find monthly timetables for student",
            description = "Returns 0 or more timetables", tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = @Content(
                    mediaType = AbstractResource.APPLICATION_HAL_JSON,
                    schema = @Schema(implementation = TimetableModel.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Student with provided ID not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/student/{studentId}/month")
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getMonthlyTimetablesForStudent(
            @Parameter(
                    description = "Student identifier for whom timetables to be obtained. Cannot be null or empty.",
                    example = "1")
            @PathVariable("studentId") @Positive(message = Message.POSITIVE_ID) Integer studentId,
            @Parameter(
                    description = "Target month for which timetables to be obtained. Cannot be null or empty.",
                    example = "May")
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

    @Operation(summary = "Register new timetable record", tags = { "timetable" })
    @ApiResponse(
            responseCode = "201",
            description = "Timetable registered",
            content = @Content(
                    mediaType = AbstractResource.APPLICATION_HAL_JSON,
                    schema = @Schema(implementation = TimetableModel.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<TimetableModel> registerNewTimetable(
            @Parameter(
                    description = "Timetable to register. Cannot be null or empty.",
                    required = true,
                    schema = @Schema(implementation = CreateTimetableData.class))
            @RequestBody @Valid CreateTimetableData timetableData) {
        log.trace("Received POST request to register new timetable, data={}, URI={}", timetableData, getRequestUri());

        Timetable timetable = timetableService.registerNewTimetable(timetableData);
        TimetableModel timetableModel = convert(timetable, TimetableModel.class);

        return ResponseEntity
                .created(timetableModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(timetableModel);
    }

    @Operation(summary = "Update existing timetable record", tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "Timetable updated",
            content = @Content(
                    mediaType = AbstractResource.APPLICATION_HAL_JSON,
                    schema = @Schema(implementation = TimetableModel.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Timetable record to update not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @PutMapping(path = "/{timetableId}")
    @ResponseStatus(OK)
    public TimetableModel updateExistingTimetable(
            @Parameter(description = "Identifier of the timetable to be updated. Cannot be null or empty.",
                    example = "1")
            @PathVariable @Positive(message = "Positive.id") Integer timetableId,
            @Parameter(
                    description = "Data for update. Cannot be null or empty.",
                    required = true,
                    schema = @Schema(implementation = UpdateTimetableData.class))
            @RequestBody @Valid UpdateTimetableData timetableData) {
        log.trace("Received PUT request to update timetable with id={}, data={}, URI={}",
                timetableId, timetableData, getRequestUri());

        Timetable timetable = timetableService.updateExistingTimetable(timetableData);
        return convert(timetable, TimetableModel.class);
    }

    @Operation(summary = "Delete existing timetable record", tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "Timetable deleted")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Timetable record to delete not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
    @DeleteMapping("/{timetableId}")
    @ResponseStatus(OK)
    public void deleteTimetable(
            @Parameter(description = "Identifier of the timetable to be deleted. Cannot be null or empty.",
                    example = "1")
            @PathVariable("timetableId") @Positive(message = "Positive.id") Integer timetableId) {
        log.trace("Received POST reuqest to delete timetable with id={}, URI={}", timetableId, getRequestUri());
        timetableService.deleteTimetableById(timetableId);
    }

}
