package org.vdragun.tms.ui.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.TeacherData;
import org.vdragun.tms.core.application.service.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Title;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * Processes teacher-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/teachers")
public class TeacherController extends AbstractController {

    @Autowired
    private TeacherService teacherService;

    @ModelAttribute("allTitles")
    public List<Title> titles() {
        return new ArrayList<>(Arrays.asList(Title.values()));
    }

    @GetMapping
    public String showAllTeachers(Model model) {
        log.trace("Received GET request to show all teachers, URI={}", getRequestUri());
        List<Teacher> result = teacherService.findAllTeachers();

        model.addAttribute(Attribute.TEACHERS, result);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.ALL_TEACHERS, result.size()));

        return Page.TEACHERS;
    }

    @GetMapping("/{teacherId}")
    public String showTeacherInfo(@PathVariable("teacherId") Integer teacherId, Model model) {
        log.trace("Received GET request to show data for teacher with id={}, URI={}", teacherId, getRequestUri());
        model.addAttribute(Attribute.TEACHER, teacherService.findTeacherById(teacherId));

        return Page.TEACHER_INFO;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.trace("Received GET request to show steacher registration form, URI={}", getRequestUri());
        model.addAttribute(Attribute.TEACHER, new TeacherData());
        
        return Page.TEACHER_FORM;
    }

    @PostMapping
    public String registerNewTeacher(@ModelAttribute TeacherData teacherData, Model model) {
        log.trace("Received POST request to register new teacher, data={}, URI={}", teacherData, getRequestUri());
        Teacher teacher = teacherService.registerNewTeacher(teacherData);
        model.addAttribute(Attribute.TEACHER, teacher);

        return Page.TEACHER_INFO;
    }
}
