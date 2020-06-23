package org.vdragun.tms.ui.web.controller.timetable;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vdragun.tms.core.application.service.classroom.ClassroomService;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.application.service.timetable.CreateTimetableData;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.Constants.View;

/**
 * Processes timetable registration requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/timetables")
public class RegisterTimetableController extends AbstractController {

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ClassroomService classroomService;

    @ModelAttribute(Attribute.CLASSROOMS)
    List<Classroom> allClassrooms() {
        return classroomService.findAllClassrooms();
    }

    @ModelAttribute(Attribute.COURSES)
    List<Course> allCourses() {
        return courseService.findAllCourses();
    }

    @ModelAttribute(Attribute.TEACHERS)
    List<Teacher> allTeachers() {
        return teacherService.findAllTeachers();
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.trace("Received GET request to show timetable registration form, URI={}", getRequestUri());
        model.addAttribute(Attribute.TIMETABLE, new CreateTimetableData());

        return View.TIMETABLE_REG_FORM;
    }

    @PostMapping
    public String registerNewTimetable(
            @Valid @ModelAttribute(Attribute.TIMETABLE) CreateTimetableData timetableData,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.trace("Received POST request to register new timetable, data={}, URI={}", timetableData, getRequestUri());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> log.trace("Validation error: {}", error));
            model.addAttribute(Attribute.VALIDATED, true);

            return View.TIMETABLE_REG_FORM;
        }

        Timetable timetable = timetableService.registerNewTimetable(timetableData);
        redirectAttributes.addFlashAttribute(
                Attribute.INFO_MESSAGE,
                getMessage(Message.TIMETABLE_REGISTER_SUCCESS));

        return redirectTo("/timetables/" + timetable.getId());
    }
}
