package org.vdragun.tms.ui.rest.resource.v1.student;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebRestConfig;
import org.vdragun.tms.core.application.service.student.CreateStudentData;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = RegisterStudentResource.class)
@Import({ WebConfig.class, WebRestConfig.class })
@DisplayName("Register Student Resource")
public class RegisterStudentResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelConverter modelConverter;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private StudentService studentServiceMock;

    @Captor
    private ArgumentCaptor<CreateStudentData> captor;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldRegisterNewStudent() throws Exception {
        CreateStudentData registerData = new CreateStudentData("Jack", "Smith", LocalDate.now());
        Student expectedStudent = generator.generateStudent();
        when(studentServiceMock.registerNewStudent(any(CreateStudentData.class))).thenReturn(expectedStudent);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/students")
                .locale(Locale.US)
                .contentType(CONTENT_TYPE_HAL_JSON)
                .content(mapper.writeValueAsString(registerData)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        verify(studentServiceMock, times(1)).registerNewStudent(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(registerData));
        verifyJson(resultActions, expectedStudent);
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
