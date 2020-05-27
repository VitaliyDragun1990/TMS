package org.vdragun.tms.ui.rest.resource.v1.course;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.OK;

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
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes course-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(path = CourseResource.BASE_URL, produces = AbstractResource.APPLICATION_HAL_JSON)
@Validated
public class CourseResource extends AbstractResource {

    public static final String BASE_URL = "/api/v1/courses";

    @Autowired
    private CourseService courseService;

    public CourseResource(ModelConverter modelConverter) {
        super(modelConverter);
    }

    @GetMapping
    @ResponseStatus(OK)
    public CollectionModel<CourseModel> getAllCourses() {
        log.trace("Received GET request to retrieve all courses, URI={}", getRequestUri());
        List<CourseModel> list = convertList(courseService.findAllCourses(), Course.class, CourseModel.class);

        return new CollectionModel<>(
                list,
                linkTo(methodOn(CourseResource.class).getAllCourses()).withSelfRel());
    }

    @GetMapping("/{courseId}")
    @ResponseStatus(OK)
    public CourseModel getCourseById(
            @PathVariable("courseId") @Positive(message = Message.POSITIVE_ID) Integer courseId) {
        log.trace("Received GET request to retrieve course with id={}, URI={}", courseId, getRequestUri());
        return convert(courseService.findCourseById(courseId), CourseModel.class);
    }

    @PostMapping
    public ResponseEntity<CourseModel> registerNewCourse(@Valid @RequestBody CourseData courseData) {
        log.trace("Received POST request to register new course, data={}, URI={}", courseData, getRequestUri());

        Course course = courseService.registerNewCourse(courseData);
        CourseModel courseModel = convert(course, CourseModel.class);

        return ResponseEntity
                .created(courseModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(courseModel);

    }

}
