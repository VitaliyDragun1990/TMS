package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import org.vdragun.tms.core.application.service.timetable.CreateTimetableData;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = RegisterTimetableResource.class)
@Import({ WebConfig.class, WebRestConfig.class })
@DisplayName("Register Timetable Resource")
public class RegisterTimetableResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final LocalDateTime TIMETABLE_START_TIME = LocalDateTime.now().plusDays(3).truncatedTo(MINUTES);
    private static final int TEACHER_ID = 3;
    private static final int CLASSROOM_ID = 2;
    private static final int COURSE_ID = 1;
    private static final int DURATION = 60;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelConverter modelConverter;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TimetableService timetableServiceMock;

    @Captor
    private ArgumentCaptor<CreateTimetableData> captor;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldRegisterNewTimetable() throws Exception {
        CreateTimetableData registerData =
                new CreateTimetableData(TIMETABLE_START_TIME, DURATION, COURSE_ID, CLASSROOM_ID, TEACHER_ID);
        Timetable expectedTimetable = generator.generateTimetable();
        when(timetableServiceMock.registerNewTimetable(any(CreateTimetableData.class))).thenReturn(expectedTimetable);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/timetables")
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(registerData)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));
        
        verify(timetableServiceMock, times(1)).registerNewTimetable(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(registerData, "startTime"));
        verifyJson(resultActions, expectedTimetable);
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
