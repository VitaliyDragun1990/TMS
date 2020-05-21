package org.vdragun.tms.ui.rest.resource.v1.student;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes student-related search requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(path = "/api/v1/students", produces = "application/hal+json")
@Validated
public class SearchStudentResource extends AbstractResource {

    @Autowired
    private StudentService studentService;

    public SearchStudentResource(ConversionService conversionService) {
        super(conversionService);
    }

    @GetMapping
    @ResponseStatus(OK)
    public CollectionModel<StudentModel> getAllStudents() {
        log.trace("Received GET request to retrieve all students, URI={}", getRequestUri());
        List<StudentModel> list = convertList(studentService.findAllStudents(), Student.class, StudentModel.class);

        return new CollectionModel<>(
                list,
                linkTo(methodOn(SearchStudentResource.class).getAllStudents()).withSelfRel());
    }

    @GetMapping("/{studentId}")
    @ResponseStatus(OK)
    public StudentModel getStudentById(
            @PathVariable("studentId") @Positive(message = "Positive.id") Integer studentId) {
        log.trace("Received GET request to retrieve student with id={}, URI={}", studentId, getRequestUri());
        return convert(studentService.findStudentById(studentId), StudentModel.class);
    }

}
