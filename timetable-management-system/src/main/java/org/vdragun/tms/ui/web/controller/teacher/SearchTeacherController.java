package org.vdragun.tms.ui.web.controller.teacher;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.common.util.Constants.Attribute;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.common.util.Constants.Page;
import org.vdragun.tms.ui.web.controller.AbstractController;

/**
 * Processes teacher-related search requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/teachers")
public class SearchTeacherController extends AbstractController {

    @Autowired
    private TeacherService teacherService;

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
}
