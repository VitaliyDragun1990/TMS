package org.vdragun.tms.ui.rest.resource.v1.course;

import static org.springframework.http.HttpStatus.OK;
import static org.vdragun.tms.util.WebUtil.getFullRequestUri;

import java.util.List;

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
import org.vdragun.tms.core.application.service.course.CourseData;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.rest.api.v1.mapper.CourseModelMapper;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.exception.ApiError;
import org.vdragun.tms.config.Constants.Message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller that processes course-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(
        path = CourseResource.BASE_URL,
        produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
@Validated
@Tag(name = "course", description = "the Course API")
public class CourseResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseResource.class);

    public static final String BASE_URL = "/api/v1/courses";

    @Autowired
    private CourseService courseService;

    @Autowired
    private RepresentationModelAssembler<Course, CourseModel> courseModelAssembler;

    @SecurityRequirements
    @Operation(
            summary = "Find page with courses available",
            tags = { "course" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CourseModel.class))),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CourseModel.class)))
            })
    @GetMapping
    @ResponseStatus(OK)
    public CollectionModel<CourseModel> getCourses(
            @Parameter(
                    required = false,
                    example = "{\"page\": 1, \"size\": 10}") Pageable pageRequest,
            @Parameter(hidden = true) PagedResourcesAssembler<Course> pagedAssembler) {
        LOG.trace("Received GET request to retrieve courses, page request:{}, URI={}",
                pageRequest, getFullRequestUri());

        Page<Course> page = courseService.findCourses(pageRequest);
        Link selfLink = new Link(getFullRequestUri());

        return pagedAssembler.toModel(page, courseModelAssembler, selfLink);
    }

    @SecurityRequirements
    @Operation(
            summary = "Find all courses available",
            tags = { "course" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CourseModel.class))),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CourseModel.class)))
            })
    @GetMapping("/all")
    @ResponseStatus(OK)
    public CollectionModel<CourseModel> getAllCourses() {
        LOG.trace("Received GET request to retrieve all courses, URI={}", getFullRequestUri());

        List<Course> courses = courseService.findAllCourses();
        Link selfLink = new Link(getFullRequestUri());

        return new CollectionModel<>(CourseModelMapper.INSTANCE.map(courses), selfLink);
    }

    @SecurityRequirements
    @Operation(
            summary = "Find course by ID",
            description = "Returns a single course",
            tags = { "course" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = CourseModel.class)),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseModel.class))
            })
    @ApiResponse(
            responseCode = "404",
            description = "Course not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/{courseId}")
    @ResponseStatus(OK)
    public CourseModel getCourseById(
            @Parameter(description = "Identifier of the course to be obtained. Cannot be null or empty", example = "1")
            @PathVariable("courseId") @Positive(message = Message.POSITIVE_ID) Integer courseId) {
        LOG.trace("Received GET request to retrieve course with id={}, URI={}", courseId, getFullRequestUri());

        return courseModelAssembler.toModel(courseService.findCourseById(courseId));
    }

    @Operation(
            summary = "Register new course record",
            tags = { "course" })
    @ApiResponse(
            responseCode = "201",
            description = "Course registered",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = CourseModel.class)),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseModel.class))
            })
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<CourseModel> registerNewCourse(
            @Parameter(
                    description = "Course to register. Cannot be null or empty.",
                    required = true,
                    schema = @Schema(implementation = CourseData.class))
            @Valid @RequestBody CourseData courseData) {
        LOG.trace("Received POST request to register new course, data={}, URI={}", courseData, getFullRequestUri());

        Course course = courseService.registerNewCourse(courseData);
        CourseModel courseModel = courseModelAssembler.toModel(course);

        return ResponseEntity
                .created(courseModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(courseModel);

    }

}
