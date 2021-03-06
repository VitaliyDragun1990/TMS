package org.vdragun.tms.ui.rest.resource.v1.teacher;

import static org.springframework.http.HttpStatus.OK;
import static org.vdragun.tms.util.WebUtil.getFullRequestUri;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.rest.exception.ApiError;
import org.vdragun.tms.config.Constants.Message;

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
@RequestMapping(
        path = TeacherResource.BASE_URL,
        produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
@Validated
@Tag(name = "teacher", description = "the Teacher API")
public class TeacherResource {

    private static final Logger LOG = LoggerFactory.getLogger(TeacherResource.class);

    public static final String BASE_URL = "/api/v1/teachers";

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private RepresentationModelAssembler<Teacher, TeacherModel> teacherModelAssembler;

    @Operation(
            summary = "Find page with teachers available",
            tags = { "teacher" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TeacherModel.class))),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(
                                    implementation = TeacherModel.class)))
            })
    @GetMapping
    @ResponseStatus(OK)
    public CollectionModel<TeacherModel> getTeachers(
            @Parameter(
                    required = false,
                    example = "{\"page\": 1, \"size\": 10}") Pageable pageRequest,
            @Parameter(hidden = true) PagedResourcesAssembler<Teacher> pagedAssembler) {
        LOG.trace("Received GET request to retrieve teachers, page request:{}, URI={}",
                pageRequest, getFullRequestUri());

        Page<Teacher> page = teacherService.findTeachers(pageRequest);
        Link selfLink = new Link(getFullRequestUri());

        return pagedAssembler.toModel(page, teacherModelAssembler, selfLink);
    }

    @Operation(
            summary = "Find teacher by ID",
            description = "Returns a single teacher",
            tags = { "teacher" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = TeacherModel.class)),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TeacherModel.class))
            })
    @ApiResponse(
            responseCode = "404",
            description = "Teacher not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/{teacherId}")
    @ResponseStatus(OK)
    public TeacherModel getTeacherById(
            @Parameter(description = "Identifier of the teacher to be obtained. Cannot be null or empty.",
                    example = "1")
            @PathVariable("teacherId") @Positive(message = Message.POSITIVE_ID) Integer teacherId) {
        LOG.trace("Received GET request to retrieve teacher with id={}, URI={}", teacherId, getFullRequestUri());

        return teacherModelAssembler.toModel(teacherService.findTeacherById(teacherId));
    }

    @Operation(
            summary = "Register new teacher record",
            tags = { "teacher" })
    @ApiResponse(
            responseCode = "201",
            description = "Teacher registered",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = TeacherModel.class)),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TeacherModel.class))
            })
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<TeacherModel> registerNewTeacher(
            @Parameter(
                    description = "Teacher to register. Cannot be null or empty.",
                    required = true,
                    schema = @Schema(implementation = TeacherData.class))
            @RequestBody @Valid TeacherData teacherData) {
        LOG.trace("Received POST request to register new teacher, data={}, URI={}", teacherData, getFullRequestUri());

        Teacher teacher = teacherService.registerNewTeacher(teacherData);
        TeacherModel teacherModel = teacherModelAssembler.toModel(teacher);

        return ResponseEntity
                .created(teacherModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(teacherModel);
    }

}
