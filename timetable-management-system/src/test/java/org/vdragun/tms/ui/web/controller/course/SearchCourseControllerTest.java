package org.vdragun.tms.ui.web.controller.course;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.SecurityConfig;
import org.vdragun.tms.config.ThymeleafConfig;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.exception.InvalidPageNumberException;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.security.dao.UserDao;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.ui.web.controller.MessageProvider;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.Constants.View;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = SearchCourseController.class)
@Import({
        WebConfig.class,
        WebMvcConfig.class,
        ThymeleafConfig.class,
        SecurityConfig.class,
        MessageProvider.class })
@TestPropertySource(properties = "secured.rest=false")
@DisplayName("Search Course Controller")
public class SearchCourseControllerTest {

    private static final int MAX_VALID_PAGE_NUMBER = 5;
    private static final int PAGE_SIZE = 20;
    private static final int INVALID_PAGE_NUMBER = 10;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private CourseService courseServiceMock;

    @MockBean
    private UserDao userDao;

    private EntityGenerator generator = new EntityGenerator();

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    @Test
    void shouldShowPageWithAvailableCourses() throws Exception {
        List<Course> courses = generator.generateCourses(10);
        Page<Course> page = new PageImpl<>(courses);
        when(courseServiceMock.findCourses(any(Pageable.class))).thenReturn(page);

        mockMvc
                .perform(get("/courses").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.COURSES, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.COURSES, hasProperty("size", equalTo(10))))
                .andExpect(model().attribute(Attribute.COURSES, equalTo(page)))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.ALL_COURSES, page.getTotalElements()))))
                .andExpect(view().name(View.COURSES));
    }

    @Test
    void shouldShowNotFoundPageIfPageNumberIsInvalid() throws Exception {
        when(courseServiceMock.findCourses(any(Pageable.class)))
                .thenThrow(invalidPageNumberException());

        mockMvc
                .perform(get("/courses?page={pageNumber}", INVALID_PAGE_NUMBER).locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/courses?page=" + INVALID_PAGE_NUMBER))))
                .andExpect(view().name(View.NOT_FOUND));
    }


    @Test
    void shouldShowCoursePageForExitingCourse() throws Exception {
        Course course = generator.generateCourse();
        when(courseServiceMock.findCourseById(course.getId())).thenReturn(course);

        mockMvc
                .perform(get("/courses/{courseId}", course.getId()).locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.COURSE))
                .andExpect(model().attribute(Attribute.COURSE, equalTo(course)))
                .andExpect(view().name(View.COURSE_INFO));
    }

    @Test
    void shouldShowNotFoundPageIfNoCourseWithGivenIdentifier() throws Exception {
        Integer courseId = 10;
        when(courseServiceMock.findCourseById(courseId))
                .thenThrow(new ResourceNotFoundException(Course.class, "Course with id=%d not found", courseId));
        
        mockMvc
                .perform(get("/courses/{courseId}", courseId).locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/courses/" + courseId))))
                .andExpect(view().name(View.NOT_FOUND));
    }

    @Test
    void shouldShowBadRequestPageIfGivenCourseIdentifierIsNotNumber() throws Exception {
        String courseId = "id";

        mockMvc
                .perform(get("/courses/{courseId}", courseId).locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.MESSAGE, Attribute.ERROR))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", courseId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/courses/" + courseId))))
                .andExpect(view().name(View.BAD_REQUEST));
    }

    private InvalidPageNumberException invalidPageNumberException() {
        return new InvalidPageNumberException(Course.class, INVALID_PAGE_NUMBER, PAGE_SIZE, MAX_VALID_PAGE_NUMBER);
    }

}
