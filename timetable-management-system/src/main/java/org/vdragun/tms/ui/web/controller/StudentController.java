package org.vdragun.tms.ui.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vdragun.tms.core.application.service.CourseService;
import org.vdragun.tms.core.application.service.CreateStudentData;
import org.vdragun.tms.core.application.service.GroupService;
import org.vdragun.tms.core.application.service.StudentService;
import org.vdragun.tms.core.application.service.UpdateStudentData;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

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

    @Autowired
    private GroupService groupService;

    @Autowired
    private CourseService courseService;

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
        model.addAttribute(Attribute.GROUPS, groupService.findAllGroups());
        model.addAttribute(Attribute.COURSES, courseService.findAllCourses());

        return Page.STUDENT_INFO;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.trace("Received GET request to show student registration form, URI={}", getRequestUri());
        model.addAttribute(Attribute.STUDENT, new CreateStudentData());

        return Page.STUDENT_FORM;
    }

    @PostMapping("/{studentId}")
    public String updateStudent(
            @PathVariable Integer studentId,
            @ModelAttribute UpdateStudentData studentData,
            Model model,
            RedirectAttributes redirectAttriutes) {
        log.trace("Received POST request to update student with id={}, data={} URI={}",
                studentId, studentData, getRequestUri());
        studentService.updateExistingStudent(studentData);

        redirectAttriutes.addFlashAttribute(
                Attribute.INFO_MESSAGE,
                getMessage(Message.STUDENT_UPDATE_SUCCESS));

        return redirectTo("students/" + studentId);
    }

    @PostMapping("/delete")
    public String deleteStudent(@RequestParam("id") Integer studentId, RedirectAttributes redirectAttributes) {
        log.trace("Received POST request to delete student with id={}, URI={}", studentId, getRequestUri());
        studentService.deleteStudentById(studentId);

        redirectAttributes.addFlashAttribute(
                Attribute.INFO_MESSAGE,
                getMessage(Message.STUDENT_DELETE_SUCCESS, studentId));

        return redirectTo(Page.STUDENTS);
    }

    @PostMapping
    public String registerNewStudent(@ModelAttribute CreateStudentData studentData, Model model) {
        log.trace("Received POST request to register new student, data={}, URI={}", studentData, getRequestUri());
        Student student = studentService.registerNewStudent(studentData);
        model.addAttribute(Attribute.STUDENT, student);

        return redirectTo("students/" + student.getId());
    }
}
