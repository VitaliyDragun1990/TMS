package org.vdragun.tms.ui.rest.resource.v1.student;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.AbstractResource.APPLICATION_HAL_JSON;
import static org.vdragun.tms.ui.rest.resource.v1.student.StudentResource.BASE_URL;

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
import org.vdragun.tms.core.application.service.student.UpdateStudentData;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.dao.StudentDao;
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
@DisplayName("Student Resource Update Functionality System Test")
public class UpdateStudentResourceSystemTest {

    private static final int STUDENT_ID = 1;
    private static final int GROUP_ID = 1;
    private static final int COURSE_A_ID = 1;
    private static final int COURSE_B_ID = 2;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers = new HttpHeaders();


    @Test
    @DataSet(value = "student-before-update.yml", cleanAfter = true, disableConstraints = true)
    @ExpectedDataSet("student-after-update.yml")
    void shouldUpdateExistingStudentInTheDatabase() throws Exception {
        UpdateStudentData updateData = new UpdateStudentData(
                STUDENT_ID,
                GROUP_ID,
                "Marty",
                "Mcfly",
                asList(COURSE_A_ID, COURSE_B_ID));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(updateData), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{studentId}",
                PUT,
                request,
                String.class,
                STUDENT_ID);
        
        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));

        Student updatedStudent = studentDao.findById(1).get();
        jsonVerifier.verifyStudentJson(response.getBody(), updatedStudent);
    }

}
