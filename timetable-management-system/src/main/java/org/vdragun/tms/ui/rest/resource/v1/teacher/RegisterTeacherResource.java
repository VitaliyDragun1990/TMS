package org.vdragun.tms.ui.rest.resource.v1.teacher;

import static org.springframework.http.HttpStatus.CREATED;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.teacher.TeacherData;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes teacher registration requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/teachers")
@Validated
public class RegisterTeacherResource extends AbstractResource {

    @Autowired
    private TeacherService teacherService;

    public RegisterTeacherResource(ModelConverter converter) {
        super(converter);
    }

    @PostMapping(produces = "application/hal+json")
    @ResponseStatus(CREATED)
    public TeacherModel registerNewTeacher(@RequestBody @Valid TeacherData teacherData) {
        log.trace("Received POST request to register new teacher, data={}, URI={}", teacherData, getRequestUri());

        Teacher teacher = teacherService.registerNewTeacher(teacherData);
        return convert(teacher, TeacherModel.class);
    }

}
