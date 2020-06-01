package org.vdragun.tms.ui.rest.resource.v1.course;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.ui.rest.resource.v1.course.CourseResource.BASE_URL;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.core.application.service.course.CourseData;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;

@DBRider
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({ EmbeddedDataSourceConfig.class, JsonVerifier.class })
@DisplayName("Course Resource System Test")
public class CourseResourceSystemTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers = new HttpHeaders();

    @Test
    @Transactional
    @DataSet(value = "empty-courses.yml", cleanAfter = true, disableConstraints = true)
    @ExpectedDataSet("one-course.yml")
    void shouldRegisterNewCourseInTheDatabase() throws Exception {
        assertThat(courseDao.findAll(), hasSize(0));
        CourseData registerData = new CourseData("New Art", "Amazing course", 1, 1);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(registerData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL,
                request,
                String.class);

        assertThat(courseDao.findAll(), hasSize(1));
        Course registeredCourse = courseDao.findAll().get(0);
        jsonVerifier.verifyCourseJson(response.getBody(), registeredCourse);
    }

    @Test
    @DataSet(value = "two-courses.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnAllAvailableCoursesFromDatabase() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class);

        jsonVerifier.verifyJson(response.getBody(), "$._embedded.courses", hasSize(2));

        List<Course> expectedCourses = courseDao.findAll();
        jsonVerifier.verifyCourseJson(response.getBody(), expectedCourses);
    }

    @Test
    @DataSet(value = "two-courses.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnCourseByGivenIdentifierFromDatabase() throws Exception {
        int courseId = 1;
        ResponseEntity<String> response = restTemplate.getForEntity(
                BASE_URL + "/{courseId}",
                String.class,
                courseId);

        Course expectedCourse = courseDao.findAll().get(0);
        jsonVerifier.verifyCourseJson(response.getBody(), expectedCourse);
    }

}
