package org.vdragun.tms.ui.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.StudentService;

/**
 * Processes student-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public String showAllStudents(Model model) {
        model.addAttribute("students", studentService.findAllStudents());
        return "students";
    }

    @GetMapping("/{studentId}")
    public String showStudentInfo(@PathVariable("studentId") Integer studentId, Model model) {
        model.addAttribute("student", studentService.findStudentById(studentId));
        return "student";
    }
}
