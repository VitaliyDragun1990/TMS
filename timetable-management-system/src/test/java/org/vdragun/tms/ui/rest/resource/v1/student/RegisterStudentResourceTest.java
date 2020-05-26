package org.vdragun.tms.ui.rest.resource.v1.student;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.AbstractResource.APPLICATION_HAL_JSON;
import static org.vdragun.tms.ui.rest.resource.v1.student.StudentResource.BASE_URL;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.vdragun.tms.core.application.service.student.CreateStudentData;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.dao.DaoTestConfig;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({ DaoTestConfig.class, JsonVerifier.class })
@DisplayName("Student Resource Register Functionality Integration Test")
public class RegisterStudentResourceTest {

    private static final String CONTENT_TYPE_JSON = "application/json";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private StudentService studentServiceMock;

    @Captor
    private ArgumentCaptor<CreateStudentData> captor;

    private EntityGenerator generator = new EntityGenerator();

    private HttpHeaders headers = new HttpHeaders();

    @Test
    void shouldRegisterNewStudent() throws Exception {
        CreateStudentData registerData = new CreateStudentData("Jack", "Smith", LocalDate.now());
        Student expectedStudent = generator.generateStudent();
        when(studentServiceMock.registerNewStudent(any(CreateStudentData.class))).thenReturn(expectedStudent);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(registerData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL,
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(CREATED));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyStudentJson(response.getBody(), expectedStudent);

        verify(studentServiceMock, times(1)).registerNewStudent(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(registerData));
    }
    
    @Test
    void shouldReturnStatusBadRequestIfProvidedRegistrationDataIsNotValid() throws Exception {
        String notLatinFirstName = "Джек";
        String tooShortLastName = "S";
        LocalDate futureRegistrationdate = LocalDate.now().plusDays(10);
        CreateStudentData invalidData = 
                new CreateStudentData(notLatinFirstName, tooShortLastName, futureRegistrationdate);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(invalidData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL,
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "firstName", "PersonName");
        jsonVerifier.verifyValidationError(response.getBody(), "lastName", "PersonName");
        jsonVerifier.verifyValidationError(response.getBody(), "enrollmentDate", "PastOrPresent.enrollmentDate");

        verify(studentServiceMock, never()).registerNewStudent(any(CreateStudentData.class));
    }

    @Test
    void shouldReturnStatusBadRequestIfRegistrationDataIsMissing() throws Exception {
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL,
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.MALFORMED_JSON_REQUEST);

        verify(studentServiceMock, never()).registerNewStudent(any(CreateStudentData.class));
    }

}
