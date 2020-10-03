package org.vdragun.tms.core.application.service.teacher;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.core.application.exception.InvalidPageNumberException;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.TeacherDao;

/**
 * Default implementation of {@link TeacherService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private static final Logger LOG = LoggerFactory.getLogger(TeacherServiceImpl.class);

    private TeacherDao dao;

    public TeacherServiceImpl(TeacherDao dao) {
        this.dao = dao;
    }

    @Override
    public Teacher registerNewTeacher(TeacherData teacherData) {
        LOG.debug("Registering new teacher using data: {}", teacherData);

        Teacher teacher = new Teacher(
                teacherData.getFirstName(),
                teacherData.getLastName(),
                teacherData.getTitle(),
                teacherData.getDateHired());
        dao.save(teacher);

        LOG.debug("New teacher has been registered: {}", teacher);
        return teacher;
    }

    @Override
    @Transactional(readOnly = true)
    public Teacher findTeacherById(Integer teacherId) {
        LOG.debug("Searching for teacher with id={}", teacherId);

        return dao.findById(teacherId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Teacher.class, "Teacher with id=%d not found", teacherId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findAllTeachers() {
        LOG.debug("Retrieving all teachers");

        List<Teacher> result = dao.findAll();

        LOG.debug("Found {} teachers", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Teacher> findTeachers(Pageable pageable) {
        Page<Teacher> page = dao.findAll(pageable);
        assertValidPageNumber(page);

        LOG.debug("Found {} teachers, page number: {}, page size: {}",
                page.getNumberOfElements(), pageable.getPageNumber(), pageable.getPageSize());

        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public Teacher findTeacherForCourse(Integer courseId) {
        LOG.debug("Searching for teacher assigned to course with id={}", courseId);

        return dao.findForCourseWithId(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        Teacher.class, "Teacher for course with id=%d not found: No course with such id", courseId));
    }

    private void assertValidPageNumber(Page<Teacher> page) {
        if (page.getNumber() >= page.getTotalPages()) {
            throw new InvalidPageNumberException(
                    Teacher.class,
                    page.getNumber() + 1, page.getSize(), page.getTotalPages() - 1);
        }
    }

}
