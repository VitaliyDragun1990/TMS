package org.vdragun.tms.system;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.security.dao.UserDao;
import org.vdragun.tms.security.model.User;
import org.vdragun.tms.security.web.service.SignupForm;
import org.vdragun.tms.security.web.service.WebAuthenticationService;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.View;
import org.vdragun.tms.util.Constants.Role;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {
                "jndi.datasource=false",
                "startup.data.initialize=false" })
@Import({
        EmbeddedDataSourceConfig.class
})
@DisplayName("Authentication Controller System Test")
public class AuthenticationControllerSystemTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserDao userDao;

    @SpyBean
    private WebAuthenticationService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldShowSignUpForm() throws Exception {
        mockMvc.perform(get("/auth/signup").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.SIGN_UP_FORM))
                .andExpect(model().attribute(Attribute.SIGN_UP_FORM, samePropertyValuesAs(new SignupForm())))
                .andExpect(view().name(View.SIGN_UP_FORM));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_roles.sql" })
    void shouldCreateNewUserInDatabaseIfSignUpSuccessfully() throws Exception {
        String username = "jack25";
        String firstName = "Jack";
        String lastName = "Smith";
        String password = "JackPassword125";
        String email = "jack@gmail.com";
        String role = Role.STUDENT;

        mockMvc.perform(post("/auth/signup").with(csrf())
                .locale(Locale.US)
                .param("username", username)
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("password", password)
                .param("confirmPassword", password)
                .param("email", email)
                .param("role", role))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/signin"));

        User expectedUser = new User(username, firstName, lastName, email, "");
        expectedUser.addRole(new org.vdragun.tms.security.model.Role(100001, role));
        User registeredUser = userDao.findByUsername(username);
        assertThat(registeredUser, samePropertyValuesAs(expectedUser, "id", "password", "created", "updated"));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_users.sql" })
    void shouldShowSignUpFormWithValidationErrorsIfUsernameAlreadyTaken() throws Exception {
        String takenUsername = "admin";
        String firstName = "Jack";
        String lastName = "Smith";
        String password = "JackPassword125";
        String email = "jack@gmail.com";
        String role = Role.STUDENT;

        mockMvc.perform(post("/auth/signup").with(csrf())
                .locale(Locale.US)
                .param("username", takenUsername)
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("password", password)
                .param("confirmPassword", password)
                .param("email", email)
                .param("role", role))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors(Attribute.SIGN_UP_FORM, "username"))
                .andExpect(view().name(View.SIGN_UP_FORM));

        verify(authService, never()).processSignUp(any(SignupForm.class));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_users.sql" })
    void shouldShowSignUpFormWithValidationErrorsIfEmailAlreadyTaken() throws Exception {
        String username = "jack125";
        String firstName = "Jack";
        String lastName = "Smith";
        String password = "JackPassword125";
        String alreadyTakenEmail = "admin@gmail.com";
        String role = Role.STUDENT;

        mockMvc.perform(post("/auth/signup").with(csrf())
                .locale(Locale.US)
                .param("username", username)
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("password", password)
                .param("confirmPassword", password)
                .param("email", alreadyTakenEmail)
                .param("role", role))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors(Attribute.SIGN_UP_FORM, "email"))
                .andExpect(view().name(View.SIGN_UP_FORM));

        verify(authService, never()).processSignUp(any(SignupForm.class));
    }

}
