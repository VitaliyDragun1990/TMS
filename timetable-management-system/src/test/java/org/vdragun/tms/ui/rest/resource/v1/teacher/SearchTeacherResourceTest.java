package org.vdragun.tms.ui.rest.resource.v1.teacher;

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
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

@WebMvcTest(controllers = SearchTeacherResource.class)
@Import({ WebConfig.class, WebRestConfig.class })
@DisplayName("Search Teacher Resource")
public class SearchTeacherResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final int NUMBER_OF_TEACHERS = 2;
    private static final int NUMBER_OF_COURSES_PER_TEACHER = 2;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelConverter modelConverter;

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

        verifyJson(resultActions, teachers);
    }

    @Test
    void shouldReturnTeacherByGivenIdentifier() throws Exception {
        Teacher teacher = generator.generateTeachersWithCourse(1, NUMBER_OF_COURSES_PER_TEACHER).get(0);
        when(teacherServiceMock.findTeacherById(teacher.getId())).thenReturn(teacher);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/teachers/{teacherId}", teacher.getId())
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        verifyJson(resultActions, teacher);
    }

    private void verifyJson(ResultActions actions, List<Teacher> teachers) throws Exception {
        List<TeacherModel> expected = modelConverter.convertList(teachers, Teacher.class, TeacherModel.class);
        for (int i = 0; i < expected.size(); i++) {
            TeacherModel expectedTeacher = expected.get(i);
            actions
                    .andExpect(jsonPath(format("$._embedded.teachers[%d].id", i),
                            equalTo(expectedTeacher.getId())))
                    .andExpect(jsonPath(format("$._embedded.teachers[%d].firstName", i),
                            equalTo(expectedTeacher.getFirstName())))
                    .andExpect(jsonPath(format("$._embedded.teachers[%d].lastName", i),
                            equalTo(expectedTeacher.getLastName())))
                    .andExpect(jsonPath(format("$._embedded.teachers[%d].title", i),
                            equalTo(expectedTeacher.getTitle())))
                    .andExpect(jsonPath(format("$._embedded.teachers[%d].dateHired", i),
                            equalTo(expectedTeacher.getDateHired())));
            for (int j = 0; j < expectedTeacher.getCourses().size(); j++) {
                CourseModel expectedCourse = expectedTeacher.getCourses().get(j);
                actions
                        .andExpect(jsonPath(format("$._embedded.teachers[%d].courses[%d].id", i, j),
                                equalTo(expectedCourse.getId())))
                        .andExpect(jsonPath(format("$._embedded.teachers[%d].courses[%d].name", i, j),
                                equalTo(expectedCourse.getName())))
                        .andExpect(jsonPath(format("$._embedded.teachers[%d].courses[%d].description", i, j),
                                equalTo(expectedCourse.getDescription())))
                        .andExpect(jsonPath(format("$._embedded.teachers[%d].courses[%d].categoryCode", i, j),
                                equalTo(expectedCourse.getCategoryCode())))
                        .andExpect(jsonPath(format("$._embedded.teachers[%d].courses[%d].teacherId", i, j),
                                equalTo(expectedCourse.getTeacherId())))
                        .andExpect(jsonPath(format("$._embedded.teachers[%d].courses[%d].teacherFullName", i, j),
                                equalTo(expectedCourse.getTeacherFullName())));
            }
        }
    }

    private void verifyJson(ResultActions actions, Teacher teacher) throws Exception {
        TeacherModel expectedTeacher = modelConverter.convert(teacher, TeacherModel.class);
        actions
                .andExpect(jsonPath("$.id", equalTo(expectedTeacher.getId())))
                .andExpect(jsonPath("$.firstName", equalTo(expectedTeacher.getFirstName())))
                .andExpect(jsonPath("$.lastName", equalTo(expectedTeacher.getLastName())))
                .andExpect(jsonPath("$.title", equalTo(expectedTeacher.getTitle())))
                .andExpect(jsonPath("$.dateHired", equalTo(expectedTeacher.getDateHired())));
        for (int j = 0; j < expectedTeacher.getCourses().size(); j++) {
            CourseModel expectedCourse = expectedTeacher.getCourses().get(j);
            actions
                    .andExpect(jsonPath(format("$.courses[%d].id", j),
                            equalTo(expectedCourse.getId())))
                    .andExpect(jsonPath(format("$.courses[%d].name", j),
                            equalTo(expectedCourse.getName())))
                    .andExpect(jsonPath(format("$.courses[%d].description", j),
                            equalTo(expectedCourse.getDescription())))
                    .andExpect(jsonPath(format("$.courses[%d].categoryCode", j),
                            equalTo(expectedCourse.getCategoryCode())))
                    .andExpect(jsonPath(format("$.courses[%d].teacherId", j),
                            equalTo(expectedCourse.getTeacherId())))
                    .andExpect(jsonPath(format("$.courses[%d].teacherFullName", j),
                            equalTo(expectedCourse.getTeacherFullName())));
        }
    }
}
