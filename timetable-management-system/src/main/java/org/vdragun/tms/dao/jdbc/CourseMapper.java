package org.vdragun.tms.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;

/**
 * Implementation of {@link RowMapper} to map result set to {@link Course}
 * instance
 * 
 * @author Vitaliy Dragun
 *
 */
public class CourseMapper implements RowMapper<Course> {

    private CategoryMapper categoryMapper;
    private TeacherMapper teacherMapper;

    public CourseMapper() {
        categoryMapper = new CategoryMapper();
        teacherMapper = new TeacherMapper();
    }

    @Override
    public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("course_id");
        String name = rs.getString("course_name");
        String description = rs.getString("course_description");
        Category category = categoryMapper.mapRow(rs, rowNum);
        Teacher teacher = teacherMapper.mapRow(rs, rowNum);

        return new Course(id, name, category, description, teacher);
    }

}
