package org.vdragun.tms.ui.web.controller.student;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.common.util.Constants.Attribute;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.common.util.Constants.Page;
import org.vdragun.tms.ui.web.controller.AbstractController;

/**
 * Processes student-related search requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/students")
public class SearchStudentController extends AbstractController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public String showAllStudents(Model model) {
        log.trace("Received GET request to show all students, URI={}", getRequestUri());
        List<Student> result = studentService.findAllStudents();

        model.addAttribute(Attribute.STUDENTS, result);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.ALL_STUDENTS, result.size()));

        return Page.STUDENTS;
    }

    @GetMapping("/{studentId}")
    public String showStudentInfo(@PathVariable("studentId") Integer studentId, Model model) {
        log.trace("Received GET request to show data for student with id={}, URI={}", studentId, getRequestUri());
        model.addAttribute(Attribute.STUDENT, studentService.findStudentById(studentId));

        return Page.STUDENT_INFO;
    }
}
