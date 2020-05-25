package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static java.time.temporal.ChronoUnit.MINUTES;
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
import static org.vdragun.tms.ui.rest.resource.v1.timetable.TimetableResource.BASE_URL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
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
import org.vdragun.tms.core.application.service.timetable.CreateTimetableData;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.application.service.timetable.UpdateTimetableData;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.common.util.Translator;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = TimetableResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Timetable Resource")
public class TimetableResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final LocalDateTime TIMETABLE_START_TIME = LocalDateTime.now().plusDays(3).truncatedTo(MINUTES);
    private static final int TEACHER_ID = 3;
    private static final int CLASSROOM_ID = 2;
    private static final int COURSE_ID = 1;
    private static final int DURATION = 60;
    private static final int NUMBER_OF_TIMETABLES = 2;
    private static final Integer STUDENT_ID = 1;
    private static final int TIMETABLE_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private Translator translator;

    @MockBean
    private TimetableService timetableServiceMock;

    @Captor
    private ArgumentCaptor<CreateTimetableData> createCaptor;

    @Captor
    private ArgumentCaptor<UpdateTimetableData> captor;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldReturnAllAvailableTimetables() throws Exception {
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findAllTimetables()).thenReturn(expectedTimetables);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON))
                .andExpect(jsonPath("$._embedded.timetables", hasSize(2)));

        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetables);
    }

    @Test
    void shouldReturnTimetableByGivenIdentifier() throws Exception {
        Timetable expectedTimetable = generator.generateTimetable();
        when(timetableServiceMock.findTimetableById(expectedTimetable.getId())).thenReturn(expectedTimetable);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{timetableId}", expectedTimetable.getId())
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetable);
    }

    @Test
    void shouldReturnDailyTimetablesForTeacher() throws Exception {
        LocalDate targetDate = LocalDate.now();
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findDailyTimetablesForTeacher(TEACHER_ID, targetDate)).thenReturn(expectedTimetables);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/teacher/{teacherId}/day", TEACHER_ID)
                .locale(Locale.US)
                .param("targetDate", translator.formatDate(targetDate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetables);
    }

    @Test
    void shouldReturnDailyTimetablesForStudent() throws Exception {
        LocalDate targetDate = LocalDate.now();
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findDailyTimetablesForStudent(STUDENT_ID, targetDate)).thenReturn(expectedTimetables);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/student/{studentId}/day", STUDENT_ID)
                .locale(Locale.US)
                .param("targetDate", translator.formatDate(targetDate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetables);
    }

    @Test
    void shouldReturnMonthlyTimetablesForTeacher() throws Exception {
        Month targetMonth = Month.MAY;
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findMonthlyTimetablesForTeacher(TEACHER_ID, targetMonth))
                .thenReturn(expectedTimetables);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/teacher/{teacherId}/month", TEACHER_ID)
                .locale(Locale.US)
                .param("targetMonth", translator.formatMonth(targetMonth)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetables);
    }

    @Test
    void shouldReturnMonthlyTimetablesForStudent() throws Exception {
        Month targetMonth = Month.MAY;
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findMonthlyTimetablesForStudent(STUDENT_ID, targetMonth))
                .thenReturn(expectedTimetables);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/student/{studentId}/month", STUDENT_ID)
                .locale(Locale.US)
                .param("targetMonth", translator.formatMonth(targetMonth)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetables);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenTimetableIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{timetableId}", invalidId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(
                resultActions,
                Message.ARGUMENT_TYPE_MISSMATCH,
                "timetableId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoTimetableWithGivenIdentifier() throws Exception {
        Integer timetableId = 1;
        when(timetableServiceMock.findTimetableById(eq(timetableId)))
                .thenThrow(new ResourceNotFoundException("Timetable with id=%d not found", timetableId));

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{timetableId}", timetableId)
                .locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.RESOURCE_NOT_FOUND);
    }

    @Test
    void shouldReturnStatuBadRequestIfGivenIdentifierIsNotValid() throws Exception {
        Integer negativeId = -1;

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{timetableId}", negativeId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "timetableId", Message.POSITIVE_ID);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenTeacherIdentifierIsNotNumberForDailyRequest() throws Exception {
        String invalidId = "id";
        LocalDate targetDate = LocalDate.now();

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/teacher/{teacherId}/day", invalidId)
                .locale(Locale.US)
                .param("targetDate", translator.formatDate(targetDate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(
                resultActions,
                Message.ARGUMENT_TYPE_MISSMATCH,
                "teacherId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoTeacherWithGivenIdentifierForDailyRequest() throws Exception {
        Integer teacherId = 1;
        LocalDate targetDate = LocalDate.now();
        when(timetableServiceMock.findDailyTimetablesForTeacher(teacherId, targetDate))
                .thenThrow(new ResourceNotFoundException("Teacher with id=%d not found", teacherId));

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/teacher/{teacherId}/day", teacherId)
                .locale(Locale.US)
                .param("targetDate", translator.formatDate(targetDate)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.RESOURCE_NOT_FOUND);
    }

    @Test
    void shouldReturnStatuBadRequestIfGivenTeacherIdentifierIsNotValidForDailyRequest() throws Exception {
        Integer negativeId = -1;
        LocalDate targetDate = LocalDate.now();

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/teacher/{teacherId}/day", negativeId)
                .locale(Locale.US)
                .param("targetDate", translator.formatDate(targetDate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "teacherId", Message.POSITIVE_ID);
    }

    @Test
    void shouldReturnStatusBadRequestIfTargetDateParameterIsMissingForDailyTeacherRequest() throws Exception {
        Integer teacherId = 1;

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/teacher/{teacherId}/day", teacherId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.MISSING_REQUIRED_PARAMETER, "targetDate");
    }

    @Test
    void shouldReturnStatusBadRequestIfTargetDateParameterIsNotValidDateForDailyTeacherRequest() throws Exception {
        Integer teacherId = 1;
        String targetDate = "invalid";

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/teacher/{teacherId}/day", teacherId)
                .locale(Locale.US)
                .param("targetDate", targetDate))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(
                resultActions,
                Message.ARGUMENT_TYPE_MISSMATCH,
                "targetDate", targetDate, LocalDate.class);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenTeacherIdentifierIsNotNumberForMonthlyRequest() throws Exception {
        String invalidId = "id";
        Month targetMonth = Month.MAY;

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/teacher/{teacherId}/month", invalidId)
                .locale(Locale.US)
                .param("targetMonth", translator.formatMonth(targetMonth)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(
                resultActions,
                Message.ARGUMENT_TYPE_MISSMATCH,
                "teacherId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoTeacherWithGivenIdentifierForMonthlyRequest() throws Exception {
        Integer teacherId = 1;
        Month targetMonth = Month.MAY;
        when(timetableServiceMock.findMonthlyTimetablesForTeacher(teacherId, targetMonth))
                .thenThrow(new ResourceNotFoundException("Teacher with id=%d not found", teacherId));

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/teacher/{teacherId}/month", teacherId)
                .locale(Locale.US)
                .param("targetMonth", translator.formatMonth(targetMonth)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.RESOURCE_NOT_FOUND);
    }

    @Test
    void shouldReturnStatuBadRequestIfGivenTeacherIdentifierIsNotValidForMonthlyRequest() throws Exception {
        Integer negativeId = -1;
        Month targetMonth = Month.MAY;

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/teacher/{teacherId}/month", negativeId)
                .locale(Locale.US)
                .param("targetMonth", translator.formatMonth(targetMonth)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "teacherId", Message.POSITIVE_ID);
    }

    @Test
    void shouldReturnStatusBadRequestIfTargetMonthParameterIsMissingForMonthlyTeacherRequest() throws Exception {
        Integer teacherId = 1;

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/teacher/{teacherId}/month", teacherId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.MISSING_REQUIRED_PARAMETER, "targetMonth");
    }

    @Test
    void shouldReturnStatusBadRequestIfTargetMonthParameterIsNotValidMonthForMonthlyTeacherRequest() throws Exception {
        Integer teacherId = 1;
        String targetMonth = "invalid";

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/teacher/{teacherId}/month", teacherId)
                .locale(Locale.US)
                .param("targetMonth", targetMonth))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(
                resultActions,
                Message.ARGUMENT_TYPE_MISSMATCH,
                "targetMonth", targetMonth, Month.class);
    }

    @Test
    void shouldRegisterNewTimetable() throws Exception {
        CreateTimetableData registerData =
                new CreateTimetableData(TIMETABLE_START_TIME, DURATION, COURSE_ID, CLASSROOM_ID, TEACHER_ID);
        Timetable expectedTimetable = generator.generateTimetable();
        when(timetableServiceMock.registerNewTimetable(any(CreateTimetableData.class))).thenReturn(expectedTimetable);

        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(registerData)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));
        
        verify(timetableServiceMock, times(1)).registerNewTimetable(createCaptor.capture());
        assertThat(createCaptor.getValue(), samePropertyValuesAs(registerData, "startTime"));
        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetable);
    }
    
    @Test
    void shouldReturnStatusBadRequestIfProvidedRegistrationDataIsNotValid() throws Exception {
        LocalDateTime startTimeInthePast = LocalDateTime.now().minusDays(3);
        int tooShortDuration = 20;
        int invalidCourseId = 0;
        int negativeTeacherId = -1;
        CreateTimetableData invalidData =
                new CreateTimetableData(startTimeInthePast, tooShortDuration, invalidCourseId, null, negativeTeacherId);

        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "startTime", "Future.startTime");
        jsonVerifier.verifyValidationError(resultActions, "duration", "TimetableDuration");
        jsonVerifier.verifyValidationError(resultActions, "courseId", "Positive.courseId");
        jsonVerifier.verifyValidationError(resultActions, "classroomId", "NotNull.classroomId");
        jsonVerifier.verifyValidationError(resultActions, "teacherId", "Positive.teacherId");
    }

    @Test
    void shouldReturnStatusBadRequestIfRegistrationDataIsMissing() throws Exception {
        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.MALFORMED_JSON_REQUEST);
    }

    @Test
    void shouldUpdateExistingTimetable() throws Exception {
        UpdateTimetableData updateData = new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION,
                CLASSROOM_ID);
        Timetable updatedTimetable = generator.generateTimetable();
        when(timetableServiceMock.updateExistingTimetable(any(UpdateTimetableData.class))).thenReturn(updatedTimetable);

        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{timetableId}", TIMETABLE_ID)
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
        UpdateTimetableData updateData = new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION,
                CLASSROOM_ID);
        when(timetableServiceMock.updateExistingTimetable(any(UpdateTimetableData.class)))
                .thenThrow(new ResourceNotFoundException("Timetable with id=%d not found", TIMETABLE_ID));

        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{timetableId}", TIMETABLE_ID)
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
        UpdateTimetableData updateData = new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION,
                CLASSROOM_ID);

        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{timetableId}", invalidId)
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
    void shouldReturnStatusBadRequestWhenUpdateIfProvidedTimetableIdentifierIsNotValid() throws Exception {
        Integer negativeId = -1;
        UpdateTimetableData updateData = new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION,
                CLASSROOM_ID);

        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{timetableId}", negativeId)
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
    void shouldReturnStatusBadRequestWhenUpdateIfProvidedUpdateDataIsNotValid() throws Exception {
        int invalidTimetableId = -1;
        LocalDateTime startTimeInthePast = LocalDateTime.now().minusDays(3);
        int tooShortDuration = 20;
        int invalidClassrommId = 0;
        UpdateTimetableData invalidData = new UpdateTimetableData(invalidTimetableId, startTimeInthePast,
                tooShortDuration,
                invalidClassrommId);

        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{timetableId}", TIMETABLE_ID)
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
        ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/{timetableId}", TIMETABLE_ID)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(timetableServiceMock, never()).updateExistingTimetable(any(UpdateTimetableData.class));
        jsonVerifier.verifyErrorMessage(resultActions, Message.MALFORMED_JSON_REQUEST);
    }

    @Test
    void shouldDeleteTimetableById() throws Exception {
        mockMvc.perform(delete("/api/v1/timetables/{timetableId}", TIMETABLE_ID)
                .locale(Locale.US))
                .andExpect(status().isOk());

        verify(timetableServiceMock, times(1)).deleteTimetableById(TIMETABLE_ID);
    }

    @Test
    void shouldReturnStatusBadReqeustWhenDeleteIfProvidedTimetableIdentifierIsNotNumber() throws Exception {
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
    void shouldReturnStatusBadRequestWhenDeleteIfProvidedTimetableIdentifierIsNotValid() throws Exception {
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
    void shouldReturnStatusNotFoundWhenDeleteIfNoTimetableWithProvidedIdentifierExist() throws Exception {
        doThrow(new ResourceNotFoundException("Timetable with id=%d not found", TIMETABLE_ID))
                .when(timetableServiceMock).deleteTimetableById(any(Integer.class));

        ResultActions resultActions = mockMvc.perform(delete(BASE_URL + "/{timetableId}", TIMETABLE_ID)
                .locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.RESOURCE_NOT_FOUND);
    }

}
