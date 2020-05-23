package org.vdragun.tms.ui.web.controller.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;

/**
 * Processes student deletion requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/students")
public class DeleteStudentController extends AbstractController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/delete")
    public String deleteStudent(@RequestParam("id") Integer studentId, RedirectAttributes redirectAttributes) {
        log.trace("Received POST request to delete student with id={}, URI={}", studentId, getRequestUri());
        studentService.deleteStudentById(studentId);

        redirectAttributes.addFlashAttribute(
                Attribute.INFO_MESSAGE,
                getMessage(Message.STUDENT_DELETE_SUCCESS, studentId));

        return redirectTo("/students");
    }
}
