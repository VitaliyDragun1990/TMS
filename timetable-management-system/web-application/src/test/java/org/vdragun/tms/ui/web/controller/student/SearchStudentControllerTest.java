package org.vdragun.tms.ui.web.controller.student;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.*;
import org.vdragun.tms.core.application.exception.InvalidPageNumberException;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.security.dao.UserDao;
import org.vdragun.tms.config.EntityGenerator;
import org.vdragun.tms.config.MessageProvider;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.Constants.View;

import java.util.List;
import java.util.Locale;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Vitaliy Dragun
 */
@WebMvcTest(controllers = SearchStudentController.class)
@Import({
        WebConfig.class,
        WebMvcConfig.class,
        ThymeleafConfig.class,
        SecurityConfig.class,
        MessageProvider.class})
@WithMockAuthenticatedUser
@DisplayName("Search Student Controller")
public class SearchStudentControllerTest {

    private static final int MAX_VALID_PAGE_NUMBER = 5;
    private static final int PAGE_SIZE = 20;
    private static final int INVALID_PAGE_NUMBER = 10;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private StudentService studentServiceMock;

    /**
     * In order to create dumb UserService bean
     */
    @MockBean
    private UserDao userDao;

    private EntityGenerator generator = new EntityGenerator();

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    @Test
    void shouldShowAllStudentsPage() throws Exception {
        List<Student> students = generator.generateStudents(10);
        Page<Student> page = new PageImpl<>(students);
        when(studentServiceMock.findStudents(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/students").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.STUDENTS, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.STUDENTS, equalTo(page)))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.ALL_STUDENTS, page.getTotalElements()))))
                .andExpect(view().name(View.STUDENTS));
    }

    @Test
    void shouldShowNotFoundPageIfPageNumberIsInvalid() throws Exception {
        when(studentServiceMock.findStudents(any(Pageable.class)))
                .thenThrow(invalidPageNumberException());

        mockMvc
                .perform(get("/students?page={pageNumber}", INVALID_PAGE_NUMBER).locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/students?page=" + INVALID_PAGE_NUMBER))))
                .andExpect(view().name(View.NOT_FOUND));
    }

    @Test
    void shouldShowStudentInfoPageForStudentWithProvidedIdentifier() throws Exception {
        Student student = generator.generateStudent();
        when(studentServiceMock.findStudentById(student.getId())).thenReturn(student);

        mockMvc.perform(get("/students/{studentId}", student.getId()).locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.STUDENT))
                .andExpect(model().attribute(Attribute.STUDENT, equalTo(student)))
                .andExpect(view().name(View.STUDENT_INFO));
    }

    @Test
    void shouldShowNotFoundPageIfNoStudentWithGivenIdentifier() throws Exception {
        Integer studentId = 1;
        when(studentServiceMock.findStudentById(studentId))
                .thenThrow(new ResourceNotFoundException(Student.class, "Student with id=%d not found", studentId));

        mockMvc.perform(get("/students/{studentId}", studentId).locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/students/" + studentId))))
                .andExpect(view().name(View.NOT_FOUND));
    }

    @Test
    void shouldShowBadRequestPageIfGivenStudentIdentifierIsNotNumber() throws Exception {
        String invalidStudentId = "id";

        mockMvc.perform(get("/students/{studentId}", invalidStudentId).locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", invalidStudentId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/students/" + invalidStudentId))))
                .andExpect(view().name(View.BAD_REQUEST));
    }

    private InvalidPageNumberException invalidPageNumberException() {
        return new InvalidPageNumberException(Student.class, INVALID_PAGE_NUMBER, PAGE_SIZE, MAX_VALID_PAGE_NUMBER);
    }

}
