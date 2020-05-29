package org.vdragun.tms.ui.rest.resource.v1.student;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.OK;
import static org.vdragun.tms.ui.rest.resource.v1.AbstractResource.APPLICATION_HAL_JSON;
import static org.vdragun.tms.ui.rest.resource.v1.student.StudentResource.BASE_URL;

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
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.dao.StudentDao;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;

@DBRider
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({ EmbeddedDataSourceConfig.class, JsonVerifier.class })
@Transactional
@DisplayName("Student Resource Search Functionality System Test")
public class SearchStudentResourceSystemTest {

    private static final int NUMBER_OF_STUDENTS = 2;
    private static final int NUMBER_OF_COURSES_PER_STUDENT = 1;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DataSet(value = "two-students.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnAllAvailableStudentsFromDatabase() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyJson(response.getBody(),
                "$._embedded.students", hasSize(NUMBER_OF_STUDENTS));
        jsonVerifier.verifyJson(response.getBody(),
                "$._embedded.students[0].courses", hasSize(NUMBER_OF_COURSES_PER_STUDENT));
        jsonVerifier.verifyJson(response.getBody(),
                "$._embedded.students[1].courses", hasSize(NUMBER_OF_COURSES_PER_STUDENT));

        List<Student> expectedStudents = studentDao.findAll();
        jsonVerifier.verifyStudentJson(response.getBody(), expectedStudents);
    }

    @Test
    @DataSet(value = "one-student.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnStudentByGivenIdentifierFromDatabase() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{studentId}", String.class,
                1);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));

        Student expectedStudent = studentDao.findAll().get(0);
        jsonVerifier.verifyStudentJson(response.getBody(), expectedStudent);
    }

}
