package org.vdragun.tms.ui.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.TeacherService;

/**
 * Processes teacher-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping
    public String showAllStudents(Model model) {
        model.addAttribute("teachers", teacherService.findAllTeachers());
        return "teachers";
    }

    @GetMapping("/{teacherId}")
    public String showTeacherInfo(@PathVariable("teacherId") Integer teacherId, Model model) {
        model.addAttribute("teacher", teacherService.findTeacherById(teacherId));
        return "teacher";
    }
}
