package org.vdragun.tms.ui.web.controller.timetable;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.vdragun.tms.util.WebUtil.getFullRequestUri;

import java.time.LocalDate;
import java.time.Month;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.config.WebConstants.Attribute;
import org.vdragun.tms.config.Constants.Message;
import org.vdragun.tms.config.WebConstants.View;

/**
 * Processes timetable-related search requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/timetables")
public class SearchTimetableController extends AbstractController {

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    @GetMapping
    public String showAllTimetables(
            Model model,
            Pageable pageable) {
        log.trace("Received GET request to show all timetables, page number: {}, URI={}",
                pageable.getPageNumber(), getFullRequestUri());
        Page<Timetable> result = timetableService.findTimetables(pageable);

        model.addAttribute(Attribute.TIMETABLES, result);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.ALL_TIMETABLES, result.getTotalElements()));

        return View.TIMETABLES;
    }

    @GetMapping("/{timetableId}")
    public String showTimetableInfo(
            @PathVariable("timetableId") Integer timetableId,
            Model model) {
        log.trace("Received GET request to show data for timetable with id={}, URI={}", timetableId,
                getFullRequestUri());
        model.addAttribute(Attribute.TIMETABLE, timetableService.findTimetableById(timetableId));

        return View.TIMETABLE_INFO;
    }

    @GetMapping("/teacher/{teacherId}/day")
    public String showlDailyTimetablesForTeacher(
            @PathVariable("teacherId") Integer teacherId,
            @RequestParam("targetDate") LocalDate targetDate,
            Model model,
            @SortDefault(sort = "startTime", direction = ASC) Pageable pageable) {
        log.trace(
                "Received GET request to show daily timetables for teacher with id={} for date={}, page number: {}, URI={}",
                teacherId, targetDate, pageable.getPageNumber(), getFullRequestUri());

        Page<Timetable> page = timetableService.findDailyTimetablesForTeacher(teacherId, targetDate, pageable);
        Teacher teacher = teacherService.findTeacherById(teacherId);
        String msg = getMessage(
                Message.TIMETABLES_FOR_TEACHER,
                page.getTotalElements(),
                teacher.getFirstName(),
                teacher.getLastName(),
                formatDate(targetDate));

        model.addAttribute(Attribute.TIMETABLES, page);
        model.addAttribute(Attribute.MESSAGE, msg);

        return View.TIMETABLES;
    }

    @GetMapping("/teacher/{teacherId}/month")
    public String showMonthlyTimetablesForTeacher(
            @PathVariable("teacherId") Integer teacherId,
            @RequestParam("targetDate") Month targetDate,
            Model model,
            @SortDefault(sort = "startTime", direction = ASC) Pageable pageable) {
        log.trace(
                "Received GET request to show monthly timetables for teacher with id={} for month={}, page number: {} URI={}",
                teacherId, targetDate, pageable.getPageNumber(), getFullRequestUri());

        Page<Timetable> page = timetableService.findMonthlyTimetablesForTeacher(teacherId, targetDate, pageable);
        Teacher teacher = teacherService.findTeacherById(teacherId);
        String msg = getMessage(
                Message.TIMETABLES_FOR_TEACHER,
                page.getTotalElements(),
                teacher.getFirstName(),
                teacher.getLastName(),
                formatMonth(targetDate));
        
        model.addAttribute(Attribute.TIMETABLES, page);
        model.addAttribute(Attribute.MESSAGE, msg);

        return View.TIMETABLES;
    }

    @GetMapping("/student/{studentId}/day")
    public String showlDailyTimetablesForStudent(
            @PathVariable("studentId") Integer studentId,
            @RequestParam("targetDate") LocalDate targetDate,
            Model model,
            @SortDefault(sort = "startTime", direction = ASC) Pageable pageable) {
        log.trace(
                "Received GET request to show daily timetables for student with id={} for date={}, page number: {}, URI={}",
                studentId, targetDate, pageable.getPageNumber(), getFullRequestUri());

        Page<Timetable> page = timetableService.findDailyTimetablesForStudent(studentId, targetDate, pageable);
        Student student = studentService.findStudentById(studentId);
        String msg = getMessage(
                Message.TIMETABLES_FOR_STUDENT,
                page.getTotalElements(),
                student.getFirstName(),
                student.getLastName(),
                formatDate(targetDate));
        
        model.addAttribute(Attribute.TIMETABLES, page);
        model.addAttribute(Attribute.MESSAGE, msg);

        return View.TIMETABLES;
    }

    @GetMapping("/student/{studentId}/month")
    public String showMonthlyTimetablesForStudent(
            @PathVariable("studentId") Integer studentId,
            @RequestParam("targetDate") Month targetDate,
            Model model,
            @SortDefault(sort = "startTime", direction = ASC) Pageable pageable) {
        log.trace(
                "Received GET request to show monthly timetables for student with id={} for month={}, page number: {}, URI={}",
                studentId, targetDate, pageable.getPageNumber(), getFullRequestUri());

        Page<Timetable> page = timetableService.findMonthlyTimetablesForStudent(studentId, targetDate, pageable);
        Student student = studentService.findStudentById(studentId);
        String msg = getMessage(
                Message.TIMETABLES_FOR_STUDENT,
                page.getTotalElements(),
                student.getFirstName(),
                student.getLastName(),
                formatMonth(targetDate));

        model.addAttribute(Attribute.TIMETABLES, page);
        model.addAttribute(Attribute.MESSAGE, msg);

        return View.TIMETABLES;
    }
}
