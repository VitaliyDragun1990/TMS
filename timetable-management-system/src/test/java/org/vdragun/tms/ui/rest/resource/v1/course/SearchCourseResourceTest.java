package org.vdragun.tms.ui.rest.resource.v1.course;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.vdragun.tms.ui.rest.resource.v1.course.CourseResource.BASE_URL;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebRestConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

@WebMvcTest(controllers = CourseResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Course Resource Search Functionality")
public class SearchCourseResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonVerifier jsonVerifier;

    @MockBean
    private CourseService courseServiceMock;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldReturnAllAvailableCourses() throws Exception {
        List<Course> courses = generator.generateCourses(2);
        when(courseServiceMock.findAllCourses()).thenReturn(courses);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL).locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON))
                .andExpect(jsonPath("$._embedded.courses", hasSize(2)));

        jsonVerifier.verifyCourseJson(resultActions, courses);
    }

    @Test
    void shouldReturnCourseByGivenIdentifier() throws Exception {
        Course course = generator.generateCourse();
        when(courseServiceMock.findCourseById(course.getId())).thenReturn(course);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{courseId}", course.getId())
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        jsonVerifier.verifyCourseJson(resultActions, course);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenCourseIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{courseId}", invalidId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(
                resultActions,
                Message.ARGUMENT_TYPE_MISSMATCH,
                "courseId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoCourseWithGivenIdentifier() throws Exception {
        Integer courseId = 1;
        when(courseServiceMock.findCourseById(eq(courseId)))
                .thenThrow(new ResourceNotFoundException(Course.class, "Course with id=%d not found", courseId));

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{courseId}", courseId)
                .locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.RESOURCE_NOT_FOUND, Course.class.getSimpleName());
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenIdentifierIsNotInvalid() throws Exception {
        Integer negativeId = -1;

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{courseId}", negativeId)
                .locale(Locale.US))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "courseId", Message.POSITIVE_ID);
    }

}
