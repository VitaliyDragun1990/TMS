package org.vdragun.tms.ui.web.controller.timetable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.ClassroomService;
import org.vdragun.tms.core.application.service.CourseService;
import org.vdragun.tms.core.application.service.CreateTimetableData;
import org.vdragun.tms.core.application.service.TeacherService;
import org.vdragun.tms.core.application.service.TimetableService;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Page;

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

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.trace("Received GET request to show timetable registration form, URI={}", getRequestUri());
        model.addAttribute(Attribute.TIMETABLE, new CreateTimetableData());
        model.addAttribute(Attribute.COURSES, courseService.findAllCourses());
        model.addAttribute(Attribute.TEACHERS, teacherService.findAllTeachers());
        model.addAttribute(Attribute.CLASSROOMS, classroomService.findAllClassrooms());

        return Page.TIMETABLE_FORM;
    }

    @PostMapping
    public String registerNewTimetable(@ModelAttribute CreateTimetableData timetableData, Model model) {
        log.trace("Received POST request to register new timetable, data={}, URI={}", timetableData, getRequestUri());
        Timetable timetable = timetableService.registerNewTimetable(timetableData);
        model.addAttribute(Attribute.TIMETABLE, timetable);

        return redirectTo("timetables/" + timetable.getId());
    }
}
