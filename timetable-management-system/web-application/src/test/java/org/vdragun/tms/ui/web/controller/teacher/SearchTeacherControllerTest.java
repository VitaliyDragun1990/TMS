package org.vdragun.tms.ui.web.controller.teacher;

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
import org.vdragun.tms.config.Constants.Message;
import org.vdragun.tms.config.EntityGenerator;
import org.vdragun.tms.config.MessageProvider;
import org.vdragun.tms.config.SecurityConfig;
import org.vdragun.tms.config.ThymeleafConfig;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebConstants.Attribute;
import org.vdragun.tms.config.WebConstants.View;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.config.WithMockAuthenticatedUser;
import org.vdragun.tms.core.application.exception.InvalidPageNumberException;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.security.dao.UserDao;

import java.util.List;
import java.util.Locale;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Vitaliy Dragun
 */
@WebMvcTest(controllers = SearchTeacherController.class)
@Import({
        WebConfig.class,
        WebMvcConfig.class,
        ThymeleafConfig.class,
        SecurityConfig.class,
        MessageProvider.class})
@WithMockAuthenticatedUser
@DisplayName("Search Teacher Controller")
public class SearchTeacherControllerTest {

    private static final int MAX_VALID_PAGE_NUMBER = 5;

    private static final int PAGE_SIZE = 20;

    private static final int INVALID_PAGE_NUMBER = 10;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private TeacherService teacherServiceMock;

    /**
     * In order to create dumb UserService bean
     */
    @MockBean
    private UserDao userDao;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldShowAllTeachersPage() throws Exception {
        List<Teacher> teachers = generator.generateTeachers(10);
        Page<Teacher> page = new PageImpl<>(teachers);
        when(teacherServiceMock.findTeachers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/teachers").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TEACHERS, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.TEACHERS, equalTo(page)))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.ALL_TEACHERS, page.getTotalElements()))))
                .andExpect(view().name(View.TEACHERS));
    }

    @Test
    void shouldShowNotFoundPageIfPageNumberIsInvalid() throws Exception {
        when(teacherServiceMock.findTeachers(any(Pageable.class)))
                .thenThrow(invalidPageNumberException());

        mockMvc
                .perform(get("/teachers?page={pageNumber}", INVALID_PAGE_NUMBER).locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/teachers?page=" + INVALID_PAGE_NUMBER))))
                .andExpect(view().name(View.NOT_FOUND));
    }

    @Test
    void shouldShowTeacherInfoPageForTeacherWithProvidedIdentifier() throws Exception {
        Teacher teacher = generator.generateTeacher();
        when(teacherServiceMock.findTeacherById(teacher.getId())).thenReturn(teacher);

        mockMvc.perform(get("/teachers/{teacherId}", teacher.getId()).locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TEACHER))
                .andExpect(model().attribute(Attribute.TEACHER, equalTo(teacher)))
                .andExpect(view().name(View.TEACHER_INFO));
    }

    @Test
    void shouldShowNotFoundPageIfNoTeacherWithGivenIdentifier() throws Exception {
        Integer teacherId = 1;
        when(teacherServiceMock.findTeacherById(teacherId))
                .thenThrow(new ResourceNotFoundException(Teacher.class, "Teacher with id=%d not found", teacherId));

        mockMvc.perform(get("/teachers/{teacherId}", teacherId).locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/teachers/" + teacherId))))
                .andExpect(view().name(View.NOT_FOUND));
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
                .andExpect(view().name(View.BAD_REQUEST));
    }

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    private InvalidPageNumberException invalidPageNumberException() {
        return new InvalidPageNumberException(Teacher.class, INVALID_PAGE_NUMBER, PAGE_SIZE, MAX_VALID_PAGE_NUMBER);
    }

}
