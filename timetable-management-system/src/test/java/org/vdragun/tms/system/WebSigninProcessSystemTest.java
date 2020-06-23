package org.vdragun.tms.system;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.security.WithMockAuthenticatedUser;
import org.vdragun.tms.util.Constants.View;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {
                "jndi.datasource=false",
                "startup.data.initialize=false" })
@Import({
        EmbeddedDataSourceConfig.class
})
@DisplayName("Web sign in process System Test")
public class WebSigninProcessSystemTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldShowSignInForm() throws Exception {
        mockMvc.perform(get("/auth/signin").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(view().name(View.SIGN_IN_FORM));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_users.sql" })
    void shouldRedirectToHomePageIfSignInIsSuccessful() throws Exception {
        mockMvc.perform(formLogin("/auth/signin")
                .user("admin")
                .password("password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_users.sql" })
    void shouldAuthenticateWithApropriateRoles() throws Exception {
        mockMvc.perform(formLogin("/auth/signin")
                .user("student")
                .password("password"))
                .andExpect(authenticated().withRoles("STUDENT"));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/three_users.sql" })
    void shouldRedirectToSignInPageIfSignInAtemptFailed() throws Exception {
        mockMvc.perform(formLogin("/auth/signin")
                .user("unknown")
                .password("invalid"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/signin?error"));
    }

    @Test
    @WithMockAuthenticatedUser
    void shouldRedirectToSignInPageIfSignOutAtemptSuccessful() throws Exception {
        mockMvc.perform(logout("/auth/signout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/signin?logout"));
    }

}
