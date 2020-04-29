package org.vdragun.tms.dao;

import java.util.List;
import java.util.Optional;

import org.vdragun.tms.core.domain.Classroom;

/**
 * Defines CRUD operations to access {@link Classroom} objects in the persistent
 * storage
 * 
 * @author Vitaliy Dragun
 *
 */
public interface ClassroomDao {

    /**
     * Saves specified class room instance. Saved object receives unique identifier.
     */
    void save(Classroom classroom);

    /**
     * Returns class room with specified identifier if any.
     */
    Optional<Classroom> findById(Integer classroomId);

    /**
     * Finds all classroom available
     */
    List<Classroom> findAll();
}
