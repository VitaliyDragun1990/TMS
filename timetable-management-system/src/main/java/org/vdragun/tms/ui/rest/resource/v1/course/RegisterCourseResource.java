package org.vdragun.tms.ui.rest.resource.v1.course;

import static org.springframework.http.HttpStatus.CREATED;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.course.CourseData;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes course registration requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/courses")
public class RegisterCourseResource extends AbstractResource {

    @Autowired
    private CourseService courseService;

    public RegisterCourseResource(ConversionService conversionService) {
        super(conversionService);
    }

    @PostMapping(produces = "application/hal+json")
    @ResponseStatus(CREATED)
    public CourseModel registerNewCourse(@Valid @RequestBody CourseData courseData) {
        log.trace("Received POST request to register new course, data={}, URI={}", courseData, getRequestUri());

        Course course = courseService.registerNewCourse(courseData);
        return convert(course, CourseModel.class);
    }

}
