package org.vdragun.tms.ui.rest.resource.v1.student;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.vdragun.tms.ui.rest.resource.v1.student.StudentResource.BASE_URL;

import java.time.LocalDate;
import java.util.List;
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
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.student.CreateStudentData;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.application.service.student.UpdateStudentData;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = StudentResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Search Student Resource")
public class StudentResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final int NUMBER_OF_STUDENTS = 2;
    private static final int NUMBER_OF_COURSES_PER_STUDENT = 2;
    private static final int GROUP_ID = 2;
    private static final int STUDENT_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private ObjectMapper mapper;

    @Captor
    private ArgumentCaptor<CreateStudentData> createCaptor;

    @Captor
    private ArgumentCaptor<UpdateStudentData> updateCaptor;

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

    @Test
    void shouldRegisterNewStudent() throws Exception {
        CreateStudentData registerData = new CreateStudentData("Jack", "Smith", LocalDate.now());
        Student expectedStudent = generator.generateStudent();
        when(studentServiceMock.registerNewStudent(any(CreateStudentData.class))).thenReturn(expectedStudent);

        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .locale(Locale.US)
                .contentType(CONTENT_TYPE_HAL_JSON)
                .content(mapper.writeValueAsString(registerData)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        verify(studentServiceMock, times(1)).registerNewStudent(createCaptor.capture());
        assertThat(createCaptor.getValue(), samePropertyValuesAs(registerData));
        jsonVerifier.verifyStudentJson(resultActions, expectedStudent);
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedRegistrationDataIsNotValid() throws Exception {
        String notLatinFirstName = "Джек";
        String tooShortLastName = "S";
        LocalDate futureRegistrationdate = LocalDate.now().plusDays(10);
        CreateStudentData invalidData = new CreateStudentData(notLatinFirstName, tooShortLastName,
                futureRegistrationdate);

        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .locale(Locale.US)
                .contentType(CONTENT_TYPE_HAL_JSON)
                .content(mapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(studentServiceMock, never()).registerNewStudent(any(CreateStudentData.class));
        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "firstName", "PersonName");
        jsonVerifier.verifyValidationError(resultActions, "lastName", "PersonName");
        jsonVerifier.verifyValidationError(resultActions, "enrollmentDate", "PastOrPresent.enrollmentDate");
    }

    @Test
    void shouldReturnStatusBadRequestIfRegistrationDataIsMissing() throws Exception {
        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(studentServiceMock, never()).registerNewStudent(any(CreateStudentData.class));
        jsonVerifier.verifyErrorMessage(resultActions, Message.MALFORMED_JSON_REQUEST);
    }

    @Test
    void shouldUpdateExistingStudent() throws Exception {
        UpdateStudentData updateData = new UpdateStudentData(STUDENT_ID, GROUP_ID, "Jack", "Smith", asList(3, 4));
        Student expectedStudent = generator.generateStudent();
        when(studentServiceMock.updateExistingStudent(any(UpdateStudentData.class))).thenReturn(expectedStudent);

        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{studentId}", STUDENT_ID)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        verify(studentServiceMock, times(1)).updateExistingStudent(updateCaptor.capture());
        assertThat(updateCaptor.getValue(), samePropertyValuesAs(updateData));
        jsonVerifier.verifyStudentJson(resultActions, expectedStudent);
    }

    @Test
    void shouldReturnStatusNotFoundIfSpecifiedStudentToUpdateNotExist() throws Exception {
        UpdateStudentData updateData = new UpdateStudentData(STUDENT_ID, GROUP_ID, "Jack", "Smith", asList(3, 4));
        when(studentServiceMock.updateExistingStudent(any(UpdateStudentData.class)))
                .thenThrow(new ResourceNotFoundException("Student with id=%d not found", STUDENT_ID));

        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{studentId}", STUDENT_ID)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.RESOURCE_NOT_FOUND);
    }

    @Test
    void shouldReturnStatusBadRequestWhenUpdateIfProvidedStudentIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";
        UpdateStudentData updateData = new UpdateStudentData(STUDENT_ID, GROUP_ID, "Jack", "Smith", asList(3, 4));

        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{studentId}", invalidId)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(studentServiceMock, never()).updateExistingStudent(any(UpdateStudentData.class));
        jsonVerifier.verifyErrorMessage(
                resultActions,
                Message.ARGUMENT_TYPE_MISSMATCH,
                "studentId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedStudentIdIsNotValid() throws Exception {
        Integer negativeId = -1;
        UpdateStudentData updateData = new UpdateStudentData(STUDENT_ID, GROUP_ID, "Jack", "Smith", asList(3, 4));

        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{studentId}", negativeId)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(studentServiceMock, never()).updateExistingStudent(any(UpdateStudentData.class));
        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "studentId", Message.POSITIVE_ID);
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedUpdateDataIsInvalid() throws Exception {
        int invalidStudentId = 0;
        int negativeGroupId = -1;
        String notLatinFirstName = "Джек";
        String tooShortLastName = "S";
        List<Integer> invalidCourseIds = asList(null, negativeGroupId);
        UpdateStudentData invalidData = new UpdateStudentData(
                invalidStudentId,
                negativeGroupId,
                notLatinFirstName,
                tooShortLastName,
                invalidCourseIds);

        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{studentId}", STUDENT_ID)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(studentServiceMock, never()).updateExistingStudent(any(UpdateStudentData.class));
        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "studentId", "Positive.studentId");
        jsonVerifier.verifyValidationError(resultActions, "groupId", "Positive.groupId");
        jsonVerifier.verifyValidationError(resultActions, "firstName", "PersonName");
        jsonVerifier.verifyValidationError(resultActions, "lastName", "PersonName");
        jsonVerifier.verifyValidationError(resultActions, "courseIds[0]", "NotNull.courseIds");
        jsonVerifier.verifyValidationError(resultActions, "courseIds[1]", "Positive");
    }

    @Test
    void shouldReturnStatusBadRequestIfUpdateDataIsMissing() throws Exception {
        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{studentId}", STUDENT_ID)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(studentServiceMock, never()).updateExistingStudent(any(UpdateStudentData.class));
        jsonVerifier.verifyErrorMessage(resultActions, Message.MALFORMED_JSON_REQUEST);
    }

    @Test
    void shouldDeleteStudentByGivenIdentifier() throws Exception {
        Integer studentId = 1;

        mockMvc.perform(delete("/api/v1/students/{studentId}", studentId)
                .locale(Locale.US))
                .andExpect(status().isOk());

        verify(studentServiceMock, times(1)).deleteStudentById(studentId);
    }

    @Test
    void shouldReturnStatusBadRequestWhenDeleteIfProvidedStudentIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";

        ResultActions resultActions = mockMvc.perform(delete(BASE_URL + "/{studentId}", invalidId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(studentServiceMock, never()).deleteStudentById(any(Integer.class));
        jsonVerifier.verifyErrorMessage(
                resultActions,
                Message.ARGUMENT_TYPE_MISSMATCH,
                "studentId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedStudentIdentifierIsNotValid() throws Exception {
        Integer negativeId = -1;

        ResultActions resultActions = mockMvc.perform(delete(BASE_URL + "/{studentId}", negativeId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(studentServiceMock, never()).deleteStudentById(any(Integer.class));
        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "studentId", Message.POSITIVE_ID);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoStudentWithProvidedIdentifierExist() throws Exception {
        Integer studentId = 1;
        doThrow(new ResourceNotFoundException("Student with id=%d not found", studentId))
                .when(studentServiceMock).deleteStudentById(studentId);

        ResultActions resultActions = mockMvc.perform(delete(BASE_URL + "/{studentId}", studentId)
                .locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.RESOURCE_NOT_FOUND);
    }

}
