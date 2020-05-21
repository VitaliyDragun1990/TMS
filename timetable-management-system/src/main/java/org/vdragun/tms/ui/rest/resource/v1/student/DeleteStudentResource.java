package org.vdragun.tms.ui.rest.resource.v1.student;

import static org.springframework.http.HttpStatus.OK;

import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes student delete requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/students")
@Validated
public class DeleteStudentResource extends AbstractResource {

    @Autowired
    private StudentService studentService;

    public DeleteStudentResource(ModelConverter converter) {
        super(converter);
    }

    @DeleteMapping("/{studentId}")
    @ResponseStatus(OK)
    public void deleteStudent(
            @PathVariable("studentId") @Positive(message = "Positive.id") Integer studentId) {
        log.trace("Received DELETE request to delete student with id={}, URI={}", studentId, getRequestUri());
        studentService.deleteStudentById(studentId);
    }

}
