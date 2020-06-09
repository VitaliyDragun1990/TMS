package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.AbstractResource.APPLICATION_HAL_JSON;
import static org.vdragun.tms.ui.rest.resource.v1.timetable.TimetableResource.BASE_URL;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.TestPropertySource;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.application.service.timetable.UpdateTimetableData;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.util.Constants.Message;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({ EmbeddedDataSourceConfig.class, JsonVerifier.class })
@TestPropertySource(properties = "secured.rest=false")
@DisplayName("Timetable Resource Update Functionality Integration Test")
public class UpdateTimetableResourceTest {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final LocalDateTime TIMETABLE_START_TIME = LocalDateTime.now().plusDays(3).truncatedTo(MINUTES);
    private static final int TIMETABLE_ID = 1;
    private static final int CLASSROOM_ID = 2;
    private static final int DURATION = 60;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TimetableService timetableServiceMock;

    @MockBean
    private AuthenticationManager authManagerMock;

    @Captor
    private ArgumentCaptor<UpdateTimetableData> captor;

    private EntityGenerator generator = new EntityGenerator();

    private HttpHeaders headers = new HttpHeaders();

    @Test
    void shouldUpdateExistingTimetable() throws Exception {
        UpdateTimetableData updateData =
                new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION, CLASSROOM_ID);
        Timetable updatedTimetable = generator.generateTimetable();
        when(timetableServiceMock.updateExistingTimetable(any(UpdateTimetableData.class))).thenReturn(updatedTimetable);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(updateData), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{timetableId}",
                PUT,
                request,
                String.class,
                TIMETABLE_ID);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyTimetableJson(response.getBody(), updatedTimetable);

        verify(timetableServiceMock, times(1)).updateExistingTimetable(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(updateData, "startTime"));
    }
    
    @Test
    void shouldReturnStatusNotFoundIfTimetableToUpdateNotExist() throws Exception {
        UpdateTimetableData updateData =
                new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION, CLASSROOM_ID);
        when(timetableServiceMock.updateExistingTimetable(any(UpdateTimetableData.class)))
                .thenThrow(new ResourceNotFoundException(Timetable.class, "Timetable with id=%d not found",
                        TIMETABLE_ID));
        
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(updateData), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{timetableId}",
                PUT,
                request,
                String.class,
                TIMETABLE_ID);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.RESOURCE_NOT_FOUND,
                Timetable.class.getSimpleName());
    }
    
    @Test
    void shouldReturnStatusBadRequestIfProvidedTimetableIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";
        UpdateTimetableData updateData =
                new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION, CLASSROOM_ID);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(updateData), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{timetableId}",
                PUT,
                request,
                String.class,
                invalidId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.ARGUMENT_TYPE_MISSMATCH,
                "timetableId", invalidId, Integer.class);

        verify(timetableServiceMock, never()).updateExistingTimetable(any(UpdateTimetableData.class));
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedTimetableIdentifierIsNotValid() throws Exception {
        Integer negativeId = -1;
        UpdateTimetableData updateData = new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION,
                CLASSROOM_ID);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(updateData), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{timetableId}",
                PUT,
                request,
                String.class,
                negativeId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "timetableId", Message.POSITIVE_ID);

        verify(timetableServiceMock, never()).updateExistingTimetable(any(UpdateTimetableData.class));
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedUpdateDataIsNotValid() throws Exception {
        int invalidTimetableId = -1;
        LocalDateTime startTimeInthePast = LocalDateTime.now().minusDays(3);
        int tooShortDuration = 20;
        int invalidClassrommId = 0;
        UpdateTimetableData invalidData = new UpdateTimetableData(
                invalidTimetableId,
                startTimeInthePast,
                tooShortDuration,
                invalidClassrommId);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(invalidData), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{timetableId}",
                PUT,
                request,
                String.class,
                TIMETABLE_ID);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "startTime", "Future.startTime");
        jsonVerifier.verifyValidationError(response.getBody(), "duration", "TimetableDuration");
        jsonVerifier.verifyValidationError(response.getBody(), "classroomId", "Positive.classroomId");
        jsonVerifier.verifyValidationError(response.getBody(), "timetableId", "Positive.timetableId");

        verify(timetableServiceMock, never()).updateExistingTimetable(any(UpdateTimetableData.class));
    }

    @Test
    void shouldReturnStatusBadRequestIfUpdateDataIsMissing() throws Exception {
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{timetableId}",
                PUT,
                request,
                String.class,
                TIMETABLE_ID);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.MALFORMED_JSON_REQUEST);

        verify(timetableServiceMock, never()).updateExistingTimetable(any(UpdateTimetableData.class));
    }

}
