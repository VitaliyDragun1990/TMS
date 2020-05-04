package org.vdragun.tms.ui.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.web.util.Constants.Message;

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

    @GetMapping
    public String showAllStudents(Model model) {
        List<Teacher> result = teacherService.findAllTeachers();

        model.addAttribute("teachers", result);
        model.addAttribute("msg", getMessage(Message.ALL_TEACHERS, result.size()));

        return "teachers";
    }

    @GetMapping("/{teacherId}")
    public String showTeacherInfo(@PathVariable("teacherId") Integer teacherId, Model model) {
        model.addAttribute("teacher", teacherService.findTeacherById(teacherId));
        return "teacher";
    }
}
