package org.vdragun.tms.ui.web.controller.student;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.ui.web.controller.MessageProvider;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.Constants.Page;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = SearchStudentController.class)
@Import({ WebConfig.class, WebMvcConfig.class, MessageProvider.class })
@ActiveProfiles("test")
@DisplayName("Search Student Controller")
public class SearchStudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private StudentService studentServiceMock;

    private EntityGenerator generator = new EntityGenerator();

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    @Test
    void shouldShowAllStudentsPage() throws Exception {
        List<Student> students = generator.generateStudents(10);
        when(studentServiceMock.findAllStudents()).thenReturn(students);

        mockMvc.perform(get("/students").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.STUDENTS, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.STUDENTS, equalTo(students)))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.ALL_STUDENTS, students.size()))))
                .andExpect(view().name(Page.STUDENTS));
    }

    @Test
    void shouldShowStudentInfoPageForStudentWithProvidedIdentifier() throws Exception {
        Student student = generator.generateStudent();
        when(studentServiceMock.findStudentById(student.getId())).thenReturn(student);

        mockMvc.perform(get("/students/{studentId}", student.getId()).locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.STUDENT))
                .andExpect(model().attribute(Attribute.STUDENT, equalTo(student)))
                .andExpect(view().name(Page.STUDENT_INFO));
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
                .andExpect(view().name(Page.NOT_FOUND));
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
                .andExpect(view().name(Page.BAD_REQUEST));
    }

}
