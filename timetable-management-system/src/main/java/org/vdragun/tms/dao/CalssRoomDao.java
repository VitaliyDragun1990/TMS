package org.vdragun.tms.dao;

import java.util.Optional;

import org.vdragun.tms.core.domain.ClassRoom;

/**
 * Defines CRUD operations to access {@link ClassRoom} objects in the persistent
 * storage
 * 
 * @author Vitaliy Dragun
 *
 */
public interface CalssRoomDao {

    /**
     * Saves specified class room instance. Saved object receives unique identifier.
     */
    void save(ClassRoom classRoom);

    /**
     * Returns class room with specified identifier if any.
     */
    Optional<ClassRoom> findById(Integer classRoomId);
}
