package org.vdragun.tms.core.application.service.group;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.dao.GroupDao;

/**
 * Default implementation of {@link GroupService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    private static final Logger LOG = LoggerFactory.getLogger(GroupServiceImpl.class);

    private GroupDao dao;

    public GroupServiceImpl(GroupDao dao) {
        this.dao = dao;
    }

    @Override
    public Group registerNewGroup(GroupData groupData) {
        LOG.debug("Registering new group with name={}", groupData.getName());

        Group group = new Group(groupData.getName());
        dao.save(group);

        LOG.debug("New group has been registered: {}", group);
        return group;
    }

    @Override
    @Transactional(readOnly = true)
    public Group findGroupById(Integer groupId) {
        LOG.debug("Searching for group with id={}", groupId);

        return dao.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group with id=%d not found", groupId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findAllGroups() {
        LOG.debug("Retrieving all groups");

        List<Group> result = dao.findAll();

        LOG.debug("Found {} groups", result.size());
        return result;
    }

}
