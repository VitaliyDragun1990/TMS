package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.TimetableDao;
import org.vdragun.tms.ui.common.util.Translator;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;

@DBRider
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({ EmbeddedDataSourceConfig.class, JsonVerifier.class })
@Transactional
@DisplayName("Timetable Resource Search Functionality Integration Test")
public class SearchTimetableResourceSystemTest {

    private static final Integer TEACHER_ID = 1;
    private static final Integer STUDENT_ID = 1;

    @Autowired
    private TimetableDao timetableDao;

    @Autowired
    private Translator translator;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DataSet(value = "two-timetables.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnAllAvailableTimetables() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(2));

        List<Timetable> expectedTimetables = timetableDao.findAll();
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }

    @Test
    @DataSet(value = "one-timetable.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnTimetableByGivenIdentifier() throws Exception {
        Integer timetableId = 1;
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{timetableId}", String.class,
                timetableId);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));

        Timetable expectedTimetable = timetableDao.findAll().get(0);
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetable);
    }

    @Test
    @DataSet(value = "two-timetables.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnDailyTimetablesForTeacher() throws Exception {
        LocalDate targetDate = LocalDate.now().plusDays(1);
        
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/day?targetDate=" + translator.formatDateDefault(targetDate),
                String.class,
                TEACHER_ID);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));

        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(1));
        List<Timetable> expectedTimetables = timetableDao.findDailyForTeacher(TEACHER_ID, targetDate);
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }

    @Test
    @DataSet(value = "two-timetables-for-student.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnDailyTimetablesForStudent() throws Exception {
        LocalDate targetDate = LocalDate.now().plusDays(1);

        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/student/{studentId}/day?targetDate=" + translator.formatDateDefault(targetDate),
                String.class,
                STUDENT_ID);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));

        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(2));
        List<Timetable> expectedTimetables = timetableDao.findDailyForStudent(STUDENT_ID, targetDate);
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }
    
    @Test
    @DataSet(value = "two-timetables.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnMonthlyTimetablesForTeacher() throws Exception {
        Month targetMonth = LocalDate.now().getMonth();
        
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/teacher/{teacherId}/month?targetMonth=" + translator.formatMonth(targetMonth),
                String.class,
                TEACHER_ID);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));

        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(1));
        List<Timetable> expectedTimetables = timetableDao.findMonthlyForTeacher(TEACHER_ID, targetMonth);
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }
    
    @Test
    @DataSet(value = "two-timetables-for-student.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnMonthlyTimetablesForStudent() throws Exception {
        Month targetMonth = LocalDate.now().getMonth();
        
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/student/{studentId}/month?targetMonth=" + translator.formatMonth(targetMonth),
                String.class,
                STUDENT_ID);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));

        jsonVerifier.verifyJson(response.getBody(), "$._embedded.timetables", hasSize(2));
        List<Timetable> expectedTimetables = timetableDao.findMonthlyForStudent(STUDENT_ID, targetMonth);
        jsonVerifier.verifyTimetableJson(response.getBody(), expectedTimetables);
    }

}
