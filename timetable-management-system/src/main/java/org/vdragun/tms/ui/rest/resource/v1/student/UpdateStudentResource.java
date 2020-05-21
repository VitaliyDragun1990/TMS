package org.vdragun.tms.ui.rest.resource.v1.student;

import static org.springframework.http.HttpStatus.OK;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.application.service.student.UpdateStudentData;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes student update requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/students")
@Validated
public class UpdateStudentResource extends AbstractResource {

    @Autowired
    private StudentService studentService;

    public UpdateStudentResource(ModelConverter converter) {
        super(converter);
    }

    @PutMapping(path = "/{studentId}", produces = "application/hal+json")
    @ResponseStatus(OK)
    public StudentModel updateExistingStudent(
            @PathVariable("studentId") @Positive(message = "Positive.id") Integer studentId,
            @RequestBody @Valid UpdateStudentData studentData) {
        log.trace("Received PUT request to update student with id={}, data={} URI={}",
                studentId, studentData, getRequestUri());

        Student student = studentService.updateExistingStudent(studentData);
        return convert(student, StudentModel.class);
    }

}
