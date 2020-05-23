package org.vdragun.tms.ui.rest.resource.v1.student;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.vdragun.tms.ui.rest.resource.v1.student.UpdateStudentResource.BASE_URL;

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
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.application.service.student.UpdateStudentData;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UpdateStudentResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Update Student Resource")
public class UpdateStudentResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final int GROUP_ID = 2;
    private static final int STUDENT_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @MockBean
    private StudentService studentServiceMock;

    @Captor
    private ArgumentCaptor<UpdateStudentData> captor;

    private EntityGenerator generator = new EntityGenerator();

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
        
        verify(studentServiceMock, times(1)).updateExistingStudent(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(updateData));
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
    void shouldReturnStatusBadRequestIfProvidedStudentIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";
        UpdateStudentData updateData = new UpdateStudentData(STUDENT_ID, GROUP_ID, "Jack", "Smith", asList(3, 4));

        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{studentId}",invalidId)
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

        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{studentId}",negativeId)
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

}
