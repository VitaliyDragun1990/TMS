package org.vdragun.tms.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;

/**
 * Implementation of {@link ResultSetExtractor} to map result set to list of
 * {@link Teacher} instances with their respective {@link Course} instances
 * 
 * @author Vitaliy Dragun
 *
 */
public class TeachersWithCoursesExtractor implements ResultSetExtractor<List<Teacher>> {

    private TeacherMapper teacherMapper;
    private CourseMapper courseMapper;

    public TeachersWithCoursesExtractor() {
        teacherMapper = new TeacherMapper();
        courseMapper = new CourseMapper();
    }

    @Override
    public List<Teacher> extractData(ResultSet rs) throws SQLException {
        Map<Integer, Teacher> result = new HashMap<>();

        int rowNum = 1;
        while (rs.next()) {
            int currentRow = rowNum++;
            int teacherId = rs.getInt("teacher_id");

            Teacher teacher = result.computeIfAbsent(teacherId, id -> mapTeacher(rs, currentRow));

            Integer courseId = rs.getObject("course_id", Integer.class);
            if (courseId != null) {
                teacher.addCourse(courseMapper.mapRow(rs, currentRow));
            }
        }

        return new ArrayList<>(result.values());
    }

    private Teacher mapTeacher(ResultSet rs, int rowNum) {
        try {
            return teacherMapper.mapRow(rs, rowNum);
        } catch (SQLException e) {
            throw new DataRetrievalFailureException(e.getMessage(), e);
        }
    }

}
