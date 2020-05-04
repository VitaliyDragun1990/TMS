package org.vdragun.tms.ui.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.web.util.Constants.Message;

/**
 * Processes student-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/students")
public class StudentController extends AbstractController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public String showAllStudents(Model model) {
        log.trace("Received GET request to show all students, URI={}", getRequestUri());

        List<Student> result = studentService.findAllStudents();

        model.addAttribute("students", result);
        model.addAttribute("msg", getMessage(Message.ALL_STUDENTS, result.size()));

        return "students";
    }

    @GetMapping("/{studentId}")
    public String showStudentInfo(@PathVariable("studentId") Integer studentId, Model model) {
        log.trace("Received GET request to show data for student with id={}, URI={}", studentId, getRequestUri());

        model.addAttribute("student", studentService.findStudentById(studentId));
        return "student";
    }
}
