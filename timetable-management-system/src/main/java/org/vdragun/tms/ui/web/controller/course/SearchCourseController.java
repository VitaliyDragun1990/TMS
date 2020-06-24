package org.vdragun.tms.ui.web.controller.course;

import static org.vdragun.tms.util.WebUtil.getFullRequestUri;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.Constants.View;

/**
 * Processes course-related search requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/courses")
public class SearchCourseController extends AbstractController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public String showAllCourses(
            Model model,
            Pageable pageable) {
        log.trace("Received GET request to show all courses, page number: {}, URI={}",
                pageable.getPageNumber(), getFullRequestUri());
        Page<Course> page = courseService.findCourses(pageable);

        model.addAttribute(Attribute.COURSES, page);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.ALL_COURSES, page.getTotalElements()));

        return View.COURSES;
    }

    @GetMapping("/{courseId}")
    public String showCourseInfo(
            @PathVariable("courseId") Integer courseId,
            Model model) {
        log.trace("Received GET request to show course data for course with id={}, URI={}",
                courseId, getFullRequestUri());
        model.addAttribute(Attribute.COURSE, courseService.findCourseById(courseId));

        return View.COURSE_INFO;
    }

}
