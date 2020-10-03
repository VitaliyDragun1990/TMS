package org.vdragun.tms.system;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.vdragun.tms.config.EmbeddedDataSourceConfig;
import org.vdragun.tms.security.dao.UserDao;
import org.vdragun.tms.security.rest.jwt.JwtTokenProvider;
import org.vdragun.tms.security.rest.service.SigninRequest;
import org.vdragun.tms.security.rest.service.SignupRequest;
import org.vdragun.tms.config.JsonVerifier;
import org.vdragun.tms.config.Constants.Message;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.vdragun.tms.security.rest.resource.v1.AuthenticationResource.BASE_URL;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {
                "jndi.datasource=false",
                "startup.data.initialize=false"})
@Import({
        EmbeddedDataSourceConfig.class,
        JsonVerifier.class
})
@DisplayName("Authentication Resource System Test")
public class AuthenticationResourceSystemTest {

    private static final String CONTENT_TYPE_JSON = "application/json";

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
    @Sql(scripts = {"/sql/clear_database.sql", "/sql/three_roles.sql"})
    void shouldRegisterNewUserInDatabase() throws Exception {
        SignupRequest requestData = new SignupRequest(
                "jack125",
                "Jack",
                "Smith",
                "jack125@gmail.com",
                "Password12",
                asList("ROLE_STUDENT", "ROLE_TEACHER"));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(requestData), headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(
                        BASE_URL + "/signup",
                        request,
                        String.class);

        assertThat(response.getStatusCode(), equalTo(CREATED));
        jsonVerifier.verifyJson(response.getBody(), "$.username", equalTo("jack125"));
        String token = jsonVerifier.getValueByExpression(response.getBody(), "$.token");
        assertTokenValid(token);
        assertThat(userDao.findByUsername("jack125"), not(nullValue()));
    }

    @Test
    @Rollback
    @Sql(scripts = {"/sql/clear_database.sql", "/sql/three_users.sql"})
    void shouldLoginExistingUser() throws Exception {
        SigninRequest requestData = new SigninRequest("admin", "password");
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(requestData), headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(
                        BASE_URL + "/signin",
                        request,
                        String.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        jsonVerifier.verifyJson(response.getBody(), "$.username", equalTo("admin"));
        String token = jsonVerifier.getValueByExpression(response.getBody(), "$.token");
        assertTokenValid(token);
    }

    @Test
    @Rollback
    @Sql(scripts = {"/sql/clear_database.sql", "/sql/three_users.sql"})
    void shouldReturnStatusUnauthorizedIfProvidedCredentialsInalid() throws Exception {
        SigninRequest requestData = new SigninRequest("invalid", "password");
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(requestData), headers);

        ResponseEntity<String> response = restTemplate
                .exchange(
                        BASE_URL + "/signin",
                        HttpMethod.POST,
                        request,
                        String.class);

        assertThat(response.getStatusCode(), equalTo(UNAUTHORIZED));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.BAD_CREDENTIALS);
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedSignInDataInvalid() throws Exception {
        String invalidUsername = "jack-123_";
        String blankPassword = "    ";
        SigninRequest invalidData = new SigninRequest(invalidUsername, blankPassword);

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(invalidData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/signin",
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "username", "Username");
        jsonVerifier.verifyValidationError(response.getBody(), "password", "NotBlank.password");
    }

    @Test
    @Rollback
    @Sql(scripts = {"/sql/clear_database.sql", "/sql/three_roles.sql"})
    void shouldReturnStatusBadRequestIfProvidedSignUpDataInvalid() throws Exception {
        String invalidUsername = "jack-123_";
        String nonLatinFirstName = "Джек";
        String lastNameWithNumbers = "Smith123";
        String invalidEmail = "jack.gmail.com";
        String tooWeakPassword = "jack123";
        String nonExistingRole = "ROLE_USER";
        String invalidRoleName = "admin";
        SignupRequest invalidData = new SignupRequest(
                invalidUsername,
                nonLatinFirstName,
                lastNameWithNumbers,
                invalidEmail,
                tooWeakPassword,
                asList(nonExistingRole, invalidRoleName));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(invalidData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/signup",
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "username", "Username");
        jsonVerifier.verifyValidationError(response.getBody(), "firstName", "PersonName");
        jsonVerifier.verifyValidationError(response.getBody(), "lastName", "PersonName");
        jsonVerifier.verifyValidationError(response.getBody(), "email", "Email");
        jsonVerifier.verifyValidationError(response.getBody(), "password", "Password");

        jsonVerifier.verifyValidationError(response.getBody(), "roles[0]", "ValidRole");
        jsonVerifier.verifyValidationError(response.getBody(), "roles[1]", "ValidRole");
    }

    @Test
    @Rollback
    @Sql(scripts = {"/sql/clear_database.sql", "/sql/three_users.sql"})
    void shouldReturnStatusBadRequestIfUsernameInSignupDataAlreadyTaken() throws Exception {
        String alreadyTakenUsername = "student";
        SignupRequest invalidData = new SignupRequest(
                alreadyTakenUsername,
                "Jack",
                "Smith",
                "jack@gmail.com",
                "Password123",
                asList("ROLE_STUDENT"));

        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(invalidData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/signup",
                request,
                String.class);

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST));
        String contentType = response.getHeaders().getContentType().toString();
        assertThat(contentType, containsString(CONTENT_TYPE_JSON));
        jsonVerifier.verifyErrorMessage(response.getBody(), Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(response.getBody(), "username", "UniqueUsername");
        jsonVerifier.verifyValidationErrorsCount(response.getBody(), 1);
    }

    private void assertTokenValid(String token) {
        assertDoesNotThrow(() -> tokenProvider.validateToken(token));
    }

}
