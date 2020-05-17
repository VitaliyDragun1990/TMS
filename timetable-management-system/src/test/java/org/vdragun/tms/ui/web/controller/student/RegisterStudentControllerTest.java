package org.vdragun.tms.ui.web.controller.student;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.service.student.CreateStudentData;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.ui.web.controller.MessageProvider;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = RegisterStudentController.class)
@Import({ WebMvcConfig.class, MessageProvider.class })
public class RegisterStudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private StudentService studentsServiceMock;

    @Captor
    private ArgumentCaptor<CreateStudentData> captor;

    private EntityGenerator generator = new EntityGenerator();

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    @Test
    void shouldShowStudentRegistrationForm() throws Exception {
        mockMvc.perform(get("/students/register").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.STUDENT))
                .andExpect(model().attribute(Attribute.STUDENT, samePropertyValuesAs(new CreateStudentData())))
                .andExpect(view().name(Page.STUDENT_REG_FORM));
    }

    @Test
    void shouldRegisterNewStudentIfNoValidationErrors() throws Exception {
        String dateFormat = getMessage(Message.DATE_FORMAT);
        CreateStudentData data = new CreateStudentData("Jack", "Smith", LocalDate.now());
        Student student = generator.generateStudent();
        when(studentsServiceMock.registerNewStudent(ArgumentMatchers.any(CreateStudentData.class))).thenReturn(student);

        mockMvc.perform(post("/students").locale(Locale.US)
                .param("firstName", data.getFirstName())
                .param("lastName", data.getLastName())
                .param("enrollmentDate", data.getEnrollmentDate().format(DateTimeFormatter.ofPattern(dateFormat))))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(Attribute.INFO_MESSAGE,
                        equalTo(getMessage(Message.STUDENT_REGISTER_SUCCESS))))
                .andExpect(redirectedUrlTemplate("/students/{studentId}", student.getId()));

        verify(studentsServiceMock, times(1)).registerNewStudent(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(data));
    }

    @Test
    void shouldShowStudentRegistrationFormIfValidationErrors() throws Exception {
        String dateFormat = getMessage(Message.DATE_FORMAT);
        String nonLatinFirstName = "Джек";
        String toShortLastName = "J";
        String enrollmentDateInTheFuture = LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern(dateFormat));

        mockMvc.perform(post("/students").locale(Locale.US)
                .param("firstName", nonLatinFirstName)
                .param("lastName", toShortLastName)
                .param("enrollmentDate", enrollmentDateInTheFuture))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(3))
                .andExpect(model().attributeHasFieldErrors("student", "firstName", "lastName", "enrollmentDate"))
                .andExpect(model().attribute(Attribute.VALIDATED, equalTo(true)))
                .andExpect(view().name(Page.STUDENT_REG_FORM));

        verify(studentsServiceMock, never()).registerNewStudent(any(CreateStudentData.class));
    }

}
