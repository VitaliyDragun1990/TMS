package org.vdragun.tms.ui.rest.resource.v1.course;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebRestConfig;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

@WebMvcTest(controllers = SearchCourseResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Search Course Resource")
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

        ResultActions resultActions = mockMvc.perform(get("/api/v1/courses").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON))
                .andExpect(jsonPath("$._embedded.courses", hasSize(2)));

        jsonVerifier.verifyCourseJson(resultActions, courses);
    }

    @Test
    void shouldReturnCourseByGivenIdentifier() throws Exception {
        Course course = generator.generateCourse();
        when(courseServiceMock.findCourseById(course.getId())).thenReturn(course);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/courses/{courseId}", course.getId())
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        jsonVerifier.verifyCourseJson(resultActions, course);
    }

}
