package org.vdragun.tms.ui.rest.resource.v1.student;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.vdragun.tms.ui.rest.resource.v1.student.DeleteStudentResource.BASE_URL;

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
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;

@WebMvcTest(controllers = DeleteStudentResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Delete Student Resource")
public class DeleteStudentResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonVerifier jsonVerifier;

    @MockBean
    private StudentService studentServiceMock;

    @Test
    void shouldDeleteStudentByGivenIdentifier() throws Exception {
        Integer studentId = 1;

        mockMvc.perform(delete("/api/v1/students/{studentId}", studentId)
                .locale(Locale.US))
                .andExpect(status().isOk());

        verify(studentServiceMock, times(1)).deleteStudentById(studentId);
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedStudentIdentifierIsNotNumber() throws Exception {
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
