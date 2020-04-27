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
import org.vdragun.tms.core.domain.Student;

/**
 * Implementation of {@link ResultSetExtractor} to map result set to list of
 * {@link Student} instances with their respective {@link Course} instances
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentsWithCoursesExtractor implements ResultSetExtractor<List<Student>> {

    private StudentMapper studentMapper;
    private GroupMapper groupMapper;
    private CourseMapper courseMapper;

    public StudentsWithCoursesExtractor() {
        studentMapper = new StudentMapper();
        groupMapper = new GroupMapper();
        courseMapper = new CourseMapper();
    }

    @Override
    public List<Student> extractData(ResultSet resultSet) throws SQLException {
        Map<Integer, Student> result = new HashMap<>();

        int rowNum = 1;
        while (resultSet.next()) {
            int currentRow = rowNum++;
            int studentId = resultSet.getInt("student_id");

            Student student = result.computeIfAbsent(studentId, id -> mapStudent(resultSet, currentRow));

            Integer groupId = resultSet.getObject("group_id", Integer.class);
            if (groupId != null) {
                student.setGroup(groupMapper.mapRow(resultSet, rowNum));
            }

            Integer courseId = resultSet.getObject("course_id", Integer.class);
            if (courseId != null) {
                student.addCourse(courseMapper.mapRow(resultSet, rowNum));
            }
        }

        return new ArrayList<>(result.values());
    }

    private Student mapStudent(ResultSet resultSet, int rowNum) {
        try {
            return studentMapper.mapRow(resultSet, rowNum);
        } catch (SQLException e) {
            throw new DataRetrievalFailureException(e.getMessage(), e);
        }
    }

}
