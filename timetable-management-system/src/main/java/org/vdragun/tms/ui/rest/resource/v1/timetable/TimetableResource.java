package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.HttpStatus.OK;
import static org.vdragun.tms.util.WebUtil.getFullRequestUri;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.MediaType;
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
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.ui.rest.exception.ApiError;
import org.vdragun.tms.util.Constants.Message;

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
@RequestMapping(
        path = TimetableResource.BASE_URL,
        produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
@Validated
@Tag(name = "timetable", description = "the Timetable API")
public class TimetableResource {

    private static final Logger LOG = LoggerFactory.getLogger(TimetableResource.class);

    public static final String BASE_URL = "/api/v1/timetables";

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private RepresentationModelAssembler<Timetable, TimetableModel> timetableModelAssembler;
    
    @Autowired
    private ConversionService conversionService;

    @Operation(
            summary = "Find all timetables available",
            tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TimetableModel.class))),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TimetableModel.class)))
            })
    @GetMapping
    @ResponseStatus(OK)
    public CollectionModel<TimetableModel> getTimetables(
            @Parameter(
                    required = false,
                    example = "{\"page\": 1, \"size\": 10}") Pageable pageRequest,
            @Parameter(hidden = true) PagedResourcesAssembler<Timetable> pagedAssembler) {

        LOG.trace("Received GET request to retrieve timetables, page request:{}, URI={}",
                pageRequest, getFullRequestUri());

        Page<Timetable> page = timetableService.findTimetables(pageRequest);
        Link selfLink = new Link(getFullRequestUri());

        return pagedAssembler.toModel(page, timetableModelAssembler, selfLink);
    }

    @Operation(
            summary = "Find timetable by ID",
            description = "Returns a single timetable",
            tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = TimetableModel.class)),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TimetableModel.class)),
            })
    @ApiResponse(
            responseCode = "404",
            description = "Timetable not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/{timetableId}")
    @ResponseStatus(OK)
    public TimetableModel getTimetableById(
            @Parameter(description = "Identifier of the timetable to be obtained. Cannot be null or empty.",
                    example = "1")
            @PathVariable("timetableId") @Positive(message = Message.POSITIVE_ID) Integer timetableId) {

        LOG.trace("Received GET request to retrieve timetable with id={}, URI={}",
                timetableId, getFullRequestUri());

        return timetableModelAssembler.toModel(timetableService.findTimetableById(timetableId));
    }

    @Operation(
            summary = "Find daily timetables for teacher",
            description = "Returns 0 or more timetables",
            tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TimetableModel.class))),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TimetableModel.class)))
            })
    @ApiResponse(
            responseCode = "404",
            description = "Teacher with provided ID not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
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
            @RequestParam("targetDate") LocalDate targetDate,
            @Parameter(
                    required = false,
                    example = "{\"page\": 1, \"size\": 10}") Pageable pageRequest,
            @Parameter(hidden = true) PagedResourcesAssembler<Timetable> pagedAssembler) {

        LOG.trace(
                "Received GET request to retrieve daily timetables for teacher with id={} for date={}, page request:{}, URI={}",
                teacherId, targetDate, pageRequest, getFullRequestUri());

        Page<Timetable> page = timetableService.findDailyTimetablesForTeacher(teacherId, targetDate, pageRequest);
        Link selfLink = new Link(getFullRequestUri());

        return pagedAssembler.toModel(page, timetableModelAssembler, selfLink);
    }

    @Operation(
            summary = "Find monthly timetables for teacher",
            description = "Returns 0 or more timetables",
            tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TimetableModel.class))),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TimetableModel.class)))
            })
    @ApiResponse(
            responseCode = "404",
            description = "Teacher with provided ID not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
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

        LOG.trace("Received GET request to retrieve monthly timetables for teacher with id={} for month={}, URI={}",
                teacherId, targetMonth, getFullRequestUri());

        List<Timetable> timetables = timetableService.findMonthlyTimetablesForTeacher(teacherId, targetMonth);
        CollectionModel<TimetableModel> result = timetableModelAssembler.toCollectionModel(timetables);
        result.add(linkTo(
                TimetableResource.class)
                        .slash("teacher")
                        .slash(teacherId)
                        .slash("month?targetMonth=" + conversionService.convert(targetMonth, String.class))
                        .withSelfRel());

        return result;
    }

    @Operation(
            summary = "Find daily timetables for student",
            description = "Returns 0 or more timetables",
            tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TimetableModel.class))),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TimetableModel.class)))
            })
    @ApiResponse(
            responseCode = "404",
            description = "Student with provided ID not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
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

        LOG.trace("Received GET request to retrieve daily timetables for student with id={} for date={}, URI={}",
                studentId, targetDate, getFullRequestUri());

        List<Timetable> timetables = timetableService.findDailyTimetablesForStudent(studentId, targetDate);
        CollectionModel<TimetableModel> result = timetableModelAssembler.toCollectionModel(timetables);
        result.add(linkTo(
                TimetableResource.class)
                        .slash("student")
                        .slash(studentId)
                        .slash("day?targetDate=" + conversionService.convert(targetDate, String.class))
                        .withSelfRel());

        return result;
    }

    @Operation(
            summary = "Find monthly timetables for student",
            description = "Returns 0 or more timetables",
            tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TimetableModel.class))),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TimetableModel.class)))
            })
    @ApiResponse(
            responseCode = "404",
            description = "Student with provided ID not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
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

        LOG.trace("Received GET request to retrieve monthly timetables for student with id={} for month={}, URI={}",
                studentId, targetMonth, getFullRequestUri());

        List<Timetable> timetables = timetableService.findMonthlyTimetablesForStudent(studentId, targetMonth);
        CollectionModel<TimetableModel> result = timetableModelAssembler.toCollectionModel(timetables);
        result.add(linkTo(
                TimetableResource.class)
                        .slash("student")
                        .slash(studentId)
                        .slash("month?targetMonth=" + conversionService.convert(targetMonth, String.class))
                        .withSelfRel());

        return result;
    }

    @Operation(
            summary = "Register new timetable record",
            tags = { "timetable" })
    @ApiResponse(
            responseCode = "201",
            description = "Timetable registered",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = TimetableModel.class)),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TimetableModel.class))
            })
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<TimetableModel> registerNewTimetable(
            @Parameter(
                    description = "Timetable to register. Cannot be null or empty.",
                    required = true,
                    schema = @Schema(implementation = CreateTimetableData.class))
            @RequestBody @Valid CreateTimetableData timetableData) {

        LOG.trace("Received POST request to register new timetable, data={}, URI={}",
                timetableData, getFullRequestUri());

        Timetable timetable = timetableService.registerNewTimetable(timetableData);
        TimetableModel timetableModel = timetableModelAssembler.toModel(timetable);

        return ResponseEntity
                .created(timetableModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(timetableModel);
    }

    @Operation(
            summary = "Update existing timetable record",
            tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "Timetable updated",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = TimetableModel.class)),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TimetableModel.class))
            })
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Timetable record to update not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
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

        LOG.trace("Received PUT request to update timetable with id={}, data={}, URI={}",
                timetableId, timetableData, getFullRequestUri());
        Timetable timetable = timetableService.updateExistingTimetable(timetableData);

        return timetableModelAssembler.toModel(timetable);
    }

    @Operation(
            summary = "Delete existing timetable record",
            tags = { "timetable" })
    @ApiResponse(
            responseCode = "200",
            description = "Timetable deleted")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Timetable record to delete not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @DeleteMapping("/{timetableId}")
    @ResponseStatus(OK)
    public void deleteTimetable(
            @Parameter(description = "Identifier of the timetable to be deleted. Cannot be null or empty.",
                    example = "1")
            @PathVariable("timetableId") @Positive(message = "Positive.id") Integer timetableId) {

        LOG.trace("Received POST reuqest to delete timetable with id={}, URI={}", timetableId, getFullRequestUri());
        timetableService.deleteTimetableById(timetableId);
    }

}
