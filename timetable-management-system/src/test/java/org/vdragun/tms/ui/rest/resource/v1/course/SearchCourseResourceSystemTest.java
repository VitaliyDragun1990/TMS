package org.vdragun.tms.ui.rest.resource.v1.course;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.OK;
import static org.vdragun.tms.ui.rest.resource.v1.AbstractResource.APPLICATION_HAL_JSON;
import static org.vdragun.tms.ui.rest.resource.v1.course.CourseResource.BASE_URL;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;

@DBRider
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({ EmbeddedDataSourceConfig.class, JsonVerifier.class })
@Transactional
@DisplayName("Course Resource Search Functionality System Test")
public class SearchCourseResourceSystemTest {

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DataSet(value = "two-courses.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnAllAvailableCoursesFromDatabase() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));
        jsonVerifier.verifyJson(response.getBody(), "$._embedded.courses", hasSize(2));

        List<Course> expectedCourses = courseDao.findAll();
        jsonVerifier.verifyCourseJson(response.getBody(), expectedCourses);
    }

    @Test
    @DataSet(value = "two-courses.yml", cleanAfter = true, disableConstraints = true)
    void shouldReturnCourseByGivenIdentifierFromDatabase() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/{courseId}", String.class,
                1);

        assertThat(response.getStatusCode(), equalTo(OK));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(APPLICATION_HAL_JSON));

        Course expectedCourse = courseDao.findAll().get(0);
        jsonVerifier.verifyCourseJson(response.getBody(), expectedCourse);
    }
}
