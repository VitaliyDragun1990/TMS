package org.vdragun.tms.ui.web.controller.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.CategoryService;
import org.vdragun.tms.core.application.service.CourseData;
import org.vdragun.tms.core.application.service.CourseService;
import org.vdragun.tms.core.application.service.TeacherService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * Processes course registration requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/courses")
public class RegisterCourseController extends AbstractController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.trace("Received GET request to show course registration from, URI={}", getRequestUri());
        model.addAttribute(Attribute.COURSE, new CourseData());
        model.addAttribute(Attribute.TEACHERS, teacherService.findAllTeachers());
        model.addAttribute(Attribute.CATEGORIES, categoryService.findAllCategories());

        return Page.COURSE_FORM;
    }

    @PostMapping
    public String registerNewCourse(@ModelAttribute CourseData courseData, Model model) {
        log.trace("Received POST request to register new course, data={}, URI={}", courseData, getRequestUri());
        Course course = courseService.registerNewCourse(courseData);
        model.addAttribute(Attribute.COURSE, course);

        return redirectTo("courses/" + course.getId());
    }
}
