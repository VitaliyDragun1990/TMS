package org.vdragun.tms.ui.rest.resource.v1.teacher;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
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
import org.springframework.http.ResponseEntity;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.DaoTestConfig;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({ DaoTestConfig.class, JsonVerifier.class })
@DisplayName("Teacher Resource Search Functionality Integration Test")
public class SearchTeacherResourceIntegrationTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final int NUMBER_OF_TEACHERS = 2;
    private static final int NUMBER_OF_COURSES_PER_TEACHER = 2;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TeacherService teacherServiceMock;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldReturnAllAvailableTeachers() throws Exception {
        List<Teacher> teachers =
                generator.generateTeachersWithCourse(NUMBER_OF_TEACHERS, NUMBER_OF_COURSES_PER_TEACHER);
        when(teacherServiceMock.findAllTeachers()).thenReturn(teachers);

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_HAL_JSON));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.teachers", hasSize(NUMBER_OF_TEACHERS));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.teachers[*].courses",
                hasSize(NUMBER_OF_COURSES_PER_TEACHER));
        jsonVerifier.verifyTeacherJson(response.getBody(), teachers);
    }

    @Test
    void shouldReturnTeacherByGivenIdentifier() throws Exception {
        Teacher teacher = generator.generateTeachersWithCourse(1, NUMBER_OF_COURSES_PER_TEACHER).get(0);
        when(teacherServiceMock.findTeacherById(teacher.getId())).thenReturn(teacher);

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{teacherId}", String.class,
                teacher.getId());

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_HAL_JSON));
        jsonVerifier.verifyTeacherJson(response.getBody(), teacher);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenTeacherIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{teacherId}", String.class,
                invalidId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(
                response.getBody(),
                Message.ARGUMENT_TYPE_MISSMATCH,
                "teacherId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoTeachertWithGivenIdentifier() throws Exception {
        Integer teacherId = 1;
        when(teacherServiceMock.findTeacherById(eq(teacherId)))
                .thenThrow(new ResourceNotFoundException("Teacher with id=%d not found", teacherId));

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{teacherId}", String.class,
                teacherId);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.RESOURCE_NOT_FOUND);
    }

    @Test
    void shouldReturnStatuBadRequestIfGivenIdentifierIsNotValid() throws Exception {
        Integer negativeId = -1;

        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{teacherId}", String.class,
                negativeId);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "teacherId", Message.POSITIVE_ID);
    }

}
