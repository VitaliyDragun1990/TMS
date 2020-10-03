package org.vdragun.tms.core.application.service.course;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.vdragun.tms.core.domain.Title.ASSOCIATE_PROFESSOR;

import java.time.LocalDate;
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
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.CategoryDao;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.dao.TeacherDao;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Course service")
public class CourseServiceImplTest {

    private static final String DESC_BIOLOGY = "Awesome biology course";

    private static final String NAME_CORE_BIOLOGY = "Core Biology";

    private static final String NAME_ADV_BIOLOGY = "Advanced Biology";

    private static final Integer CATEGORY_ID = 2;

    private static final Integer TEACHER_ID = 1;

    private static final Integer COURSE_ID = 3;

    @Mock
    private CategoryDao categoryDaoMock;

    @Mock
    private TeacherDao teacherDaoMock;

    @Mock
    private CourseDao courseDaoMock;

    @Captor
    private ArgumentCaptor<Course> captor;

    private CourseService service;

    @BeforeEach
    void setUp() {
        service = new CourseServiceImpl(courseDaoMock, categoryDaoMock, teacherDaoMock);
    }

    @Test
    void shouldThrowExceptionIfRegisterCourseWithNonExistingCategory() {
        when(categoryDaoMock.findById(any(Integer.class))).thenReturn(Optional.empty());
        when(teacherDaoMock.findById(any(Integer.class))).thenReturn(Optional.of(provideTeacher()));
        CourseData data = new CourseData(NAME_CORE_BIOLOGY, DESC_BIOLOGY, CATEGORY_ID, TEACHER_ID);

        assertThrows(ResourceNotFoundException.class, () -> service.registerNewCourse(data));

        verify(courseDaoMock, never()).save(any(Course.class));
    }

    @Test
    void shouldThrowExceptionIfRegisterCourseWithNonExistingTeacher() {
        when(categoryDaoMock.findById(any(Integer.class))).thenReturn(Optional.of(provideCategory()));
        when(teacherDaoMock.findById(any(Integer.class))).thenReturn(Optional.empty());
        CourseData data = new CourseData(NAME_CORE_BIOLOGY, DESC_BIOLOGY, CATEGORY_ID, TEACHER_ID);

        assertThrows(ResourceNotFoundException.class, () -> service.registerNewCourse(data));

        verify(courseDaoMock, never()).save(any(Course.class));
    }

    @Test
    void shouldRegisterNewCourse() {
        when(categoryDaoMock.findById(any(Integer.class))).thenReturn(Optional.of(provideCategory()));
        when(teacherDaoMock.findById(any(Integer.class))).thenReturn(Optional.of(provideTeacher()));
        CourseData data = new CourseData(NAME_CORE_BIOLOGY, DESC_BIOLOGY, CATEGORY_ID, TEACHER_ID);

        service.registerNewCourse(data);

        verify(courseDaoMock, times(1)).save(captor.capture());
        Course savedCourse = captor.getValue();
        assertThat(savedCourse.getName(), equalTo(NAME_CORE_BIOLOGY));
        assertThat(savedCourse.getDescription(), equalTo(DESC_BIOLOGY));
        assertThat(savedCourse.getCategory().getId(), equalTo(CATEGORY_ID));
        assertThat(savedCourse.getTeacher().getId(), equalTo(TEACHER_ID));
    }

    @Test
    void shouldThrowExceptionIfNoCourseWithGivenIdentifier() {
        when(courseDaoMock.findById(any(Integer.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findCourseById(COURSE_ID));
    }

    @Test
    void shouldFindCourseByIdentifier() {
        Course expected = new Course(COURSE_ID, NAME_CORE_BIOLOGY, provideCategory(), DESC_BIOLOGY, provideTeacher());
        when(courseDaoMock.findById(COURSE_ID)).thenReturn(Optional.of(expected));

        Course result = service.findCourseById(COURSE_ID);

        assertThat(result, equalTo(expected));
    }

    @Test
    void shouldFindAllExistingCourses() {
        Course coreBiology = new Course(COURSE_ID, NAME_CORE_BIOLOGY, provideCategory(), DESC_BIOLOGY, provideTeacher());
        Course advancedBiology = new Course(COURSE_ID + 1, NAME_ADV_BIOLOGY, provideCategory(), DESC_BIOLOGY,
                provideTeacher());
        when(courseDaoMock.findAll()).thenReturn(asList(coreBiology, advancedBiology));

        List<Course> result = service.findAllCourses();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(coreBiology, advancedBiology));
    }

    @Test
    void shouldFindAllCoursesForCategory() {
        Course coreBiology = new Course(COURSE_ID, NAME_CORE_BIOLOGY, provideCategory(), DESC_BIOLOGY, provideTeacher());
        Course advancedBiology = new Course(COURSE_ID + 1, NAME_ADV_BIOLOGY, provideCategory(), DESC_BIOLOGY,
                provideTeacher());
        when(courseDaoMock.findByCategoryId(CATEGORY_ID)).thenReturn(asList(coreBiology, advancedBiology));

        List<Course> result = service.findCoursesByCategory(CATEGORY_ID);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(coreBiology, advancedBiology));
    }

    private Teacher provideTeacher() {
        return new Teacher(TEACHER_ID, "Jack", "Smith", ASSOCIATE_PROFESSOR, LocalDate.now());
    }

    private Category provideCategory() {
        return new Category(CATEGORY_ID, "BIO", "Biology");
    }

}
