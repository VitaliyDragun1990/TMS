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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.StudentData;
import org.vdragun.tms.core.application.service.StudentService;
import org.vdragun.tms.core.domain.Student;
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

    private StudentService service;

    @BeforeEach
    void setUp() {
        service = new StudentServiceImpl(studentDaoMock, groupDaoMock, courseDaoMock);
    }

    @Test
    void shouldRegisterNewStudent() {
        StudentData data = new StudentData(JACK, SMITH, ENROLLMENT_DATE);

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
    void shouldThrowExceptionWhenFindStudentsForNonExitingGroup() {
        when(groupDaoMock.existsById(any(Integer.class))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.findStudentsForGroup(GROUP_ID));
    }

    @Test
    void shouldFindStudentsForGroupWithGivenIdentifier() {
        Student jack = new Student(STUDENT_ID, JACK, SMITH, ENROLLMENT_DATE);
        Student anna = new Student(STUDENT_ID + 1, ANNA, PORTER, ENROLLMENT_DATE);
        when(groupDaoMock.existsById(eq(GROUP_ID))).thenReturn(true);
        when(studentDaoMock.findForGroup(GROUP_ID)).thenReturn(asList(jack, anna));

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
    void shouldFindStudentsForCourseWithGivenIdentifier() {
        Student jack = new Student(STUDENT_ID, JACK, SMITH, ENROLLMENT_DATE);
        Student anna = new Student(STUDENT_ID + 1, ANNA, PORTER, ENROLLMENT_DATE);
        when(courseDaoMock.existsById(eq(COURSE_ID))).thenReturn(true);
        when(studentDaoMock.findForCourse(COURSE_ID)).thenReturn(asList(jack, anna));

        List<Student> result = service.findStudentsForCourse(COURSE_ID);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(jack, anna));
    }

    @Test
    void shouldThrowExceptionWhenAddStudentToNonExistingGroup() {
        when(groupDaoMock.existsById(any(Integer.class))).thenReturn(false);
        when(studentDaoMock.existsById(eq(STUDENT_ID))).thenReturn(true);

        assertThrows(ResourceNotFoundException.class, () -> service.addStudentToGroup(STUDENT_ID, GROUP_ID));
    }

    @Test
    void shouldThrowExceptionWhenAddNonExistingStudentToGroup() {
        when(groupDaoMock.existsById(eq(GROUP_ID))).thenReturn(true);
        when(studentDaoMock.existsById(any(Integer.class))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.addStudentToGroup(STUDENT_ID, GROUP_ID));

        verify(studentDaoMock, never()).addToGroup(any(Integer.class), any(Integer.class));
    }

    @Test
    void shouldAddStudentToGroup() {
        when(groupDaoMock.existsById(eq(GROUP_ID))).thenReturn(true);
        when(studentDaoMock.existsById(eq(STUDENT_ID))).thenReturn(true);

        service.addStudentToGroup(STUDENT_ID, GROUP_ID);

        verify(studentDaoMock, times(1)).addToGroup(eq(STUDENT_ID), eq(GROUP_ID));
    }

    @Test
    void shouldThrowExceptionWhenRemoveNonExistingStudentFromGroup() {
        when(studentDaoMock.existsById(any(Integer.class))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.removeStudentFromGroup(STUDENT_ID));

        verify(studentDaoMock, never()).removeFromGroup(any(Integer.class));
    }

    @Test
    void shouldRemoveStudentFromCurrentGroup() {
        when(studentDaoMock.existsById(eq(STUDENT_ID))).thenReturn(true);

        service.removeStudentFromGroup(STUDENT_ID);

        verify(studentDaoMock, times(1)).removeFromGroup(eq(STUDENT_ID));
    }

    @Test
    void shouldThrowExceptionWhenAddStudentToNonExistingCourses() {
        when(studentDaoMock.existsById(eq(STUDENT_ID))).thenReturn(true);
        when(courseDaoMock.existsById(eq(COURSE_ID))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.setStudentCourses(STUDENT_ID, asList(COURSE_ID, COURSE_ID + 1)));

        verify(studentDaoMock, never()).removeFromAllCourses(any(Integer.class));
        verify(studentDaoMock, never()).addToCourse(any(Integer.class), any(Integer.class));
    }

    @Test
    void shouldThrowExceptionWhenSetCoursesToNonExistingStudent() {
        when(studentDaoMock.existsById(any(Integer.class))).thenReturn(false);
        when(courseDaoMock.existsById(any(Integer.class))).thenReturn(true);

        assertThrows(ResourceNotFoundException.class,
                () -> service.setStudentCourses(STUDENT_ID, asList(COURSE_ID, COURSE_ID + 1)));

        verify(studentDaoMock, never()).removeFromAllCourses(any(Integer.class));
        verify(studentDaoMock, never()).addToCourse(any(Integer.class), any(Integer.class));
    }

    @Test
    void shouldSetStudentCourses() {
        when(studentDaoMock.existsById(eq(STUDENT_ID))).thenReturn(true);
        when(courseDaoMock.existsById(any(Integer.class))).thenReturn(true);

        service.setStudentCourses(STUDENT_ID, asList(COURSE_ID, COURSE_ID + 1));

        InOrder order = Mockito.inOrder(studentDaoMock);
        order.verify(studentDaoMock).removeFromAllCourses(eq(STUDENT_ID));
        order.verify(studentDaoMock).addToCourse(eq(STUDENT_ID), eq(COURSE_ID));
        order.verify(studentDaoMock).addToCourse(eq(STUDENT_ID), eq(COURSE_ID + 1));
    }

    @Test
    void shouldThrowExceptionWhenRemoveNonExistingStudentFromCourses() {
        when(studentDaoMock.existsById(any(Integer.class))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.removeStudentFromAllCourses(STUDENT_ID));

        verify(studentDaoMock, never()).removeFromAllCourses(any(Integer.class));
    }

    @Test
    void shouldRemoveStudentFromAllCourses() {
        when(studentDaoMock.existsById(eq(STUDENT_ID))).thenReturn(true);

        service.removeStudentFromAllCourses(STUDENT_ID);

        verify(studentDaoMock).removeFromAllCourses(eq(STUDENT_ID));
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

}
