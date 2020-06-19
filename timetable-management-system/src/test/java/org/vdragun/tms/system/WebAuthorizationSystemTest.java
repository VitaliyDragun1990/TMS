package org.vdragun.tms.system;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.security.WithMockAuthenticatedUser;
import org.vdragun.tms.ui.web.controller.MessageProvider;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.Constants.Page;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {
                "jndi.datasource=false",
                "startup.data.initialize=false" })
@Import({
        EmbeddedDataSourceConfig.class,
        MessageProvider.class
})
@DisplayName("Web authorixation System Test")
public class WebAuthorizationSystemTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MessageProvider messageProvider;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldRedirectToSignInPageIftryToAccessProtectedResourceUnauthorized() throws Exception {
        mockMvc.perform(get("/students").locale(Locale.US))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/signin"));
    }

    @Test
    @WithMockAuthenticatedUser(roles = { "ROLE_STUDENT" })
    void shouldRedirectToAccessDeniedPageIfTryToAccessProtectedResourceWithoutRequiredAuthorities() throws Exception {
        mockMvc.perform(get("/timetables/register").locale(Locale.US))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(request().sessionAttribute(
                        Attribute.ACCESS_DENIED_MSG,
                        messageProvider.getMessage(Message.REQUESTED_RESOURCE, "/timetables/register")))
                .andExpect(redirectedUrl("/403"));
    }

    @Test
    @WithMockAuthenticatedUser(roles = { "ROLE_ADMIN" })
    void shouldShowProtectedResourceIfProvideRequiredAuthorities() throws Exception {
        mockMvc.perform(get("/timetables/register").locale(Locale.US))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(Page.TIMETABLE_REG_FORM));
    }

}
