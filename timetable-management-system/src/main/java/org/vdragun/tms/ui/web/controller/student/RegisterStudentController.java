package org.vdragun.tms.ui.web.controller.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.CreateStudentData;
import org.vdragun.tms.core.application.service.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * Processes student registration requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/students")
public class RegisterStudentController extends AbstractController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.trace("Received GET request to show student registration form, URI={}", getRequestUri());
        model.addAttribute(Attribute.STUDENT, new CreateStudentData());

        return Page.STUDENT_REG_FORM;
    }

    @PostMapping
    public String registerNewStudent(@ModelAttribute CreateStudentData studentData, Model model) {
        log.trace("Received POST request to register new student, data={}, URI={}", studentData, getRequestUri());
        Student student = studentService.registerNewStudent(studentData);
        model.addAttribute(Attribute.STUDENT, student);

        return redirectTo("/students/" + student.getId());
    }
}
