package org.vdragun.tms.core.application.service.classroom;

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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.dao.ClassroomDao;

@ExtendWith(MockitoExtension.class)
@DisplayName("Classroom service")
public class ClassroomServiceImplTest {

    private static final int ID = 1;
    private static final int CAPACITY = 25;

    @Mock
    private ClassroomDao daoMock;

    private ClassroomService service;

    @BeforeEach
    void setUp() {
        service = new ClassroomServiceImpl(daoMock);
    }

    @Test
    void shouldRegisterNewClassroom() {
        service.registerNewClassroom(new ClassroomData(CAPACITY));

        ArgumentCaptor<Classroom> captor = ArgumentCaptor.forClass(Classroom.class);
        verify(daoMock, times(1)).save(captor.capture());
        Classroom classroomToSave = captor.getValue();
        assertThat(classroomToSave.getCapacity(), equalTo(CAPACITY));
    }

    @Test
    void shouldThrowExceptionIfNoClassroomWithGivenIdentifier() {
        when(daoMock.findById(any(Integer.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findClassroomById(1));
    }

    @Test
    void shouldFindClassroomWithGivenIdentifier() {
        Classroom expected = new Classroom(ID, CAPACITY);
        when(daoMock.findById(eq(1))).thenReturn(Optional.of(expected));

        Classroom result = service.findClassroomById(1);

        assertThat(result, equalTo(expected));
    }

    @Test
    void shouldFindAllClassrooms() {
        Classroom classroomA = new Classroom(ID, CAPACITY);
        Classroom classroomB = new Classroom(ID + 1, CAPACITY);
        when(daoMock.findAll()).thenReturn(Arrays.asList(classroomA, classroomB));

        List<Classroom> result = service.findAllClassrooms();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(classroomA, classroomB));
    }

}
