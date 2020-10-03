package org.vdragun.tms.ui.web.controller.teacher;

import static java.util.Arrays.asList;
import static org.vdragun.tms.util.WebUtil.getFullRequestUri;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vdragun.tms.core.application.service.teacher.TeacherData;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Title;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.config.WebConstants.Attribute;
import org.vdragun.tms.config.Constants.Message;
import org.vdragun.tms.config.WebConstants.View;

/**
 * Processes teacher registration requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/teachers")
public class RegisterTeacherController extends AbstractController {

    @Autowired
    private TeacherService teacherService;

    @InitBinder
    void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @ModelAttribute(Attribute.TITLES)
    public List<Title> titles() {
        return new ArrayList<>(asList(Title.values()));
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.trace("Received GET request to show steacher registration form, URI={}", getFullRequestUri());
        model.addAttribute(Attribute.TEACHER, new TeacherData());

        return View.TEACHER_FORM;
    }

    @PostMapping
    public String registerNewTeacher(
            @Valid @ModelAttribute(Attribute.TEACHER) TeacherData teacherData,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.trace("Received POST request to register new teacher, data={}, URI={}", teacherData, getFullRequestUri());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> log.trace("Validation error: {}", error));
            model.addAttribute(Attribute.VALIDATED, true);

            return View.TEACHER_FORM;
        }

        Teacher teacher = teacherService.registerNewTeacher(teacherData);
        redirectAttributes.addFlashAttribute(
                Attribute.INFO_MESSAGE,
                getMessage(Message.TEACHER_REGISTER_SUCCESS));

        return redirectTo("/teachers/" + teacher.getId());
    }
}
