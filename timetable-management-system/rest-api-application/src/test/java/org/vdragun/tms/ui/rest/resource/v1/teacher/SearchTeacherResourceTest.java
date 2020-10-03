package org.vdragun.tms.ui.rest.resource.v1.teacher;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.teacher.TeacherResource.BASE_URL;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.vdragun.tms.config.EmbeddedDataSourceConfig;
import org.vdragun.tms.config.EntityGenerator;
import org.vdragun.tms.config.TestSecurityConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.config.JsonVerifier;
import org.vdragun.tms.config.Constants.Message;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {
                "jndi.datasource=false",
                "startup.data.initialize=false",
                "secured.rest=false",
                "secured.none=true",
        })
@Import({
        EmbeddedDataSourceConfig.class,
        JsonVerifier.class,
        TestSecurityConfig.class })
@DisplayName("Teacher Resource Search Functionality Integration Test")
class SearchTeacherResourceTest {

    private static final int NUMBER_OF_TEACHERS = 2;

    private static final int NUMBER_OF_COURSES_PER_TEACHER = 2;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TeacherService teacherServiceMock;

    @MockBean
    private AuthenticationManager authManagerMock;

    private EntityGenerator generator = new EntityGenerator();

    private HttpHeaders headers = new HttpHeaders();

    @Test
    void shouldReturnPageWithTeachers() throws Exception {
        List<Teacher> teachers =
                generator.generateTeachersWithCourse(NUMBER_OF_TEACHERS, NUMBER_OF_COURSES_PER_TEACHER);
        when(teacherServiceMock.findTeachers(any(Pageable.class))).thenReturn(new PageImpl<>(teachers));

        headers.add(HttpHeaders.ACCEPT, HAL_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(HAL_JSON_VALUE));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.teachers", hasSize(NUMBER_OF_TEACHERS));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.teachers[*].courses",
                hasSize(NUMBER_OF_COURSES_PER_TEACHER));
        jsonVerifier.verifyTeacherJson(response.getBody(), teachers);
    }

    @Test
    void shouldReturnTeacherByGivenIdentifier() throws Exception {
        Teacher teacher = generator.generateTeachersWithCourse(1, NUMBER_OF_COURSES_PER_TEACHER).get(0);
        when(teacherServiceMock.findTeacherById(teacher.getId())).thenReturn(teacher);

        headers.add(HttpHeaders.ACCEPT, HAL_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{teacherId}",
                HttpMethod.GET,
                request,
                String.class,
                teacher.getId());

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(HAL_JSON_VALUE));
        jsonVerifier.verifyTeacherJson(response.getBody(), teacher);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenTeacherIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";

        headers.add(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{teacherId}",
                HttpMethod.GET,
                request,
                String.class,
                invalidId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_JSON_VALUE));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.ARGUMENT_TYPE_MISSMATCH,
                "teacherId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoTeachertWithGivenIdentifier() throws Exception {
        Integer teacherId = 1;
        when(teacherServiceMock.findTeacherById(eq(teacherId)))
                .thenThrow(new ResourceNotFoundException(Teacher.class, "Teacher with id=%d not found", teacherId));

        headers.add(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{teacherId}",
                HttpMethod.GET,
                request,
                String.class,
                teacherId);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_JSON_VALUE));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.RESOURCE_NOT_FOUND, Teacher.class.getSimpleName());
    }

    @Test
    void shouldReturnStatuBadRequestIfGivenIdentifierIsNotValid() throws Exception {
        Integer negativeId = -1;

        headers.add(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{teacherId}",
                HttpMethod.GET,
                request,
                String.class,
                negativeId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_JSON_VALUE));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "teacherId", Message.POSITIVE_ID);
    }

}
