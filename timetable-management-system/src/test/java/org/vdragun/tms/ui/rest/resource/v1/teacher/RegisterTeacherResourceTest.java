package org.vdragun.tms.ui.rest.resource.v1.teacher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebRestConfig;
import org.vdragun.tms.core.application.service.teacher.TeacherData;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Title;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = RegisterTeacherResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Register Teacher Resource")
public class RegisterTeacherResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @MockBean
    private TeacherService teacherServiceMock;

    @Captor
    private ArgumentCaptor<TeacherData> captor;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldRegisterNewTeacher() throws Exception {
        TeacherData registerData = new TeacherData("Jack", "Smith", LocalDate.now(), Title.INSTRUCTOR);
        Teacher registered = generator.generateTeacher();
        when(teacherServiceMock.registerNewTeacher(any(TeacherData.class))).thenReturn(registered);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/teachers")
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(registerData)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        verify(teacherServiceMock, times(1)).registerNewTeacher(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(registerData));
        jsonVerifier.verifyTeacherJson(resultActions, registered);
    }

    @Test
    void shouldReturnStatusBadRequestIfProvidedRegistrationDataIsNotValid() throws Exception {
        String notLatinFirstName = "Джек";
        String tooShortLastName = "S";
        LocalDate dateHiredInFuture = LocalDate.now().plusDays(3);
        TeacherData invalidData = new TeacherData(notLatinFirstName, tooShortLastName, dateHiredInFuture, null);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/teachers")
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "firstName", "PersonName");
        jsonVerifier.verifyValidationError(resultActions, "lastName", "PersonName");
        jsonVerifier.verifyValidationError(resultActions, "dateHired", "PastOrPresent.dateHired");
        jsonVerifier.verifyValidationError(resultActions, "title", "NotNull.title");
    }

    @Test
    void shouldReturnStatusBadRequestIfRegistrationDataIsMissing() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/api/v1/teachers")
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.MALFORMED_JSON_REQUEST);
    }
}
