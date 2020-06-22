package org.vdragun.tms.core.application.service.timetable;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.ClassroomDao;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.dao.StudentDao;
import org.vdragun.tms.dao.TeacherDao;
import org.vdragun.tms.dao.TimetableDao;

/**
 * Default implementation of {@link TimetableService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
@Transactional
public class TimetableServiceImpl implements TimetableService {

    private static final Logger LOG = LoggerFactory.getLogger(TimetableServiceImpl.class);

    private TimetableDao timetableDao;
    private CourseDao courseDao;
    private TeacherDao teacherDao;
    private ClassroomDao classroomDao;
    private StudentDao studentDao;

    public TimetableServiceImpl(TimetableDao timetableDao, CourseDao courseDao, TeacherDao teacherDao,
            ClassroomDao classroomDao, StudentDao studentDao) {
        this.timetableDao = timetableDao;
        this.courseDao = courseDao;
        this.teacherDao = teacherDao;
        this.classroomDao = classroomDao;
        this.studentDao = studentDao;
    }

    @Override
    public Timetable registerNewTimetable(CreateTimetableData timetableData) {
        LOG.debug("Registering new timetable using data: {}", timetableData);

        Classroom classroom = getClassroom(timetableData.getClassroomId());
        Course course = getCourse(timetableData.getCourseId());
        Teacher teacher = getTeacher(timetableData.getTeacherId());

        Timetable timetable = new Timetable(
                timetableData.getStartTime(),
                timetableData.getDuration(),
                course,
                classroom,
                teacher);
        timetableDao.save(timetable);

        LOG.debug("New timetable has been registered: {}", timetable);

        return timetable;
    }

    @Override
    public Timetable updateExistingTimetable(UpdateTimetableData timetableData) {
        LOG.debug("Updating existing timetable suing data: {}", timetableData);

        Timetable timetable = getTimetable(timetableData.getTimetableId());
        Classroom classroom = getClassroom(timetableData.getClassroomId());

        timetable.setClassroom(classroom);
        timetable.setDurationInMinutes(timetableData.getDuration());
        timetable.setStartTime(timetableData.getStartTime());
        timetableDao.update(timetable);

        LOG.debug("Timetable with id={} has been successfully updated", timetable.getId());

        return timetable;
    }

    @Override
    @Transactional(readOnly = true)
    public Timetable findTimetableById(Integer timetableId) {
        LOG.debug("Searching for timetable with id={}", timetableId);

        return getTimetable(timetableId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Timetable> findAllTimetables() {
        LOG.debug("Retrieving all timetables");

        List<Timetable> result = timetableDao.findAll();
        LOG.debug("Found {} timetables", result.size());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Timetable> findTimetables(Pageable pageable) {
        Page<Timetable> page = timetableDao.findAll(pageable);

        LOG.debug("Found {} timetables, page number: {}, page size: {}",
                page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());

        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Timetable> findDailyTimetablesForStudent(Integer studentId, LocalDate date) {
        LOG.debug("Retrieving all timetables for student with id={} for date={}", studentId, date);
        assertStudentExists(studentId);

        List<Timetable> result = timetableDao.findDailyForStudent(studentId, date);

        LOG.debug("Found {} timetables for student with id={} for date={}", result.size(), studentId, date);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Timetable> findDailyTimetablesForStudent(Integer studentId, LocalDate date, Pageable pageable) {
        assertStudentExists(studentId);

        Page<Timetable> page = timetableDao.findDailyForStudent(studentId, date, pageable);

        LOG.debug("Found {} timetables for student with id={} for date={}, page number: {}, page size: {}",
                page.getTotalElements(), studentId, date, page.getNumber(), page.getSize());

        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Timetable> findMonthlyTimetablesForStudent(Integer studentId, Month month) {
        LOG.debug("Retrieving all timetables for student with id={} for month={}", studentId, month);
        assertStudentExists(studentId);

        List<Timetable> result = timetableDao.findMonthlyForStudent(studentId, month);

        LOG.debug("Found {} timetables for student with id={} for month={}", result.size(), studentId, month);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Timetable> findMonthlyTimetablesForStudent(Integer studentId, Month month, Pageable pageable) {
        assertStudentExists(studentId);

        Page<Timetable> page = timetableDao.findMonthlyForStudent(studentId, month, pageable);

        LOG.debug("Found {} timetables for student with id={} for month={}, page number: {}, page size: {}",
                page.getTotalElements(), studentId, month, page.getNumber(), page.getSize());

        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Timetable> findDailyTimetablesForTeacher(Integer teacherId, LocalDate date) {
        LOG.debug("Retrieving all timetables for teacher with id={} for date={}", teacherId, date);
        assertTeacherExists(teacherId);

        List<Timetable> result = timetableDao.findDailyForTeacher(teacherId, date);

        LOG.debug("Found {} timetables for teacher with id={} for date={}", result.size(), teacherId, date);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Timetable> findDailyTimetablesForTeacher(Integer teacherId, LocalDate date, Pageable pageable) {
        assertTeacherExists(teacherId);

        Page<Timetable> page = timetableDao.findDailyForTeacher(teacherId, date, pageable);

        LOG.debug("Found {} timetables for teacher with id={} for date={}, page number: {}, page size: {}",
                page.getTotalElements(), teacherId, date, page.getNumber(), page.getSize());
        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Timetable> findMonthlyTimetablesForTeacher(Integer teacherId, Month month) {
        LOG.debug("Retrieving all timetables for teacher with id={} for month={}", teacherId, month);
        assertTeacherExists(teacherId);

        List<Timetable> result = timetableDao.findMonthlyForTeacher(teacherId, month);

        LOG.debug("Found {} timetables for teacher with id={} for month={}", result.size(), teacherId, month);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Timetable> findMonthlyTimetablesForTeacher(Integer teacherId, Month month, Pageable pageable) {
        assertTeacherExists(teacherId);

        Page<Timetable> page = timetableDao.findMonthlyForTeacher(teacherId, month, pageable);

        LOG.debug("Found {} timetables for teacher with id={} for month={}, page number: {}, page size: {}",
                page.getTotalElements(), teacherId, month, page.getNumber(), page.getSize());

        return page;
    }

    @Override
    public void deleteTimetableById(Integer timetableId) {
        LOG.debug("Deleting timetable with id={}", timetableId);
        
        if (timetableDao.existsById(timetableId)) {
            timetableDao.deleteById(timetableId);
        } else {
            throw new ResourceNotFoundException(
                    Timetable.class,
                    "Fail to delete timetable: timetable with id=%d does not exist",
                    timetableId);
        }
    }

    private void assertTeacherExists(Integer teacherId) {
        if (!teacherDao.existsById(teacherId)) {
            throw new ResourceNotFoundException(Teacher.class, "Teacher with id=%d does not exist", teacherId);
        }
    }

    private void assertStudentExists(Integer studentId) {
        if (!studentDao.existsById(studentId)) {
            throw new ResourceNotFoundException(Student.class, "Student with id=%d does not exist", studentId);
        }
    }


    private Classroom getClassroom(Integer classroomId) {
        return classroomDao.findById(classroomId)
                .orElseThrow(
                        () -> 
                        new ResourceNotFoundException(Classroom.class,
                                "Classroom with id=%d does not exist", classroomId));
    }

    private Timetable getTimetable(Integer timetableId) {
        return timetableDao.findById(timetableId)
                .orElseThrow(
                        () -> 
                        new ResourceNotFoundException(Timetable.class,
                                "Timetable with id=%d does not exist", timetableId));
    }
    
    private Course getCourse(Integer courseId) {
        return courseDao.findById(courseId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Course.class,
                                "Course with id=%d does not exist", courseId));
    }

    private Teacher getTeacher(Integer teacherId) {
        return teacherDao.findById(teacherId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Teacher.class,
                                "Teacher with id=%d does not exist", teacherId));
    }

}
