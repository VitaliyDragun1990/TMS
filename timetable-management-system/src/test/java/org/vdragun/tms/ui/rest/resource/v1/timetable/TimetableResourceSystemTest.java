package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.timetable.TimetableResource.BASE_URL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.core.application.service.timetable.CreateTimetableData;
import org.vdragun.tms.core.application.service.timetable.UpdateTimetableData;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.TimetableDao;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.rest.resource.v1.TestTokenGenerator;
import org.vdragun.tms.util.Constants.Roles;
import org.vdragun.tms.util.translator.Translator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;

@DBRider
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({
        EmbeddedDataSourceConfig.class,
        JsonVerifier.class,
        TestTokenGenerator.class
})
@Transactional
@DisplayName("Timetable Resource System Test")
public class TimetableResourceSystemTest {

    private static final String BEARER = "Bearer_";
    private static final String ADMIN = "admin";

    private static final LocalDateTime REGISTER_TIMETABLE_START_TIME = LocalDateTime.now()
            .plusDays(1)
            .withHour(10)
            .withMinute(0)
            .truncatedTo(MINUTES);
    private static final LocalDateTime UPDATE_TIMETABLE_START_TIME = LocalDateTime.now()
            .plusDays(2)
            .withHour(11)
            .withMinute(0)
            .truncatedTo(MINUTES);
    private static final Integer TIMETABLE_ID = 1;
    private static final Integer STUDENT_ID = 1;
    private static final Integer TEACHER_ID = 1;
    private static final Integer CLASSROOM_ID_ONE = 1;
    private static final Integer CLASSROOM_ID_TWO = 2;
    private static final Integer COURSE_ID = 1;
    private static final int DURATION_SIXTY = 60;
    private static final int DURATION_SEVENTY = 70;

    private String authToken;

    @Autowired
    private TimetableDao timetableDao;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Translator translator;

    @Autowired
    private TestTokenGenerator tokenGenerator;

    private HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    void generateAuthToken() {
        authToken = tokenGenerator.generateToken(ADMIN, Roles.ADMIN);
    }

    @Test
    @DataSet(value = { "no-timetables.yml", "three-users.yml" }, cleanAfter = true, disableConstraints = true)
    @ExpectedDataSet("one-timetable.yml")
    void shouldRegisterNewTimetableInDatabase() throws Exception {
        assertThat(timetableDao.findAll(), hasSize(0));

        CreateTimetableData registerData = new CreateTimetableData(
                REGISTER_TIMETABLE_START_TIME,
                DURATION_SIXTY,
                COURSE_ID,
                CLASSROOM_ID_ONE,
                TEACHER_ID);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, BEARER + authToken);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(registerData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL,
                request,
                String.class);

