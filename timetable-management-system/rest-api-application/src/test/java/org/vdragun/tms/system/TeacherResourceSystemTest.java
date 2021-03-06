package org.vdragun.tms.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;
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
import org.vdragun.tms.config.EmbeddedDataSourceConfig;
import org.vdragun.tms.core.application.service.teacher.TeacherData;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Title;
import org.vdragun.tms.dao.TeacherDao;
import org.vdragun.tms.config.JsonVerifier;
import org.vdragun.tms.config.TestTokenGenerator;
import org.vdragun.tms.config.Constants.Role;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.teacher.TeacherResource.BASE_URL;

@DBRider
@DBUnit(schema = "PUBLIC")
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {
                "jndi.datasource=false",
                "startup.data.initialize=false"})
@Import({
        EmbeddedDataSourceConfig.class,
        JsonVerifier.class,
        TestTokenGenerator.class
})
@DisplayName("Teacher Resource System Test")
class TeacherResourceSystemTest {

    private static final String BEARER = "Bearer_";

    private static final String ADMIN = "admin";

    private static final int NUMBER_OF_TEACHERS = 2;

    private static final int NUMBER_OF_COURSES_PER_TEACHER = 1;

    private String authToken;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestTokenGenerator tokenGenerator;

    private HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    void generateAuthToken() {
        authToken = tokenGenerator.generateToken(ADMIN, Role.ADMIN);
    }

    @Test
    @DataSet({"three-users.yml"})
    @ExpectedDataSet({"one-teacher.yml"})
    void shouldRegisterNewTeacherInDatabase() throws Exception {
        assertThat(teacherDao.findAll(), hasSize(0));
        TeacherData registerData = new TeacherData("Jack", "Smith", LocalDate.of(2020, 5, 9), Title.PROFESSOR);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, BEARER + authToken);
        headers.add(HttpHeaders.ACCEPT, HAL_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(registerData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL,
                request,
                String.class);

        assertThat(teacherDao.findAll(), hasSize(1));
        Teacher registeredTeacher = teacherDao.findAll().get(0);
        jsonVerifier.verifyTeacherJson(response.getBody(), registeredTeacher);
    }

    @Test
    @DataSet(value = {"two-teachers.yml", "three-users.yml"}, cleanAfter = true, disableConstraints = true)
    void shouldReturnAllAvailableTeachersFromDatabase() throws Exception {
        headers.add(AUTHORIZATION, BEARER + authToken);
        headers.add(HttpHeaders.ACCEPT, HAL_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                request,
                String.class);

        jsonVerifier.verifyJson(response.getBody(), "$._embedded.teachers", hasSize(NUMBER_OF_TEACHERS));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.teachers[0].courses",
                hasSize(NUMBER_OF_COURSES_PER_TEACHER));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.teachers[1].courses",
                hasSize(NUMBER_OF_COURSES_PER_TEACHER));

        List<Teacher> expectedTeachers = teacherDao.findAll();
        jsonVerifier.verifyTeacherJson(response.getBody(), expectedTeachers);
    }

    @Test
    @DataSet(value = {"one-teacher.yml", "three-users.yml"}, cleanAfter = true, disableConstraints = true)
    void shouldReturnTeacherByGivenIdentifierFromDatabase() throws Exception {
        int teacherId = 1;

        headers.add(AUTHORIZATION, BEARER + authToken);
        headers.add(HttpHeaders.ACCEPT, HAL_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{teacherId}",
                HttpMethod.GET,
                request,
                String.class,
                teacherId);

        Teacher expectedTeacher = teacherDao.findAll().get(0);
        jsonVerifier.verifyTeacherJson(response.getBody(), expectedTeacher);
    }

}
