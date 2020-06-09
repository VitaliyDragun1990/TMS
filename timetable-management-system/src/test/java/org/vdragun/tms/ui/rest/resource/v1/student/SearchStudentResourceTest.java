package org.vdragun.tms.ui.rest.resource.v1.student;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.vdragun.tms.ui.rest.resource.v1.AbstractResource.APPLICATION_HAL_JSON;
import static org.vdragun.tms.ui.rest.resource.v1.student.StudentResource.BASE_URL;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.TestPropertySource;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.util.Constants.Message;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({ EmbeddedDataSourceConfig.class, JsonVerifier.class })
@TestPropertySource(properties = "secured.rest=false")
@DisplayName("Student Resource Search Functionality Integration Test")
public class SearchStudentResourceTest {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final int NUMBER_OF_STUDENTS = 2;
    private static final int NUMBER_OF_COURSES_PER_STUDENT = 2;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private StudentService studentServiceMock;

    @MockBean
    private AuthenticationManager authManagerMock;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldReturnAllAvailableStudents() throws Exception {
        List<Student> expectedStudents =
                generator.generateStudentsWithCourses(NUMBER_OF_STUDENTS, NUMBER_OF_COURSES_PER_STUDENT);
        when(studentServiceMock.findAllStudents()).thenReturn(expectedStudents);

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyJson(response.getBody(),
                "$._embedded.students", hasSize(NUMBER_OF_STUDENTS));
        jsonVerifier.verifyJson(response.getBody(),
                "$._embedded.students[*].courses", hasSize(NUMBER_OF_COURSES_PER_STUDENT));
        jsonVerifier.verifyStudentJson(response.getBody(), expectedStudents);
    }

    @Test
    void shouldReturnStudentByGivenIdentifier() throws Exception {
        Student expectedStudent = generator.generateStudentsWithCourses(1, NUMBER_OF_COURSES_PER_STUDENT).get(0);
        when(studentServiceMock.findStudentById(expectedStudent.getId())).thenReturn(expectedStudent);

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{studentId}", String.class,
                expectedStudent.getId());

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyStudentJson(response.getBody(), expectedStudent);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenStudentIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{studentId}", String.class,
                invalidId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.ARGUMENT_TYPE_MISSMATCH,
                "studentId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoStudentWithGivenIdentifier() throws Exception {
        Integer studentId = 1;
        when(studentServiceMock.findStudentById(eq(studentId)))
                .thenThrow(new ResourceNotFoundException(Student.class, "Student with id=%d not found", studentId));
        
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{studentId}", String.class,
                studentId);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.RESOURCE_NOT_FOUND, Student.class.getSimpleName());
    }

    @Test
    void shouldReturnStatuBadRequestIfGivenIdentifierIsNotValid() throws Exception {
        Integer negativeId = -1;

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{studentId}", String.class,
                negativeId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "studentId", Message.POSITIVE_ID);
    }

}
