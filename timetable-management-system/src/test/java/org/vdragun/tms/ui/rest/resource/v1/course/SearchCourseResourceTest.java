package org.vdragun.tms.ui.rest.resource.v1.course;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
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
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

@WebMvcTest(controllers = SearchCourseResource.class)
@Import({ WebConfig.class, WebRestConfig.class })
@DisplayName("Search Course Resource")
public class SearchCourseResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelConverter modelConverter;

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

        verifyJson(resultActions, courses);
    }

    @Test
    void shouldReturnCourseByGivenIdentifier() throws Exception {
        Course course = generator.generateCourse();
        when(courseServiceMock.findCourseById(course.getId())).thenReturn(course);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/courses/{courseId}", course.getId())
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        verifyJson(resultActions, course);
    }

    private void verifyJson(ResultActions actions, Course course) throws Exception {
        CourseModel expected = modelConverter.convert(course, CourseModel.class);
        actions
                .andExpect(jsonPath("$.id", equalTo(expected.getId())))
                .andExpect(jsonPath("$.name", equalTo(expected.getName())))
                .andExpect(jsonPath("$.description", equalTo(expected.getDescription())))
                .andExpect(jsonPath("$.categoryCode", equalTo(expected.getCategoryCode())))
                .andExpect(jsonPath("$.teacherId", equalTo(expected.getTeacherId())))
                .andExpect(jsonPath("$.teacherFullName", equalTo(expected.getTeacherFullName())));
    }

    private void verifyJson(ResultActions actions, List<Course> courses) throws Exception {
        List<CourseModel> expected = modelConverter.convertList(courses, Course.class, CourseModel.class);
        for (int i = 0; i < expected.size(); i++) {
            CourseModel model = expected.get(i);
            actions
                    .andExpect(jsonPath(format("$._embedded.courses[%d].id", i),
                            equalTo(model.getId())))
                    .andExpect(jsonPath(format("$._embedded.courses[%d].name", i),
                            equalTo(model.getName())))
                    .andExpect(jsonPath(format("$._embedded.courses[%d].description", i),
                            equalTo(model.getDescription())))
                    .andExpect(jsonPath(format("$._embedded.courses[%d].categoryCode", i),
                            equalTo(model.getCategoryCode())))
                    .andExpect(jsonPath(format("$._embedded.courses[%d].teacherId", i),
                            equalTo(model.getTeacherId())))
                    .andExpect(jsonPath(format("$._embedded.courses[%d].teacherFullName", i),
                            equalTo(model.getTeacherFullName())));
        }
    }

}
