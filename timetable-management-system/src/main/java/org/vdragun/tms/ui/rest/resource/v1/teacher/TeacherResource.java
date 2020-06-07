package org.vdragun.tms.ui.rest.resource.v1.teacher;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.teacher.TeacherData;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.rest.exception.ApiError;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;
import org.vdragun.tms.util.Constants.Message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller that processes teacher-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(path = TeacherResource.BASE_URL, produces = AbstractResource.APPLICATION_HAL_JSON)
@Validated
@Tag(name = "teacher", description = "the Teacher API")
public class TeacherResource extends AbstractResource {

    public static final String BASE_URL = "/api/v1/teachers";

    @Autowired
    private TeacherService teacherService;

    public TeacherResource(ModelConverter converter) {
        super(converter);
    }

    @Operation(summary = "Find all teachers available", tags = { "teacher" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = @Content(
                    mediaType = APPLICATION_HAL_JSON,
                    array = @ArraySchema(schema = @Schema(implementation = TeacherModel.class))))
    @GetMapping
    @ResponseStatus(OK)
    public CollectionModel<TeacherModel> getAllTeachers() {
        log.trace("Received GET request to retrieve all teachers, URI={}", getRequestUri());
        List<TeacherModel> list = convertList(teacherService.findAllTeachers(), Teacher.class, TeacherModel.class);

        return new CollectionModel<>(
                list,
                linkTo(methodOn(TeacherResource.class).getAllTeachers()).withSelfRel());
    }

    @Operation(summary = "Find teacher by ID", description = "Returns a single teacher", tags = { "teacher" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = @Content(
                    mediaType = APPLICATION_HAL_JSON,
                    schema = @Schema(implementation = TeacherModel.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Teacher not found",
            content = @Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/{teacherId}")
    @ResponseStatus(OK)
    public TeacherModel getTeacherById(
            @Parameter(description = "Identifier of the teacher to be obtained. Cannot be null or empty.",
                    example = "1")
            @PathVariable("teacherId") @Positive(message = Message.POSITIVE_ID) Integer teacherId) {
        log.trace("Received GET request to retrieve teacher with id={}, URI={}", teacherId, getRequestUri());
        return convert(teacherService.findTeacherById(teacherId), TeacherModel.class);
    }

    @Operation(summary = "Register new teacher record", tags = { "teacher" })
    @ApiResponse(
            responseCode = "201",
            description = "Teacher registered",
            content = @Content(
                    mediaType = APPLICATION_HAL_JSON,
                    schema = @Schema(implementation = TeacherModel.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<TeacherModel> registerNewTeacher(
            @Parameter(
                    description = "Teacher to register. Cannot be null or empty.",
                    required = true,
                    schema = @Schema(implementation = TeacherData.class))
            @RequestBody @Valid TeacherData teacherData) {
        log.trace("Received POST request to register new teacher, data={}, URI={}", teacherData, getRequestUri());

        Teacher teacher = teacherService.registerNewTeacher(teacherData);
        TeacherModel teacherModel = convert(teacher, TeacherModel.class);

        return ResponseEntity
                .created(teacherModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(teacherModel);
    }

}
