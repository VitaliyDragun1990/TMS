package org.vdragun.tms.ui.rest.resource.v1.teacher;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherDTO;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes teacher-related search requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/teachers")
public class SearchTeacherResource extends AbstractResource {

    @Autowired
    private TeacherService teacherService;

    public SearchTeacherResource(ConversionService conversionService) {
        super(conversionService);
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<TeacherDTO> getAllTeachers() {
        log.trace("Received GET request to retrieve all teachers, URI={}", getRequestUri());
        return convertList(teacherService.findAllTeachers(), Teacher.class, TeacherDTO.class);
    }

    @GetMapping("/{teacherId}")
    @ResponseStatus(OK)
    public TeacherDTO getTeacherById(@PathVariable("teacherId") Integer teacherId) {
        log.trace("Received GET request to retrieve teacher with id={}, URI={}", teacherId, getRequestUri());
        return convert(teacherService.findTeacherById(teacherId), TeacherDTO.class);
    }

}
