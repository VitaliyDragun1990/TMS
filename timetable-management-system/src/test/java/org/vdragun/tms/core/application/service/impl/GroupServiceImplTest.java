package org.vdragun.tms.core.application.service.impl;

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
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.GroupService;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.dao.GroupDao;

@ExtendWith(MockitoExtension.class)
@DisplayName("Group Service")
class GroupServiceImplTest {

    private static final int ID = 1;
    private static final String NAME = "ph-25";

    @Mock
    private GroupDao daoMock;

    @Captor
    private ArgumentCaptor<Group> captor;

    private GroupService service;

    @BeforeEach
    void setUp() {
        service = new GroupServiceImpl(daoMock);
    }

    @Test
    void shouldRegisterNewGroup() {
        service.registerNewGroup(NAME);

        verify(daoMock, times(1)).save(captor.capture());
        Group savedGroup = captor.getValue();
        assertThat(savedGroup.getName(), equalTo(NAME));
    }

    @Test
    void shouldThrowExceptionIfNoGroupWithGivenIdentifier() {
        when(daoMock.findById(any(Integer.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findGroupById(ID));
    }

    @Test
    void shouldFindGroupById() {
        Group expected = new Group(ID, NAME);
        when(daoMock.findById(eq(ID))).thenReturn(Optional.of(expected));

        Group result = service.findGroupById(ID);

        assertThat(result, equalTo(expected));
    }

    @Test
    void shouldFindAllGroupsAvailable() {
        Group groupA = new Group(ID, NAME);
        Group groupB = new Group(ID + 1, NAME);
        when(daoMock.findAll()).thenReturn(Arrays.asList(groupA, groupB));

        List<Group> result = service.findAllGroups();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(groupA, groupB));
    }

}
