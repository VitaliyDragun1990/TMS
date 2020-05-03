package org.vdragun.tms.ui.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.CourseService;

/**
 * Processes course-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public String showAllCourses(Model model) {
        model.addAttribute("courses", courseService.findAllCourses());
        return "courses";
    }
}
