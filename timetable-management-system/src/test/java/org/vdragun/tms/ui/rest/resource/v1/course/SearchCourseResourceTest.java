package org.vdragun.tms.ui.rest.resource.v1.course;

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
import static org.vdragun.tms.ui.rest.resource.v1.course.CourseResource.BASE_URL;

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
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.util.Constants.Message;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {
                "jndi.datasource=false",
                "startup.data.initialize=false",
                "secured.rest=false" })
@Import({ EmbeddedDataSourceConfig.class, JsonVerifier.class })
@DisplayName("Course Resource Search Functionality Integration Test")
public class SearchCourseResourceTest {

    private static final String CONTENT_TYPE_JSON = "application/json";

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private CourseService courseServiceMock;

    @MockBean
    private AuthenticationManager authManagerMock;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldReturnAllAvailableCourses() throws Exception {
        List<Course> courses = generator.generateCourses(2);
        when(courseServiceMock.findAllCourses()).thenReturn(courses);

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.courses", hasSize(2));
        jsonVerifier.verifyCourseJson(response.getBody(), courses);
    }

    @Test
    void shouldReturnCourseByGivenIdentifier() throws Exception {
        Course course = generator.generateCourse();
        when(courseServiceMock.findCourseById(course.getId())).thenReturn(course);

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{courseId}", String.class,
                course.getId());

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyCourseJson(response.getBody(), course);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenCourseIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{courseId}", String.class,
                invalidId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
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

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{courseId}", String.class,
                courseId);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.RESOURCE_NOT_FOUND, Course.class.getSimpleName());
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenIdentifierIsNotInvalid() throws Exception {
        Integer negativeId = -1;

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{courseId}", String.class,
                negativeId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "courseId", Message.POSITIVE_ID);
    }
}
