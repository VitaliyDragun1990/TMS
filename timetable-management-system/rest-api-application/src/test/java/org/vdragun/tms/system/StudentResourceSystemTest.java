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
import org.vdragun.tms.core.application.service.student.CreateStudentData;
import org.vdragun.tms.core.application.service.student.UpdateStudentData;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.dao.StudentDao;
import org.vdragun.tms.config.JsonVerifier;
import org.vdragun.tms.config.TestTokenGenerator;
import org.vdragun.tms.util.Constants.Role;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.student.StudentResource.BASE_URL;

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
@DisplayName("Student Resource System Test")
public class StudentResourceSystemTest {

    private static final String BEARER = "Bearer_";
    private static final String ADMIN = "admin";

    private static final int NUMBER_OF_STUDENTS = 2;
    private static final int NUMBER_OF_COURSES_PER_STUDENT = 1;
    private static final Integer STUDENT_ID = 1;
    private static final Integer ID_STUDENT_TO_DELETE = 1;
    private static final Integer GROUP_ID = 1;
    private static final Integer COURSE_A_ID = 1;
    private static final Integer COURSE_B_ID = 2;

    private String authToken;

    @Autowired
    private StudentDao studentDao;

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
    @ExpectedDataSet("one-student.yml")
    void shouldRegisterNewStudentInDatabase() throws Exception {
        CreateStudentData registerData = new CreateStudentData("Jack", "Smith", LocalDate.of(2020, 5, 9));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, BEARER + authToken);
        headers.add(HttpHeaders.ACCEPT, HAL_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(registerData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL,
                request,
                String.class);

        Student registeredStudent = studentDao.findAll().get(0);
        jsonVerifier.verifyStudentJson(response.getBody(), registeredStudent);
    }

    @Test
    @DataSet(value = {"two-students.yml", "three-users.yml"}, cleanAfter = true, disableConstraints = true)
    void shouldReturnAllAvailableStudentsFromDatabase() throws Exception {
        headers.add(AUTHORIZATION, BEARER + authToken);
        headers.add(HttpHeaders.ACCEPT, HAL_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                request,
                String.class);

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
    @DataSet(value = {"one-student.yml", "three-users.yml"}, cleanAfter = true, disableConstraints = true)
    void shouldReturnStudentByGivenIdentifierFromDatabase() throws Exception {
        headers.add(AUTHORIZATION, BEARER + authToken);
        headers.add(HttpHeaders.ACCEPT, HAL_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{studentId}",
                HttpMethod.GET,
                request,
                String.class,
                STUDENT_ID);

        Student expectedStudent = studentDao.findAll().get(0);
        jsonVerifier.verifyStudentJson(response.getBody(), expectedStudent);
    }

    @Test
    @DataSet(value = {"student-before-update.yml", "three-users.yml"}, cleanAfter = true, disableConstraints = true)
    @ExpectedDataSet("student-after-update.yml")
    void shouldUpdateExistingStudentInTheDatabase() throws Exception {
        UpdateStudentData updateData = new UpdateStudentData(
                STUDENT_ID,
                GROUP_ID,
                "Marty",
                "Mcfly",
                asList(COURSE_A_ID, COURSE_B_ID));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, BEARER + authToken);
        headers.add(HttpHeaders.ACCEPT, HAL_JSON_VALUE);

        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(updateData), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{studentId}",
                PUT,
                request,
                String.class,
                STUDENT_ID);

        Student updatedStudent = studentDao.findById(1).get();
        jsonVerifier.verifyStudentJson(response.getBody(), updatedStudent);
    }

    @Test
    @DataSet(
            value = {"two-students-before-delete.yml", "three-users.yml"},
            cleanAfter = true,
            disableConstraints = true)
    @ExpectedDataSet("one-student-after-delete.yml")
    void shouldDeleteStudentByGivenIdentifierFromTheDatabase() throws Exception {
        assertThat(studentDao.findAll(), hasSize(2));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, BEARER + authToken);
        headers.add(HttpHeaders.ACCEPT, HAL_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        restTemplate.exchange(
                BASE_URL + "/{studentId}",
                DELETE,
                request,
                Void.class,
                ID_STUDENT_TO_DELETE);

        assertThat(studentDao.findAll(), hasSize(1));
    }

}
