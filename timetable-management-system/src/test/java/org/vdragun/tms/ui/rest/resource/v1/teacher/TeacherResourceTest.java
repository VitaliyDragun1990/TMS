package org.vdragun.tms.ui.rest.resource.v1.teacher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.vdragun.tms.ui.rest.resource.v1.teacher.TeacherResource.BASE_URL;

import java.time.LocalDate;
import java.util.List;
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
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.teacher.TeacherData;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Title;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = TeacherResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Teacher Resource")
public class TeacherResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final int NUMBER_OF_TEACHERS = 2;
    private static final int NUMBER_OF_COURSES_PER_TEACHER = 2;

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
    void shouldReturnAllAvailableTeachers() throws Exception {
        List<Teacher> teachers = generator.generateTeachersWithCourse(NUMBER_OF_TEACHERS,
                NUMBER_OF_COURSES_PER_TEACHER);
        when(teacherServiceMock.findAllTeachers()).thenReturn(teachers);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON))
                .andExpect(jsonPath("$._embedded.teachers", hasSize(NUMBER_OF_TEACHERS)))
                .andExpect(jsonPath("$._embedded.teachers[*].courses", hasSize(NUMBER_OF_COURSES_PER_TEACHER)));

        jsonVerifier.verifyTeacherJson(resultActions, teachers);
    }

    @Test
    void shouldReturnTeacherByGivenIdentifier() throws Exception {
        Teacher teacher = generator.generateTeachersWithCourse(1, NUMBER_OF_COURSES_PER_TEACHER).get(0);
        when(teacherServiceMock.findTeacherById(teacher.getId())).thenReturn(teacher);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{teacherId}", teacher.getId())
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        jsonVerifier.verifyTeacherJson(resultActions, teacher);
    }

    @Test
    void shouldReturnStatusBadRequestIfGivenTeacherIdentifierIsNotNumber() throws Exception {
        String invalidId = "id";

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{teacherId}", invalidId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(
                resultActions,
                Message.ARGUMENT_TYPE_MISSMATCH,
                "teacherId", invalidId, Integer.class);
    }

    @Test
    void shouldReturnStatusNotFoundIfNoTeachertWithGivenIdentifier() throws Exception {
        Integer teacherId = 1;
        when(teacherServiceMock.findTeacherById(eq(teacherId)))
                .thenThrow(new ResourceNotFoundException("Teacher with id=%d not found", teacherId));

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{teacherId}", teacherId)
                .locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.RESOURCE_NOT_FOUND);
    }

    @Test
    void shouldReturnStatuBadrequestIfGivenIdentifierIsNotValid() throws Exception {
        Integer negativeId = -1;

        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{teacherId}", negativeId)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.VALIDATION_ERROR);
        jsonVerifier.verifyValidationError(resultActions, "teacherId", Message.POSITIVE_ID);
    }

    @Test
    void shouldRegisterNewTeacher() throws Exception {
        TeacherData registerData = new TeacherData("Jack", "Smith", LocalDate.now(), Title.INSTRUCTOR);
        Teacher registered = generator.generateTeacher();
        when(teacherServiceMock.registerNewTeacher(any(TeacherData.class))).thenReturn(registered);

        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
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

        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
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
        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        jsonVerifier.verifyErrorMessage(resultActions, Message.MALFORMED_JSON_REQUEST);
    }
}
