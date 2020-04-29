package org.vdragun.tms.core.application.service;

import java.util.List;

import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Classroom;

/**
 * Application entry point to work with {@link Classroom}
 * 
 * @author Vitaliy Dragun
 *
 */
public interface ClassroomService {

    /**
     * Register new {@link Classroom} instance
     * 
     * @param capacity capacity of the new classroom
     * @return newly registered classroom instance
     */
    Classroom registerNewClassroom(int capacity);

    /**
     * Returns classroom with given identifier
     * 
     * @param classroomId existing classroom identifier
     * @return classroom with given identifier
     * @throws ResourceNotFoundException if no classroom with given identifier
     *                                   exists
     */
    Classroom findClassroomById(Integer classroomId);

    /**
     * Finds all classroom available
     * 
     * @return list of all available classrooms
     */
    List<Classroom> findAllClassrooms();
}
