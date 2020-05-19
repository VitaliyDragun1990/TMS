package org.vdragun.tms.ui.web.controller.timetable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.service.classroom.ClassroomService;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.application.service.timetable.CreateTimetableData;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.common.util.Constants.Attribute;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.common.util.Constants.Page;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.ui.web.controller.MessageProvider;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = RegisterTimetableController.class)
@Import({ WebMvcConfig.class, MessageProvider.class })
@DisplayName("Register Timetable Controller")
public class RegisterTimetableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private TimetableService timetableServiceMock;

    @MockBean
    private TeacherService teacherServiceMock;

    @MockBean
    private CourseService courseServiceMock;

    @MockBean
    private ClassroomService classroomServiceMock;

    @Captor
    private ArgumentCaptor<CreateTimetableData> captor;

    private EntityGenerator generator = new EntityGenerator();

    private String dateTimeFormat;

    @BeforeEach
    void setUp() {
        dateTimeFormat = getMessage(Message.DATE_TIME_FORMAT);
    }

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(dateTimeFormat));
    }

    @Test
    void shouldShowTimetableRegistrationForm() throws Exception {
        List<Teacher> teachers = generator.generateTeachers(10);
        List<Course> courses = generator.generateCourses(10);
        List<Classroom> classrooms = generator.generateClassrooms(10);
        when(teacherServiceMock.findAllTeachers()).thenReturn(teachers);
        when(courseServiceMock.findAllCourses()).thenReturn(courses);
        when(classroomServiceMock.findAllClassrooms()).thenReturn(classrooms);

        mockMvc.perform(get("/timetables/register").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TEACHERS, Attribute.COURSES, Attribute.CLASSROOMS,
                        Attribute.TIMETABLE))
                .andExpect(model().attribute(Attribute.TEACHERS, equalTo(teachers)))
                .andExpect(model().attribute(Attribute.COURSES, equalTo(courses)))
                .andExpect(model().attribute(Attribute.CLASSROOMS, equalTo(classrooms)))
                .andExpect(model().attribute(Attribute.TIMETABLE, samePropertyValuesAs(new CreateTimetableData())))
                .andExpect(view().name(Page.TIMETABLE_REG_FORM));
    }

    @Test
    void shouldRegisterNewTimetableIfNoValidationErrors() throws Exception {
        LocalDateTime startTime = LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.MINUTES);
        Integer duration = 60;
        Integer courseId = 1;
        Integer classroomId = 1;
        Integer teacherId = 1;

        Timetable registered = generator.generateTimetable();
        when(timetableServiceMock.registerNewTimetable(any(CreateTimetableData.class))).thenReturn(registered);
        
        mockMvc.perform(post("/timetables").locale(Locale.US)
                .param("startTime", formatDateTime(startTime))
                .param("durationInMinutes", duration.toString())
                .param("courseId", courseId.toString())
                .param("classroomId", classroomId.toString())
                .param("teacherId", teacherId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(Attribute.INFO_MESSAGE,
                        equalTo(getMessage(Message.TIMETABLE_REGISTER_SUCCESS))))
                .andExpect(redirectedUrlTemplate("/timetables/{timetableId}", registered.getId()));

        CreateTimetableData expected = new CreateTimetableData(
                startTime,
                duration,
                courseId,
                classroomId,
                teacherId);
        verify(timetableServiceMock, times(1)).registerNewTimetable(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(expected));
    }

    @Test
    void shouldShowTimetableRegistrationFormIfValidationErrors() throws Exception {
        LocalDateTime pastStartTime = LocalDateTime.now().minusDays(3).truncatedTo(ChronoUnit.MINUTES);
        Integer invalidDuration = 10;
        Integer invalidCourseId = 0;
        Integer invalidClassroomId = -1;
        Integer invalidTeacherId = -100;

        mockMvc.perform(post("/timetables").locale(Locale.US)
                .param("startTime", formatDateTime(pastStartTime))
                .param("durationInMinutes", invalidDuration.toString())
                .param("courseId", invalidCourseId.toString())
                .param("classroomId", invalidClassroomId.toString())
                .param("teacherId", invalidTeacherId.toString()))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(5))
                .andExpect(model().attributeHasFieldErrors("timetable", "startTime", "durationInMinutes", "courseId",
                        "classroomId", "teacherId"))
                .andExpect(model().attribute(Attribute.VALIDATED, equalTo(true)))
                .andExpect(view().name(Page.TIMETABLE_REG_FORM));

        verify(timetableServiceMock, never()).registerNewTimetable(any(CreateTimetableData.class));
    }

}
