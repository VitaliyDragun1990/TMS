package org.vdragun.tms.ui.rest.resource.v1.course;

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
import org.vdragun.tms.core.application.service.course.CourseData;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
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
 * REST controller that processes course-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(path = CourseResource.BASE_URL, produces = AbstractResource.APPLICATION_HAL_JSON)
@Validated
@Tag(name = "course", description = "the Course API")
public class CourseResource extends AbstractResource {

    public static final String BASE_URL = "/api/v1/courses";

    @Autowired
    private CourseService courseService;

    public CourseResource(ModelConverter modelConverter) {
        super(modelConverter);
    }

    @Operation(summary = "Find all courses available", tags = { "course" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = @Content(
                    mediaType = APPLICATION_HAL_JSON,
                    array = @ArraySchema(schema = @Schema(implementation = CourseModel.class))))
    @GetMapping
    @ResponseStatus(OK)
    public CollectionModel<CourseModel> getAllCourses() {
        log.trace("Received GET request to retrieve all courses, URI={}", getRequestUri());
        List<CourseModel> list = convertList(courseService.findAllCourses(), Course.class, CourseModel.class);

        return new CollectionModel<>(
                list,
                linkTo(methodOn(CourseResource.class).getAllCourses()).withSelfRel());
    }

    @Operation(summary = "Find course by ID", description = "Returns a single course", tags = { "course" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = @Content(
                    mediaType = APPLICATION_HAL_JSON,
                    schema = @Schema(implementation = CourseModel.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Course not found",
            content = @Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/{courseId}")
    @ResponseStatus(OK)
    public CourseModel getCourseById(
            @Parameter(description = "Identifier of the course to be obtained. Cannot be null or empty", example = "1")
            @PathVariable("courseId") @Positive(message = Message.POSITIVE_ID) Integer courseId) {
        log.trace("Received GET request to retrieve course with id={}, URI={}", courseId, getRequestUri());
        return convert(courseService.findCourseById(courseId), CourseModel.class);
    }

    @Operation(summary = "Register new course record", tags = { "course" })
    @ApiResponse(
            responseCode = "201",
            description = "Course registered",
            content = @Content(
                    mediaType = APPLICATION_HAL_JSON,
                    schema = @Schema(implementation = CourseModel.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<CourseModel> registerNewCourse(
            @Parameter(
                    description = "Course to register. Cannot be null or empty.",
                    required = true,
                    schema = @Schema(implementation = CourseData.class))
            @Valid @RequestBody CourseData courseData) {
        log.trace("Received POST request to register new course, data={}, URI={}", courseData, getRequestUri());

        Course course = courseService.registerNewCourse(courseData);
        CourseModel courseModel = convert(course, CourseModel.class);

        return ResponseEntity
                .created(courseModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(courseModel);

    }

}
