package org.vdragun.tms.core.application.service.impl;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.vdragun.tms.core.domain.Title.ASSOCIATE_PROFESSOR;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.TimetableData;
import org.vdragun.tms.core.application.service.TimetableService;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.ClassroomDao;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.dao.StudentDao;
import org.vdragun.tms.dao.TeacherDao;
import org.vdragun.tms.dao.TimetableDao;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Timetable servce")
class TimetableServiceImplTest {

    private static final int NINETY_MINUTES = 90;
    private static final Integer TIMETABLE_ID = 1;
    private static final Integer STUDENT_ID = 2;
    private static final Integer TEACHER_ID = 3;
    private static final Integer CLASSROOM_ID = 4;
    private static final Integer COURSE_ID = 5;
    private static final LocalDateTime MARCH_TEN_NINE_THIRTY = LocalDateTime.of(2020, 3, 10, 9, 30);
    private static final LocalDateTime MARCH_THIRTEEN_NINE_THIRTY = LocalDateTime.of(2020, 3, 13, 9, 30);
    private static final LocalDate MARCH_TEN = LocalDate.of(2020, 3, 10);

    @Mock
    private StudentDao studentDaoMock;
    @Mock
    private ClassroomDao classroomDaoMock;
    @Mock
    private TeacherDao teacherDaoMock;
    @Mock
    private CourseDao courseDaoMock;
    @Mock
    private TimetableDao timetableDaoMock;

    @Captor
    private ArgumentCaptor<Timetable> captor;

    private TimetableService service;

    @BeforeEach
    void setUp() {
        service = new TimetableServiceImpl(timetableDaoMock, courseDaoMock, teacherDaoMock,
                classroomDaoMock, studentDaoMock);
    }

