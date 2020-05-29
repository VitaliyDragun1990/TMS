package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.timetable.TimetableResource.BASE_URL;

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
import org.vdragun.tms.dao.TimetableDao;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;

@DBRider
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({ EmbeddedDataSourceConfig.class, JsonVerifier.class })
@Transactional
@DisplayName("Timetable Resource Delete Functionality Integration Test")
public class DeleteTimetableResourceSystemTest {

    private static final Integer TIMETABLE_ID = 1;
    
    @Autowired
    private TimetableDao timetableDao;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers = new HttpHeaders();

    @Test
    @DataSet(value = "one-timetable.yml", cleanAfter = true, disableConstraints = true)
    @ExpectedDataSet("no-timetables.yml")
    void shouldDeleteTimetableById() throws Exception {
        assertThat(timetableDao.findAll(), hasSize(1));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{timetableId}",
                DELETE,
                request,
                Void.class,
                TIMETABLE_ID);

        assertThat(response.getStatusCode(), equalTo(OK));

        assertThat(timetableDao.findAll(), hasSize(0));
    }

}
