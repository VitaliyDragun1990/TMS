package org.vdragun.tms.ui.web.controller.course;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.ui.web.controller.MessageProvider;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = SearchCourseController.class)
@Import({ WebMvcConfig.class, MessageProvider.class })
public class SearchCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private CourseService courseServiceMock;

    private EntityGenerator generator = new EntityGenerator();

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
                .andExpect(view().name(Page.COURSES));
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
                .andExpect(view().name(Page.COURSE_INFO));
    }

    @Test
    void shouldShowNotFoundPageAndStatus404IfNoCourseWithGivenIdentifier() throws Exception {
        Integer courseId = 10;
        when(courseServiceMock.findCourseById(courseId))
                .thenThrow(new ResourceNotFoundException("Course with id=%d not found", courseId));
        
        mockMvc
                .perform(get("/courses/{courseId}", courseId).locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/courses/" + courseId))))
                .andExpect(view().name(Page.NOT_FOUND));
    }

    @Test
    void shouldShowBadRequestPageAndStatus400IfGivenCourseIdentifierIsNotNumber() throws Exception {
        String courseId = "id";

        mockMvc
                .perform(get("/courses/{courseId}", courseId).locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.MESSAGE, Attribute.ERROR))
                .andExpect(model().attribute(Attribute.ERROR, equalTo(format("\"%s\"", courseId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/courses/" + courseId))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

}
