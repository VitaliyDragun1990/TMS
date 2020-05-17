package org.vdragun.tms.ui.web.controller.teacher;

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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.service.teacher.TeacherData;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Title;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.ui.web.controller.MessageProvider;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = RegisterTeacherController.class)
@Import({ WebMvcConfig.class, MessageProvider.class })
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

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    @Test
    void shouldShowTeacherRegistrationForm() throws Exception {
        List<Title> titles = Arrays.asList(Title.values());

        mockMvc.perform(get("/teachers/register").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TEACHER, Attribute.TITLES))
                .andExpect(model().attribute(Attribute.TITLES, equalTo(titles)))
                .andExpect(model().attribute(Attribute.TEACHER, samePropertyValuesAs(new TeacherData())))
                .andExpect(view().name(Page.TEACHER_FORM));
    }

    @Test
    void shouldRegisterNewTeacherIfNoValidationErrors() throws Exception {
        String dateFormat = getMessage(Message.DATE_FORMAT);
        TeacherData teacherData = new TeacherData("Jack", "Smith", LocalDate.now(), Title.PROFESSOR);
        Teacher teacher = generator.generateTeacher();
        when(teacherServiceMock.registerNewTeacher(any(TeacherData.class))).thenReturn(teacher);
        
        mockMvc.perform(post("/teachers").locale(Locale.US)
                .param("firstName", teacherData.getFirstName())
                .param("lastName", teacherData.getLastName())
                .param("title", teacherData.getTitle().toString())
                .param("dateHired", teacherData.getDateHired().format(DateTimeFormatter.ofPattern(dateFormat))))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(Attribute.INFO_MESSAGE,
                        equalTo(getMessage(Message.TEACHER_REGISTER_SUCCESS))))
                .andExpect(redirectedUrlTemplate("/teachers/{teacherId}", teacher.getId()));

        verify(teacherServiceMock, times(1)).registerNewTeacher(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(teacherData));
    }

    @Test
    void shouldShowTeacherRegistrationFormIfValidationErrors() throws Exception {
        String dateFormat = getMessage(Message.DATE_FORMAT);
        String nonLatinFirstName = "Джек";
        String toShortLastName = "J";
        String dateHiredInTheFuture = LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern(dateFormat));

        mockMvc.perform(post("/teachers").locale(Locale.US)
                .param("firstName", nonLatinFirstName)
                .param("lastName", toShortLastName)
                .param("dateHired", dateHiredInTheFuture))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(4))
                .andExpect(model().attributeHasFieldErrors("teacher", "firstName", "lastName", "title", "dateHired"))
                .andExpect(model().attribute(Attribute.VALIDATED, equalTo(true)))
                .andExpect(view().name(Page.TEACHER_FORM));

        verify(teacherServiceMock, never()).registerNewTeacher(any(TeacherData.class));
    }

}
