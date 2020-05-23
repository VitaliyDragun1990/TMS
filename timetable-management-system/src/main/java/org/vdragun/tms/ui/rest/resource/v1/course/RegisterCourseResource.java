package org.vdragun.tms.ui.rest.resource.v1.course;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.course.CourseData;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes course registration requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(RegisterCourseResource.BASE_URL)
public class RegisterCourseResource extends AbstractResource {

    public static final String BASE_URL = "/api/v1/courses";

    @Autowired
    private CourseService courseService;

    public RegisterCourseResource(ModelConverter converter) {
        super(converter);
    }

    @PostMapping(produces = "application/hal+json")
    public ResponseEntity<CourseModel> registerNewCourse(@Valid @RequestBody CourseData courseData) {
        log.trace("Received POST request to register new course, data={}, URI={}", courseData, getRequestUri());

        Course course = courseService.registerNewCourse(courseData);
        CourseModel courseModel = convert(course, CourseModel.class);

        return ResponseEntity
                .created(courseModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(courseModel);

    }

}
