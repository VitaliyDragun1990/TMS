package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.AbstractResource.APPLICATION_HAL_JSON;
import static org.vdragun.tms.ui.rest.resource.v1.timetable.TimetableResource.BASE_URL;

import java.time.LocalDateTime;

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
import org.vdragun.tms.core.application.service.timetable.UpdateTimetableData;
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
@DisplayName("Timetable Resource Update Functionality Integration Test")
public class UpdateTimetableResourceSystemTest {

    private static final LocalDateTime TIMETABLE_START_TIME = LocalDateTime.now()
            .plusDays(2)
            .withHour(11)
            .withMinute(0)
            .truncatedTo(MINUTES);
    private static final Integer TIMETABLE_ID = 1;
    private static final Integer CLASSROOM_ID = 2;
    private static final int DURATION = 70;

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
    @DataSet(value = "one-timetable.yml", cleanAfter = true, disableConstraints = true)
    @ExpectedDataSet("one-timetable-updated.yml")
    void shouldUpdateExistingTimetable() throws Exception {
        UpdateTimetableData updateData =
                new UpdateTimetableData(TIMETABLE_ID, TIMETABLE_START_TIME, DURATION, CLASSROOM_ID);

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

        Timetable updatedTimetable = timetableDao.findById(TIMETABLE_ID).get();
        jsonVerifier.verifyTimetableJson(response.getBody(), updatedTimetable);
    }
    
}
