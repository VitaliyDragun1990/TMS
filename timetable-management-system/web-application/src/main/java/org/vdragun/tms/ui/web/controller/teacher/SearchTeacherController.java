package org.vdragun.tms.ui.web.controller.teacher;

import static org.vdragun.tms.util.WebUtil.getFullRequestUri;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.Constants.View;

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
    public String showAllTeachers(
            Model model,
            Pageable pageable) {
        log.trace("Received GET request to show all teachers, page number: {}, URI={}",
                pageable.getPageNumber(), getFullRequestUri());
        Page<Teacher> page = teacherService.findTeachers(pageable);

        model.addAttribute(Attribute.TEACHERS, page);
        model.addAttribute(Attribute.MESSAGE, getMessage(Message.ALL_TEACHERS, page.getTotalElements()));

        return View.TEACHERS;
    }

    @GetMapping("/{teacherId}")
    public String showTeacherInfo(
            @PathVariable("teacherId") Integer teacherId,
            Model model) {
        log.trace("Received GET request to show data for teacher with id={}, URI={}", teacherId, getFullRequestUri());
        model.addAttribute(Attribute.TEACHER, teacherService.findTeacherById(teacherId));

        return View.TEACHER_INFO;
    }
}
