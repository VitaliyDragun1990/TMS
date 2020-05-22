package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
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
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

@WebMvcTest(controllers = SearchTimetableResource.class)
@Import({ WebConfig.class, WebRestConfig.class })
@DisplayName("Search Timetable Resource")
public class SearchTimetableResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final int NUMBER_OF_TIMETABLES = 2;
    private static final Integer TEACHER_ID = 1;
    private static final Integer STUDENT_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelConverter modelConverter;

    @Autowired
    private Translator translator;

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

        verifyJson(resultActions, expectedTimetables);
    }

    @Test
    void shouldReturnTimetableByGivenIdentifier() throws Exception {
        Timetable expectedTimetable = generator.generateTimetable();
        when(timetableServiceMock.findTimetableById(expectedTimetable.getId())).thenReturn(expectedTimetable);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/timetables/{timetableId}", expectedTimetable.getId())
                .locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));

        verifyJson(resultActions, expectedTimetable);
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
        
        verifyJson(resultActions, expectedTimetables);
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

        verifyJson(resultActions, expectedTimetables);
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
        
        verifyJson(resultActions, expectedTimetables);
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
        
        verifyJson(resultActions, expectedTimetables);
    }

    private void verifyJson(ResultActions actions, List<Timetable> timetables) throws Exception {
        List<TimetableModel> expected = modelConverter.convertList(timetables, Timetable.class, TimetableModel.class);
        for (int i = 0; i < expected.size(); i++) {
            TimetableModel expectedStudent = expected.get(i);
            actions
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].id", i),
                            equalTo(expectedStudent.getId())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].startTime", i),
                            equalTo(expectedStudent.getStartTime())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].duration", i),
                            equalTo(expectedStudent.getDuration())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].classroomId", i),
                            equalTo(expectedStudent.getClassroomId())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].classroomCapacity", i),
                            equalTo(expectedStudent.getClassroomCapacity())));

            CourseModel expectedCourse = expectedStudent.getCourse();
            actions
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].course.id", i),
                            equalTo(expectedCourse.getId())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].course.name", i),
                            equalTo(expectedCourse.getName())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].course.description", i),
                            equalTo(expectedCourse.getDescription())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].course.categoryCode", i),
                            equalTo(expectedCourse.getCategoryCode())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].course.teacherId", i),
                            equalTo(expectedCourse.getTeacherId())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].course.teacherFullName", i),
                            equalTo(expectedCourse.getTeacherFullName())));
        }
    }

    private void verifyJson(ResultActions actions, Timetable timetable) throws Exception {
        TimetableModel expected = modelConverter.convert(timetable, TimetableModel.class);
        actions
                .andExpect(jsonPath("$.id", equalTo(expected.getId())))
                .andExpect(jsonPath("$.startTime", equalTo(expected.getStartTime())))
                .andExpect(jsonPath("$.duration", equalTo(expected.getDuration())))
                .andExpect(jsonPath("$.classroomId", equalTo(expected.getClassroomId())))
                .andExpect(jsonPath("$.classroomCapacity", equalTo(expected.getClassroomCapacity())));

        CourseModel expectedCourse = expected.getCourse();
        actions
                .andExpect(jsonPath("$.course.id", equalTo(expectedCourse.getId())))
                .andExpect(jsonPath("$.course.name", equalTo(expectedCourse.getName())))
                .andExpect(jsonPath("$.course.description", equalTo(expectedCourse.getDescription())))
                .andExpect(jsonPath("$.course.categoryCode", equalTo(expectedCourse.getCategoryCode())))
                .andExpect(jsonPath("$.course.teacherId", equalTo(expectedCourse.getTeacherId())))
                .andExpect(jsonPath("$.course.teacherFullName", equalTo(expectedCourse.getTeacherFullName())));
    }

}
