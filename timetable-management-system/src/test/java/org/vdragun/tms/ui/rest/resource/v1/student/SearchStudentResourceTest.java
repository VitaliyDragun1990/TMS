package org.vdragun.tms.ui.rest.resource.v1.student;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.vdragun.tms.ui.rest.resource.v1.student.SearchStudentResource.BASE_URL;

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
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

@WebMvcTest(controllers = SearchStudentResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Search Student Resource")
public class SearchStudentResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final int NUMBER_OF_STUDENTS = 2;
    private static final int NUMBER_OF_COURSES_PER_STUDENT = 2;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonVerifier jsonVerifier;

    @MockBean
    private StudentService studentServiceMock;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldReturnAllAvailableStudents() throws Exception {
        List<Student> expectedStudents =
                generator.generateStudentsWithCourses(NUMBER_OF_STUDENTS, NUMBER_OF_COURSES_PER_STUDENT);
        when(studentServiceMock.findAllStudents()).thenReturn(expectedStudents);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL).locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON))
                .andExpect(jsonPath("$._embedded.students", hasSize(NUMBER_OF_STUDENTS)))
                .andExpect(jsonPath("$._embedded.students[*].courses", hasSize(NUMBER_OF_COURSES_PER_STUDENT)));

        jsonVerifier.verifyStudentJson(resultActions, expectedStudents);
    }

    @Test
    void shouldReturnStudentByGivenIdentifier() throws Exception {
        Student expectedStudent = generator.generateStudentsWithCourses(1, NUMBER_OF_COURSES_PER_STUDENT).get(0);
        when(studentServiceMock.findStudentById(expectedStudent.getId())).thenReturn(expectedStudent);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{studentId}", expectedStudent.getId())
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        jsonVerifier.verifyStudentJson(resultActions, expectedStudent);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenStudentIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{studentId}", invalidId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(
                resultActions,
                Message.ARGUMENT_TYPE_MISSMATCH,
                "studentId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoStudentWithGivenIdentifier() throws Exception {
        Integer studentId = 1;
        when(studentServiceMock.findStudentById(eq(studentId)))
                .thenThrow(new ResourceNotFoundException("Student with id=%d not found", studentId));
        
        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{studentId}", studentId)
                .locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        jsonVerifier.verifyErrorMessage(resultActions, Message.RESOURCE_NOT_FOUND);
    }

    @Test
    void shouldReturnStatuBadRequestIfGivenIdentifierIsNotValid() throws Exception {
        Integer negatveId = -1;

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{studentId}", negatveId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "studentId", Message.POSITIVE_ID);
    }

}
