package org.vdragun.tms.ui.web.controller.course;

import static org.vdragun.tms.util.WebUtil.getFullRequestUri;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vdragun.tms.core.application.service.category.CategoryService;
import org.vdragun.tms.core.application.service.course.CourseData;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.config.WebConstants.Attribute;
import org.vdragun.tms.config.Constants.Message;
import org.vdragun.tms.config.WebConstants.View;

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

    @InitBinder
    void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @ModelAttribute
    void allTeachers(Model model) {
        model.addAttribute(Attribute.TEACHERS, teacherService.findAllTeachers());
    }

    @ModelAttribute
    void allCategories(Model model) {
        model.addAttribute(Attribute.CATEGORIES, categoryService.findAllCategories());
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.trace("Received GET request to show course registration from, URI={}", getFullRequestUri());
        model.addAttribute(Attribute.COURSE, new CourseData());

        return View.COURSE_FORM;
    }

    @PostMapping
    public String registerNewCourse(
            @Valid @ModelAttribute(Attribute.COURSE) CourseData courseData,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.trace("Received POST request to register new course, data={}, URI={}", courseData, getFullRequestUri());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> log.trace("Validation error: {}", error));
            model.addAttribute(Attribute.VALIDATED, true);

            return View.COURSE_FORM;
        }

        Course course = courseService.registerNewCourse(courseData);
        redirectAttributes.addFlashAttribute(
                Attribute.INFO_MESSAGE,
                getMessage(Message.COURSE_REGISTER_SUCCESS));

        return redirectTo("/courses/" + course.getId());
    }
}
