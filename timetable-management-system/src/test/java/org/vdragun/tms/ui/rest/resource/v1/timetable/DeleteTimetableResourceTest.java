package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.vdragun.tms.ui.rest.resource.v1.timetable.TimetableResource.BASE_URL;

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
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;

@WebMvcTest(controllers = TimetableResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Timetable Resource Delete Fucntionality")
public class DeleteTimetableResourceTest {

    private static final Integer TIMETABLE_ID = 1;

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private JsonVerifier jsonVerifier;

    @MockBean
    private TimetableService timetableServiceMock;

    @Test
    void shouldDeleteTimetableById() throws Exception {
        mockMvc.perform(delete("/api/v1/timetables/{timetableId}", TIMETABLE_ID)
                .locale(Locale.US))
                .andExpect(status().isOk());

        verify(timetableServiceMock, times(1)).deleteTimetableById(TIMETABLE_ID);
    }

    @Test
    void shouldReturnStatusBadReqeustIfProvidedTimetableIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";

        ResultActions resultActions = mockMvc.perform(delete(BASE_URL + "/{timetableId}", invalidId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(timetableServiceMock, never()).deleteTimetableById(any(Integer.class));
        jsonVerifier.verifyErrorMessage(
                resultActions,
                Message.ARGUMENT_TYPE_MISSMATCH,
                "timetableId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedTimetableIdentifierIsNotValid() throws Exception {
        Integer negativeId = -1;

        ResultActions resultActions = mockMvc.perform(delete(BASE_URL + "/{timetableId}", negativeId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(timetableServiceMock, never()).deleteTimetableById(any(Integer.class));
        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "timetableId", Message.POSITIVE_ID);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoTimetableWithProvidedIdentifierExist() throws Exception {
        doThrow(new ResourceNotFoundException(Timetable.class, "Timetable with id=%d not found", TIMETABLE_ID))
                .when(timetableServiceMock).deleteTimetableById(any(Integer.class));

        ResultActions resultActions = mockMvc.perform(delete(BASE_URL + "/{timetableId}", TIMETABLE_ID)
                .locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.RESOURCE_NOT_FOUND, Timetable.class.getSimpleName());
    }

}
