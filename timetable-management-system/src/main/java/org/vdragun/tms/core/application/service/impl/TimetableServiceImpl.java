package org.vdragun.tms.core.application.service.impl;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.springframework.stereotype.Service;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.TimetableData;
import org.vdragun.tms.core.application.service.TimetableService;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
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
public class TimetableServiceImpl implements TimetableService {

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
    public Timetable registerNewTimetable(TimetableData timetableData) {
        Classroom classroom = requireExistingClassroom(timetableData.getClassroomId());
        Course course = requireExistingCourse(timetableData.getCourseId());
        Teacher teacher = requireExistingTeacher(timetableData.getTeacherId());

        Timetable timetable = new Timetable(
                timetableData.getStartTime(),
                timetableData.getDurationInMinutes(),
                course,
                classroom,
                teacher);
        timetableDao.save(timetable);

        return timetable;
    }

    @Override
    public Timetable findTimetableById(Integer timetableId) {
        return timetableDao.findById(timetableId)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable with id=%d not found", timetableId));
    }

    @Override
    public List<Timetable> findAllTimetables() {
        return timetableDao.findAll();
    }

    @Override
    public List<Timetable> findDailyTimetablesForStudent(Integer studentId, LocalDate date) {
        assertStudentExists(studentId, "Fail to find daily timetables for student");

        return timetableDao.findDailyForStudent(studentId, date);
    }

    @Override
    public List<Timetable> findMonthlyTimetablesForStudent(Integer studentId, Month month) {
        assertStudentExists(studentId, "Fail to find monthly timetables for student");

        return timetableDao.findMonthlyForStudent(studentId, month);
    }

    @Override
    public List<Timetable> findDailyTimetablesForTeacher(Integer teacherId, LocalDate date) {
        assertTeacherExists(teacherId, "Fail to find daily timetables for teacher");

        return timetableDao.findDailyForTeacher(teacherId, date);
    }

    @Override
    public List<Timetable> findMonthlyTimetablesForTeacher(Integer teacherId, Month month) {
        assertTeacherExists(teacherId, "Fail to find monthly timetables for teacher");

        return timetableDao.findMonthlyForTeacher(teacherId, month);
    }

    private void assertTeacherExists(Integer teacherId, String msg) {
        if (!teacherDao.existsById(teacherId)) {
            throw new ResourceNotFoundException("%s: teacher with id=%d does not exist", msg, teacherId);
        }
    }

    private void assertStudentExists(Integer studentId, String msg) {
        if (!studentDao.existsById(studentId)) {
            throw new ResourceNotFoundException("%s: student with id=%d does not exist", msg, studentId);
        }
    }

    private Classroom requireExistingClassroom(Integer classroomId) {
        return classroomDao.findById(classroomId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Fail to register new timetable: classroom with id=%d does not exist",
                                classroomId));
    }
    
    private Course requireExistingCourse(Integer courseId) {
        return courseDao.findById(courseId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Fail to register new timetable: course with id=%d does not exist", courseId));
    }

    private Teacher requireExistingTeacher(Integer teacherId) {
        return teacherDao.findById(teacherId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Fail to register new timetable: teacher with id=%d does not exist", teacherId));
    }

}
