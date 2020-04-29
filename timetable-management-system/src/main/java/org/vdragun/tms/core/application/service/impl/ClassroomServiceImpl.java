package org.vdragun.tms.core.application.service.impl;

import java.util.List;
import java.util.Optional;

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

    private ClassroomDao dao;

    public ClassroomServiceImpl(ClassroomDao dao) {
        this.dao = dao;
    }

    @Override
    public Classroom registerNewClassroom(int capacity) {
        Classroom classroom = new Classroom(capacity);
        dao.save(classroom);
        return classroom;
    }

    @Override
    public Classroom findClassroomById(Integer classroomId) {
        Optional<Classroom> result = dao.findById(classroomId);
        return result
                .orElseThrow(() -> new ResourceNotFoundException("Classroom with id=%d not found", classroomId));
    }

    @Override
    public List<Classroom> findAllClassrooms() {
        return dao.findAll();
    }

}
