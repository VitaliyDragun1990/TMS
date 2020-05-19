package org.vdragun.tms.ui.rest.resource.v1.student;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.api.v1.model.StudentDTO;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes student-related search requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/students")
public class SearchStudentResource extends AbstractResource {

    @Autowired
    private StudentService studentService;

    public SearchStudentResource(ConversionService conversionService) {
        super(conversionService);
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<StudentDTO> getAllStudents() {
        log.trace("Received GET request to retrieve all students, URI={}", getRequestUri());
        return convertList(studentService.findAllStudents(), Student.class, StudentDTO.class);
    }

    @GetMapping("/{studentId}")
    @ResponseStatus(OK)
    public StudentDTO getStudentById(@PathVariable("studentId") Integer studentId) {
        log.trace("Received GET request to retrieve student with id={}, URI={}", studentId, getRequestUri());
        return convert(studentService.findStudentById(studentId), StudentDTO.class);
    }

}
