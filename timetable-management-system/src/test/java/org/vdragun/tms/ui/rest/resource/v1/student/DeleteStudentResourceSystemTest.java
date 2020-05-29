package org.vdragun.tms.ui.rest.resource.v1.student;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
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
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.dao.StudentDao;
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
@DisplayName("Student Resource Delete Functionality System Test")
public class DeleteStudentResourceSystemTest {

    private static final Integer STUDENT_ID_TO_DELETE = 1;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers = new HttpHeaders();

    @Test
    @DataSet(value = "two-students-before-delete.yml", cleanAfter = true, disableConstraints = true)
    @ExpectedDataSet("one-student-after-delete.yml")
    void shouldDeleteStudentByGivenIdentifierFromTheDatabase() throws Exception {
        assertThat(studentDao.findAll(), hasSize(2));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        restTemplate.exchange(
                BASE_URL + "/{studentId}",
                DELETE,
                request,
                Void.class,
                STUDENT_ID_TO_DELETE);

        assertThat(studentDao.findAll(), hasSize(1));
    }

}
