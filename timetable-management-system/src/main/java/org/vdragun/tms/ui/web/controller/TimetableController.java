package org.vdragun.tms.ui.web.controller;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.vdragun.tms.core.application.service.TeacherService;
import org.vdragun.tms.core.application.service.TimetableService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.web.converter.StringToLocalDateCustomFormatter;

/**
 * Processes teacher-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/timetables")
public class TimetableController extends AbstractController {

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StringToLocalDateCustomFormatter stringToLocalDateCustomFormatter;

    @InitBinder("targetDate")
    public void setupBinder(WebDataBinder binder) {
        binder.addCustomFormatter(stringToLocalDateCustomFormatter);
    }

    @GetMapping
    public String showAllTimetables(Model model) {
        List<Timetable> result = timetableService.findAllTimetables();

        model.addAttribute("timetables", result);
        model.addAttribute("msg", getMessage("msg.timetablesAll", result.size()));

        return "timetables";
    }

    @PostMapping("/teacher/{teacherId}/day")
    public String showlDailyTimetablesForTeacher(
            @PathVariable("teacherId") Integer teacherId,
            @RequestParam("targetDate") LocalDate targetDate,
            Model model) {

        List<Timetable> result = timetableService.findDailyTimetablesForTeacher(teacherId, targetDate);
        Teacher teacher = teacherService.findTeacherById(teacherId);
        String msg = getMessage("msg.timetablesForTeacher", result.size(), teacher.getFirstName(),
                teacher.getLastName(), formatDate(targetDate));

        model.addAttribute("timetables", result);
        model.addAttribute("msg", msg);

        return "timetables";
    }

    @PostMapping("/teacher/{teacherId}/month")
    public String showMonthlyTimetablesForTeacher(
            @PathVariable("teacherId") Integer teacherId,
            @RequestParam("targetDate") Month targetDate,
            Model model) {

        List<Timetable> result = timetableService.findMonthlyTimetablesForTeacher(teacherId, targetDate);
        Teacher teacher = teacherService.findTeacherById(teacherId);
        String msg = getMessage("msg.timetablesForTeacher", result.size(), teacher.getFirstName(),
                teacher.getLastName(), formatMonth(targetDate));

        model.addAttribute("timetables", result);
        model.addAttribute("msg", msg);

        return "timetables";
    }

}
