package org.vdragun.tms.ui.web.controller;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.vdragun.tms.core.application.service.StudentService;
import org.vdragun.tms.core.application.service.TeacherService;
import org.vdragun.tms.core.application.service.TimetableService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

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
    private StudentService studentService;

    @GetMapping
    public String showAllTimetables(Model model) {
        log.trace("Received GET request to show all timetables, URI={}", getRequestUri());
        List<Timetable> result = timetableService.findAllTimetables();

        model.addAttribute(Attribute.TIMETABLES, result);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.ALL_TIMETABLES, result.size()));

        return Page.TIMETABLES;
    }

    @GetMapping("/{timetableId}")
    public String showTimetableInfo(@PathVariable("timetableId") Integer timetableId, Model model) {
        log.trace("Received GET request to show data for timetable with id={}, URI={}", timetableId, getRequestUri());
        model.addAttribute(Attribute.TIMETABLE, timetableService.findTimetableById(timetableId));
        
        return Page.TIMETABLE;
    }

    @GetMapping("/teacher/{teacherId}/day")
    public String showlDailyTimetablesForTeacher(
            @PathVariable("teacherId") Integer teacherId,
            @RequestParam("targetDate") LocalDate targetDate,
            Model model) {
        log.trace("Received GET request to show daily timetables for teacher with id={} for date={}, URI={}",
                teacherId, targetDate, getRequestUri());

        List<Timetable> result = timetableService.findDailyTimetablesForTeacher(teacherId, targetDate);
        Teacher teacher = teacherService.findTeacherById(teacherId);
        String msg = getMessage(
                Message.TIMETABLES_FOR_TEACHER,
                result.size(),
                teacher.getFirstName(),
                teacher.getLastName(),
                formatDate(targetDate));

        model.addAttribute(Attribute.TIMETABLES, result);
        model.addAttribute(Attribute.MESSAGE, msg);

        return Page.TIMETABLES;
    }

    @GetMapping("/teacher/{teacherId}/month")
    public String showMonthlyTimetablesForTeacher(
            @PathVariable("teacherId") Integer teacherId,
            @RequestParam("targetDate") Month targetDate,
            Model model) {
        log.trace("Received GET request to show monthly timetables for teacher with id={} for month={}, URI={}",
                teacherId, targetDate, getRequestUri());

        List<Timetable> result = timetableService.findMonthlyTimetablesForTeacher(teacherId, targetDate);
        Teacher teacher = teacherService.findTeacherById(teacherId);
        String msg = getMessage(
                Message.TIMETABLES_FOR_TEACHER,
                result.size(),
                teacher.getFirstName(),
                teacher.getLastName(),
                formatMonth(targetDate));

        model.addAttribute(Attribute.TIMETABLES, result);
        model.addAttribute(Attribute.MESSAGE, msg);

        return Page.TIMETABLES;
    }

    @GetMapping("/student/{studentId}/day")
    public String showlDailyTimetablesForStudent(
            @PathVariable("studentId") Integer studentId,
            @RequestParam("targetDate") LocalDate targetDate,
            Model model) {
        log.trace("Received GET request to show daily timetables for student with id={} for date={}, URI={}",
                studentId, targetDate, getRequestUri());

        List<Timetable> result = timetableService.findDailyTimetablesForStudent(studentId, targetDate);
        Student student = studentService.findStudentById(studentId);
        String msg = getMessage(
                Message.TIMETABLES_FOR_STUDENT,
                result.size(),
                student.getFirstName(),
                student.getLastName(),
                formatDate(targetDate));

        model.addAttribute(Attribute.TIMETABLES, result);
        model.addAttribute(Attribute.MESSAGE, msg);

        return Page.TIMETABLES;
    }

    @GetMapping("/student/{studentId}/month")
    public String showMonthlyTimetablesForStudent(
            @PathVariable("studentId") Integer studentId,
            @RequestParam("targetDate") Month targetDate,
            Model model) {
        log.trace("Received GET request to show monthly timetables for student with id={} for month={}, URI={}",
                studentId, targetDate, getRequestUri());

        List<Timetable> result = timetableService.findMonthlyTimetablesForStudent(studentId, targetDate);
        Student student = studentService.findStudentById(studentId);
        String msg = getMessage(
                Message.TIMETABLES_FOR_STUDENT,
                result.size(),
                student.getFirstName(),
                student.getLastName(),
                formatMonth(targetDate));

        model.addAttribute(Attribute.TIMETABLES, result);
        model.addAttribute(Attribute.MESSAGE, msg);

        return Page.TIMETABLES;
    }

}
