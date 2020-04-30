package org.vdragun.tms.core.application.service.impl;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.TeacherData;
import org.vdragun.tms.core.application.service.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.TeacherDao;

@ExtendWith(MockitoExtension.class)
@DisplayName("Teacher service")
class TeacherServiceImplTest {

    private static final int ID = 1;
    private static final int COURSE_ID = 5;
    private static final String JACK = "Jack";
    private static final String ANNA = "Anna";
    private static final String SMITH = "Smith";
    private static final String PORTER = "Porter";
    private static final LocalDate DATE_HIRED = LocalDate.now();

    @Mock
    private TeacherDao daoMock;

    @Captor
    private ArgumentCaptor<Teacher> captor;

    private TeacherService service;

    @BeforeEach
    void setUp() {
        service = new TeacherServiceImpl(daoMock);
    }

    @Test
    void shouldRegisterNewTeacher() {
        TeacherData data = new TeacherData(JACK, SMITH, DATE_HIRED, ASSOCIATE_PROFESSOR);

        service.registerNewTeacher(data);
        
        verify(daoMock, times(1)).save(captor.capture());
        Teacher savedTeacher = captor.getValue();
        assertThat(savedTeacher.getFirstName(), equalTo(JACK));
        assertThat(savedTeacher.getLastName(), equalTo(SMITH));
        assertThat(savedTeacher.getDateHired(), equalTo(DATE_HIRED));
        assertThat(savedTeacher.getTitle(), equalTo(ASSOCIATE_PROFESSOR));
    }

    @Test
    void shouldThrowExceptionIfNoTeacherWithGivenIdentifier() {
        when(daoMock.findById(any(Integer.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findTeacherById(ID));
    }

    @Test
    void shouldFindTeacherById() {
        Teacher expected = new Teacher(ID, JACK, SMITH, ASSOCIATE_PROFESSOR, DATE_HIRED);
        when(daoMock.findById(eq(ID))).thenReturn(Optional.of(expected));

        Teacher result = service.findTeacherById(ID);

        assertThat(result, equalTo(expected));
    }

    @Test
    void shouldFindAllTeachers() {
        Teacher jack = new Teacher(ID, JACK, SMITH, ASSOCIATE_PROFESSOR, DATE_HIRED);
        Teacher anna = new Teacher(ID + 1, ANNA, PORTER, ASSOCIATE_PROFESSOR, DATE_HIRED);
        when(daoMock.findAll()).thenReturn(asList(jack, anna));

        List<Teacher> result = service.findAllTeachers();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(jack, anna));
    }

    @Test
    void shouldFindTeacherForCourseWithGivenIdentifier() {
        Teacher expected = new Teacher(ID, JACK, SMITH, ASSOCIATE_PROFESSOR, DATE_HIRED);
        when(daoMock.findForCourse(eq(COURSE_ID))).thenReturn(Optional.of(expected));

        Teacher result = service.findTeacherForCourse(COURSE_ID);

        assertThat(result, equalTo(expected));
    }

    @Test
    void shouldThrowExceptionIfFailToFindTeacherForCourseWithGivenId() {
        when(daoMock.findForCourse(COURSE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findTeacherForCourse(COURSE_ID));
    }

}
