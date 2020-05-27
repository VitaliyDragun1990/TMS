package org.vdragun.tms.ui.rest.resource.v1.student;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.student.CreateStudentData;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.application.service.student.UpdateStudentData;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes student-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(path = StudentResource.BASE_URL, produces = AbstractResource.APPLICATION_HAL_JSON)
@Validated
public class StudentResource extends AbstractResource {

    public static final String BASE_URL = "/api/v1/students";

    @Autowired
    private StudentService studentService;

    public StudentResource(ModelConverter converter) {
        super(converter);
    }

    @GetMapping
    @ResponseStatus(OK)
    public CollectionModel<StudentModel> getAllStudents() {
        log.trace("Received GET request to retrieve all students, URI={}", getRequestUri());
        List<StudentModel> list = convertList(studentService.findAllStudents(), Student.class, StudentModel.class);

        return new CollectionModel<>(
                list,
                linkTo(methodOn(StudentResource.class).getAllStudents()).withSelfRel());
    }

    @GetMapping("/{studentId}")
    @ResponseStatus(OK)
    public StudentModel getStudentById(
            @PathVariable("studentId") @Positive(message = Message.POSITIVE_ID) Integer studentId) {
        log.trace("Received GET request to retrieve student with id={}, URI={}", studentId, getRequestUri());
        return convert(studentService.findStudentById(studentId), StudentModel.class);
    }

    @PostMapping
    public ResponseEntity<StudentModel> registerNewStudent(@RequestBody @Valid CreateStudentData studentData) {
        log.trace("Received POST request to register new student, data={}, URI={}", studentData, getRequestUri());

        Student student = studentService.registerNewStudent(studentData);
        StudentModel studentModel = convert(student, StudentModel.class);

        return ResponseEntity
                .created(studentModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(studentModel);
    }

    @PutMapping(path = "/{studentId}")
    @ResponseStatus(OK)
    public StudentModel updateExistingStudent(
            @PathVariable("studentId") @Positive(message = "Positive.id") Integer studentId,
            @RequestBody @Valid UpdateStudentData studentData) {
        log.trace("Received PUT request to update student with id={}, data={} URI={}",
                studentId, studentData, getRequestUri());

        Student student = studentService.updateExistingStudent(studentData);
        return convert(student, StudentModel.class);
    }

    @DeleteMapping("/{studentId}")
    @ResponseStatus(OK)
    public void deleteStudent(
            @PathVariable("studentId") @Positive(message = "Positive.id") Integer studentId) {
        log.trace("Received DELETE request to delete student with id={}, URI={}", studentId, getRequestUri());
        studentService.deleteStudentById(studentId);
    }

}
