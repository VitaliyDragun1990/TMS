package org.vdragun.tms.ui.rest.resource.v1.student;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.student.CreateStudentData;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes student registration requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/students")
public class RegisterStudentResource extends AbstractResource {

    @Autowired
    private StudentService studentService;

    public RegisterStudentResource(ModelConverter converter) {
        super(converter);
    }

    @PostMapping(produces = "application/hal+json")
    public ResponseEntity<StudentModel> registerNewStudent(@RequestBody @Valid CreateStudentData studentData) {
        log.trace("Received POST request to register new student, data={}, URI={}", studentData, getRequestUri());

        Student student = studentService.registerNewStudent(studentData);
        StudentModel studentModel = convert(student, StudentModel.class);

        return ResponseEntity
                .created(studentModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(studentModel);
    }

}
