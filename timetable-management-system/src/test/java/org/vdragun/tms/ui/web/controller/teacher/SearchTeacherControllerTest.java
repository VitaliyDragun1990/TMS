package org.vdragun.tms.ui.web.controller.teacher;

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
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.ui.web.controller.MessageProvider;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = SearchTeacherController.class)
@Import({ WebMvcConfig.class, MessageProvider.class })
@DisplayName("Search Teacher Controller")
public class SearchTeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private TeacherService teacherServiceMock;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldShowAllTeachersPage() throws Exception {
        List<Teacher> teachers = generator.generateTeachers(10);
        when(teacherServiceMock.findAllTeachers()).thenReturn(teachers);

        mockMvc.perform(get("/teachers").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TEACHERS, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.TEACHERS, equalTo(teachers)))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.ALL_TEACHERS, teachers.size()))))
                .andExpect(view().name(Page.TEACHERS));
    }

    @Test
    void shouldShowTeacherInfoPageForTeacherWithProvidedIdentifier() throws Exception {
        Teacher teacher = generator.generateTeacher();
        when(teacherServiceMock.findTeacherById(teacher.getId())).thenReturn(teacher);

        mockMvc.perform(get("/teachers/{teacherId}", teacher.getId()).locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TEACHER))
                .andExpect(model().attribute(Attribute.TEACHER, equalTo(teacher)))
                .andExpect(view().name(Page.TEACHER_INFO));
    }

    @Test
    void shouldShowNotFoundPageIfNoTeacherWithGivenIdentifier() throws Exception {
        Integer teacherId = 1;
        when(teacherServiceMock.findTeacherById(teacherId))
                .thenThrow(new ResourceNotFoundException("Teacher with id=%d not found", teacherId));
        
        mockMvc.perform(get("/teachers/{teacherId}", teacherId).locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/teachers/" + teacherId))))
                .andExpect(view().name(Page.NOT_FOUND));
    }

    @Test
    void shouldShowBadRequestPageIfGivenTeacherIdentifierIsNotNumber() throws Exception {
        String teacherId = "id";

        mockMvc.perform(get("/teachers/{teacherId}", teacherId).locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.MESSAGE, Attribute.ERROR))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", teacherId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/teachers/" + teacherId))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

}
