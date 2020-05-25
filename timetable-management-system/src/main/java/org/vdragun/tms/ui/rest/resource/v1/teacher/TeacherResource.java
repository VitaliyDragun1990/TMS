package org.vdragun.tms.ui.rest.resource.v1.teacher;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.teacher.TeacherData;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes teacher-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(path = TeacherResource.BASE_URL, produces = "application/hal+json")
@Validated
public class TeacherResource extends AbstractResource {

    public static final String BASE_URL = "/api/v1/teachers";

    @Autowired
    private TeacherService teacherService;

    public TeacherResource(ModelConverter converter) {
        super(converter);
    }

    @GetMapping
    @ResponseStatus(OK)
    public CollectionModel<TeacherModel> getAllTeachers() {
        log.trace("Received GET request to retrieve all teachers, URI={}", getRequestUri());
        List<TeacherModel> list = convertList(teacherService.findAllTeachers(), Teacher.class, TeacherModel.class);

        return new CollectionModel<>(
                list,
                linkTo(methodOn(TeacherResource.class).getAllTeachers()).withSelfRel());
    }

    @GetMapping("/{teacherId}")
    @ResponseStatus(OK)
    public TeacherModel getTeacherById(
            @PathVariable("teacherId") @Positive(message = Message.POSITIVE_ID) Integer teacherId) {
        log.trace("Received GET request to retrieve teacher with id={}, URI={}", teacherId, getRequestUri());
        return convert(teacherService.findTeacherById(teacherId), TeacherModel.class);
    }

    @PostMapping(produces = "application/hal+json")
    public ResponseEntity<TeacherModel> registerNewTeacher(@RequestBody @Valid TeacherData teacherData) {
        log.trace("Received POST request to register new teacher, data={}, URI={}", teacherData, getRequestUri());

        Teacher teacher = teacherService.registerNewTeacher(teacherData);
        TeacherModel teacherModel = convert(teacher, TeacherModel.class);

        return ResponseEntity
                .created(teacherModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(teacherModel);
    }

}
