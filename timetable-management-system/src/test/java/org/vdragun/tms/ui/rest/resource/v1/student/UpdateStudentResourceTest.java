package org.vdragun.tms.ui.rest.resource.v1.student;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.AbstractResource.APPLICATION_HAL_JSON;
import static org.vdragun.tms.ui.rest.resource.v1.student.StudentResource.BASE_URL;

import java.util.List;

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
import org.springframework.security.authentication.AuthenticationManager;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.application.service.student.UpdateStudentData;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.security.TestSecurityConfig;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.util.Constants.Message;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {
                "jndi.datasource=false",
                "startup.data.initialize=false",
                "secured.rest=false",
                "secured.web=false",
                "secured.none=true",
        })
@Import({
        EmbeddedDataSourceConfig.class,
        JsonVerifier.class,
        TestSecurityConfig.class })
@DisplayName("Student Resource Update Functionality Integration Test")
public class UpdateStudentResourceTest {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final int GROUP_ID = 2;
    private static final int STUDENT_ID = 1;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private StudentService studentServiceMock;

    @MockBean
    private AuthenticationManager authManagerMock;

    @Captor
    private ArgumentCaptor<UpdateStudentData> captor;


    private EntityGenerator generator = new EntityGenerator();

    private HttpHeaders headers = new HttpHeaders();

    @Test
    void shouldUpdateExistingStudent() throws Exception {
        UpdateStudentData updateData = new UpdateStudentData(STUDENT_ID, GROUP_ID, "Jack", "Smith", asList(3, 4));
        Student expectedStudent = generator.generateStudent();
        when(studentServiceMock.updateExistingStudent(any(UpdateStudentData.class))).thenReturn(expectedStudent);

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
        jsonVerifier.verifyStudentJson(response.getBody(), expectedStudent);
        
        verify(studentServiceMock, times(1)).updateExistingStudent(captor.capture());
    }

    @Test
    void shouldReturnStatusNotFoundIfSpecifiedStudentToUpdateNotExist() throws Exception {
        UpdateStudentData updateData = new UpdateStudentData(STUDENT_ID, GROUP_ID, "Jack", "Smith", asList(3, 4));
        when(studentServiceMock.updateExistingStudent(any(UpdateStudentData.class)))
                .thenThrow(new ResourceNotFoundException(Student.class, "Student with id=%d not found", STUDENT_ID));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(updateData), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{studentId}",
                PUT,
                request,
                String.class,
                STUDENT_ID);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.RESOURCE_NOT_FOUND, Student.class.getSimpleName());
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedStudentIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";
        UpdateStudentData updateData = new UpdateStudentData(STUDENT_ID, GROUP_ID, "Jack", "Smith", asList(3, 4));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(updateData), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{studentId}",
                PUT,
                request,
                String.class,
                invalidId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.ARGUMENT_TYPE_MISSMATCH,
                "studentId", invalidId, Integer.class);

        verify(studentServiceMock, never()).updateExistingStudent(any(UpdateStudentData.class));
    }
    
    @Test
    void shouldReturnStatusBadRequestIfProvidedStudentIdIsNotValid() throws Exception {
        Integer negativeId = -1;
        UpdateStudentData updateData = new UpdateStudentData(STUDENT_ID, GROUP_ID, "Jack", "Smith", asList(3, 4));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(updateData), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{studentId}",
                PUT,
                request,
                String.class,
                negativeId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "studentId", Message.POSITIVE_ID);

        verify(studentServiceMock, never()).updateExistingStudent(any(UpdateStudentData.class));
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedUpdateDataIsInvalid() throws Exception {
        int invalidStudentId = 0;
        int negativeGroupId = -1;
        String notLatinFirstName = "Джек";
        String tooShortLastName = "S";
        List<Integer> invalidCourseIds = asList(null, negativeGroupId);
        UpdateStudentData invalidData = new UpdateStudentData(
                invalidStudentId,
                negativeGroupId,
                notLatinFirstName,
                tooShortLastName,
                invalidCourseIds);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(invalidData), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{studentId}",
                PUT,
                request,
                String.class,
                STUDENT_ID);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "studentId", "Positive.studentId");
        jsonVerifier.verifyValidationError(response.getBody(), "groupId", "Positive.groupId");
        jsonVerifier.verifyValidationError(response.getBody(), "firstName", "PersonName");
        jsonVerifier.verifyValidationError(response.getBody(), "lastName", "PersonName");
        jsonVerifier.verifyValidationError(response.getBody(), "courseIds[0]", "NotNull.courseIds");
        jsonVerifier.verifyValidationError(response.getBody(), "courseIds[1]", "Positive");

        verify(studentServiceMock, never()).updateExistingStudent(any(UpdateStudentData.class));
    }

    @Test
    void shouldReturnStatusBadRequestIfUpdateDataIsMissing() throws Exception {
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{studentId}",
                PUT,
                request,
                String.class,
                STUDENT_ID);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.MALFORMED_JSON_REQUEST);

        verify(studentServiceMock, never()).updateExistingStudent(any(UpdateStudentData.class));
    }

}
