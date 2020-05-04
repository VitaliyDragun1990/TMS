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
import org.vdragun.tms.core.application.service.StudentService;
import org.vdragun.tms.core.application.service.TeacherService;
import org.vdragun.tms.core.application.service.TimetableService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.web.converter.StringToLocalDateCustomFormatter;
import org.vdragun.tms.ui.web.util.Constants.Message;

/**
 * Processes teacher-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/timetables")
public class TimetableController extends AbstractController {

    private static final String TIMETABLES_PAGE = "timetables";

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

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
        model.addAttribute("msg", getMessage(Message.ALL_TIMETABLES, result.size()));

        return TIMETABLES_PAGE;
    }

    @PostMapping("/teacher/{teacherId}/day")
    public String showlDailyTimetablesForTeacher(
            @PathVariable("teacherId") Integer teacherId,
            @RequestParam("targetDate") LocalDate targetDate,
            Model model) {

        List<Timetable> result = timetableService.findDailyTimetablesForTeacher(teacherId, targetDate);
        Teacher teacher = teacherService.findTeacherById(teacherId);
        String msg = getMessage(Message.TIMETABLES_FOR_TEACHER, result.size(), teacher.getFirstName(),
                teacher.getLastName(), formatDate(targetDate));

        model.addAttribute("timetables", result);
        model.addAttribute("msg", msg);

        return TIMETABLES_PAGE;
    }

    @PostMapping("/teacher/{teacherId}/month")
    public String showMonthlyTimetablesForTeacher(
            @PathVariable("teacherId") Integer teacherId,
            @RequestParam("targetDate") Month targetDate,
            Model model) {

        List<Timetable> result = timetableService.findMonthlyTimetablesForTeacher(teacherId, targetDate);
        Teacher teacher = teacherService.findTeacherById(teacherId);
        String msg = getMessage(Message.TIMETABLES_FOR_TEACHER, result.size(), teacher.getFirstName(),
                teacher.getLastName(), formatMonth(targetDate));

        model.addAttribute("timetables", result);
        model.addAttribute("msg", msg);

        return TIMETABLES_PAGE;
    }

    @PostMapping("/student/{studentId}/day")
    public String showlDailyTimetablesForStudent(
            @PathVariable("studentId") Integer studentId,
            @RequestParam("targetDate") LocalDate targetDate,
            Model model) {

        List<Timetable> result = timetableService.findDailyTimetablesForStudent(studentId, targetDate);
        Student student = studentService.findStudentById(studentId);
        String msg = getMessage(Message.TIMETABLES_FOR_STUDENT, result.size(), student
                .getFirstName(), student.getLastName(), formatDate(targetDate));

        model.addAttribute("timetables", result);
        model.addAttribute("msg", msg);

        return TIMETABLES_PAGE;
    }

    @PostMapping("/student/{studentId}/month")
    public String showMonthlyTimetablesForStudent(
            @PathVariable("studentId") Integer studentId,
            @RequestParam("targetDate") Month targetDate,
            Model model) {

        List<Timetable> result = timetableService.findMonthlyTimetablesForStudent(studentId, targetDate);
        Student student = studentService.findStudentById(studentId);
        String msg = getMessage(Message.TIMETABLES_FOR_STUDENT, result.size(), student.getFirstName(),
                student.getLastName(), formatMonth(targetDate));

        model.addAttribute("timetables", result);
        model.addAttribute("msg", msg);

        return TIMETABLES_PAGE;
    }

}
