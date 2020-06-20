package org.vdragun.tms.ui.web.controller.course;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.SecurityConfig;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebMvcConfig;
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
        SecurityConfig.class,
        MessageProvider.class })
@TestPropertySource(properties = "secured.rest=false")
@DisplayName("Search Course Controller")
public class SearchCourseControllerTest {

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
        when(courseServiceMock.findAllCourses()).thenReturn(courses);

        mockMvc
                .perform(get("/courses").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.COURSES, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.COURSES, hasSize(courses.size())))
                .andExpect(model().attribute(Attribute.COURSES, equalTo(courses)))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.ALL_COURSES, courses.size()))))
                .andExpect(view().name(View.COURSES));
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

}
