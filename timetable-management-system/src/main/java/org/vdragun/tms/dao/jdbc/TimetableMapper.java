package org.vdragun.tms.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;

/**
 * Implementation of {@link RowMapper} to map result set to {@link Timetable}
 * instance
 * 
 * @author Vitaliy Dragun
 *
 */
public class TimetableMapper implements RowMapper<Timetable> {

    private ClassroomMapper classroomMapper;
    private TeacherMapper teacherMapper;
    private CourseMapper courseMapper;

    public TimetableMapper() {
        classroomMapper = new ClassroomMapper();
        teacherMapper = new TeacherMapper();
        courseMapper = new CourseMapper();
    }

    @Override
    public Timetable mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("timetable_id");
        LocalDateTime startTime = rs.getObject("start_date_time", LocalDateTime.class);
        int duration = rs.getInt("duration");
        Course course = courseMapper.mapRow(rs, rowNum);
        Teacher teacher = teacherMapper.mapRow(rs, rowNum);
        Classroom classroom = classroomMapper.mapRow(rs, rowNum);

        return new Timetable(id, startTime, duration, course, classroom, teacher);
    }

}
