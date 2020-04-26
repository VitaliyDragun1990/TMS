package org.vdragun.tms.dao;

import java.util.Optional;

import org.vdragun.tms.core.domain.Classroom;

/**
 * Defines CRUD operations to access {@link Classroom} objects in the persistent
 * storage
 * 
 * @author Vitaliy Dragun
 *
 */
public interface CalssroomDao {

    /**
     * Saves specified class room instance. Saved object receives unique identifier.
     */
    void save(Classroom classRoom);

    /**
     * Returns class room with specified identifier if any.
     */
    Optional<Classroom> findById(Integer classRoomId);
}
