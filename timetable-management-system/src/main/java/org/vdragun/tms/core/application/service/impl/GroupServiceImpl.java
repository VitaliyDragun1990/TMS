package org.vdragun.tms.core.application.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.GroupService;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.dao.GroupDao;

/**
 * Default implementation of {@link GroupService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
public class GroupServiceImpl implements GroupService {

    private GroupDao dao;

    public GroupServiceImpl(GroupDao dao) {
        this.dao = dao;
    }

    @Override
    public Group registerNewGroup(String name) {
        Group group = new Group(name);
        dao.save(group);
        return group;
    }

    @Override
    public Group findGroupById(Integer groupId) {
        return dao.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group with id=%d not found", groupId));
    }

    @Override
    public List<Group> findAllGroups() {
        return dao.findAll();
    }

}
