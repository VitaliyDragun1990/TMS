package org.vdragun.tms.ui.rest.resource.v1.course;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.vdragun.tms.config.Constants.Message;
import org.vdragun.tms.config.EmbeddedDataSourceConfig;
import org.vdragun.tms.config.EntityGenerator;
import org.vdragun.tms.config.JsonVerifier;
import org.vdragun.tms.config.TestSecurityConfig;
import org.vdragun.tms.core.application.service.course.CourseData;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.domain.Course;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.course.CourseResource.BASE_URL;

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
        TestSecurityConfig.class})
@DisplayName("Course Resource Register Functionality Integration Test")
class RegisterCourseResourceTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private CourseService courseServiceMock;

    @MockBean
    private AuthenticationManager authManagerMock;

    @Captor
    private ArgumentCaptor<CourseData> captor;

    private EntityGenerator generator = new EntityGenerator();

    private HttpHeaders headers = new HttpHeaders();

    @Test
    void shouldRegisterNewCourse() throws Exception {
        CourseData registerData = new CourseData("English", "Course description", 1, 1);
        Course registered = generator.generateCourse();
        when(courseServiceMock.registerNewCourse(any(CourseData.class))).thenReturn(registered);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, HAL_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(registerData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL,
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(CREATED));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(HAL_JSON_VALUE));
        jsonVerifier.verifyCourseJson(response.getBody(), registered);

        verify(courseServiceMock, times(1)).registerNewCourse(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(registerData));
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedRegistrationDataIsNotValid() throws Exception {
        String invalidCourseName = "eng-25";
        String notLatinDescription = "не латинские символы";
        int negativeCategoryId = -1;
        CourseData invalidData = new CourseData(invalidCourseName, notLatinDescription, negativeCategoryId, null);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(invalidData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL,
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_JSON_VALUE));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "name", "CourseName");
        jsonVerifier.verifyValidationError(response.getBody(), "description", "LatinSentence");
        jsonVerifier.verifyValidationError(response.getBody(), "categoryId", "Positive.categoryId");
        jsonVerifier.verifyValidationError(response.getBody(), "teacherId", "NotNull.teacherId");

        verify(courseServiceMock, never()).registerNewCourse(any(CourseData.class));
    }

    @Test
    void shouldReturnStatusBadRequestIfRegistrationDataIsMissing() throws Exception {
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL,
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_JSON_VALUE));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.MALFORMED_JSON_REQUEST);

        verify(courseServiceMock, never()).registerNewCourse(any(CourseData.class));
    }

}
