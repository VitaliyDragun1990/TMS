package org.vdragun.tms.ui.rest.resource.v1.course;

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
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.config.JsonVerifier;
import org.vdragun.tms.util.Constants.Message;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.course.CourseResource.BASE_URL;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {
                "jndi.datasource=false",
                "startup.data.initialize=false",
                "secured.rest=false",
                "secured.none=true"
        })
@Import({
        EmbeddedDataSourceConfig.class,
        JsonVerifier.class,
        TestSecurityConfig.class})
@DisplayName("Course Resource Search Functionality Integration Test")
public class SearchCourseResourceTest {

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private CourseService courseServiceMock;

    @MockBean
    private AuthenticationManager authManagerMock;

    private EntityGenerator generator = new EntityGenerator();

    private HttpHeaders headers = new HttpHeaders();

    @Test
    void shouldReturnPageWithCourses() throws Exception {
        List<Course> courses = generator.generateCourses(2);
        when(courseServiceMock.findCourses(any(Pageable.class))).thenReturn(new PageImpl<>(courses));

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
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.courses", hasSize(2));
        jsonVerifier.verifyCourseJson(response.getBody(), courses);
    }

    @Test
    void shouldReturnCourseByGivenIdentifier() throws Exception {
        Course course = generator.generateCourse();
        when(courseServiceMock.findCourseById(course.getId())).thenReturn(course);

        headers.add(HttpHeaders.ACCEPT, HAL_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{courseId}",
                HttpMethod.GET,
                request,
                String.class,
                course.getId());

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(HAL_JSON_VALUE));
        jsonVerifier.verifyCourseJson(response.getBody(), course);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenCourseIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";

        headers.add(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{courseId}",
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
                "courseId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoCourseWithGivenIdentifier() throws Exception {
        Integer courseId = 1;
        when(courseServiceMock.findCourseById(eq(courseId)))
                .thenThrow(new ResourceNotFoundException(Course.class, "Course with id=%d not found", courseId));

        headers.add(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{courseId}",
                HttpMethod.GET,
                request,
                String.class,
                courseId);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_JSON_VALUE));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.RESOURCE_NOT_FOUND, Course.class.getSimpleName());
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenIdentifierIsNotInvalid() throws Exception {
        Integer negativeId = -1;

        headers.add(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/{courseId}",
                HttpMethod.GET,
                request,
                String.class,
                negativeId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_JSON_VALUE));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "courseId", Message.POSITIVE_ID);
    }
}