    @Test
    void shouldThrowExceptionWhenRegisterTimetableWithNonExistingClassroom() {
        when(classroomDaoMock.findById(any(Integer.class))).thenReturn(Optional.empty());
        when(courseDaoMock.findById(eq(COURSE_ID))).thenReturn(Optional.of(provideCourse()));
        when(teacherDaoMock.findById(eq(TEACHER_ID))).thenReturn(Optional.of(provideTeacher()));
        TimetableData data = new TimetableData(MARCH_THIRTEEN_NINE_THIRTY, NINETY_MINUTES, COURSE_ID, CLASSROOM_ID,
                TEACHER_ID);

        assertThrows(ResourceNotFoundException.class, () -> service.registerNewTimetable(data));
        
        verify(timetableDaoMock, never()).save(any(Timetable.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisterTimetableWithNonExistingCourse() {
        when(classroomDaoMock.findById(eq(CLASSROOM_ID))).thenReturn(Optional.of(provideClassroom()));
        when(courseDaoMock.findById(any(Integer.class))).thenReturn(Optional.empty());
        when(teacherDaoMock.findById(eq(TEACHER_ID))).thenReturn(Optional.of(provideTeacher()));
        TimetableData data = new TimetableData(MARCH_THIRTEEN_NINE_THIRTY, NINETY_MINUTES, COURSE_ID, CLASSROOM_ID,
                TEACHER_ID);

        assertThrows(ResourceNotFoundException.class, () -> service.registerNewTimetable(data));

        verify(timetableDaoMock, never()).save(any(Timetable.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisterTimetableWithNonExistingTeacher() {
        when(classroomDaoMock.findById(eq(CLASSROOM_ID))).thenReturn(Optional.of(provideClassroom()));
        when(courseDaoMock.findById(eq(COURSE_ID))).thenReturn(Optional.of(provideCourse()));
        when(teacherDaoMock.findById(any(Integer.class))).thenReturn(Optional.empty());
        TimetableData data = new TimetableData(MARCH_THIRTEEN_NINE_THIRTY, NINETY_MINUTES, COURSE_ID, CLASSROOM_ID,
                TEACHER_ID);

        assertThrows(ResourceNotFoundException.class, () -> service.registerNewTimetable(data));

        verify(timetableDaoMock, never()).save(any(Timetable.class));
    }

    @Test
    void shouldRegisterNewTimetable() {
        when(classroomDaoMock.findById(eq(CLASSROOM_ID))).thenReturn(Optional.of(provideClassroom()));
        when(courseDaoMock.findById(eq(COURSE_ID))).thenReturn(Optional.of(provideCourse()));
        when(teacherDaoMock.findById(eq(TEACHER_ID))).thenReturn(Optional.of(provideTeacher()));
        TimetableData data = new TimetableData(MARCH_THIRTEEN_NINE_THIRTY, NINETY_MINUTES, COURSE_ID, CLASSROOM_ID,
                TEACHER_ID);

        service.registerNewTimetable(data);

        verify(timetableDaoMock, times(1)).save(captor.capture());
        Timetable savedTimetable = captor.getValue();
        assertThat(savedTimetable.getStartTime(), equalTo(MARCH_THIRTEEN_NINE_THIRTY));
        assertThat(savedTimetable.getDurationInMinutes(), equalTo(NINETY_MINUTES));
        assertThat(savedTimetable.getClassroom().getId(), equalTo(CLASSROOM_ID));
        assertThat(savedTimetable.getCourse().getId(), equalTo(COURSE_ID));
        assertThat(savedTimetable.getTeacher().getId(), equalTo(TEACHER_ID));
    }

    @Test
    void shouldThrowExceptionIfNoTimetableWithGivenIdentifier() {
        when(timetableDaoMock.findById(any(Integer.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findTimetableById(TIMETABLE_ID));
    }

    @Test
    void shouldFindTimetableWithGivenIdentifier() {
        Timetable expected = buildTimetable(TIMETABLE_ID, MARCH_THIRTEEN_NINE_THIRTY, NINETY_MINUTES);
        when(timetableDaoMock.findById(eq(TIMETABLE_ID))).thenReturn(Optional.of(expected));

        Timetable result = service.findTimetableById(TIMETABLE_ID);

        assertThat(result, equalTo(expected));
    }

    @Test
    void shouldFindAllTimetables() {
        Timetable timetableA = buildTimetable(TIMETABLE_ID, MARCH_THIRTEEN_NINE_THIRTY, NINETY_MINUTES);
        Timetable timetableB = buildTimetable(TIMETABLE_ID + 1, MARCH_TEN_NINE_THIRTY, NINETY_MINUTES);
        when(timetableDaoMock.findAll()).thenReturn(asList(timetableA, timetableB));

        List<Timetable> result = service.findAllTimetables();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
    }

    @Test
    void shouldThrowExceptionWhenFindDailyTimetablesForNonExistingStudent() {
        when(studentDaoMock.existsById(any(Integer.class))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.findDailyTimetablesForStudent(STUDENT_ID, MARCH_TEN));
    }

    @Test
    void shouldFindDailyTimetablesForStudent() {
        Timetable timetable = buildTimetable(TIMETABLE_ID, MARCH_TEN_NINE_THIRTY, NINETY_MINUTES);
        when(studentDaoMock.existsById(eq(STUDENT_ID))).thenReturn(true);
        when(timetableDaoMock.findDailyForStudent(eq(STUDENT_ID), eq(MARCH_TEN))).thenReturn(asList(timetable));

        List<Timetable> result = service.findDailyTimetablesForStudent(STUDENT_ID, MARCH_TEN);

        assertThat(result, hasSize(1));
        assertThat(result, containsInAnyOrder(timetable));
    }

    @Test
    void shouldThrowExceptionWhenFindMonthlyTimetablesForNonExistingStudent() {
        when(studentDaoMock.existsById(any(Integer.class))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.findMonthlyTimetablesForStudent(STUDENT_ID, Month.MARCH));
    }
    
    @Test
    void shouldFindMonthlyTimetablesForStudent() {
        Timetable timetableA = buildTimetable(TIMETABLE_ID, MARCH_THIRTEEN_NINE_THIRTY, NINETY_MINUTES);
        Timetable timetableB = buildTimetable(TIMETABLE_ID + 1, MARCH_TEN_NINE_THIRTY, NINETY_MINUTES);
        when(studentDaoMock.existsById(eq(STUDENT_ID))).thenReturn(true);
        when(timetableDaoMock.findMonthlyForStudent(eq(STUDENT_ID), eq(Month.MARCH)))
                .thenReturn(asList(timetableA, timetableB));

        List<Timetable> result = service.findMonthlyTimetablesForStudent(STUDENT_ID, Month.MARCH);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
    }

    @Test
    void shouldThrowExceptionWhenFindDailyTimetablesForNonExistingTeacher() {
        when(teacherDaoMock.existsById(any(Integer.class))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.findDailyTimetablesForTeacher(TEACHER_ID, MARCH_TEN));
    }

    @Test
    void shouldFindDailyTimetablesForTeacher() {
        Timetable timetable = buildTimetable(TIMETABLE_ID, MARCH_TEN_NINE_THIRTY, NINETY_MINUTES);
        when(teacherDaoMock.existsById(eq(TEACHER_ID))).thenReturn(true);
        when(timetableDaoMock.findDailyForTeacher(eq(TEACHER_ID), eq(MARCH_TEN))).thenReturn(asList(timetable));

        List<Timetable> result = service.findDailyTimetablesForTeacher(TEACHER_ID, MARCH_TEN);

        assertThat(result, hasSize(1));
        assertThat(result, containsInAnyOrder(timetable));
    }

    @Test
    void shouldThrowExceptionWhenFindMonthlyTimetablesForNonExistingTeacher() {
        when(teacherDaoMock.existsById(any(Integer.class))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.findMonthlyTimetablesForTeacher(TEACHER_ID, Month.MARCH));
    }

    @Test
    void shouldFindMonthlyTimetablesForTeacher() {
        Timetable timetableA = buildTimetable(TIMETABLE_ID, MARCH_THIRTEEN_NINE_THIRTY, NINETY_MINUTES);
        Timetable timetableB = buildTimetable(TIMETABLE_ID + 1, MARCH_TEN_NINE_THIRTY, NINETY_MINUTES);
        when(teacherDaoMock.existsById(eq(TEACHER_ID))).thenReturn(true);
        when(timetableDaoMock.findMonthlyForTeacher(eq(TEACHER_ID), eq(Month.MARCH)))
                .thenReturn(asList(timetableA, timetableB));

        List<Timetable> result = service.findMonthlyTimetablesForTeacher(TEACHER_ID, Month.MARCH);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
    }

    private Timetable buildTimetable(Integer timetableId, LocalDateTime startTime, int duration) {
        return new Timetable(timetableId, startTime, duration, provideCourse(), provideClassroom(), provideTeacher());
    }

    private Classroom provideClassroom() {
        return new Classroom(CLASSROOM_ID, 10);
    }

    private Teacher provideTeacher() {
        return new Teacher(TEACHER_ID, "Jack", "Smith", ASSOCIATE_PROFESSOR, LocalDate.now());
    }

    private Course provideCourse() {
        Category category = new Category(10, "BIO", "Biology");
        return new Course(COURSE_ID, "Core Biology", category, "Awesome Biology", provideTeacher());
    }

}
