package org.vdragun.tms.ui.rest.resource.v1.course;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebRestConfig;
import org.vdragun.tms.core.application.service.course.CourseData;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = RegisterCourseResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Register Course Resource")
public class RegisterCourseResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @MockBean
    private CourseService courseServiceMock;

    @Captor
    private ArgumentCaptor<CourseData> captor;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldRegisterNewCourse() throws Exception {
        CourseData registerData = new CourseData("English", "Course description", 1, 1);
        Course registered = generator.generateCourse();
        when(courseServiceMock.registerNewCourse(any(CourseData.class))).thenReturn(registered);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/courses")
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(registerData)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        verify(courseServiceMock, times(1)).registerNewCourse(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(registerData));
        jsonVerifier.verifyCourseJson(resultActions, registered);
    }

}
