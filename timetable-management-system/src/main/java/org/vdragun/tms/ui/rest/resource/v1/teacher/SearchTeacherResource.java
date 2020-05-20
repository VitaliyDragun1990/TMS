package org.vdragun.tms.ui.rest.resource.v1.teacher;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes teacher-related search requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(path = "/api/v1/teachers", produces = "application/hal+json")
public class SearchTeacherResource extends AbstractResource {

    @Autowired
    private TeacherService teacherService;

    public SearchTeacherResource(ConversionService conversionService) {
        super(conversionService);
    }

    @GetMapping
    @ResponseStatus(OK)
    public CollectionModel<TeacherModel> getAllTeachers() {
        log.trace("Received GET request to retrieve all teachers, URI={}", getRequestUri());
        List<TeacherModel> list = convertList(teacherService.findAllTeachers(), Teacher.class, TeacherModel.class);

        return new CollectionModel<>(
                list,
                linkTo(methodOn(SearchTeacherResource.class).getAllTeachers()).withSelfRel());
    }

    @GetMapping("/{teacherId}")
    @ResponseStatus(OK)
    public TeacherModel getTeacherById(@PathVariable("teacherId") Integer teacherId) {
        log.trace("Received GET request to retrieve teacher with id={}, URI={}", teacherId, getRequestUri());
        return convert(teacherService.findTeacherById(teacherId), TeacherModel.class);
    }

}
