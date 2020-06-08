package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.vdragun.tms.ui.rest.resource.v1.AbstractResource.APPLICATION_HAL_JSON;
import static org.vdragun.tms.ui.rest.resource.v1.timetable.TimetableResource.BASE_URL;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.localizer.TemporalLocalizer;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({ EmbeddedDataSourceConfig.class, JsonVerifier.class })
@ActiveProfiles("test")
@DisplayName("Timetable Resource Search Functionality Integration Test")
public class SearchTimetableResourceTest {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final int NUMBER_OF_TIMETABLES = 2;
    private static final Integer TEACHER_ID = 1;
    private static final Integer STUDENT_ID = 1;

    @Autowired
    private TemporalLocalizer temporalLocalizer;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TimetableService timetableServiceMock;

    @MockBean
    private AuthenticationManager authManagerMock;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldReturnAllAvailableTimetables() throws Exception {
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findAllTimetables()).thenReturn(expectedTimetables);

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(2));
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }

    @Test
    void shouldReturnTimetableByGivenIdentifier() throws Exception {
        Timetable expectedTimetable = generator.generateTimetable();
        when(timetableServiceMock.findTimetableById(expectedTimetable.getId())).thenReturn(expectedTimetable);

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{timetableId}", String.class,
                expectedTimetable.getId());

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetable);
    }

    @Test
    void shouldReturnDailyTimetablesForTeacher() throws Exception {
        LocalDate targetDate = LocalDate.now();
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findDailyTimetablesForTeacher(TEACHER_ID, targetDate)).thenReturn(expectedTimetables);
        
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/day?targetDate=" + temporalLocalizer.localizeDateDefault(targetDate),
                String.class,
                TEACHER_ID);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(NUMBER_OF_TIMETABLES));
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }

    @Test
    void shouldReturnDailyTimetablesForStudent() throws Exception {
        LocalDate targetDate = LocalDate.now();
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findDailyTimetablesForStudent(STUDENT_ID, targetDate)).thenReturn(expectedTimetables);

        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/student/{studentId}/day?targetDate=" + temporalLocalizer.localizeDateDefault(targetDate),
                String.class,
                STUDENT_ID);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(NUMBER_OF_TIMETABLES));
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }
    
    @Test
    void shouldReturnMonthlyTimetablesForTeacher() throws Exception {
        Month targetMonth = Month.MAY;
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findMonthlyTimetablesForTeacher(TEACHER_ID, targetMonth))
                .thenReturn(expectedTimetables);
        
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/month?targetMonth=" + temporalLocalizer.localizeMonth(targetMonth),
                String.class,
                TEACHER_ID);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(NUMBER_OF_TIMETABLES));
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }
    
    @Test
    void shouldReturnMonthlyTimetablesForStudent() throws Exception {
        Month targetMonth = Month.MAY;
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findMonthlyTimetablesForStudent(STUDENT_ID, targetMonth))
                .thenReturn(expectedTimetables);
        
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/student/{studentId}/month?targetMonth=" + temporalLocalizer.localizeMonth(targetMonth),
                String.class,
                STUDENT_ID);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(NUMBER_OF_TIMETABLES));
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenTimetableIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{timetableId}", String.class,
                invalidId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.ARGUMENT_TYPE_MISSMATCH,
                "timetableId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoTimetableWithGivenIdentifier() throws Exception {
        Integer timetableId = 1;
        when(timetableServiceMock.findTimetableById(eq(timetableId)))
                .thenThrow(new ResourceNotFoundException(Timetable.class, "Timetable with id=%d not found",
                        timetableId));

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{timetableId}", String.class,
                timetableId);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.RESOURCE_NOT_FOUND,
                Timetable.class.getSimpleName());
    }

    @Test
    void shouldReturnStatuBadRequestIfGivenIdentifierIsNotValid() throws Exception {
        Integer negativeId = -1;

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{timetableId}", String.class,
                negativeId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "timetableId", Message.POSITIVE_ID);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenTeacherIdentifierIsNotNumberForDailyRequest() throws Exception {
        String invalidId = "id";
        LocalDate targetDate = LocalDate.now();

        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/day?targetDate=" + temporalLocalizer.localizeDateDefault(targetDate),
                String.class,
                invalidId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.ARGUMENT_TYPE_MISSMATCH,
                "teacherId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoTeacherWithGivenIdentifierForDailyRequest() throws Exception {
        LocalDate targetDate = LocalDate.now();
        when(timetableServiceMock.findDailyTimetablesForTeacher(TEACHER_ID, targetDate))
                .thenThrow(new ResourceNotFoundException(Teacher.class, "Teacher with id=%d not found", TEACHER_ID));

        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/day?targetDate=" + temporalLocalizer.localizeDateDefault(targetDate),
                String.class,
                TEACHER_ID);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.RESOURCE_NOT_FOUND,
                Teacher.class.getSimpleName());
    }

    @Test
    void shouldReturnStatuBadRequestIfGivenTeacherIdentifierIsNotValidForDailyRequest() throws Exception {
        Integer negativeId = -1;
        LocalDate targetDate = LocalDate.now();

        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/day?targetDate=" + temporalLocalizer.localizeDateDefault(targetDate),
                String.class,
                negativeId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "teacherId", Message.POSITIVE_ID);
    }

    @Test
    void shouldReturnStatusBadRequestIfTargetDateParameterIsMissingForDailyTeacherRequest() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/day",
                String.class,
                TEACHER_ID);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.MISSING_REQUIRED_PARAMETER,
                "targetDate");
    }

    @Test
    void shouldReturnStatusBadRequestIfTargetDateParameterIsNotValidDateForDailyTeacherRequest() throws Exception {
        String targetDate = "invalid";

        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/day?targetDate=" + targetDate,
                String.class,
                TEACHER_ID);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.ARGUMENT_TYPE_MISSMATCH,
                "targetDate", targetDate, LocalDate.class);
    }
    @Test
    void shouldReturnStatusBadRequestIfGivenTeacherIdentifierIsNotNumberForMonthlyRequest() throws Exception {
        String invalidId = "id";
        Month targetMonth = Month.MAY;

        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/month?targetMonth=" + temporalLocalizer.localizeMonth(targetMonth),
                String.class,
                invalidId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.ARGUMENT_TYPE_MISSMATCH,
                "teacherId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoTeacherWithGivenIdentifierForMonthlyRequest() throws Exception {
        Month targetMonth = Month.MAY;
        when(timetableServiceMock.findMonthlyTimetablesForTeacher(TEACHER_ID, targetMonth))
                .thenThrow(new ResourceNotFoundException(Teacher.class, "Teacher with id=%d not found", TEACHER_ID));

        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/month?targetMonth=" + temporalLocalizer.localizeMonth(targetMonth),
                String.class,
                TEACHER_ID);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.RESOURCE_NOT_FOUND,
                Teacher.class.getSimpleName());
    }

    @Test
    void shouldReturnStatuBadRequestIfGivenTeacherIdentifierIsNotValidForMonthlyRequest() throws Exception {
        Integer negativeId = -1;
        Month targetMonth = Month.MAY;

        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/month?targetMonth=" + temporalLocalizer.localizeMonth(targetMonth),
                String.class,
                negativeId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "teacherId", Message.POSITIVE_ID);
    }

    @Test
    void shouldReturnStatusBadRequestIfTargetMonthParameterIsMissingForMonthlyTeacherRequest() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/month",
                String.class,
                TEACHER_ID);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.MISSING_REQUIRED_PARAMETER,
                "targetMonth");
    }

    @Test
    void shouldReturnStatusBadRequestIfTargetMonthParameterIsNotValidMonthForMonthlyTeacherRequest() throws Exception {
        String targetMonth = "invalid";

        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/month?targetMonth=" + targetMonth,
                String.class,
                TEACHER_ID);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.ARGUMENT_TYPE_MISSMATCH,
                "targetMonth", targetMonth, Month.class);
    }

}
