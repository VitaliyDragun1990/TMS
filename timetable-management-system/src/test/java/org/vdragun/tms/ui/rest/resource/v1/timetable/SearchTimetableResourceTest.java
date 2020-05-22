package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebRestConfig;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.common.util.Translator;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

@WebMvcTest(controllers = SearchTimetableResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Search Timetable Resource")
public class SearchTimetableResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final int NUMBER_OF_TIMETABLES = 2;
    private static final Integer TEACHER_ID = 1;
    private static final Integer STUDENT_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Translator translator;

    @Autowired
    private JsonVerifier jsonVerifier;

    @MockBean
    private TimetableService timetableServiceMock;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldReturnAllAvailableTimetables() throws Exception {
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findAllTimetables()).thenReturn(expectedTimetables);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/timetables").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON))
                .andExpect(jsonPath("$._embedded.timetables", hasSize(2)));

        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetables);
    }

    @Test
    void shouldReturnTimetableByGivenIdentifier() throws Exception {
        Timetable expectedTimetable = generator.generateTimetable();
        when(timetableServiceMock.findTimetableById(expectedTimetable.getId())).thenReturn(expectedTimetable);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/timetables/{timetableId}", expectedTimetable.getId())
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetable);
    }

    @Test
    void shouldReturnDailyTimetablesForTeacher() throws Exception {
        LocalDate targetDate = LocalDate.now();
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findDailyTimetablesForTeacher(TEACHER_ID, targetDate)).thenReturn(expectedTimetables);
        
        ResultActions resultActions = mockMvc.perform(get("/api/v1/timetables/teacher/{teacherId}/day", TEACHER_ID)
                .locale(Locale.US)
                .param("targetDate", translator.formatDate(targetDate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));
        
        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetables);
    }

    @Test
    void shouldReturnDailyTimetablesForStudent() throws Exception {
        LocalDate targetDate = LocalDate.now();
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findDailyTimetablesForStudent(STUDENT_ID, targetDate)).thenReturn(expectedTimetables);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/timetables/student/{studentId}/day", STUDENT_ID)
                .locale(Locale.US)
                .param("targetDate", translator.formatDate(targetDate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetables);
    }
    
    @Test
    void shouldReturnMonthlyTimetablesForTeacher() throws Exception {
        Month targetMonth = Month.MAY;
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findMonthlyTimetablesForTeacher(TEACHER_ID, targetMonth))
                .thenReturn(expectedTimetables);
        
        ResultActions resultActions = mockMvc.perform(get("/api/v1/timetables/teacher/{teacherId}/month", TEACHER_ID)
                .locale(Locale.US)
                .param("targetMonth", translator.formatMonth(targetMonth)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));
        
        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetables);
    }
    
    @Test
    void shouldReturnMonthlyTimetablesForStudent() throws Exception {
        Month targetMonth = Month.MAY;
        List<Timetable> expectedTimetables = generator.generateTimetables(NUMBER_OF_TIMETABLES);
        when(timetableServiceMock.findMonthlyTimetablesForStudent(STUDENT_ID, targetMonth))
                .thenReturn(expectedTimetables);
        
        ResultActions resultActions = mockMvc.perform(get("/api/v1/timetables/student/{studentId}/month", STUDENT_ID)
                .locale(Locale.US)
                .param("targetMonth", translator.formatMonth(targetMonth)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));
        
        jsonVerifier.verifyTimetableJson(resultActions, expectedTimetables);
    }

}
