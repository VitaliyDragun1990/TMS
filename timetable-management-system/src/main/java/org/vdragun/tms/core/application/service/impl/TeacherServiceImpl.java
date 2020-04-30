package org.vdragun.tms.core.application.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.TeacherData;
import org.vdragun.tms.core.application.service.TeacherService;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.TeacherDao;

/**
 * Default implementation of {@link TeacherService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    private TeacherDao dao;

    public TeacherServiceImpl(TeacherDao dao) {
        this.dao = dao;
    }

    @Override
    public Teacher registerNewTeacher(TeacherData teacherData) {
        Teacher teacher = new Teacher(
                teacherData.getFirstName(),
                teacherData.getLastName(),
                teacherData.getTitle(),
                teacherData.getDateHired());
        dao.save(teacher);
        return teacher;
    }

    @Override
    public Teacher findTeacherById(Integer teacherId) {
        return dao.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher with id=%d not found", teacherId));
    }

    @Override
    public List<Teacher> findAllTeachers() {
        return dao.findAll();
    }

    @Override
    public Teacher findTeacherForCourse(Integer courseId) {
        return dao.findForCourse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher for course with id=%d not found: No course with such id", courseId));
    }

}
