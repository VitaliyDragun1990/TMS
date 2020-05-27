package org.vdragun.tms.ui.web.controller.timetable;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Student;
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
@WebMvcTest(controllers = SearchTimetableController.class)
@Import({ WebConfig.class, WebMvcConfig.class, MessageProvider.class })
@DisplayName("Search Timetable Controller")
public class SearchTimetableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private TimetableService timetableServiceMock;

    @MockBean
    private TeacherService teacherServiceMock;

    @MockBean
    private StudentService studentServiceMock;

    private EntityGenerator generator = new EntityGenerator();

    private String dateFormat;

    @BeforeEach
    void setUp() {
        dateFormat = getMessage(Message.DATE_FORMAT);
    }

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(dateFormat));
    }

    private String formatMonth(Month month) {
        return month.getDisplayName(TextStyle.FULL, Locale.US);
    }

    @Test
    void shouldShowAllTimetablesPage() throws Exception {
        List<Timetable> timetables = generator.generateTimetables(10);
        when(timetableServiceMock.findAllTimetables()).thenReturn(timetables);

        mockMvc.perform(get("/timetables").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TIMETABLES, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.TIMETABLES, equalTo(timetables)))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.ALL_TIMETABLES, timetables.size()))))
                .andExpect(view().name(Page.TIMETABLES));
    }

    @Test
    void shouldShowTimetableInfoPageForTimetableWithProvidedIdentifier() throws Exception {
        Timetable timetable = generator.generateTimetable();
        when(timetableServiceMock.findTimetableById(timetable.getId())).thenReturn(timetable);
        
        mockMvc.perform(get("/timetables/{timetableId}", timetable.getId()).locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TIMETABLE))
                .andExpect(model().attribute(Attribute.TIMETABLE, equalTo(timetable)))
                .andExpect(view().name(Page.TIMETABLE_INFO));
    }

    @Test
    void shouldShowNotFoundPageIfNoTimetableWithProvidedIdentifier() throws Exception {
        Integer timetableId = 1;
        when(timetableServiceMock.findTimetableById(timetableId))
                .thenThrow(new ResourceNotFoundException(Timetable.class, "Timetable with id=%d not found",
                        timetableId));

        mockMvc.perform(get("/timetables/{timetableId}", timetableId).locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/timetables/" + timetableId))))
                .andExpect(view().name(Page.NOT_FOUND));
    }

    @Test
    void shouldShowPageBadRequestIfProvidedTimetableIdentifierIsNotNumber() throws Exception {
        String invalidTimetableId = "id";

        mockMvc.perform(get("/timetables/{timetableId}", invalidTimetableId).locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", invalidTimetableId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/timetables/" + invalidTimetableId))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    @Test
    void shouldShowPageWithDailyTimetablesForTicherWithProvidedIdentifier() throws Exception {
        Teacher teacher = generator.generateTeacher();
        List<Timetable> timetables = generator.generateTimetables(10);
        LocalDate targetDate = LocalDate.now();

        when(teacherServiceMock.findTeacherById(teacher.getId())).thenReturn(teacher);
        when(timetableServiceMock.findDailyTimetablesForTeacher(teacher.getId(), targetDate)).thenReturn(timetables);

        mockMvc.perform(get("/timetables/teacher/{teacherId}/day", teacher.getId()).locale(Locale.US)
                .param("targetDate", formatDate(targetDate)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TIMETABLES, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.TIMETABLES, equalTo(timetables)))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.TIMETABLES_FOR_TEACHER,
                                timetables.size(),
                                teacher.getFirstName(),
                                teacher.getLastName(),
                                formatDate(targetDate)))))
                .andExpect(view().name(Page.TIMETABLES));
    }

    @Test
    void shouldShowNotFoundPageWhenSearchDailyTimetablesForNonExistentTeacher() throws Exception {
        Integer nonExistentTeacherId = 1;
        when(timetableServiceMock.findDailyTimetablesForTeacher(eq(nonExistentTeacherId), any(LocalDate.class)))
                .thenThrow(new ResourceNotFoundException(Teacher.class, "Teacher with id=%d not found",
                        nonExistentTeacherId));

        mockMvc.perform(get("/timetables/teacher/{teacherId}/day", nonExistentTeacherId).locale(Locale.US)
                .param("targetDate", formatDate(LocalDate.now())))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.REQUESTED_RESOURCE,
                                "/timetables/teacher/" + nonExistentTeacherId + "/day"))))
                .andExpect(view().name(Page.NOT_FOUND));
    }

    @Test
    void shouldShowBadRequestPageWhenSearchDailyTimetablesForTeacherUsingNonNumberIdentifier() throws Exception {
        String invalidTeacherId = "id";

        mockMvc.perform(get("/timetables/teacher/{teacherId}/day", invalidTeacherId).locale(Locale.US)
                .param("targetDate", formatDate(LocalDate.now())))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", invalidTeacherId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.REQUESTED_RESOURCE,
                                "/timetables/teacher/" + invalidTeacherId + "/day"))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    @Test
    void shouldShowBadRequestPageWhenSearchDailyTimetablesForTeacherWithoutTargetDate() throws Exception {
        Integer teacherId = 1;

        mockMvc.perform(get("/timetables/teacher/{teacherId}/day", teacherId).locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR,
                        equalTo(getMessage(Message.REQUIRED_REQUEST_PARAMETER, "targetDate"))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.REQUESTED_RESOURCE,
                                "/timetables/teacher/" + teacherId + "/day"))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    @Test
    void shouldShowPageWithDailyTimetablesForStudentWithProvidedIdentifier() throws Exception {
        Student student = generator.generateStudent();
        List<Timetable> timetables = generator.generateTimetables(10);
        LocalDate targetDate = LocalDate.now();

        when(studentServiceMock.findStudentById(student.getId())).thenReturn(student);
        when(timetableServiceMock.findDailyTimetablesForStudent(student.getId(), targetDate)).thenReturn(timetables);

        mockMvc.perform(get("/timetables/student/{studentId}/day", student.getId()).locale(Locale.US)
                .param("targetDate", formatDate(targetDate)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TIMETABLES, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.TIMETABLES, equalTo(timetables)))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.TIMETABLES_FOR_STUDENT,
                                timetables.size(),
                                student.getFirstName(),
                                student.getLastName(),
                                formatDate(targetDate)))))
                .andExpect(view().name(Page.TIMETABLES));
    }

    @Test
    void shouldShowNotFoundPageWhenSearchDailyTimetablesForNonExistentStudent() throws Exception {
        Integer nonExistentStudentId = 1;
        when(timetableServiceMock.findDailyTimetablesForStudent(eq(nonExistentStudentId), any(LocalDate.class)))
                .thenThrow(new ResourceNotFoundException(Student.class, "Student with id=%d not found",
                        nonExistentStudentId));

        mockMvc.perform(get("/timetables/student/{studentId}/day", nonExistentStudentId).locale(Locale.US)
                .param("targetDate", formatDate(LocalDate.now())))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.REQUESTED_RESOURCE,
                                "/timetables/student/" + nonExistentStudentId + "/day"))))
                .andExpect(view().name(Page.NOT_FOUND));
    }

    @Test
    void shouldShowBadRequestPageWhenSearchDailyTimetablesForStudentUsingNonNumberIdentifier() throws Exception {
        String invalidStudentId = "id";

        mockMvc.perform(get("/timetables/student/{studentId}/day", invalidStudentId).locale(Locale.US)
                .param("targetDate", formatDate(LocalDate.now())))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", invalidStudentId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.REQUESTED_RESOURCE,
                                "/timetables/student/" + invalidStudentId + "/day"))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    @Test
    void shouldShowBadRequestPageWhenSearchDailyTimetablesForStudentWithoutTargetDate() throws Exception {
        Integer studentId = 1;

        mockMvc.perform(get("/timetables/student/{studentId}/day", studentId).locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR,
                        equalTo(getMessage(Message.REQUIRED_REQUEST_PARAMETER, "targetDate"))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.REQUESTED_RESOURCE,
                                "/timetables/student/" + studentId + "/day"))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    @Test
    void shouldShowPageWithMonthlyTimetablesForTicherWithProvidedIdentifier() throws Exception {
        Teacher teacher = generator.generateTeacher();
        List<Timetable> timetables = generator.generateTimetables(10);
        Month targetDate = LocalDate.now().getMonth();

        when(teacherServiceMock.findTeacherById(teacher.getId())).thenReturn(teacher);
        when(timetableServiceMock.findMonthlyTimetablesForTeacher(teacher.getId(), targetDate)).thenReturn(timetables);

        mockMvc.perform(get("/timetables/teacher/{teacherId}/month", teacher.getId()).locale(Locale.US)
                .param("targetDate", targetDate.toString()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TIMETABLES, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.TIMETABLES, equalTo(timetables)))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.TIMETABLES_FOR_TEACHER,
                                timetables.size(),
                                teacher.getFirstName(),
                                teacher.getLastName(),
                                formatMonth(targetDate)))))
                .andExpect(view().name(Page.TIMETABLES));
    }

    @Test
    void shouldShowNotFoundPageWhenSearchMonthlyTimetablesForNonExistentTeacher() throws Exception {
        Integer nonExistentTeacherId = 1;
        when(timetableServiceMock.findMonthlyTimetablesForTeacher(eq(nonExistentTeacherId), any(Month.class)))
                .thenThrow(new ResourceNotFoundException(Teacher.class, "Teacher with id=%d not found",
                        nonExistentTeacherId));

        mockMvc.perform(get("/timetables/teacher/{teacherId}/month", nonExistentTeacherId).locale(Locale.US)
                .param("targetDate", formatMonth(LocalDate.now().getMonth())))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.REQUESTED_RESOURCE,
                                "/timetables/teacher/" + nonExistentTeacherId + "/month"))))
                .andExpect(view().name(Page.NOT_FOUND));
    }

    @Test
    void shouldShowBadRequestPageWhenSearchMonthlyTimetablesForTeacherUsingNonNumberIdentifier() throws Exception {
        String invalidTeacherId = "id";

        mockMvc.perform(get("/timetables/teacher/{teacherId}/month", invalidTeacherId).locale(Locale.US)
                .param("targetDate", formatMonth(LocalDate.now().getMonth())))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", invalidTeacherId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.REQUESTED_RESOURCE,
                                "/timetables/teacher/" + invalidTeacherId + "/month"))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    @Test
    void shouldShowBadRequestPageWhenSearchMonthlyTimetablesForTeacherWithoutTargetDate() throws Exception {
        Integer teacherId = 1;

        mockMvc.perform(get("/timetables/teacher/{teacherId}/month", teacherId).locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR,
                        equalTo(getMessage(Message.REQUIRED_REQUEST_PARAMETER, "targetDate"))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.REQUESTED_RESOURCE,
                                "/timetables/teacher/" + teacherId + "/month"))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    @Test
    void shouldShowPageWithMonthlyTimetablesForStudentWithProvidedIdentifier() throws Exception {
        Student student = generator.generateStudent();
        List<Timetable> timetables = generator.generateTimetables(10);
        Month targetDate = LocalDate.now().getMonth();

        when(studentServiceMock.findStudentById(student.getId())).thenReturn(student);
        when(timetableServiceMock.findMonthlyTimetablesForStudent(student.getId(), targetDate)).thenReturn(timetables);

        mockMvc.perform(get("/timetables/student/{studentId}/month", student.getId()).locale(Locale.US)
                .param("targetDate", targetDate.toString()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TIMETABLES, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.TIMETABLES, equalTo(timetables)))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.TIMETABLES_FOR_STUDENT,
                                timetables.size(),
                                student.getFirstName(),
                                student.getLastName(),
                                formatMonth(targetDate)))))
                .andExpect(view().name(Page.TIMETABLES));
    }

    @Test
    void shouldShowNotFoundPageWhenSearchMonthlyTimetablesForNonExistentStudent() throws Exception {
        Integer nonExistentStudentId = 1;
        when(timetableServiceMock.findMonthlyTimetablesForStudent(eq(nonExistentStudentId), any(Month.class)))
                .thenThrow(new ResourceNotFoundException(Student.class, "Student with id=%d not found",
                        nonExistentStudentId));

        mockMvc.perform(get("/timetables/student/{studentId}/month", nonExistentStudentId).locale(Locale.US)
                .param("targetDate", formatMonth(LocalDate.now().getMonth())))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.REQUESTED_RESOURCE,
                                "/timetables/student/" + nonExistentStudentId + "/month"))))
                .andExpect(view().name(Page.NOT_FOUND));
    }

    @Test
    void shouldShowBadRequestPageWhenSearchMonthlyTimetablesForStudentUsingNonNumberIdentifier() throws Exception {
        String invalidStudentId = "id";

        mockMvc.perform(get("/timetables/student/{studentId}/month", invalidStudentId).locale(Locale.US)
                .param("targetDate", formatMonth(LocalDate.now().getMonth())))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", invalidStudentId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.REQUESTED_RESOURCE,
                                "/timetables/student/" + invalidStudentId + "/month"))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    @Test
    void shouldShowBadRequestPageWhenSearchMonthlyTimetablesForStudentWithoutTargetDate() throws Exception {
        Integer studentId = 1;

        mockMvc.perform(get("/timetables/student/{studentId}/month", studentId).locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR,
                        equalTo(getMessage(Message.REQUIRED_REQUEST_PARAMETER, "targetDate"))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(
                                Message.REQUESTED_RESOURCE,
                                "/timetables/student/" + studentId + "/month"))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

}
