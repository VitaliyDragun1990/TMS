package org.vdragun.tms.ui.web.controller.teacher;

import static java.util.Arrays.asList;
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
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.service.teacher.TeacherData;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Title;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.ui.web.controller.MessageProvider;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.Constants.Page;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = RegisterTeacherController.class)
@Import({ WebConfig.class, WebMvcConfig.class, MessageProvider.class })
@ActiveProfiles("test")
@DisplayName("Register Teacher Controller")
public class RegisterTeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private TeacherService teacherServiceMock;

    @Captor
    private ArgumentCaptor<TeacherData> captor;

    private EntityGenerator generator = new EntityGenerator();

    private String dateFormat;

    @BeforeEach
    void setUp() {
        dateFormat = getMessage(Message.DATE_FORMAT);
    }

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(dateFormat));
    }

    @Test
    void shouldShowTeacherRegistrationForm() throws Exception {
        List<Title> titles = asList(Title.values());

        mockMvc.perform(get("/teachers/register").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TEACHER, Attribute.TITLES))
                .andExpect(model().attribute(Attribute.TITLES, equalTo(titles)))
                .andExpect(model().attribute(Attribute.TEACHER, samePropertyValuesAs(new TeacherData())))
                .andExpect(view().name(Page.TEACHER_FORM));
    }

    @Test
    void shouldRegisterNewTeacherIfNoValidationErrors() throws Exception {
        String firstName = "Jack";
        String lastName = "Smith";
        LocalDate dateHired = LocalDate.now();
        Title title = Title.PROFESSOR;
        
        Teacher registered = generator.generateTeacher();
        when(teacherServiceMock.registerNewTeacher(any(TeacherData.class))).thenReturn(registered);
        
        mockMvc.perform(post("/teachers").locale(Locale.US)
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("title", title.toString())
                .param("dateHired", formatDate(dateHired)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(Attribute.INFO_MESSAGE,
                        equalTo(getMessage(Message.TEACHER_REGISTER_SUCCESS))))
                .andExpect(redirectedUrlTemplate("/teachers/{teacherId}", registered.getId()));

        TeacherData expected = new TeacherData(firstName, lastName, dateHired, title);
        verify(teacherServiceMock, times(1)).registerNewTeacher(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(expected));
    }

    @Test
    void shouldShowTeacherRegistrationFormIfValidationErrors() throws Exception {
        String nonLatinFirstName = "Джек";
        String toShortLastName = "J";
        LocalDate dateHiredInTheFuture = LocalDate.now().plusDays(5);

        mockMvc.perform(post("/teachers").locale(Locale.US)
                .param("firstName", nonLatinFirstName)
                .param("lastName", toShortLastName)
                .param("dateHired", formatDate(dateHiredInTheFuture)))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(4))
                .andExpect(model().attributeHasFieldErrors("teacher", "firstName", "lastName", "title", "dateHired"))
                .andExpect(model().attribute(Attribute.VALIDATED, equalTo(true)))
                .andExpect(view().name(Page.TEACHER_FORM));

        verify(teacherServiceMock, never()).registerNewTeacher(any(TeacherData.class));
    }

}
