package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static java.time.temporal.ChronoUnit.MINUTES;
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

import java.time.LocalDateTime;
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
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.application.service.timetable.UpdateTimetableData;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UpdateTimetableResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Update Timetable Resource")
public class UpdateTimetableResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final LocalDateTime TIMETABLE_START_TIME = LocalDateTime.now().plusDays(3).truncatedTo(MINUTES);
    private static final int TIMETABLE_ID = 1;
    private static final int CLASSROOM_ID = 2;
    private static final int DURATION = 60;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @MockBean
    private TimetableService timetableServiceMock;

    @Captor
    private ArgumentCaptor<UpdateTimetableData> captor;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldUpdateExistingTimetable() throws Exception {
        UpdateTimetableData updateData =
                new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION, CLASSROOM_ID);
        Timetable updatedTimetable = generator.generateTimetable();
        when(timetableServiceMock.updateExistingTimetable(any(UpdateTimetableData.class))).thenReturn(updatedTimetable);

        ResultActions resultActions = mockMvc.perform(put("/api/v1/timetables/{timetableId}", TIMETABLE_ID)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        verify(timetableServiceMock, times(1)).updateExistingTimetable(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(updateData, "startTime"));
        jsonVerifier.verifyTimetableJson(resultActions, updatedTimetable);
    }
    
    @Test
    void shouldReturnStatusNotFoundIfTimetableToUpdateNotExist() throws Exception {
        UpdateTimetableData updateData =
                new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION, CLASSROOM_ID);
        when(timetableServiceMock.updateExistingTimetable(any(UpdateTimetableData.class)))
                .thenThrow(new ResourceNotFoundException("Timetable with id=%d not found", TIMETABLE_ID));
        
        ResultActions resultActions = mockMvc.perform(put("/api/v1/timetables/{timetableId}", TIMETABLE_ID)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        jsonVerifier.verifyErrorMessage(resultActions, Message.RESOURCE_NOT_FOUND);
    }
    
    @Test
    void shouldReturnStatusBadRequestIfProvidedTimetableIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";
        UpdateTimetableData updateData =
                new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION, CLASSROOM_ID);

        ResultActions resultActions = mockMvc.perform(put("/api/v1/timetables/{timetableId}", invalidId)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(timetableServiceMock, never()).updateExistingTimetable(any(UpdateTimetableData.class));
        jsonVerifier.verifyErrorMessage(
                resultActions,
                Message.ARGUMENT_TYPE_MISSMATCH,
                "timetableId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedTimetableIdentifierIsNotValid() throws Exception {
        Integer negativeId = -1;
        UpdateTimetableData updateData = new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION,
                CLASSROOM_ID);

        ResultActions resultActions = mockMvc.perform(put("/api/v1/timetables/{timetableId}", negativeId)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(timetableServiceMock, never()).updateExistingTimetable(any(UpdateTimetableData.class));
        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "timetableId", Message.POSITIVE_ID);
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedUpdateDataIsNotValid() throws Exception {
        int invalidTimetableId = -1;
        LocalDateTime startTimeInthePast = LocalDateTime.now().minusDays(3);
        int tooShortDuration = 20;
        int invalidClassrommId = 0;
        UpdateTimetableData invalidData = new UpdateTimetableData(invalidTimetableId, startTimeInthePast, tooShortDuration,
                invalidClassrommId);

        ResultActions resultActions = mockMvc.perform(put("/api/v1/timetables/{timetableId}", TIMETABLE_ID)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(timetableServiceMock, never()).updateExistingTimetable(any(UpdateTimetableData.class));
        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "startTime", "Future.startTime");
        jsonVerifier.verifyValidationError(resultActions, "duration", "TimetableDuration");
        jsonVerifier.verifyValidationError(resultActions, "classroomId", "Positive.classroomId");
        jsonVerifier.verifyValidationError(resultActions, "timetableId", "Positive.timetableId");
    }

    @Test
    void shouldReturnStatusBadRequestIfUpdateDataIsMissing() throws Exception {
        ResultActions resultActions = mockMvc.perform(put("/api/v1/timetables/{timetableId}", TIMETABLE_ID)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(timetableServiceMock, never()).updateExistingTimetable(any(UpdateTimetableData.class));
        jsonVerifier.verifyErrorMessage(resultActions, Message.MALFORMED_JSON_REQUEST);
    }

}
