package org.vdragun.tms.ui.rest.resource.v1.student;

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
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

@WebMvcTest(controllers = SearchStudentResource.class)
@Import({ WebConfig.class, WebRestConfig.class })
@DisplayName("Search Student Resource")
public class SearchStudentResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final int NUMBER_OF_STUDENTS = 2;
    private static final int NUMBER_OF_COURSES_PER_STUDENT = 2;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelConverter modelConverter;

    @MockBean
    private StudentService studentServiceMock;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldReturnAllAvailableStudents() throws Exception {
        List<Student> expectedStudents =
                generator.generateStudentsWithCourses(NUMBER_OF_STUDENTS, NUMBER_OF_COURSES_PER_STUDENT);
        when(studentServiceMock.findAllStudents()).thenReturn(expectedStudents);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/students").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON))
                .andExpect(jsonPath("$._embedded.students", hasSize(NUMBER_OF_STUDENTS)))
                .andExpect(jsonPath("$._embedded.students[*].courses", hasSize(NUMBER_OF_COURSES_PER_STUDENT)));

        verifyJson(resultActions, expectedStudents);
    }

    @Test
    void shouldReturnStudentByGivenIdentifier() throws Exception {
        Student expectedStudent = generator.generateStudentsWithCourses(1, NUMBER_OF_COURSES_PER_STUDENT).get(0);
        when(studentServiceMock.findStudentById(expectedStudent.getId())).thenReturn(expectedStudent);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/students/{studentId}", expectedStudent.getId())
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        verifyJson(resultActions, expectedStudent);
    }

    private void verifyJson(ResultActions actions, List<Student> students) throws Exception {
        List<StudentModel> expected = modelConverter.convertList(students, Student.class, StudentModel.class);
        for (int i = 0; i < expected.size(); i++) {
            StudentModel expectedStudent = expected.get(i);
            actions
                    .andExpect(jsonPath(format("$._embedded.students[%d].id", i),
                            equalTo(expectedStudent.getId())))
                    .andExpect(jsonPath(format("$._embedded.students[%d].firstName", i),
                            equalTo(expectedStudent.getFirstName())))
                    .andExpect(jsonPath(format("$._embedded.students[%d].lastName", i),
                            equalTo(expectedStudent.getLastName())))
                    .andExpect(jsonPath(format("$._embedded.students[%d].group", i),
                            equalTo(expectedStudent.getGroup())))
                    .andExpect(jsonPath(format("$._embedded.students[%d].enrollmentDate", i),
                            equalTo(expectedStudent.getEnrollmentDate())));
            for (int j = 0; j < expectedStudent.getCourses().size(); j++) {
                CourseModel expectedCourse = expectedStudent.getCourses().get(j);
                actions
                        .andExpect(jsonPath(format("$._embedded.students[%d].courses[%d].id", i, j),
                                equalTo(expectedCourse.getId())))
                        .andExpect(jsonPath(format("$._embedded.students[%d].courses[%d].name", i, j),
                                equalTo(expectedCourse.getName())))
                        .andExpect(jsonPath(format("$._embedded.students[%d].courses[%d].description", i, j),
                                equalTo(expectedCourse.getDescription())))
                        .andExpect(jsonPath(format("$._embedded.students[%d].courses[%d].categoryCode", i, j),
                                equalTo(expectedCourse.getCategoryCode())))
                        .andExpect(jsonPath(format("$._embedded.students[%d].courses[%d].teacherId", i, j),
                                equalTo(expectedCourse.getTeacherId())))
                        .andExpect(jsonPath(format("$._embedded.students[%d].courses[%d].teacherFullName", i, j),
                                equalTo(expectedCourse.getTeacherFullName())));
            }
        }
    }

    private void verifyJson(ResultActions actions, Student student) throws Exception {
        StudentModel expectedStudent = modelConverter.convert(student, StudentModel.class);
        actions
                .andExpect(jsonPath("$.id", equalTo(expectedStudent.getId())))
                .andExpect(jsonPath("$.firstName", equalTo(expectedStudent.getFirstName())))
                .andExpect(jsonPath("$.lastName", equalTo(expectedStudent.getLastName())))
                .andExpect(jsonPath("$.group", equalTo(expectedStudent.getGroup())))
                .andExpect(jsonPath("$.enrollmentDate", equalTo(expectedStudent.getEnrollmentDate())));

        for (int j = 0; j < expectedStudent.getCourses().size(); j++) {
            CourseModel expectedCourse = expectedStudent.getCourses().get(j);
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
