package org.vdragun.tms.core.application.service.classroom;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.dao.ClassroomDao;

/**
 * Default implementation of {@link ClassroomService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
@Transactional
public class ClassroomServiceImpl implements ClassroomService {

    private static final Logger LOG = LoggerFactory.getLogger(ClassroomServiceImpl.class);

    private ClassroomDao dao;

    public ClassroomServiceImpl(ClassroomDao dao) {
        this.dao = dao;
    }

    @Override
    public Classroom registerNewClassroom(ClassroomData classroomData) {
        LOG.debug("Registering new classroom with capacity={}", classroomData.getCapacity());

        Classroom classroom = new Classroom(classroomData.getCapacity());
        dao.save(classroom);

        LOG.debug("New classroom has been registered: {}", classroom);
        return classroom;
    }

    @Override
    @Transactional(readOnly = true)
    public Classroom findClassroomById(Integer classroomId) {
        LOG.debug("Searching for classroom with id={}", classroomId);

        return dao.findById(classroomId)
                .orElseThrow(() -> 
                new ResourceNotFoundException(Classroom.class, "Classroom with id=%d not found", classroomId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Classroom> findAllClassrooms() {
        LOG.debug("Retrieving all classrooms");

        List<Classroom> result = dao.findAll();

        LOG.debug("Found {} classrooms", result.size());
        return result;
    }

}
