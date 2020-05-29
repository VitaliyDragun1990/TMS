package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.AbstractResource.APPLICATION_HAL_JSON;
import static org.vdragun.tms.ui.rest.resource.v1.timetable.TimetableResource.BASE_URL;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.core.application.service.timetable.CreateTimetableData;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.TimetableDao;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;

@DBRider
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({ EmbeddedDataSourceConfig.class, JsonVerifier.class })
@Transactional
@DisplayName("Timetable Resource Register Functionality System Test")
public class RegisterTimetableResourceSystemTest {

    private static final LocalDateTime TIMETABLE_START_TIME = LocalDateTime.now()
            .plusDays(1)
            .withHour(10)
            .withMinute(0)
            .truncatedTo(MINUTES);
    private static final int TEACHER_ID = 1;
    private static final int CLASSROOM_ID = 1;
    private static final int COURSE_ID = 1;
    private static final int DURATION = 60;

    @Autowired
    private TimetableDao timetableDao;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers = new HttpHeaders();

    @Test
    @DataSet(value = "no-timetables.yml", cleanAfter = true, disableConstraints = true)
    @ExpectedDataSet("one-timetable.yml")
    void shouldRegisterNewTimetable() throws Exception {
        assertThat(timetableDao.findAll(), hasSize(0));

        CreateTimetableData registerData =
                new CreateTimetableData(TIMETABLE_START_TIME, DURATION, COURSE_ID, CLASSROOM_ID, TEACHER_ID);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(registerData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL,
                request,
                String.class);
        
        assertThat(response.getStatusCode(), equalTo(CREATED));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));


        List<Timetable> allTimetables = timetableDao.findAll();
        assertThat(allTimetables, hasSize(1));
        jsonVerifier.verifyTimetableJson(response.getBody(), allTimetables.get(0));
    }
    
}
