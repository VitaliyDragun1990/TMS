package org.vdragun.tms.dao;

import java.util.List;
import java.util.Optional;

import org.vdragun.tms.core.domain.Group;

/**
 * Defines CRUD operations to access {@link Group} objects in the persistent
 * storage
 * 
 * @author Vitaliy Dragun
 *
 */
public interface GroupDao {

    /**
     * Saves specified group instance. Saved object receives unique identifier.
     */
    void save(Group group);

    /**
     * Saves all specified group instances. Each saved object receives unique
     * identifier.
     */
    void saveAll(Iterable<Group> groups);

    /**
     * Returns group with specified identifier if any.
     */
    Optional<Group> findById(int groupId);

    /**
     * Finds all available groups.
     */
    List<Group> findAll();

    /**
     * Checks whether group with specified identifier exists
     * 
     * @return {@code true} if such group exists, {@code false} otherwise
     */
    boolean existsById(Integer groupId);
}