        List<Timetable> allTimetables = timetableDao.findAll();
        assertThat(allTimetables, hasSize(1));
        jsonVerifier.verifyTimetableJson(response.getBody(), allTimetables.get(0));
    }
    
    @Test
    @DataSet(value = { "two-timetables.yml", "three-users.yml" }, cleanAfter = true, disableConstraints = true)
    void shouldReturnAllAvailableTimetablesFromDatabase() throws Exception {
        headers.add(AUTHORIZATION, BEARER + authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                request,
                String.class);

        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(2));

        List<Timetable> expectedTimetables = timetableDao.findAll();
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }

    @Test
    @DataSet(value = { "one-timetable.yml", "three-users.yml" }, cleanAfter = true, disableConstraints = true)
    void shouldReturnTimetableByGivenIdentifierFromDatabase() throws Exception {
        headers.add(AUTHORIZATION, BEARER + authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{timetableId}",
                HttpMethod.GET,
                request,
                String.class,
                TIMETABLE_ID);

        Timetable expectedTimetable = timetableDao.findAll().get(0);
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetable);
    }

    @Test
    @DataSet(value = { "two-timetables.yml", "three-users.yml" }, cleanAfter = true, disableConstraints = true)
    void shouldReturnDailyTimetablesForTeacherFromDatabase() throws Exception {
        LocalDate targetDate = LocalDate.now().plusDays(1);

        headers.add(AUTHORIZATION, BEARER + authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/teacher/{teacherId}/day?targetDate=" + translator.formatDateDefault(targetDate),
                HttpMethod.GET,
                request,
                String.class,
                TEACHER_ID);

        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(1));
        List<Timetable> expectedTimetables = timetableDao.findDailyForTeacher(TEACHER_ID, targetDate);
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }

    @Test
    @DataSet(
            value = { "two-timetables-for-student.yml", "three-users.yml" },
            cleanAfter = true,
            disableConstraints = true)
    void shouldReturnDailyTimetablesForStudentFromDatabase() throws Exception {
        LocalDate targetDate = LocalDate.now().plusDays(1);

        headers.add(AUTHORIZATION, BEARER + authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/student/{studentId}/day?targetDate=" + translator.formatDateDefault(targetDate),
                HttpMethod.GET,
                request,
                String.class,
                STUDENT_ID);

        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(2));
        List<Timetable> expectedTimetables = timetableDao.findDailyForStudent(STUDENT_ID, targetDate);
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }

    @Test
    @DataSet(value = { "two-timetables.yml", "three-users.yml" }, cleanAfter = true, disableConstraints = true)
    void shouldReturnMonthlyTimetablesForTeacherFromDatabase() throws Exception {
        Month targetMonth = LocalDate.now().plusDays(1).getMonth();

        headers.add(AUTHORIZATION, BEARER + authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate
                .exchange(
                BASE_URL + "/teacher/{teacherId}/month?targetMonth=" + translator.formatMonth(targetMonth),
                        HttpMethod.GET,
                        request,
                String.class,
                TEACHER_ID);

        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(1));
        List<Timetable> expectedTimetables = timetableDao.findMonthlyForTeacher(TEACHER_ID, targetMonth);
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }

    @Test
    @DataSet(
            value = { "two-timetables-for-student.yml", "three-users.yml" },
            cleanAfter = true,
            disableConstraints = true)
    void shouldReturnMonthlyTimetablesForStudentFromDatabase() throws Exception {
        Month targetMonth = LocalDate.now().plusDays(1).getMonth();

        headers.add(AUTHORIZATION, BEARER + authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/student/{studentId}/month?targetMonth=" + translator.formatMonth(targetMonth),
                HttpMethod.GET,
                request,
                String.class,
                STUDENT_ID);

        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(2));
        List<Timetable> expectedTimetables = timetableDao.findMonthlyForStudent(STUDENT_ID, targetMonth);
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }

    @Test
    @DataSet(value = { "one-timetable.yml", "three-users.yml" }, cleanAfter = true, disableConstraints = true)
    @ExpectedDataSet("one-timetable-updated.yml")
    void shouldUpdateExistingTimetableInDatabase() throws Exception {
        UpdateTimetableData updateData = new UpdateTimetableData(
                TIMETABLE_ID,
                UPDATE_TIMETABLE_START_TIME,
                DURATION_SEVENTY,
                CLASSROOM_ID_TWO);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, BEARER + authToken);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(updateData), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{timetableId}",
                PUT,
                request,
                String.class,
                TIMETABLE_ID);

        Timetable updatedTimetable = timetableDao.findById(TIMETABLE_ID).get();
        jsonVerifier.verifyTimetableJson(response.getBody(), updatedTimetable);
    }

    @Test
    @DataSet(value = { "one-timetable.yml", "three-users.yml" }, cleanAfter = true, disableConstraints = true)
    @ExpectedDataSet("no-timetables.yml")
    void shouldDeleteTimetableByIdFromDatabase() throws Exception {
        assertThat(timetableDao.findAll(), hasSize(1));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, BEARER + authToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        restTemplate.exchange(
                BASE_URL + "/{timetableId}",
                DELETE,
                request,
                Void.class,
                TIMETABLE_ID);

        assertThat(timetableDao.findAll(), hasSize(0));
    }

}
