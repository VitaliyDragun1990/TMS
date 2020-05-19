package org.vdragun.tms.ui.rest.resource.v1.student;

import static org.springframework.http.HttpStatus.CREATED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.student.CreateStudentData;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.api.v1.model.StudentDTO;
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

    @InitBinder
    void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    public RegisterStudentResource(ConversionService conversionService) {
        super(conversionService);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public StudentDTO registerNewStudent(@RequestBody CreateStudentData studentData) {
        log.trace("Received POST request to register new student, data={}, URI={}", studentData, getRequestUri());

        Student student = studentService.registerNewStudent(studentData);
        return convert(student, StudentDTO.class);
    }

}
