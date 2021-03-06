package org.vdragun.tms.ui.web.controller.student;

import static org.vdragun.tms.util.WebUtil.getFullRequestUri;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.config.WebConstants.Attribute;
import org.vdragun.tms.config.Constants.Message;
import org.vdragun.tms.config.WebConstants.View;

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
    public String showAllStudents(
            Model model,
            Pageable pageable) {
        log.trace("Received GET request to show all students, page number: {}, URI={}",
                pageable.getPageNumber(), getFullRequestUri());
        Page<Student> page = studentService.findStudents(pageable);

        model.addAttribute(Attribute.STUDENTS, page);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.ALL_STUDENTS, page.getTotalElements()));

        return View.STUDENTS;
    }

    @GetMapping("/{studentId}")
    public String showStudentInfo(
            @PathVariable("studentId") Integer studentId,
            Model model) {
        log.trace("Received GET request to show data for student with id={}, URI={}", studentId, getFullRequestUri());
        model.addAttribute(Attribute.STUDENT, studentService.findStudentById(studentId));

        return View.STUDENT_INFO;
    }
}
