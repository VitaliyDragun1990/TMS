package org.vdragun.tms.core.application.service.group;

import java.util.List;

import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Group;

/**
 * Application entry point to work with {@link Group}
 * 
 * @author Vitaliy Dragun
 *
 */
public interface GroupService {

    /**
     * Registers new group with given name
     * 
     * @param name name of the group to register
     * @return newly registered group instance
     */
    Group registerNewGroup(String name);

    /**
     * Returns group with given identifier
     * 
     * @param groupId existing group identifier
     * @return group with given identifier
     * @throws ResourceNotFoundException if no group with given identifier
     */
    Group findGroupById(Integer groupId);

    /**
     * Finds all groups available
     * 
     * @return list of all available groups
     */
    List<Group> findAllGroups();
}
