package org.vdragun.tms.ui.web.controller.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vdragun.tms.core.application.service.StudentService;
import org.vdragun.tms.core.application.service.UpdateStudentData;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;

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

}
