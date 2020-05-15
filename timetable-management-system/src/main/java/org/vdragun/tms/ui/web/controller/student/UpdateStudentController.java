package org.vdragun.tms.ui.web.controller.student;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vdragun.tms.core.application.service.CourseService;
import org.vdragun.tms.core.application.service.GroupService;
import org.vdragun.tms.core.application.service.StudentService;
import org.vdragun.tms.core.application.service.UpdateStudentData;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * Processes student update requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/students")
public class UpdateStudentController extends AbstractController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ConversionService conversionService;

    @InitBinder
    void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @ModelAttribute(Attribute.COURSES)
    List<Course> allCourses() {
        return courseService.findAllCourses();
    }

    @ModelAttribute(Attribute.GROUPS)
    List<Group> allGroups() {
        return groupService.findAllGroups();
    }

    @GetMapping("/{studentId}/update")
    public String showUpdateForm(@PathVariable("studentId") Integer studentId, Model model) {
        log.trace("Received GET request to show student update form for student with id={}, URI={}",
                studentId, getRequestUri());
        model.addAttribute(
                Attribute.STUDENT,
                conversionService.convert(studentService.findStudentById(studentId), UpdateStudentData.class));

        return Page.STUDENT_UPDATE_FORM;
    }

    @PostMapping("/{studentId}")
    public String updateStudent(
            @PathVariable Integer studentId,
            @Valid @ModelAttribute("student") UpdateStudentData studentData,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttriutes) {
        log.trace("Received POST request to update student with id={}, data={} URI={}",
                studentId, studentData, getRequestUri());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> log.trace("Validation error: {}", error));
            model.addAttribute(Attribute.VALIDATED, true);

            return Page.STUDENT_UPDATE_FORM;
        }

        studentService.updateExistingStudent(studentData);
        redirectAttriutes.addFlashAttribute(
                Attribute.INFO_MESSAGE,
                getMessage(Message.STUDENT_UPDATE_SUCCESS));

        return redirectTo("/students/" + studentId);
    }

}
