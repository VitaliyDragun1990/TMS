package org.vdragun.tms.ui.rest.resource.v1.teacher;

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
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

@WebMvcTest(controllers = SearchTeacherResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Search Teacher Resource")
public class SearchTeacherResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final int NUMBER_OF_TEACHERS = 2;
    private static final int NUMBER_OF_COURSES_PER_TEACHER = 2;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonVerifier jsonVerifier;

    @MockBean
    private TeacherService teacherServiceMock;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldReturnAllAvailableTeachers() throws Exception {
        List<Teacher> teachers =
                generator.generateTeachersWithCourse(NUMBER_OF_TEACHERS, NUMBER_OF_COURSES_PER_TEACHER);
        when(teacherServiceMock.findAllTeachers()).thenReturn(teachers);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/teachers").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON))
                .andExpect(jsonPath("$._embedded.teachers", hasSize(NUMBER_OF_TEACHERS)))
                .andExpect(jsonPath("$._embedded.teachers[*].courses", hasSize(NUMBER_OF_COURSES_PER_TEACHER)));

        jsonVerifier.verifyTeacherJson(resultActions, teachers);
    }

    @Test
    void shouldReturnTeacherByGivenIdentifier() throws Exception {
        Teacher teacher = generator.generateTeachersWithCourse(1, NUMBER_OF_COURSES_PER_TEACHER).get(0);
        when(teacherServiceMock.findTeacherById(teacher.getId())).thenReturn(teacher);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/teachers/{teacherId}", teacher.getId())
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        jsonVerifier.verifyTeacherJson(resultActions, teacher);
    }

}
