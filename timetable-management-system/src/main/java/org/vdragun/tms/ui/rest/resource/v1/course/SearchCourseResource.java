package org.vdragun.tms.ui.rest.resource.v1.course;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.rest.api.v1.model.CourseDTO;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes course-related search requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/courses")
public class SearchCourseResource extends AbstractResource {

    @Autowired
    private CourseService courseService;

    public SearchCourseResource(ConversionService conversionService) {
        super(conversionService);
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<CourseDTO> getAllCourses() {
        log.trace("Received GET request to retrieve all courses, URI={}", getRequestUri());
        return convertList(courseService.findAllCourses(), Course.class, CourseDTO.class);
    }

    @GetMapping("/{courseId}")
    @ResponseStatus(OK)
    public CourseDTO getCourseById(@PathVariable("courseId") Integer courseId) {
        log.trace("Received GET request to retrieve course with id={}, URI={}", courseId, getRequestUri());
        return convert(courseService.findCourseById(courseId), CourseDTO.class);
    }
}
