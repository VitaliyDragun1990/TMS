package org.vdragun.tms.core.application.service.student;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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
import static org.vdragun.tms.core.domain.Title.PROFESSOR;

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
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.dao.GroupDao;
import org.vdragun.tms.dao.StudentDao;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Student service")
public class StudentServiceImplTest {

    private static final LocalDate ENROLLMENT_DATE = LocalDate.now();
    private static final String JACK = "Jack";
    private static final String ANNA = "Anna";
    private static final String SMITH = "Smith";
    private static final String PORTER = "Porter";
    private static final Integer STUDENT_ID = 1;
    private static final Integer COURSE_ID = 2;
    private static final Integer GROUP_ID = 3;
    @Mock
    private CourseDao courseDaoMock;
    @Mock
    private GroupDao groupDaoMock;
    @Mock
    private StudentDao studentDaoMock;

    @Captor
    private ArgumentCaptor<Student> captor;

    private StudentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new StudentServiceImpl(studentDaoMock, groupDaoMock, courseDaoMock);
    }

    @Test
    void shouldRegisterNewStudent() {
        CreateStudentData data = new CreateStudentData(JACK, SMITH, ENROLLMENT_DATE);

        service.registerNewStudent(data);

        verify(studentDaoMock, times(1)).save(captor.capture());
        Student savedStudent = captor.getValue();
        assertThat(savedStudent.getFirstName(), equalTo(JACK));
        assertThat(savedStudent.getLastName(), equalTo(SMITH));
        assertThat(savedStudent.getEnrollmentDate(), equalTo(ENROLLMENT_DATE));
    }

    @Test
    void shouldThrowExceptionIfNoStudentWithGivenIdentifier() {
        when(studentDaoMock.findById(any(Integer.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findStudentById(STUDENT_ID));
    }

    @Test
    void shouldFindStudentByIdentifier() {
        Student expected = new Student(STUDENT_ID, JACK, SMITH, ENROLLMENT_DATE);
        when(studentDaoMock.findById(eq(STUDENT_ID))).thenReturn(Optional.of(expected));

        Student result = service.findStudentById(STUDENT_ID);

        assertThat(result, equalTo(expected));
    }

    @Test
    void shouldFindAllStudents() {
        Student jack = new Student(STUDENT_ID, JACK, SMITH, ENROLLMENT_DATE);
        Student anna = new Student(STUDENT_ID + 1, ANNA, PORTER, ENROLLMENT_DATE);
        when(studentDaoMock.findAll()).thenReturn(asList(jack, anna));

        List<Student> result = service.findAllStudents();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(jack, anna));
    }

    @Test
    void shouldThrowExceptionWhenSearchStudentsForNonExitingGroup() {
        when(groupDaoMock.existsById(any(Integer.class))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.findStudentsForGroup(GROUP_ID));
    }

    @Test
    void shouldFindStudentsForGroup() {
        Student jack = new Student(STUDENT_ID, JACK, SMITH, ENROLLMENT_DATE);
        Student anna = new Student(STUDENT_ID + 1, ANNA, PORTER, ENROLLMENT_DATE);
        when(groupDaoMock.existsById(eq(GROUP_ID))).thenReturn(true);
        when(studentDaoMock.findByGroupId(GROUP_ID)).thenReturn(asList(jack, anna));

        List<Student> result = service.findStudentsForGroup(GROUP_ID);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(jack, anna));
    }

    @Test
    void shouldThrowExceptionWhenFindStudentsForNonExistingCourse() {
        when(courseDaoMock.existsById(COURSE_ID)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.findStudentsForCourse(COURSE_ID));
    }

    @Test
    void shouldFindStudentsForCourse() {
        Student jack = new Student(STUDENT_ID, JACK, SMITH, ENROLLMENT_DATE);
        Student anna = new Student(STUDENT_ID + 1, ANNA, PORTER, ENROLLMENT_DATE);
        when(courseDaoMock.existsById(eq(COURSE_ID))).thenReturn(true);
        when(studentDaoMock.findByCourseId(COURSE_ID)).thenReturn(asList(jack, anna));

        List<Student> result = service.findStudentsForCourse(COURSE_ID);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(jack, anna));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingStudent() {
        when(studentDaoMock.existsById(any(Integer.class))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteStudentById(STUDENT_ID));
    }

    @Test
    void shouldDeleteStudentById() {
        when(studentDaoMock.existsById(eq(STUDENT_ID))).thenReturn(true);

        service.deleteStudentById(STUDENT_ID);

        verify(studentDaoMock).deleteById(eq(STUDENT_ID));
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistingStudent() {
        when(studentDaoMock.findById(any(Integer.class))).thenReturn(Optional.empty());
        UpdateStudentData studentData = updateData(STUDENT_ID, GROUP_ID, ANNA, PORTER, asList(COURSE_ID));
        
        assertThrows(ResourceNotFoundException.class, () -> service.updateExistingStudent(studentData));

        verify(studentDaoMock, never()).save(any(Student.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingStudentWithNonExistingGroup() {
        when(studentDaoMock.findById(eq(STUDENT_ID))).thenReturn(Optional.of(studentStub()));
        when(groupDaoMock.findById(any(Integer.class))).thenReturn(Optional.empty());
        UpdateStudentData studentData = updateData(STUDENT_ID, GROUP_ID, JACK, SMITH, asList(COURSE_ID));

        assertThrows(ResourceNotFoundException.class, () -> service.updateExistingStudent(studentData));

        verify(studentDaoMock, never()).save(any(Student.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingStudentWithNonExistingCourse() {
        when(studentDaoMock.findById(eq(STUDENT_ID))).thenReturn(Optional.of(studentStub()));
        when(groupDaoMock.findById(any(Integer.class))).thenReturn(Optional.of(groupStub()));
        when(courseDaoMock.findById(eq(COURSE_ID))).thenReturn(Optional.empty());
        UpdateStudentData studentData = updateData(STUDENT_ID, GROUP_ID, JACK, SMITH, asList(COURSE_ID));

        assertThrows(ResourceNotFoundException.class, () -> service.updateExistingStudent(studentData));

        verify(studentDaoMock, never()).save(any(Student.class));
    }

    @Test
    void shouldRemoveStudentFroumGroupIfGroupIdentifierIsNull() {
        when(studentDaoMock.findById(eq(STUDENT_ID))).thenReturn(Optional.of(studentStub()));
        when(groupDaoMock.findById(any(Integer.class))).thenReturn(Optional.of(groupStub()));
        when(courseDaoMock.findById(eq(COURSE_ID))).thenReturn(Optional.of(courseStab()));
        UpdateStudentData studentData = updateData(STUDENT_ID, null, JACK, SMITH, asList(COURSE_ID));

        Student result = service.updateExistingStudent(studentData);

        assertThat(result.getGroup(), is(nullValue()));
    }

    @Test
    void shouldAddStudentToGroupIfGroupIdentifierPresent() {
        Group expectedGroup = groupStub();
        when(studentDaoMock.findById(eq(STUDENT_ID))).thenReturn(Optional.of(studentStub()));
        when(groupDaoMock.findById(any(Integer.class))).thenReturn(Optional.of(expectedGroup));
        when(courseDaoMock.findById(eq(COURSE_ID))).thenReturn(Optional.of(courseStab()));
        UpdateStudentData studentData = updateData(STUDENT_ID, GROUP_ID, JACK, SMITH, asList(COURSE_ID));

        Student result = service.updateExistingStudent(studentData);

        assertThat(result.getGroup(), equalTo(expectedGroup));
    }

    @Test
    void shouldAddStudentToEachSpecifiedCourseWhenUpdating() {
        when(studentDaoMock.findById(eq(STUDENT_ID))).thenReturn(Optional.of(studentStub()));
        when(groupDaoMock.findById(any(Integer.class))).thenReturn(Optional.of(groupStub()));
        Course expectedCourse = courseStab();
        when(courseDaoMock.findById(eq(COURSE_ID))).thenReturn(Optional.of(expectedCourse));
        UpdateStudentData studentData = updateData(STUDENT_ID, GROUP_ID, JACK, SMITH, asList(COURSE_ID));

        Student result = service.updateExistingStudent(studentData);

        assertThat(result.getCourses(), hasSize(1));
        assertThat(result.getCourses(), hasItem(expectedCourse));
    }

    @Test
    void shouldUpdateStudentFirstAndLastNameWhenUpdating() {
        when(studentDaoMock.findById(eq(STUDENT_ID))).thenReturn(Optional.of(studentStub()));
        when(groupDaoMock.findById(any(Integer.class))).thenReturn(Optional.of(groupStub()));
        when(courseDaoMock.findById(eq(COURSE_ID))).thenReturn(Optional.of(courseStab()));
        UpdateStudentData studentData = updateData(STUDENT_ID, GROUP_ID, ANNA, PORTER, asList(COURSE_ID));

        Student result = service.updateExistingStudent(studentData);

        assertThat(result.getFirstName(), equalTo(ANNA));
        assertThat(result.getLastName(), equalTo(PORTER));
    }

    private UpdateStudentData updateData(Integer studentId, Integer groupId, String fName, String lName,
            List<Integer> courseIds) {
        return new UpdateStudentData(studentId, groupId, fName, lName, courseIds);
    }

    private Student studentStub() {
        return new Student(STUDENT_ID, JACK, SMITH, ENROLLMENT_DATE);
    }

    private Group groupStub() {
        return new Group(GROUP_ID, "st-25");
    }

    private Course courseStab() {
        return new Course(
                COURSE_ID,
                "English",
                new Category(1, "ENG", "Description"),
                new Teacher(1, "Anna", "Smith", PROFESSOR, LocalDate.now()));
    }

}
