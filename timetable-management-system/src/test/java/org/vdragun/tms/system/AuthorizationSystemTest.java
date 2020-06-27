package org.vdragun.tms.system;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.rest.resource.v1.TestTokenGenerator;
import org.vdragun.tms.ui.rest.resource.v1.student.StudentResource;
import org.vdragun.tms.ui.rest.resource.v1.teacher.TeacherResource;
import org.vdragun.tms.util.Constants.Message;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {
                "jndi.datasource=false",
                "startup.data.initialize=false" })
@Import({
        EmbeddedDataSourceConfig.class,
        JsonVerifier.class,
        TestTokenGenerator.class
})
@TestPropertySource("/system-test.properties")
public class AuthorizationSystemTest {

    private static final String BEARER = "Bearer_";

    private String authToken;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private TestTokenGenerator tokenGenerator;

    @Value("${invalidToken}")
    private String invalidToken;

    private HttpHeaders headers = new HttpHeaders();

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_users.sql" })
    void shouldReturnStatusUnauthorizedIfTryToAccessProtectedResource() throws Exception {
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                StudentResource.BASE_URL,
                HttpMethod.GET,
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.AUTHENTICATION_REQUIRED);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_users.sql" })
    void shouldReturnStatusUnauthorizedIftryToAccessProtectedResourceWithInvalidToken() throws Exception {
        headers.add(AUTHORIZATION, BEARER + invalidToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                TeacherResource.BASE_URL,
                HttpMethod.GET,
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.INVALID_JWT_TOKEN);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_users.sql", "/sql/teacher_data.sql" })
    void shouldAllowToAccessProtectedResourceIfUserPossessesEnoughAuthority() {
        authToken = tokenGenerator.generateToken("teacher", "ROLE_TEACHER");
        headers.add(AUTHORIZATION, BEARER + authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                TeacherResource.BASE_URL,
                HttpMethod.GET,
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_users.sql" })
    void shouldReturnStatusForbiddenIfTryToAccessProtectedResourceWithoutRequiredAuthority() throws Exception {
        authToken = tokenGenerator.generateToken("student", "ROLE_STUDENT");
        headers.add(AUTHORIZATION, BEARER + authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                TeacherResource.BASE_URL,
                HttpMethod.GET,
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.ACCESS_DENIED);
    }

}
