package org.vdragun.tms.ui.rest.resource.v1.teacher;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.OK;
import static org.vdragun.tms.ui.rest.resource.v1.AbstractResource.APPLICATION_HAL_JSON;
import static org.vdragun.tms.ui.rest.resource.v1.teacher.TeacherResource.BASE_URL;

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
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.TeacherDao;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;

@DBRider
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({ EmbeddedDataSourceConfig.class, JsonVerifier.class })
@Transactional
@DisplayName("Teacher Resource Search Functionality Integration Test")
public class SearchTeacherResourceSystemTest {

    private static final int NUMBER_OF_TEACHERS = 2;
    private static final int NUMBER_OF_COURSES_PER_TEACHER = 1;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DataSet(value = "two-teachers.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnAllAvailableTeachersFromDatabase() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.teachers", hasSize(NUMBER_OF_TEACHERS));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.teachers[0].courses",
                hasSize(NUMBER_OF_COURSES_PER_TEACHER));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.teachers[1].courses",
                hasSize(NUMBER_OF_COURSES_PER_TEACHER));

        List<Teacher> expectedTeachers = teacherDao.findAll();
        jsonVerifier.verifyTeacherJson(response.getBody(), expectedTeachers);
    }

    @Test
    @DataSet(value = "one-teacher.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnTeacherByGivenIdentifierFromDatabase() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{teacherId}", String.class,
                1);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));

        Teacher expectedTeacher = teacherDao.findAll().get(0);
        jsonVerifier.verifyTeacherJson(response.getBody(), expectedTeacher);
    }

}
