package org.vdragun.tms.system;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.security.rest.resource.v1.AuthenticationResource.BASE_URL;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.security.dao.UserDao;
import org.vdragun.tms.security.rest.jwt.JwtTokenProvider;
import org.vdragun.tms.security.rest.service.SigninRequest;
import org.vdragun.tms.security.rest.service.SignupRequest;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.util.Constants.Message;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "tms.stage.development=false")
@Import({
        EmbeddedDataSourceConfig.class,
        JsonVerifier.class
})
@DisplayName("Authentication Resource System Test")
public class AuthenticationResourceSystemTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserDao userDao;

    private HttpHeaders headers = new HttpHeaders();

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_roles.sql" })
    void shouldRegisterNewUserInDatabase() throws Exception {
        SignupRequest requestData = new SignupRequest(
                "jack125",
                "Jack",
                "Smith",
                "jack125@gmail.com",
                "password",
                asList("ROLE_STUDENT", "ROLE_TEACHER"));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(requestData), headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(
                        BASE_URL + "/signup",
                        request,
                        String.class);

        jsonVerifier.verifyJson(response.getBody(), "$.username", equalTo("jack125"));
        String token = jsonVerifier.getValueByExpression(response.getBody(), "$.token");
        assertTokenValid(token);
        assertThat(userDao.findByUsername("jack125"), not(nullValue()));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_users.sql" })
    void shouldLoginExistingUser() throws Exception {
        SigninRequest requestData = new SigninRequest("admin", "password");
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(requestData), headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(
                        BASE_URL + "/signin",
                        request,
                        String.class);

        jsonVerifier.verifyJson(response.getBody(), "$.username", equalTo("admin"));
        String token = jsonVerifier.getValueByExpression(response.getBody(), "$.token");
        assertTokenValid(token);
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_users.sql" })
    void shouldReturnStatusUnauthorizedIfProvidedCredentialsInInalid() throws Exception {
        SigninRequest requestData = new SigninRequest("invalid", "password");
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(requestData), headers);

        ResponseEntity<String> response = restTemplate
                .exchange(
                        BASE_URL + "/signin",
                        HttpMethod.POST,
                        request,
                        String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.BAD_CREDENTIALS);
    }

    private void assertTokenValid(String token) {
        assertDoesNotThrow(() -> tokenProvider.validateToken(token));
    }

}
