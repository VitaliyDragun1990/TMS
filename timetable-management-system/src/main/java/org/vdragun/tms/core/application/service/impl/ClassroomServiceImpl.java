package org.vdragun.tms.core.application.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.ClassroomService;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.dao.ClassroomDao;

/**
 * Default implementation of {@link ClassroomService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
public class ClassroomServiceImpl implements ClassroomService {

    private static final Logger LOG = LoggerFactory.getLogger(ClassroomServiceImpl.class);

    private ClassroomDao dao;

    public ClassroomServiceImpl(ClassroomDao dao) {
        this.dao = dao;
    }

    @Override
    public Classroom registerNewClassroom(int capacity) {
        LOG.debug("Registering new classroom with capacity={}", capacity);

        Classroom classroom = new Classroom(capacity);
        dao.save(classroom);

        LOG.debug("New classroom has been registered: {}", classroom);
        return classroom;
    }

    @Override
    public Classroom findClassroomById(Integer classroomId) {
        LOG.debug("Searching for classroom with id={}", classroomId);

        return dao.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom with id=%d not found", classroomId));
    }

    @Override
    public List<Classroom> findAllClassrooms() {
        LOG.debug("Retrieving all classrooms");

        List<Classroom> result = dao.findAll();

        LOG.debug("Found {} classrooms", result.size());
        return result;
    }

}
